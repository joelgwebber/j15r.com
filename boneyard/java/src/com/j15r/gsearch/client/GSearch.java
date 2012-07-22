package com.j15r.gsearch.client;

import static com.google.gwt.dom.client.Style.Unit.PX;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.topspin.ui.client.Widget;
import com.j15r.common.client.widgets.LayoutPanel;
import com.j15r.common.client.widgets.LayoutRoot;

/**
 * Ajax Search API Docs:
 * http://code.google.com/apis/ajaxsearch/documentation/reference.html
 */
public class GSearch implements EntryPoint {

  private SearchForm searchForm;
  private LayoutPanel layout;

  private SearchType<?> curSearchType;
  private WebSearchType webSearchType;
  private ImageSearchType imageSearchType;
  private VideoSearchType videoSearchType;
  private NewsSearchType newsSearchType;

  private void layoutSearchView(Widget view) {
    layout.getLayer(view).setLeftRight(4, PX, 4, PX);
    layout.getLayer(view).setTopBottom(60, PX, 4, PX);
  }

  public void onModuleLoad() {
    layout = new LayoutPanel(LayoutRoot.getContainer());

    searchForm = new SearchForm(layout.getContainer(), new SearchForm.Delegate() {
      public void onSearch(String query) {
        doSearch(query);
      }

      public void onImageTypeSelected() {
        showType(imageSearchType);
      }

      public void onNewsTypeSelected() {
        showType(newsSearchType);
      }

      public void onVideoTypeSelected() {
        showType(videoSearchType);
      }

      public void onWebTypeSelected() {
        showType(webSearchType);
      }
    });

    layout.getLayer(searchForm).setLeftRight(4, PX, 4, PX);
    layout.getLayer(searchForm).setTopHeight(4, PX, 60, PX);

    webSearchType = new WebSearchType(layout.getContainer());
    imageSearchType = new ImageSearchType(layout.getContainer());
    videoSearchType = new VideoSearchType(layout.getContainer());
    newsSearchType = new NewsSearchType(layout.getContainer());

    layout.fillWindow();
    layout.doLayout();
    showType(webSearchType);
  }

  private void doSearch(String query) {
    if (curSearchType != null) {
      if (query.trim().length() > 0) {
        curSearchType.setQuery(query);
      }
    }
  }

  private void showType(SearchType<?> type) {
    if (type == curSearchType) {
      return;
    }

    if (curSearchType != null) {
      layout.setLayerVisible((Widget) curSearchType.getView(), false);
    }

    curSearchType = type;
    curSearchType.ensureCreated();
    layout.setLayerVisible((Widget) curSearchType.getView(), true);
    layoutSearchView(curSearchType.getViewWidget());
    layout.doLayout();
    doSearch(searchForm.getSearchText());
  }
}
