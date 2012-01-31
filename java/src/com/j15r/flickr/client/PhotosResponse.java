package com.j15r.flickr.client;

import com.google.gwt.core.client.JavaScriptObject;

public class PhotosResponse extends JavaScriptObject {

  protected PhotosResponse() {
  }

  public final native Photos getPhotos() /*-{
    return this.photos;
  }-*/;

  public final native String getStat() /*-{
    return this.stat;
  }-*/;
}
