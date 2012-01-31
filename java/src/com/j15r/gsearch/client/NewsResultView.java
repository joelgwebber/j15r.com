package com.j15r.gsearch.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.AnchorElement;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.topspin.ui.client.Container;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.j15r.common.client.widgets.Scroller.ItemView;
import com.j15r.gsearch.client.model.NewsSearchResult;

public class NewsResultView extends ResultView<NewsSearchResult> implements
    ItemView<NewsSearchResult> {

  interface Binder extends UiBinder<Element, NewsResultView> { }
  private static Binder binder = GWT.create(Binder.class);

  @UiField AnchorElement anchor;
  @UiField DivElement content;
  @UiField Element publisher;
  @UiField SpanElement date;

  public NewsResultView(Container container) {
    create(binder.createAndBindUi(this), container);
  }

  public void setData(NewsSearchResult data) {
    super.setData(data);

    anchor.setHref(data.getUnescapedUrl());
    anchor.setInnerHTML(data.getTitle());
    content.setInnerHTML(data.getContent());
    publisher.setInnerText(data.getPublisher());
    date.setInnerText(data.getPublishedDate());
  }
}
