package com.j15r.flickr.client;

import com.google.gwt.core.client.JavaScriptObject;

public class Tag extends JavaScriptObject {

  protected Tag() {
  }

  public final native String getId() /*-{
    return this.id;
  }-*/;

  public final native String getAuthor() /*-{
    return this.author;
  }-*/;

  public final native String getAuthorname() /*-{
    return this.authorname;
  }-*/;

  public final native String getRaw() /*-{
    return this.raw;
  }-*/;

  public final native String getCooked() /*-{
    return this._content;
  }-*/;
}
