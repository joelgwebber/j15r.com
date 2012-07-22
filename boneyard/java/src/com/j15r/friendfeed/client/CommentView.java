package com.j15r.friendfeed.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.AnchorElement;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.topspin.ui.client.Container;
import com.google.gwt.topspin.ui.client.Widget;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;

import com.j15r.common.client.mvc.ObjectView;
import com.j15r.friendfeed.client.Feed.Comment;
import com.j15r.friendfeed.client.Feed.User;

public class CommentView extends Widget implements ObjectView<Feed.Comment> {

  interface Binder extends UiBinder<Element, CommentView> { }
  private static Binder binder = GWT.create(Binder.class);

  private Comment comment;

  @UiField SpanElement bodyElem;
  @UiField AnchorElement userElem;

  protected CommentView(Container container) {
    create(binder.createAndBindUi(this), container);
  }

  public Comment getData() {
    return comment;
  }

  public void setData(Comment data) {
    comment = data;
    bodyElem.setInnerHTML(Util.linkify(comment.getBody()));

    User user = comment.getUser();
    if (user == null) {
      // Newly-entered comments come back with a null user
      userElem.setHref("#");
      userElem.setInnerText("You");
    } else {
      userElem.setHref(user.getProfileUrl());
      userElem.setInnerText(user.getName());
    }
  }
}
