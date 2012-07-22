package com.j15r.flickr.client;

import com.google.gwt.core.client.JsArray;
import com.google.gwt.jsonp.client.JsonpRequestBuilder;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

import com.j15r.common.client.JsArrayIterable;
import com.j15r.common.client.mvc.SimpleListController;

public class FlickrTagsController extends SimpleListController<Tag> {

  private String photoId;

  public String getPhotoId() {
    return photoId;
  }

  public void setPhotoId(String photoId) {
    this.photoId = photoId;

    if (photoId != null) {
      JsonpRequestBuilder rb = new JsonpRequestBuilder();
      rb.setCallbackParam("jsoncallback");
      rb.requestObject(feedUrl(getPhotoId()), new AsyncCallback<PhotoTagsResponse>() {

        public void onFailure(Throwable caught) {
          Window.alert("something's rotten in denmark");
        }

        public void onSuccess(PhotoTagsResponse result) {
          JsArray<Tag> tags = result.getPhoto().getTag();
          setData(new JsArrayIterable<Tag>(tags));
        }
      });
    }
  }

  protected String feedUrl(String photoId) {
    return "http://api.flickr.com/services/rest/"
        + "?method=flickr.tags.getListPhoto" + "&format=json"
        + "&api_key=e9ff8d9a9e9f7cda9a77cf4e9da9fc1c" + "&photo_id=" + photoId;
  }
}
