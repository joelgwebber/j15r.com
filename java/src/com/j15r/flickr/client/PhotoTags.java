package com.j15r.flickr.client;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;

public class PhotoTags extends JavaScriptObject {

  protected PhotoTags() {
  }

  public final native String getId() /*-{
    return this.id;
  }-*/;

  public final native JsArray<Tag> getTag() /*-{
    return this.tags.tag;
  }-*/;
}
