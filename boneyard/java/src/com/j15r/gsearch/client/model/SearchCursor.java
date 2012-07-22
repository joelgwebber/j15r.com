/**
 * 
 */
package com.j15r.gsearch.client.model;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;

public class SearchCursor extends JavaScriptObject {
  protected SearchCursor() {
  }

  public final native JsArray<SearchCursorPage> getPages() /*-{
    return this.pages;
  }-*/;

  public final native int getEstimatedResultCount() /*-{
    return this.estimatedResultCount;
  }-*/;

  public final native int getCurrentPageIndex() /*-{
    return this.currentPageIndex;
  }-*/;

  public final native String getMoreResultsUrl() /*-{
    return this.moreResultsUrl;
  }-*/;
}