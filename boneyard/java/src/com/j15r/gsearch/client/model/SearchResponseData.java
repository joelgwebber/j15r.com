/**
 * 
 */
package com.j15r.gsearch.client.model;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;

public class SearchResponseData<ResultType extends SearchResult> extends JavaScriptObject {
  protected SearchResponseData() {
  }

  public final native JsArray<ResultType> getResults() /*-{
    return this.results;
  }-*/;

  public final native SearchCursor getCursor() /*-{
    return this.cursor;
  }-*/;
}