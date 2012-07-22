package com.j15r.flickr.client;

import com.google.gwt.core.client.JavaScriptObject;

public class PhotoTagsResponse extends JavaScriptObject {

  protected PhotoTagsResponse() {
  }

  public final native PhotoTags getPhoto() /*-{
    return this.photo;
  }-*/;

  public final native int getStat() /*-{
    return this.stat;
  }-*/;
}
