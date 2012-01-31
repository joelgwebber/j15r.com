package com.j15r.common.client.widgets;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.VerticalAlign;

public abstract class GridScroller<T> extends Scroller<T> {

  private final int itemWidth;
  private final int itemHeight;

  public GridScroller(LayoutContainer container, int itemWidth, int itemHeight) {
    super(container);
    this.itemWidth = itemWidth;
    this.itemHeight = itemHeight;

    getInnerDiv().getStyle().setProperty("textAlign", "center");
    update();
  }

  @Override
  protected Element doCreateContainer() {
    SpanElement span = getElement().getOwnerDocument().createSpanElement();
    span.getStyle().setPropertyPx("width", itemWidth);
    span.getStyle().setPropertyPx("height", itemHeight);
    span.getStyle().setProperty("overflow", "hidden");
    span.getStyle().setDisplay(Display.INLINE_BLOCK);
    span.getStyle().setVerticalAlign(VerticalAlign.TOP);
    span.getStyle().setProperty("zoom", "1");
    return span;
  }

  @Override
  protected Range computeRangeAndUpdateScrolling() {
    Range r = new Range();

    int width = getElement().getScrollWidth(), height = getOffsetHeight();
    int scrollTop = getElement().getScrollTop();
    int itemsPerLine = width / itemWidth;
    r.start = (scrollTop / itemHeight) * itemsPerLine;
    r.length = ((height / itemHeight) * itemsPerLine) + (2 * itemsPerLine);

    if (itemsPerLine != 0) {
      updateScrolling((r.start / itemsPerLine) * itemHeight,
          (getMaxItems() / itemsPerLine) * itemHeight);
    }
    return r;
  }
}
