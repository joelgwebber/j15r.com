package com.j15r.gsearch.client.model;

import com.google.gwt.core.client.JsArray;

public class NewsSearchResult extends SearchResult {

  protected NewsSearchResult() {
  }

  public final native String getClusterUrl() /*-{
    return this.clusterUrl;
  }-*/;

  public final native String getPublisher() /*-{
    return this.publisher;
  }-*/;

  public final native String getLocation() /*-{
    return this.location;
  }-*/;

  public final native String getPublishedDate() /*-{
    return this.publishedDate;
  }-*/;

  public final native JsArray<NewsRelatedStory> getRelatedStories() /*-{
    return this.relatedStories;
  }-*/;

  public final native NewsImage getImage() /*-{
    return this.image;
  }-*/;
}
