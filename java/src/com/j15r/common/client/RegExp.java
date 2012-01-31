package com.j15r.common.client;

import com.google.gwt.core.client.JavaScriptObject;

public class RegExp extends JavaScriptObject {

  public static native RegExp create(String expr) /*-{
    return new RegExp(expr);
  }-*/;

  public static native RegExp create(String expr, String flags) /*-{
    return new RegExp(expr, flags);
  }-*/;

  protected RegExp() {}

  public final native boolean test(String s) /*-{
    return this.test(s);
  }-*/;

  public final native MatchResult exec(String s) /*-{
    return this.exec(s);
  }-*/;
}
