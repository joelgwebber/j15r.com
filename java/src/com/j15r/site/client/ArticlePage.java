package com.j15r.site.client;

public class ArticlePage extends Page {

  protected ArticlePage() {
  }

  public final native double date() /*-{
    return this.date;
  }-*/;

  public final native String html() /*-{
    return this.html;
  }-*/;
}
