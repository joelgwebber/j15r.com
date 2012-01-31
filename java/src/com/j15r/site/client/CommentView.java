package com.j15r.site.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.AnchorElement;
import com.google.gwt.dom.client.Element;
import com.google.gwt.topspin.ui.client.Container;
import com.google.gwt.topspin.ui.client.Widget;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;

import com.j15r.common.client.DateUtil;

import java.util.Date;

public class CommentView extends Widget {

  interface Binder extends UiBinder<Element, CommentView> { }
  private static Binder binder = GWT.create(Binder.class);

  @UiField AnchorElement nameElem;
  @UiField Element dateElem, commentElem;

  public CommentView(Container container) {
    create(binder.createAndBindUi(this), container);
  }

  public void setComment(Comment comment) {
    nameElem.setInnerText(comment.name());
    if (comment.site() != null) {
      nameElem.setHref(comment.site());
    }

    Date date = DateUtil.parseRfc3339Date(comment.date());
    dateElem.setInnerText(DateUtil.formatDateRelativeToNow(date));

    commentElem.setInnerHTML(comment.html());
  }
}
