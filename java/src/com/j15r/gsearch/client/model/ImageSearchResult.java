package com.j15r.gsearch.client.model;

public class ImageSearchResult extends SearchResult {

  public static final int THUMB_WIDTH = 160;
  public static final int THUMB_HEIGHT = 160;

  private static final int STRIP_SIZE = 4;

  private static class Strip {
    String url = "strip?w=" + THUMB_WIDTH + "&h=" + THUMB_HEIGHT;
    StripImage[] images = new StripImage[STRIP_SIZE];
    int imageCount;
  }

  private static class StripImage {
    int pos;
    ImageSearchResult result;
  }

  private static Strip curStrip;

  private static void loadImage(ImageSearchResult result, String url) {
    if (curStrip == null) {
      curStrip = new Strip();
    }

    StripImage image = curStrip.images[curStrip.imageCount] = new StripImage();
    image.result = result;
    image.pos = THUMB_HEIGHT * curStrip.imageCount;
    curStrip.url += "&u=" + url;
    ++curStrip.imageCount;

    if (curStrip.imageCount == STRIP_SIZE) {
      flushLoadingImages();
    }
  }

  public static void flushLoadingImages() {
    if (curStrip == null) {
      return;
    }

    for (int i = 0; i < STRIP_SIZE; ++i) {
      StripImage curImage = curStrip.images[i];
      if (curImage != null) {
        curImage.result.setThumbBackgroundStyle("url(" + curStrip.url + ") no-repeat 0 -"
            + curImage.pos + "px");
      }
    }
    curStrip = null;
  }

  protected ImageSearchResult() {
  }

  public final native int getWidth() /*-{
    return parseInt(this.width);
  }-*/;

  public final native int getHeight() /*-{
    return parseInt(this.height);
  }-*/;

  public final native String getImageId() /*-{
    return this.imageId;
  }-*/;

  public final native int getTbWidth() /*-{
    return parseInt(this.tbWidth);
  }-*/;

  public final native int getTbHeight() /*-{
    return parseInt(this.tbHeight);
  }-*/;

  public final native String getOriginalContextUrl() /*-{
    return this.originalContextUrl;
  }-*/;

  public final native String getTbUrl() /*-{
    return this.tbUrl;
  }-*/;

  public final native String getVisibleUrl() /*-{
    return this.visibleUrl;
  }-*/;

  public final native String getThumbBackgroundStyle() /*-{
    return this.__bgstyle;
  }-*/;

  public final void loadThumbBackground() {
    loadImage(this, getTbUrl());
  }

  private final native void setThumbBackgroundStyle(String style) /*-{
    this.__bgstyle = style;
  }-*/;
}
