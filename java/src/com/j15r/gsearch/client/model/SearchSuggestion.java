package com.j15r.gsearch.client.model;

import com.google.gwt.core.client.JavaScriptObject;

public class SearchSuggestion extends JavaScriptObject {

  protected SearchSuggestion() {
  }

  public final native String getSuggestion() /*-{
    return this[0];
  }-*/;

  public final native String getResultCountString() /*-{
    return this[1];
  }-*/;
}
