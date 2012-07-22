package com.j15r.gsearch.client.model;

public class VideoSearchResult extends SearchResult {

  protected VideoSearchResult() {
  }

  public final native String getAuthor() /*-{
    return this.author;
  }-*/;

  public final native int getDuration() /*-{
    return parseInt(this.duration);
  }-*/;

  public final native String getPlayUrl() /*-{
    return this.playUrl;
  }-*/;

  public final native String getPublished() /*-{
    return this.published;
  }-*/;

  public final native String getPublisher() /*-{
    return this.publisher;
  }-*/;

  public final native int getRating() /*-{
    return parseInt(this.rating);
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

  public final native int getViewCount() /*-{
    return parseInt(this.viewCount);
  }-*/;
}
