package com.j15r.gsearch.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.AnchorElement;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.topspin.ui.client.Container;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.j15r.common.client.widgets.GridScroller;
import com.j15r.gsearch.client.model.VideoSearchResult;

public class VideoResultView extends ResultView<VideoSearchResult> implements
    GridScroller.ItemView<VideoSearchResult> {

  interface Binder extends UiBinder<Element, VideoResultView> { }
  private static Binder binder = GWT.create(Binder.class);

  @UiField AnchorElement anchor;
  @UiField ImageElement thumb;
  @UiField Element content;
  @UiField Element publisher;

  public VideoResultView(Container container) {
    create(binder.createAndBindUi(this), container);
  }

  public void setData(VideoSearchResult data) {
    super.setData(data);

    anchor.setHref(data.getUrl());
    thumb.setSrc(data.getTbUrl());
    content.setInnerHTML(data.getContent());
    publisher.setInnerHTML(data.getPublisher());
  }

  @Override
  public void setLoading() {
    super.setLoading();
    thumb.setSrc("clear.cache.gif");
  }
}
