package com.j15r.gsearch.client;

import com.google.gwt.topspin.ui.client.Container;
import com.google.gwt.topspin.ui.client.Widget;
import com.j15r.common.client.mvc.ListView;
import com.j15r.common.client.widgets.GridScroller;
import com.j15r.common.client.widgets.LayoutContainer;
import com.j15r.gsearch.client.model.VideoSearchResult;

public class VideoSearchType extends SearchType<VideoSearchResult> {

  public static class VideoSearchView extends GridScroller<VideoSearchResult>
      implements ListView<VideoSearchResult> {
    public VideoSearchView(LayoutContainer container) {
      super(container, 256, 192);
    }

    @Override
    protected com.j15r.common.client.widgets.Scroller.ItemView<VideoSearchResult> doCreateView(
        Container container) {
      return new VideoResultView(container);
    }
  }

  private VideoSearchView viewWidget;

  protected VideoSearchType(LayoutContainer viewContainer) {
    super(viewContainer, "video");
  }

  @Override
  protected ListView<VideoSearchResult> doCreateView(LayoutContainer container) {
    viewWidget = new VideoSearchView(container);
    return viewWidget;
  }

  @Override
  protected Widget getViewWidget() {
    return viewWidget;
  }
}
