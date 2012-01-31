package com.j15r.friendfeed.client;

import com.google.gwt.core.client.JavaScriptException;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.xhr.client.ReadyStateChangeHandler;
import com.google.gwt.xhr.client.XMLHttpRequest;

public class ProxyJsonRequester {

  public <T extends JavaScriptObject> void get(String user, String token,
      String url, final AsyncCallback<T> callback) {
    XMLHttpRequest req = XMLHttpRequest.create();
    req.open("GET", authUrl(user, token, url, false));
    req.setOnReadyStateChange(new ReadyStateChangeHandler() {
      public void onReadyStateChange(XMLHttpRequest xhr) {
        if (xhr.getReadyState() == XMLHttpRequest.DONE) {
          String responseText = xhr.getResponseText();
          try {
            T feed = eval("(" + responseText + ")");
            callback.onSuccess(feed);
          } catch (JavaScriptException e) {
            callback.onFailure(e);
          }
        }
      }
    });
    req.send();
  }

  public <T extends JavaScriptObject> void post(String user, String token,
      String url, final AsyncCallback<T> callback) {
    XMLHttpRequest req = XMLHttpRequest.create();
    req.open("POST", authUrl(user, token, url, false));
    req.setOnReadyStateChange(new ReadyStateChangeHandler() {
      public void onReadyStateChange(XMLHttpRequest xhr) {
        if (xhr.getReadyState() == XMLHttpRequest.DONE) {
          try {
            T feed = eval("(" + xhr.getResponseText() + ")");
            callback.onSuccess(feed);
          } catch (JavaScriptException e) {
            callback.onFailure(e);
          }
        }
      }
    });
    req.send();
  }

  private String authUrl(String user, String token, String url, boolean post) {
    return "/ff-auth?_user=" + user + "&_token=" + token
        + "&_url=" + url + (post ? "&_post" : "");
  }

  private native <T extends JavaScriptObject> T eval(String json) /*-{
    return eval(json);
  }-*/;
}
