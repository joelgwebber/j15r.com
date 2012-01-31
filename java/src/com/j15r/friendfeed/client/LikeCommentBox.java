package com.j15r.friendfeed.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.AnchorElement;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.events.client.Event;
import com.google.gwt.events.client.EventListener;
import com.google.gwt.topspin.ui.client.Container;
import com.google.gwt.topspin.ui.client.Widget;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;

public class LikeCommentBox extends Widget {

  public interface Delegate {
    void commentClicked();

    void likeClicked();
  }

  interface Binder extends UiBinder<Element, LikeCommentBox> { }
  private static Binder binder = GWT.create(Binder.class);

  @UiField AnchorElement commentElem, likeElem;
  @UiField ImageElement likeImageElem;

  public LikeCommentBox(Container container, final Delegate delegate) {
    create(binder.createAndBindUi(this), container);

    Event.addEventListener("click", commentElem, new EventListener() {
      public void handleEvent(Event event) {
        delegate.commentClicked();
      }
    });

    Event.addEventListener("click", likeElem, new EventListener() {
      public void handleEvent(Event event) {
        delegate.likeClicked();
      }
    });
  }

  public void setCanLike(boolean canLike) {
    likeImageElem.setSrc(canLike ? "smile.png" : "frown.png");
  }
}
