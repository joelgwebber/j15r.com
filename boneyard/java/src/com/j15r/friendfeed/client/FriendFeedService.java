package com.j15r.friendfeed.client;

import com.google.gwt.http.client.URL;
import com.google.gwt.jsonp.client.JsonpRequestBuilder;
import com.google.gwt.user.client.rpc.AsyncCallback;

import com.j15r.friendfeed.client.Feed.Comment;

/**
 * share response: Feed
 * 
 * comment response: {"body":"...", "id":"[comment id]"}
 * 
 * like, like/delete response: entry/hide response: {"success":true}
 * {"errorCode":"..."}
 * 
 * comment/delete response:
 * {"body":null,"id":"17f6c3d1-4d30-425f-970e-42b4741947aa"}
 * 
 * share response: Feed (just with a single new entry)
 */
public class FriendFeedService {

  private static final String API_URL = "http://friendfeed.com/api";
  private static final String FEED_URL = API_URL + "/feed";
  private static final String USER_FEED_URL = FEED_URL + "/user";
  private static final String HOME_FEED_URL = FEED_URL + "/home";
  private static final String SEARCH_FEED_URL = FEED_URL + "/search";

  private static final String COMMENTS_PATH = "/comments";
  private static final String LIKES_PATH = "/likes";
  private static final String DISCUSSION_PATH = "/discussion";

  private static final String SHARE_URL = API_URL + "/share";
  private static final String COMMENT_URL = API_URL + "/comment";
  private static final String COMMENT_DELETE_URL = API_URL + "/comment/delete";
  private static final String LIKE_URL = API_URL + "/like";
  private static final String LIKE_DELETE_URL = API_URL + "/like/delete";

  private static final String FORMAT_PARAM = "format=json";

  private final JsonpRequestBuilder requestBuilder = new JsonpRequestBuilder();
  private final ProxyJsonRequester proxyRequester = new ProxyJsonRequester();
  private final String user;
  private final String token;

  public FriendFeedService(String user, String token) {
    this.user = user;
    this.token = token;
  }

  public void comment(String entryId, String body, String commentIdToEdit,
      AsyncCallback<Comment> callback) {
    String url = COMMENT_URL + "&entry=" + entryId + "&body="
        + URL.encodeComponent(body);
    if (commentIdToEdit != null) {
      url += "&comment=" + commentIdToEdit;
    }
    proxyRequester.post(user, token, url, callback);
  }

  public void getHomeFeed(int start, int count,
      final AsyncCallback<Feed> callback) {
    proxyRequester.get(user, token, HOME_FEED_URL + "&start=" + start + "&num="
        + count, callback);
  }

  public void getUserComments(String user, AsyncCallback<Feed> callback) {
    requestBuilder.requestObject(USER_FEED_URL + "/" + user + COMMENTS_PATH + "?",
        callback);
  }

  public void getUserFeed(String user, AsyncCallback<Feed> callback) {
    requestBuilder.requestObject(USER_FEED_URL + "/" + user + "?", callback);
  }

  public void getUserFeed(String[] users, AsyncCallback<Feed> callback) {
    String url = USER_FEED_URL + "?nickname=";
    url = join(users, url);
    requestBuilder.requestObject(url, callback);
  }

  public void like(String entryId, AsyncCallback<SimpleResponse> callback) {
    proxyRequester.post(user, token, LIKE_URL + "&entry=" + entryId, callback);
  }

  public void share(String title, String link, String comment,
      AsyncCallback<Feed> callback) {
    String url = SHARE_URL + "&title=" + URL.encodeComponent(title);
    if (link != null) {
      url += "&link=" + link;
    }
    if (comment != null) {
      url += "&comment=" + URL.encodeComponent(comment);
    }
    proxyRequester.post(user, token, url, callback);
  }

  public void unlike(String entryId, AsyncCallback<SimpleResponse> callback) {
    proxyRequester.post(user, token, LIKE_DELETE_URL + "&entry=" + entryId,
        callback);
  }

  private String join(String[] users, String url) {
    for (int i = 0; i < users.length; ++i) {
      url += users[i];
      if (i < users.length - 1) {
        url += ",";
      }
    }
    return url;
  }
}
