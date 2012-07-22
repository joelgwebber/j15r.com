package com.j15r.gsearch.client.model;

import com.google.gwt.core.client.JavaScriptObject;

public class NewsRelatedStory extends JavaScriptObject {

  protected NewsRelatedStory() {
  }

  public final native String getLocation() /*-{
    return this.location;
  }-*/;

  public final native String getPublishedDate() /*-{
    return this.publishedDate;
  }-*/;

  public final native String getPublisher() /*-{
    return this.publisher;
  }-*/;

  public final native String getUnescapedUrl() /*-{
    return this.unescapedUrl;
  }-*/;
}
