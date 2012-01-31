package com.j15r.common.client.widgets;

import com.google.gwt.topspin.ui.client.Container;
import com.google.gwt.topspin.ui.client.DefaultContainerImpl;
import com.google.gwt.topspin.ui.client.Widget;

import com.j15r.common.client.mvc.ListView;
import com.j15r.common.client.mvc.ObjectView;

import java.util.ArrayList;
import java.util.List;

public abstract class PagedListView<T> extends Widget implements ListView<T> {

  public interface ItemView<T> extends ObjectView<T> {
    void destroy();
  }

  private int firstItem, itemCount;
  private Delegate<T> delegate;
  private List<ItemView<T>> views = new ArrayList<ItemView<T>>();
  private int maxItems;
  private Container itemContainer;

  public PagedListView(Container container) {
    this(container, 10);
  }

  public PagedListView(Container container, int itemCount) {
    super(container.getDocument().createDivElement(), container);
    itemContainer = new DefaultContainerImpl(getElement());
    setItemCount(itemCount);
  }

  public ListView.Range getRange() {
    return new Range(firstItem, itemCount);
  }

  public void refresh() {
    firstItem = 0;
    if (delegate != null) {
      delegate.rangeChanged(this);
    }

    clearItems();
  }

  private void clearItems() {
    for (ItemView<T> view : views) {
      view.setData(null);
    }
  }

  public void setData(int start, Iterable<T> data) {
    int i = start;
    for (T value : data) {
      if (i >= firstItem) {
        views.get(i - firstItem).setData(value);
      }
      if (i >= firstItem + itemCount) {
        return;
      }
      ++i;
    }
  }

  public void setListDelegate(ListView.Delegate<T> delegate) {
    this.delegate = delegate;
  }

  public void setMaxItems(int maxItems) {
    this.maxItems = maxItems;
  }

  public void setFirstItem(int firstItem) {
    this.firstItem = firstItem;
    if (firstItem + itemCount > maxItems) {
      firstItem = maxItems - itemCount;
    }
    if (firstItem < 0) {
      firstItem = 0;
    }

    if (delegate != null) {
      delegate.rangeChanged(this);
    }
  }

  public void nextPage() {
    if (firstItem + itemCount < maxItems) {
      clearItems();

      firstItem += itemCount;
      if (delegate != null) {
        delegate.rangeChanged(this);
      }
    }
  }

  public void prevPage() {
    if (firstItem > 0) {
      clearItems();

      firstItem -= itemCount;
      if (firstItem < 0) {
        firstItem = 0;
      }
      if (delegate != null) {
        delegate.rangeChanged(this);
      }
    }
  }

  public void setItemCount(int itemCount) {
    assert (itemCount > 0) : "TODO";

    if (itemCount == this.itemCount) {
      return;
    }

    if (itemCount > this.itemCount) {
      for (int i = this.itemCount; i < itemCount; ++i) {
        views.add(doCreateItem(itemContainer));
      }
    } else {
      for (int i = this.itemCount - 1; i >= itemCount; --i) {
        views.remove(i).destroy();
      }
    }

    this.itemCount = itemCount;
    if (delegate != null) {
      delegate.rangeChanged(this);
    }
  }

  protected abstract ItemView<T> doCreateItem(Container container);
}
