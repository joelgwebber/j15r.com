package com.j15r.flickr.client;

import com.google.gwt.core.client.JavaScriptObject;

public class Photo extends JavaScriptObject {

  protected Photo() {
  }

  public final native String getId() /*-{
    return this.id;
  }-*/;

  public final native String getOwner() /*-{
    return this.owner;
  }-*/;

  public final native String getSecret() /*-{
    return this.secret;
  }-*/;

  public final native String getServer() /*-{
    return this.server;
  }-*/;

  public final native int getFarm() /*-{
    return this.farm || 0;
  }-*/;

  public final native String getTitle() /*-{
    return this.title;
  }-*/;

  public final native int getIspublic() /*-{
    return this.ispublic || 0;
  }-*/;

  public final native int getIsfriend() /*-{
    return this.isfriend || 0;
  }-*/;

  public final native int getIsfamily() /*-{
    return this.isfamily || 0;
  }-*/;

  public final String getThumbUrl() {
    return "http://farm" + getFarm() + ".static.flickr.com/" + getServer()
        + "/" + getId() + "_" + getSecret() + "_t.jpg";
  }

  public final String getSmallUrl() {
    return "http://farm" + getFarm() + ".static.flickr.com/" + getServer()
        + "/" + getId() + "_" + getSecret() + "_m.jpg";
  }

  public final String getFlickrUrl() {
    return "http://www.flickr.com/photos/" + getOwner() + "/" + getId();
  }

  public final String getPhotoUrl() {
    return "http://farm" + getFarm() + ".static.flickr.com/" + getServer()
        + "/" + getId() + "_" + getSecret() + ".jpg";
  }
}
