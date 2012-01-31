package com.j15r.gsearch.client;

import com.google.gwt.core.client.JsArray;
import com.google.gwt.topspin.ui.client.Container;
import com.google.gwt.topspin.ui.client.Widget;
import com.j15r.common.client.mvc.ListView;
import com.j15r.common.client.widgets.GridScroller;
import com.j15r.common.client.widgets.LayoutContainer;
import com.j15r.gsearch.client.model.ImageSearchResult;

public class ImageSearchType extends SearchType<ImageSearchResult> {

  static class ImageSearchController extends
      SearchController<ImageSearchResult> {
    public ImageSearchController() {
      super("images");
    }

    @Override
    protected void processResults(JsArray<ImageSearchResult> results) {
      for (int i = 0; i < results.length(); ++i) {
        results.get(i).loadThumbBackground();
      }
      ImageSearchResult.flushLoadingImages();
    }
  }

  static class ImageSearchView extends GridScroller<ImageSearchResult>
      implements ListView<ImageSearchResult> {
    public ImageSearchView(LayoutContainer container) {
      super(container, 256, 224);
    }

    @Override
    protected ItemView<ImageSearchResult> doCreateView(Container container) {
      return new ImageResultView(container);
    }
  }

  private ImageSearchView viewWidget;

  protected ImageSearchType(LayoutContainer viewContainer) {
    super(viewContainer, "images");
  }

  protected SearchController<ImageSearchResult> doCreateController() {
    return new ImageSearchController();
  }

  @Override
  protected ListView<ImageSearchResult> doCreateView(LayoutContainer container) {
    viewWidget = new ImageSearchView(container);
    return viewWidget;
  }

  @Override
  protected Widget getViewWidget() {
    return viewWidget;
  }
}
