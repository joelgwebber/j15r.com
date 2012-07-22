/**
 * 
 */
package com.j15r.gsearch.client.model;

import com.google.gwt.core.client.JavaScriptObject;

public class SearchResponse<ResultType extends SearchResult> extends JavaScriptObject {
  protected SearchResponse() {
  }

  public final native SearchResponseData<ResultType> getResponseData() /*-{
    return this.responseData;
  }-*/;

  public final native String /* TODO */getResponseDetails() /*-{
    return this.responseDetails;
  }-*/;

  public final native int getResponseCode() /*-{
    return this.responseCode;
  }-*/;
}