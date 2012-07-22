package com.j15r.site.client;

import com.google.gwt.core.client.JavaScriptObject;

public class PageRef extends JavaScriptObject {

  protected PageRef() { }

  public final native String title() /*-{ return this.title; }-*/;
  public final native String id() /*-{ return this.id; }-*/;
}
