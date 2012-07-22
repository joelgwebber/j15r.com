package com.j15r.gsearch.client;

import com.google.gwt.core.client.JsArray;
import com.google.gwt.jsonp.client.JsonpRequestBuilder;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.j15r.common.client.JsArrayIterable;
import com.j15r.common.client.mvc.CachingListController;
import com.j15r.gsearch.client.model.SearchResponse;
import com.j15r.gsearch.client.model.SearchResult;

public class SearchController<T extends SearchResult> extends
    CachingListController<T> {

//  private static final String BACKEND = "http://ajax.googleapis.com";
//  private static final int MAX_RESULTS = 64;

  private static final String BACKEND = "http://the.atl.corp.google.com:3222";
  private static final int MAX_RESULTS = 1024;

  private static final int PAGE_SIZE = 8;

  private String query;
  private String type;

  public SearchController(String type) {
    super(PAGE_SIZE);
    this.type = type;
  }

  public void setQuery(String query) {
    if (query.equals(this.query)) {
      return;
    }

    this.query = query;
    clearCache();
  }

  @Override
  protected void doRequestPage(final int pageIdx) {
    if (query == null) {
      return;
    }

    JsonpRequestBuilder rb = new JsonpRequestBuilder();
    rb.requestObject(requestUrl(pageIdx * PAGE_SIZE, query), new AsyncCallback<SearchResponse<T>>() {
      public void onFailure(Throwable caught) {
        Window.alert("TODO: something's rotten in denmark");
      }

      public void onSuccess(SearchResponse<T> result) {
        JsArray<T> results = result.getResponseData().getResults();
        processResults(results);
        JsArrayIterable<T> iterable = new JsArrayIterable<T>(results);
        providePage(pageIdx, iterable);
        setMaxItems(guessMaxItems(pageIdx, results));
      }
    });
  }

  protected void processResults(JsArray<T> results) {
  }

  protected String requestUrl(int start, String query) {
    return BACKEND + "/ajax/services/search/" + type
      + "?v=1.0&rsz=large&start=" + start + "&q=" + query;
  }

  private int guessMaxItems(int pageIdx, JsArray<T> results) {
    // Set the max number of items. The only way to determine this from
    // the Ajax Search API is to see when a page comes up short. The
    // default maximum is 64 (this is not documented, but is
    // emperically true).
    if (results.length() < PAGE_SIZE) {
      return pageIdx * PAGE_SIZE + results.length();
    }

    return MAX_RESULTS;
  }
}
