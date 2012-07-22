package com.j15r.common.client;

import com.google.gwt.core.client.JavaScriptObject;

public class MatchResult extends JavaScriptObject {

  protected MatchResult() {
  }

  public final native int index() /*-{
    return this.index;
  }-*/;

  public final native String input() /*-{
    return this.input;
  }-*/;

  public final native String group(int index) /*-{
    return this[index];
  }-*/;
}
