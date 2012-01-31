package com.j15r.gsearch.client.model;

public class WebSearchResult extends SearchResult {

  protected WebSearchResult() {
  }

  public final native String getVisibleUrl() /*-{
    return this.visibleUrl;
  }-*/;

  public final native String getCacheUrl() /*-{
    return this.cacheUrl;
  }-*/;
}
