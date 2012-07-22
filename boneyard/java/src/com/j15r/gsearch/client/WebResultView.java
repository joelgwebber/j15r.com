package com.j15r.gsearch.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.AnchorElement;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Element;
import com.google.gwt.topspin.ui.client.Container;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.j15r.common.client.widgets.Scroller.ItemView;
import com.j15r.gsearch.client.model.WebSearchResult;

public class WebResultView extends ResultView<WebSearchResult> implements
    ItemView<WebSearchResult> {

  interface Binder extends UiBinder<Element, WebResultView> { }
  private static Binder binder = GWT.create(Binder.class);

  @UiField AnchorElement anchor;
  @UiField DivElement content;
  @UiField Element visibleUrl;
  @UiField AnchorElement cached;

  public WebResultView(Container container) {
    create(binder.createAndBindUi(this), container);
  }

  public void setData(WebSearchResult data) {
    super.setData(data);

    anchor.setHref(data.getUnescapedUrl());
    anchor.setInnerHTML(data.getTitle());
    content.setInnerHTML(data.getContent());
    visibleUrl.setInnerText(data.getVisibleUrl());
    cached.setHref(data.getCacheUrl());
  }
}
