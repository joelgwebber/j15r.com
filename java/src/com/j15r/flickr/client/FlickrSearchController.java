package com.j15r.flickr.client;

import com.google.gwt.core.client.JsArray;
import com.google.gwt.jsonp.client.JsonpRequestBuilder;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

import com.j15r.common.client.JsArrayIterable;
import com.j15r.common.client.mvc.CachingListController;

/**
 * Docs:http://www.flickr.com/services/api/
 * Key:e9ff8d9a9e9f7cda9a77cf4e9da9fc1c
 * Secret:359287b7f8e75bcf
 */
public class FlickrSearchController extends CachingListController<Photo> {

  private static final int MAX_RESULTS = 1024;
  private static final int PAGE_SIZE = 32;

  private String commaSeparatedTags;
  private String[] tags;

  public FlickrSearchController() {
    super(PAGE_SIZE);
  }

  public String[] getTags() {
    return tags;
  }

  public void setTags(String[] tags) {
    this.tags = tags;
    if (tags == null) {
      commaSeparatedTags = null;
    } else {
      StringBuilder builder = new StringBuilder();
      for (int i = 0; i < tags.length; ++i) {
        builder.append(tags[i]);
        if (i != tags.length - 1) {
          builder.append(",");
        }
      }
      commaSeparatedTags = builder.toString();
    }

    clearCache();
  }

  @Override
  protected void doRequestPage(final int pageIdx) {
    JsonpRequestBuilder rb = new JsonpRequestBuilder();
    rb.setCallbackParam("jsoncallback");
    rb.requestObject(feedUrl(pageIdx + 1, PAGE_SIZE), new AsyncCallback<PhotosResponse>() {

      public void onFailure(Throwable caught) {
        Window.alert("something's rotten in denmark");
      }

      public void onSuccess(PhotosResponse result) {
        Photos photos = result.getPhotos();

        int total = photos.getTotal();
        if (total > MAX_RESULTS) {
          total = MAX_RESULTS;
        }
        setMaxItems(total);

        JsArray<Photo> array = photos.getPhotoArray();
        providePage(pageIdx, new JsArrayIterable<Photo>(array));
      }
    });
  }

  private String feedUrl(int page, int perPage) {
    if (commaSeparatedTags == null) {
      return "http://api.flickr.com/services/rest/"
          + "?method=flickr.interestingness.getList" + "&format=json"
          + "&api_key=e9ff8d9a9e9f7cda9a77cf4e9da9fc1c" + "&page=" + page
          + "&per_page=" + perPage;
    }

    return "http://api.flickr.com/services/rest/"
        + "?method=flickr.photos.search" + "&format=json"
        + "&api_key=e9ff8d9a9e9f7cda9a77cf4e9da9fc1c" + "&page=" + page
        + "&per_page=" + perPage + "&tag_mode=all&tags=" + commaSeparatedTags;
  }
}
