package com.j15r.gsearch.client.model;

public class SearchResult extends SearchResultBase {

  protected SearchResult() {
  }

  public final native String getContent() /*-{
    return this.content;
  }-*/;

  public final native String getGsearchResultClass() /*-{
    return this.GsearchResultClass;
  }-*/;

  public final native String getUnescapedUrl() /*-{
    return this.unescapedUrl;
  }-*/;
}
