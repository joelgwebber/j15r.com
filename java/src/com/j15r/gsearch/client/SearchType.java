package com.j15r.gsearch.client;

import com.google.gwt.topspin.ui.client.Widget;
import com.j15r.common.client.mvc.ListView;
import com.j15r.common.client.widgets.LayoutContainer;
import com.j15r.gsearch.client.model.SearchResult;

public abstract class SearchType<ResultType extends SearchResult> {

  private SearchController<ResultType> model;
  private ListView<ResultType> view;
  private final String apiType;
  private final LayoutContainer viewContainer;

  protected SearchType(LayoutContainer viewContainer, String apiType) {
    this.viewContainer = viewContainer;
    this.apiType = apiType;
  }

  public ListView<ResultType> getView() {
    return view;
  }

  public void setQuery(String query) {
    model.setQuery(query);
  }

  protected SearchController<ResultType> doCreateController() {
    return new SearchController<ResultType>(apiType);
  }

  protected abstract ListView<ResultType> doCreateView(LayoutContainer container);

  protected abstract Widget getViewWidget();

  public void ensureCreated() {
    if (model == null) {
      model = doCreateController();
      view = doCreateView(viewContainer);
      model.addView(view);
    }
  }
}
