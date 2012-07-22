package com.j15r.gsearch.client;

import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Style.Visibility;
import com.google.gwt.topspin.ui.client.Widget;
import com.google.gwt.uibinder.client.UiField;
import com.j15r.gsearch.client.model.SearchResult;

public abstract class ResultView<T extends SearchResult> extends Widget {

  private T data;

  @UiField DivElement loading;
  @UiField DivElement display;

  public void setData(T data) {
    this.data = data;
    loading.getStyle().setVisibility(Visibility.HIDDEN);
    display.getStyle().setVisibility(Visibility.VISIBLE);
  }

  public void setLoading() {
    display.getStyle().setVisibility(Visibility.HIDDEN);
    loading.getStyle().setVisibility(Visibility.VISIBLE);
  }

  public void setSelected(boolean selected) {
  }

  public T getData() {
    return data;
  }
}
