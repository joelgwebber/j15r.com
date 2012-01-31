package com.j15r.gsearch.client.model;

import com.google.gwt.core.client.JavaScriptObject;

public class SearchResultBase extends JavaScriptObject {

  protected SearchResultBase() {
  }

  public final native String getTitle() /*-{
    return this.title;
  }-*/;

  public final native String getTitleNoFormatting() /*-{
    return this.titleNoFormatting;
  }-*/;

  public final native String getUrl() /*-{
    return this.url;
  }-*/;
}
