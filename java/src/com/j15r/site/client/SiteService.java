package com.j15r.site.client;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.xhr.client.ReadyStateChangeHandler;
import com.google.gwt.xhr.client.XMLHttpRequest;

import java.util.HashMap;
import java.util.Map;

public class SiteService {

  public interface Listener {
    void onPageReceived(Page page);

    void onIndexReceived(JsArray<PageRef> index);

    void onCommentsReceived(String pageId, JsArray<Comment> comments);
  }

  public interface Callback {
    void done();
  }

  private static boolean waitingOnInit = true;
  private static Listener listener;
  private static Map<String, Page> pages = new HashMap<String, Page>();
  private static JsArray<PageRef> index;

  public static void init(Listener listener, final Callback callback) {
    assert listener != null;
    SiteService.listener = listener;

    waitForInitData(new Callback() {
      public void done() {
        JsArray<Page> initData = getInitData();
        for (int i = 0; i < initData.length(); ++i) {
          Page page = initData.get(i);
          String pageId = page.id();
          pages.put(pageId, page);
          SiteService.listener.onPageReceived(page);
        }

        waitingOnInit = false;
        callback.done();
      }
    });
  }

  public static void getPage(final String id) {
    assert !waitingOnInit : "Do not call getPage() before init complete";

    if (pages.containsKey(id)) {
      listener.onPageReceived(pages.get(id));
      return;
    }

    fetch("/rpc?page=" + id, new AsyncCallback<String>() {
      public void onSuccess(String result) {
        Page page = eval(result).<Page> cast();
        pages.put(id, page);
        listener.onPageReceived(page);
      }

      public void onFailure(Throwable caught) {
        // TODO
      }
    });
  }

  public static void getIndex() {
    if (index != null) {
      listener.onIndexReceived(index);
      return;
    }

    fetch("/rpc?index", new AsyncCallback<String>() {
      public void onSuccess(String result) {
        index = eval(result).<JsArray<PageRef>> cast();
        listener.onIndexReceived(index);
      }

      public void onFailure(Throwable caught) {
        // TODO
      }
    });
  }

  public static void getComments(final String pageId) {
    fetch("/rpc?comments=" + pageId, new AsyncCallback<String>() {
      public void onSuccess(String result) {
        JsArray<Comment> comments = eval(result).<JsArray<Comment>> cast();
        listener.onCommentsReceived(pageId, comments);
      }

      public void onFailure(Throwable caught) {
        // TODO
      }
    });
  }

  private static void fetch(String url, final AsyncCallback<String> callback) {
    XMLHttpRequest xhr = XMLHttpRequest.create();
    xhr.setOnReadyStateChange(new ReadyStateChangeHandler() {
      public void onReadyStateChange(XMLHttpRequest xhr) {
        if (xhr.getReadyState() == XMLHttpRequest.DONE) {
          callback.onSuccess(xhr.getResponseText());
        }
      }
    });

    xhr.open("GET", url);
    xhr.send();
  }

  private static void waitForInitData(Callback callback) {
    if (getInitData() != null) {
      callback.done();
      return;
    }

    injectCallback(callback);
  }

  private static native JsArray<Page> getInitData() /*-{
    return $wnd.__init;
  }-*/;

  private static native void injectCallback(Callback callback) /*-{
    $wnd.__initCallback = function() {
      callback.@com.j15r.site.client.SiteService.Callback::done()();
    };
  }-*/;

  private static native JavaScriptObject eval(String result) /*-{
    return eval("(" + result + ")");
  }-*/;
}
