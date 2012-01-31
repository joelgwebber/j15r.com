package com.j15r.friendfeed.client;

import com.j15r.friendfeed.client.Feed.Service;

import java.util.HashMap;
import java.util.Map;

public class Services {

  public static class ServiceInfo {
    final public String verb;
    final public boolean isLink;

    public ServiceInfo(String verb, boolean isLink) {
      this.verb = verb;
      this.isLink = isLink;
    }
  }

  private static final Map<String, ServiceInfo> serviceStrings = new HashMap<String, ServiceInfo>();

  static {
    serviceStrings.put("amazon", new ServiceInfo("added", true));
    serviceStrings.put("backtype", new ServiceInfo("commented on", true));
    serviceStrings.put("brightkite", new ServiceInfo("checked in", true));
    serviceStrings.put("delicious", new ServiceInfo("bookmarked", true));
    serviceStrings.put("digg_comment", new ServiceInfo("commented on", true));
    serviceStrings.put("digg_digg", new ServiceInfo("dugg", true));
    serviceStrings.put("diigo", new ServiceInfo("bookmarked", true));
    serviceStrings.put("disqus", new ServiceInfo("commented", true));
    serviceStrings.put("facebook_note", new ServiceInfo("posted", true));
    serviceStrings.put("facebook_post", new ServiceInfo("shared", true));
    serviceStrings.put("facebook_status", new ServiceInfo("posted", false));
    serviceStrings.put("flickr_favorite", new ServiceInfo("favorited", true));
    serviceStrings.put("flickr_publish", new ServiceInfo("published", true));
    serviceStrings.put("goodreads", new ServiceInfo("read", true));
    serviceStrings.put("googletalk", new ServiceInfo("had a new status message", false));
    serviceStrings.put("googlereader", new ServiceInfo("shared", true));
    serviceStrings.put("googleshared", new ServiceInfo("shared", true));
    serviceStrings.put("internal_link", new ServiceInfo("posted a link", true));
    serviceStrings.put("internal_message", new ServiceInfo("posted a message", false));
    serviceStrings.put("intensedebate", new ServiceInfo("commented", true));
    serviceStrings.put("joost", new ServiceInfo("watched", false));
    serviceStrings.put("lastfm", new ServiceInfo("loved", false));
    serviceStrings.put("librarything", new ServiceInfo("added", true));
    serviceStrings.put("linkedin_leftjob", new ServiceInfo("left their job", false));
    serviceStrings.put("linkedin_newjob", new ServiceInfo("got a new job", false));
    serviceStrings.put("magnolia", new ServiceInfo("bookmarked", true));
    serviceStrings.put("misterwong", new ServiceInfo("bookmarked", true));
    serviceStrings.put("mixx", new ServiceInfo("submitted", true));
    serviceStrings.put("netflix", new ServiceInfo("added", true));
    serviceStrings.put("netvibes", new ServiceInfo("starred", true));
    serviceStrings.put("pandora_artist", new ServiceInfo("bookmarked the artist", true));
    serviceStrings.put("pandora_song", new ServiceInfo("bookmarked the song", true));
    serviceStrings.put("picasa", new ServiceInfo("published", true));
    serviceStrings.put("polyvore", new ServiceInfo("created", true));
    serviceStrings.put("reddit_comment", new ServiceInfo("commented on", true));
    serviceStrings.put("reddit_like", new ServiceInfo("liked", true));
    serviceStrings.put("smugmug", new ServiceInfo("published", true));
    serviceStrings.put("stumbleupon", new ServiceInfo("stumbled upon", true));
    serviceStrings.put("tipjoy", new ServiceInfo("tipped", true));
    serviceStrings.put("twitter", new ServiceInfo("posted a message", false));
    serviceStrings.put("upcoming", new ServiceInfo("added", true));
    serviceStrings.put("vimeo_like", new ServiceInfo("liked", true));
    serviceStrings.put("vimeo_publish", new ServiceInfo("published", true));
    serviceStrings.put("wakoopa_review", new ServiceInfo("reviewed", true));
    serviceStrings.put("wakoopa_use", new ServiceInfo("started using", true));
    serviceStrings.put("yelp", new ServiceInfo("reviewed", true));
    serviceStrings.put("youtube_favorite", new ServiceInfo("favorited", true));
    serviceStrings.put("youtube_publish", new ServiceInfo("published", true));
    serviceStrings.put("zoomr_favorite", new ServiceInfo("favorited", true));
    serviceStrings.put("zoomr_publish", new ServiceInfo("published", true));
  }

  public static ServiceInfo getServiceInfo(Service service) {
    String key = service.getId();
    if (service.getEntryType() != null) {
      key += "_" + service.getEntryType();
    }

    ServiceInfo info = serviceStrings.get(key);
    if (info == null) {
      info = new ServiceInfo("posted", true);
    }

    return info;
  }
}
