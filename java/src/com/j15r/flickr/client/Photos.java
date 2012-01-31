package com.j15r.flickr.client;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;

public class Photos extends JavaScriptObject {

  protected Photos() {
  }

  public final native int getPage() /*-{
    return this.page || 0;
  }-*/;

  public final native int getPages() /*-{
    return this.pages || 0;
  }-*/;

  public final native int getPerpage() /*-{
    return this.perpage || 0;
  }-*/;

  public final native int getTotal() /*-{
    return parseInt(this.total);
  }-*/;

  public final native JsArray<Photo> getPhotoArray() /*-{
    return this.photo;
  }-*/;
}
