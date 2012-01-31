package com.j15r.site.client;

import com.google.gwt.core.client.JavaScriptObject;

public class Comment extends JavaScriptObject {

  protected Comment() {
  }

  public final native String pageId() /*-{ return this.pageId; }-*/;
  public final native String html() /*-{ return this.html; }-*/;
  public final native String date() /*-{ return this.date; }-*/;
  public final native String name() /*-{ return this.name; }-*/;
  public final native String email() /*-{ return this.email; }-*/;
  public final native String site() /*-{ return this.site; }-*/;
}
