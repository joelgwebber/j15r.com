package com.j15r.site.client;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.topspin.ui.client.Container;
import com.google.gwt.topspin.ui.client.Widget;

public abstract class PageView extends Widget {

  @Override
  protected void create(Element element, Container container) {
    super.create(element, container);
    setStyleName("page");
    getElement().getStyle().setOverflow(Overflow.AUTO);
  }

  public abstract void setPage(Page page);
}
