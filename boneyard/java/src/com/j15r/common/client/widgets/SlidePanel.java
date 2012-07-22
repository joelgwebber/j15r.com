package com.j15r.common.client.widgets;

import com.google.gwt.animation.client.Animation;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.topspin.ui.client.Container;
import com.google.gwt.topspin.ui.client.ContainerImpl;
import com.google.gwt.topspin.ui.client.Widget;

/**
 * A container that shows one of its children at a time, panning horizontally
 * between them when a different child is shown. For this container to work
 * properly, it is necessary to call {@link #onResize(int)} explicitly.
 */
public class SlidePanel extends Widget {

  private class SlideAnimation extends Animation {

    private final int targetPos;
    private final int startPos;

    public SlideAnimation(int targetPos) {
      this.startPos = visibleWidget.getElement().getOffsetLeft();
      this.targetPos = targetPos;
    }

    @Override
    protected void onUpdate(double progress) {
      // Adjust the position using the scrollLeft attribute.
      int pos = startPos + (int) ((targetPos - startPos) * progress);
      getElement().setScrollLeft(pos);
    }
  }

  private Container defaultContainer = new ContainerImpl() {
    public void add(Widget widget) {
      // Append the widget to the inner div, setting it to 'float:left' so that
      // it stacks up horizontally against the others.
      innerDiv.appendChild(widget.getElement());
      widget.getElement().getStyle().setProperty("float", "left");

      // Always start by showing the first widget added.
      if (visibleWidget == null) {
        showWidget(widget);
      }
    }

    public Document getDocument() {
      return getElement().getOwnerDocument();
    }

    public void remove(Widget widget) {
      assertIsChild(widget);
      innerDiv.removeChild(widget.getElement());
    }
  };

  private Element innerDiv;
  private Widget visibleWidget;

  /**
   * Creates a new slide panel in the given container.
   */
  public SlidePanel(Container container) {
    super(container.getDocument().createDivElement(), container);
    
    // Append the inner div, making it large enough to hold lots of pages.
    // The inner div is required to make the float:left trick work.
    innerDiv = container.getDocument().createDivElement();
    getElement().appendChild(innerDiv);
    innerDiv.getStyle().setPropertyPx("width", 4096); // big enough for many pages

    // Make the outer div overflow:hidden, so that its contents will be clipped
    // and no scrollbars displayed.
    getElement().getStyle().setProperty("overflow", "hidden");
  }

  /**
   * Gets the slid panel's default container.
   */
  public Container getContainer() {
    return defaultContainer;
  }

  /**
   * Gets the currently-visible widget.
   */
  public Widget getVisibleWidget() {
    return visibleWidget;
  }

  /**
   * Shows the given widget, sliding it into view.
   */
  public void showWidget(Widget widget) {
    assertIsChild(widget);
    if (widget == visibleWidget) {
      return;
    }

    if (visibleWidget != null) {
      int target = widget.getElement().getOffsetLeft();
      new SlideAnimation(target).run(500);
    }

    visibleWidget = widget;
  }

  /**
   * This method must be called explicitly, so that the panel can update its
   * scroll offset and outer width.
   */
  public void onResize(int width) {
    getElement().getStyle().setPropertyPx("width", width);

    if (visibleWidget == null) {
      return;
    }

    int pos = visibleWidget.getElement().getOffsetLeft();
    getElement().setScrollLeft(pos);
  }

  private void assertIsChild(Widget widget) {
    assert (widget.getElement().getParentElement().equals(innerDiv));
  }
}
