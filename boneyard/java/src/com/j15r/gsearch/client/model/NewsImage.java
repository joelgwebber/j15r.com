package com.j15r.gsearch.client.model;

public class NewsImage extends SearchResultBase {

  protected NewsImage() {
  }

  public final native String getPublisher() /*-{
    return this.publisher;
  }-*/;

  public final native int getTbHeight() /*-{
    return parseInt(this.tbHeight);
  }-*/;

  public final native String getTbUrl() /*-{
    return this.tbUrl;
  }-*/;

  public final native int getTbWidth() /*-{
    return parseInt(this.tbWidth);
  }-*/;
}
