package com.j15r.gsearch.client;

import com.google.gwt.jsonp.client.JsonpRequestBuilder;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.j15r.common.client.JsArrayIterable;
import com.j15r.common.client.mvc.SuggestController;
import com.j15r.common.client.mvc.SuggestView;
import com.j15r.common.client.mvc.SuggestViewListener;
import com.j15r.gsearch.client.model.SearchSuggestResponse;
import com.j15r.gsearch.client.model.SearchSuggestion;

import java.util.ArrayList;

public class SearchSuggestController implements
    SuggestController<SearchSuggestion>, SuggestViewListener<SearchSuggestion> {

  private ArrayList<SuggestView<SearchSuggestion>> views = new ArrayList<SuggestView<SearchSuggestion>>();

  public void addView(SuggestView<SearchSuggestion> view) {
    views.add(view);
    view.setListener(this);
  }

  public void removeView(SuggestView<SearchSuggestion> view) {
    assert views.contains(view) : "TODO";

    views.remove(view);
    view.setListener(null);
  }

  public void onQueryChanged(final SuggestView<SearchSuggestion> view) {
    JsonpRequestBuilder rb = new JsonpRequestBuilder();
    rb.setCallbackParam("jsonp");
    rb.requestObject(requestUrl(view.getQuery()), new AsyncCallback<SearchSuggestResponse>() {
      public void onFailure(Throwable caught) {
        Window.alert("TODO: something's rotten in denmark");
      }

      public void onSuccess(SearchSuggestResponse result) {
        view.setData(new JsArrayIterable<SearchSuggestion>(
            result.getSuggestions()));
      }
    });
  }

  private String requestUrl(String query) {
    return "http://suggestqueries.google.com/complete/search?hl=en&q=" + query;
  }
}
