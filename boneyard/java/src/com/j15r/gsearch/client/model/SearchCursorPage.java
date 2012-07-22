/**
 * 
 */
package com.j15r.gsearch.client.model;

import com.google.gwt.core.client.JavaScriptObject;

public class SearchCursorPage extends JavaScriptObject {
  protected SearchCursorPage() {
  }

  public final native int getStart() /*-{
    return this.start;
  }-*/;

  public final native int getLabel() /*-{
    return this.label;
  }-*/;
}