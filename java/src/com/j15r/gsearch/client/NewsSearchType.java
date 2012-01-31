package com.j15r.gsearch.client;

import com.google.gwt.topspin.ui.client.Container;
import com.google.gwt.topspin.ui.client.Widget;
import com.j15r.common.client.mvc.ListView;
import com.j15r.common.client.widgets.LayoutContainer;
import com.j15r.common.client.widgets.ListScroller;
import com.j15r.gsearch.client.model.NewsSearchResult;

public class NewsSearchType extends SearchType<NewsSearchResult> {

  public static class NewsSearchView extends ListScroller<NewsSearchResult>
      implements ListView<NewsSearchResult> {
    public NewsSearchView(LayoutContainer container) {
      super(container, 80);
    }

    @Override
    protected ItemView<NewsSearchResult> doCreateView(Container container) {
      return new NewsResultView(container);
    }
  }

  private NewsSearchView viewWidget;

  protected NewsSearchType(LayoutContainer viewContainer) {
    super(viewContainer, "news");
  }

  @Override
  protected ListView<NewsSearchResult> doCreateView(LayoutContainer container) {
    viewWidget = new NewsSearchView(container);
    return viewWidget;
  }

  @Override
  protected Widget getViewWidget() {
    return viewWidget;
  }
}
