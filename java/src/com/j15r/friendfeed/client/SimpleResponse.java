package com.j15r.friendfeed.client;

import com.google.gwt.core.client.JavaScriptObject;

public class SimpleResponse extends JavaScriptObject {

  protected SimpleResponse() {
  }

  public final native boolean success() /*-{
    return this.success || false;
  }-*/;

  public final native String errorCode() /*-{
    return this.errorCode;
  }-*/;
}
