package com.j15r.gsearch.client;

import com.google.gwt.topspin.ui.client.Container;
import com.google.gwt.topspin.ui.client.Widget;
import com.j15r.common.client.mvc.ListView;
import com.j15r.common.client.widgets.LayoutContainer;
import com.j15r.common.client.widgets.ListScroller;
import com.j15r.gsearch.client.model.WebSearchResult;

public class WebSearchType extends SearchType<WebSearchResult> {

  public static class WebSearchView extends ListScroller<WebSearchResult>
      implements ListView<WebSearchResult> {

    public WebSearchView(LayoutContainer container) {
      super(container, 80);
    }

    @Override
    protected ItemView<WebSearchResult> doCreateView(Container container) {
      return new WebResultView(container);
    }
  }

  private WebSearchView viewWidget;

  protected WebSearchType(LayoutContainer viewContainer) {
    super(viewContainer, "web");
  }

  @Override
  protected ListView<WebSearchResult> doCreateView(LayoutContainer container) {
    viewWidget = new WebSearchView(container);
    return viewWidget;
  }

  @Override
  protected Widget getViewWidget() {
    return viewWidget;
  }
}
