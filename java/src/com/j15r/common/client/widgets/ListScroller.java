package com.j15r.common.client.widgets;

import com.google.gwt.dom.client.DivElement;

public abstract class ListScroller<T> extends Scroller<T> {

  private final int itemHeight;

  public ListScroller(LayoutContainer container, int itemHeight) {
    super(container);
    this.itemHeight = itemHeight;
    update();
  }

  @Override
  protected DivElement doCreateContainer() {
    DivElement div = getElement().getOwnerDocument().createDivElement();
    div.getStyle().setPropertyPx("height", itemHeight);
    div.getStyle().setProperty("overflow", "hidden");
    return div;
  }

  @Override
  protected Range computeRangeAndUpdateScrolling() {
    Range r = new Range();

    int height = getOffsetHeight();
    int scrollTop = getElement().getScrollTop();
    r.start = scrollTop / itemHeight;
    r.length = (height / itemHeight) + 2;

    updateScrolling(r.start * itemHeight, getMaxItems() * itemHeight);
    return r;
  }
}
