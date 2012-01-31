package com.j15r.site.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.AnchorElement;
import com.google.gwt.dom.client.Element;
import com.google.gwt.topspin.ui.client.Container;
import com.google.gwt.topspin.ui.client.Widget;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;

public class IndexItemView extends Widget {

  interface Binder extends UiBinder<Element, IndexItemView> { }
  private static Binder binder = GWT.create(Binder.class);

  @UiField AnchorElement anchor;

  public IndexItemView(Container container) {
    create(binder.createAndBindUi(this), container);
  }

  public void setPageRef(PageRef ref) {
    anchor.setInnerText(ref.title());
    anchor.setHref("#" + ref.id());
  }
}
