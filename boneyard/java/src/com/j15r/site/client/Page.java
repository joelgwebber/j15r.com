package com.j15r.site.client;

import com.google.gwt.core.client.JavaScriptObject;

public class Page extends JavaScriptObject {

  protected Page() {
  }

  public final native String id() /*-{
    return this.id;
  }-*/;

  public final native String title() /*-{
    return this.title;
  }-*/;

  public final native String type() /*-{
    return this.type;
  }-*/;
}
