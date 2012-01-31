package com.j15r.gsearch.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.AnchorElement;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.topspin.ui.client.Container;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.j15r.common.client.widgets.GridScroller;
import com.j15r.gsearch.client.model.ImageSearchResult;

public class ImageResultView extends ResultView<ImageSearchResult> implements
    GridScroller.ItemView<ImageSearchResult> {

  interface Binder extends UiBinder<Element, ImageResultView> { }
  private static Binder binder = GWT.create(Binder.class);

  @UiField AnchorElement anchor;
  @UiField ImageElement thumb;
  @UiField DivElement content;
  @UiField DivElement size;
  @UiField DivElement domain;

  public ImageResultView(Container container) {
    create(binder.createAndBindUi(this), container);
  }

  public void setData(ImageSearchResult data) {
    super.setData(data);

    thumb.setSrc(data.getTbUrl());
    thumb.setWidth(ImageSearchResult.THUMB_WIDTH);
    thumb.setHeight(ImageSearchResult.THUMB_HEIGHT);

    anchor.setHref(data.getOriginalContextUrl());
    content.setInnerHTML(data.getContent());
    size.setInnerText(data.getWidth() + " x " + data.getHeight());
    domain.setInnerText(data.getVisibleUrl());
  }

  @Override
  public void setLoading() {
    super.setLoading();
    thumb.setSrc("clear.cache.gif");
  }
}
