package com.j15r.gsearch.client.model;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;

public class SearchSuggestResponse extends JavaScriptObject {

  protected SearchSuggestResponse() {
  }

  public final native String getQuery() /*-{
    return this[0];
  }-*/;

  public final native JsArray<SearchSuggestion> getSuggestions() /*-{
    return this[1];
  }-*/;
}
