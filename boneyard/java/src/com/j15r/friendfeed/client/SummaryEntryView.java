package com.j15r.friendfeed.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.topspin.ui.client.Container;
import com.google.gwt.topspin.ui.client.Widget;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;

import com.j15r.common.client.widgets.PagedListView;
import com.j15r.friendfeed.client.Feed.Entry;

public class SummaryEntryView extends Widget implements EntryView,
    PagedListView.ItemView<Entry> {

  interface Binder extends UiBinder<Element, SummaryEntryView> { }
  private static final Binder binder = GWT.create(Binder.class);

  private Entry data;

  @UiField ImageElement serviceIcon;
  @UiField SpanElement userSpan, serviceSpan;
  @UiField DivElement titleDiv;

  public SummaryEntryView(Container container) {
    create(binder.createAndBindUi(this), container);
  }

  public void setDelegate(Delegate delegate) {
  }

  public Entry getData() {
    return data;
  }

  public void setData(Entry data) {
    this.data = data;
    if (data == null) {
      serviceIcon.setSrc("clear.cache.gif");
      userSpan.setInnerText("");
      serviceSpan.setInnerText("");
      titleDiv.setInnerText("");
      return;
    }

    serviceIcon.setSrc(data.getService().getIconUrl());
    userSpan.setInnerText(data.getUser().getNickname());
    serviceSpan.setInnerText(data.getService().getName());
    titleDiv.setInnerText(data.getTitle());
  }
}
