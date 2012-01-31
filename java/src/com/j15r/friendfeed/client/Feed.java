package com.j15r.friendfeed.client;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;

/**
 * http://friendfeed.com/api
 */
public class Feed extends JavaScriptObject {

  public static class Comment extends JavaScriptObject {
    protected Comment() {
    }

    public native final String getBody() /*-{ return this.body; }-*/;

    public native final String getDate() /*-{ return this.date; }-*/;

    public native final String getId() /*-{ return this.id; }-*/;

    /**
     * TODO
     * 
     * @return the user that commented (a <code>null</code> return value refers
     *         to the current user)
     */
    public native final User getUser() /*-{ return this.user; }-*/;
  }

  public static class Content extends JavaScriptObject {
    protected Content() {
    }

    public native final int getHeight() /*-{ return this.height; }-*/;

    public native final String getType() /*-{ return this.type; }-*/;

    public native final String getUrl() /*-{ return this.url; }-*/;

    public native final int getWidth() /*-{ return this.width; }-*/;
  }

  public static class Enclosure extends JavaScriptObject {
    protected Enclosure() {
    }

    public native final int getLength() /*-{ return this.length; }-*/;

    public native final String getType() /*-{ return this.type; }-*/;

    public native final String getUrl() /*-{ return this.url; }-*/;
  }

  public static class Entry extends JavaScriptObject {
    protected Entry() {
    }

    public native final JsArray<Comment> getComments() /*-{ return this.comments; }-*/;

    public native final String getId() /*-{ return this.id; }-*/;

    public native final JsArray<Like> getLikes() /*-{ return this.likes; }-*/;

    public native final String getLink() /*-{ return this.link; }-*/;

    public native final JsArray<Media> getMedia() /*-{ return this.media; }-*/;

    public native final String getPublished() /*-{ return this.published; }-*/;

    public native final Service getService() /*-{ return this.service; }-*/;

    public native final String getTitle() /*-{ return this.title; }-*/;

    public native final String getUpdated() /*-{ return this.updated; }-*/;

    public native final User getUser() /*-{ return this.user; }-*/;

    public native final Via getVia() /*-{ return this.via; }-*/;

    public native final boolean isAnonymous() /*-{ return this.anonymous || false; }-*/;

    public native final boolean isHidden() /*-{ return this.hidden || false; }-*/;
  }

  public static class Like extends JavaScriptObject {
    public static native Like create(String date, User user) /*-{
      return {
        "date": date,
        "user": user
      };
    }-*/;

    protected Like() {
    }

    public native final String getDate() /*-{ return this.date; }-*/;

    public native final User getUser() /*-{ return this.user; }-*/;
  }

  public static class Media extends JavaScriptObject {
    protected Media() {
    }

    public native final JsArray<Content> getContent() /*-{ return this.content; }-*/;

    public native final JsArray<Enclosure> getEnclosures() /*-{ return this.enclosures; }-*/;

    public native final String getLink() /*-{ return this.link; }-*/;

    public native final String getPlayer() /*-{ return this.player; }-*/;

    public native final JsArray<Thumbnail> getThumbnails() /*-{ return this.thumbnails; }-*/;

    public native final String getTitle() /*-{ return this.title; }-*/;
  }

  public static class Room extends JavaScriptObject {
    protected Room() {
    }

    public native final String getId() /*-{ return this.id; }-*/;

    public native final String getName() /*-{ return this.name; }-*/;

    public native final String getNickname() /*-{ return this.nickname; }-*/;

    public native final String getUrl() /*-{ return this.url; }-*/;
  }

  public static class Service extends JavaScriptObject {
    protected Service() {
    }

    public native final String getEntryType() /*-{ return this.entryType; }-*/;

    public native final String getIconUrl() /*-{ return this.iconUrl; }-*/;

    public native final String getId() /*-{ return this.id; }-*/;

    public native final String getName() /*-{ return this.name; }-*/;

    public native final String getProfileUrl() /*-{ return this.profileUrl; }-*/;
  }

  public static class Thumbnail extends JavaScriptObject {
    protected Thumbnail() {
    }

    public native final int getHeight() /*-{ return this.height; }-*/;

    public native final String getUrl() /*-{ return this.url; }-*/;

    public native final int getWidth() /*-{ return this.width; }-*/;
  }

  public static class User extends JavaScriptObject {
    public static native User create(String id, String name, String nickname, String profileUrl) /*-{
      return {
        id: id,
        name: name,
        nickname: nickname,
        profileUrl: profileUrl
      };
    }-*/;

    protected User() {
    }

    public native final String getId() /*-{ return this.id; }-*/;

    public native final String getName() /*-{ return this.name; }-*/;

    public native final String getNickname() /*-{ return this.nickname; }-*/;

    public native final String getProfileUrl() /*-{ return this.profileUrl; }-*/;
  }

  public static class Via extends JavaScriptObject {
    protected Via() {
    }

    public native final String getName() /*-{ return this.name; }-*/;

    public native final String getUrl() /*-{ return this.url; }-*/;
  }

  protected Feed() {
  }

  public native final JsArray<Entry> getEntries() /*-{ return this.entries; }-*/;
}
