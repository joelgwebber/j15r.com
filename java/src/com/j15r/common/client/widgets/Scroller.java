package com.j15r.common.client.widgets;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.topspin.ui.client.ClickEvent;
import com.google.gwt.topspin.ui.client.ClickListener;
import com.google.gwt.topspin.ui.client.Container;
import com.google.gwt.topspin.ui.client.DefaultContainerImpl;
import com.google.gwt.topspin.ui.client.Widget;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.ScrollListener;
import com.j15r.common.client.mvc.ListView;
import com.j15r.common.client.mvc.ObjectView;
import com.j15r.common.client.mvc.Selectable;

public abstract class Scroller<T> extends Widget implements ListView<T>,
    Selectable<T> {

  public interface ItemView<T> extends ObjectView<T> {

    EventListenerRemover addClickListener(ClickListener listener);

    void destroy();

    void setLoading();

    void setSelected(boolean selected);
  }

  private static class ItemStuff<T> {
    public Element container;
    public ItemView<T> view;
  }

  private boolean timerPending;
  private Timer updateTimer = new Timer() {
    @Override
    public void run() {
      timerPending = false;
      listDelegate.rangeChanged(Scroller.this);
    }
  };

  private class Listeners implements ScrollListener, ClickListener {
    public void onClick(ClickEvent event) {
      assert event.getSource() instanceof ItemView : "TODO";
      ItemView<T> view = (ItemView<T>) event.getSource();

      clearSelection();
      addToSelection(view.getData());
    }

    public void onScroll(ScrollEvent event) {
      update();
    }
  }

  private final List<ItemStuff<T>> stuffs = new ArrayList<ItemStuff<T>>();
  private final Listeners listeners = new Listeners();
  private DivElement innerDiv;
  private DivElement spacerDiv;
  private final Range range = new Range();
  private Delegate<T> listDelegate;
  private int maxItems;
  private Set<T> selection = new HashSet<T>();

  public Scroller(LayoutContainer container) {
    super(container.getDocument().createDivElement(), container);

    range.start = -1;

    spacerDiv = container.getDocument().createDivElement();
    getElement().appendChild(spacerDiv);

    innerDiv = container.getDocument().createDivElement();
    innerDiv.getStyle().setProperty("position", "absolute");
    innerDiv.getStyle().setProperty("width", "100%");
    getElement().appendChild(innerDiv);

    getElement().getStyle().setProperty("overflowY", "scroll");
    getElement().getStyle().setProperty("overflowX", "auto");

    addScrollListener(listeners);
  }

  public void addToSelection(T item) {
    selection.add(item);
    ItemView<T> view = getViewForItem(item);
    if (view != null) {
      view.setSelected(true);
    }
  }

  public void clearSelection() {
    for (T item : selection) {
      ItemView<T> view = getViewForItem(item);
      if (view != null) {
        view.setSelected(false);
      }
    }

    selection.clear();
  }

  public void doLayout() {
    // TODO
  }

  public Range getRange() {
    return new Range(range.start, range.length);
  }

  public Iterable<T> getSelection() {
    return selection;
  }

  public boolean isSelected(T item) {
    return selection.contains(item);
  }

  public void refresh() {
    range.start = -1;
    for (ItemStuff<T> stuff : stuffs) {
      stuff.view.setLoading();
    }
    update();
    getElement().setScrollTop(0);
  }

  public void removeFromSelection(T item) {
    selection.remove(item);
    ItemView<T> view = getViewForItem(item);
    if (view != null) {
      view.setSelected(false);
    }
  }

  public void setData(int start, Iterable<T> data) {
    int index = 0;
    for (T item : data) {
      if (start + index < range.start) {
        ++index;
        continue;
      } else if (start + index >= range.start + range.length) {
        break;
      }

      ItemStuff<T> stuff = stuffs.get(start + index - range.start);
      if (stuff.view.getData() != item) {
        stuff.view.setData(item);
      }
      stuff.view.setSelected(selection.contains(item));

      ++index;
    }
  }

  public void setListDelegate(Delegate<T> listener) {
    this.listDelegate = listener;
  }

  public void setMaxItems(int maxItems) {
    this.maxItems = maxItems;
    update();
  }

  protected abstract Range computeRangeAndUpdateScrolling();

  protected abstract Element doCreateContainer();

  protected abstract ItemView<T> doCreateView(Container container);

  protected DivElement getInnerDiv() {
    return innerDiv;
  }

  protected int getMaxItems() {
    return maxItems;
  }

  protected void update() {
    int itemsCreated = 0;

    Range newRange = computeRangeAndUpdateScrolling();

    // Cap the number of items to maxItems
    if (newRange.start + newRange.length > maxItems) {
      newRange.length = maxItems - newRange.start;
    }

    // Adjust the number of views if necessary.
    if (newRange.length < range.length) {
      for (int i = range.length - 1; i >= newRange.length; --i) {
        removeItem(i);
      }
    } else if (newRange.length > range.length) {
      for (int i = range.length; i < newRange.length; ++i) {
        appendItem();
        ++itemsCreated;
      }
    }

    // Shuffle the items around.
    if (newRange.start > range.start) {
      for (int i = 0; (i < newRange.start - range.start)
          && (i < newRange.length); ++i) {
        ItemStuff<T> stuff = stuffs.remove(0);
        innerDiv.removeChild(stuff.container);
        innerDiv.appendChild(stuff.container);
        stuff.view.setLoading();
        stuffs.add(stuff);
      }
    } else if (newRange.start < range.start) {
      for (int i = 0; (i < range.start - newRange.start)
          && (i < newRange.length); ++i) {
        ItemStuff<T> stuff = stuffs.remove(newRange.length - 1);
        innerDiv.removeChild(stuff.container);
        innerDiv.insertBefore(stuff.container, innerDiv.getFirstChild());
        stuff.view.setLoading();
        stuffs.add(0, stuff);
      }
    }

    // Notify listener of range change.
    if ((range.start != newRange.start) || (range.length != newRange.length)) {
      range.start = newRange.start;
      range.length = newRange.length;
      maybeFireRangeChanged();
    }
  }

  protected void updateScrolling(int firstRowPx, int maxRowPx) {
    // Position the inner div.
    innerDiv.getStyle().setPropertyPx("top", firstRowPx);

    // Adjust the spacer div.
    spacerDiv.getStyle().setPropertyPx("height", maxRowPx);
  }

  private EventListenerRemover addScrollListener(final ScrollListener listener) {
    return ScrollEvent.addScrollListener(this, getElement(), listener);
  }

  private void appendItem() {
    ItemStuff<T> stuff = new ItemStuff<T>();
    stuff.container = doCreateContainer();
    innerDiv.appendChild(stuff.container);

    Container container = new DefaultContainerImpl(stuff.container);
    stuff.view = doCreateView(container);
    stuff.view.addClickListener(listeners);
    stuffs.add(stuff);
  }

  private ItemView<T> getViewForItem(T item) {
    for (ItemStuff<T> stuff : stuffs) {
      if (stuff.view.getData() == item) {
        return stuff.view;
      }
    }
    return null;
  }

  private void maybeFireRangeChanged() {
    if (listDelegate != null) {
      if (listDelegate.isRangeInCache(range.start, range.length)) {
        if (timerPending) {
          updateTimer.cancel();
        }
        listDelegate.rangeChanged(this);
      } else {
        timerPending = true;
        updateTimer.schedule(100);
      }
    }
  }

  private void removeItem(int i) {
    ItemStuff<T> stuff = stuffs.get(i);
    stuff.view.destroy();
    stuff.container.getParentNode().removeChild(stuff.container);

    stuffs.remove(i);
  }
}
