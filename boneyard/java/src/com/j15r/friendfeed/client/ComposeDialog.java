package com.j15r.friendfeed.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.AnchorElement;
import com.google.gwt.dom.client.ButtonElement;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.dom.client.TextAreaElement;
import com.google.gwt.topspin.ui.client.Anchor;
import com.google.gwt.topspin.ui.client.Button;
import com.google.gwt.topspin.ui.client.ClickEvent;
import com.google.gwt.topspin.ui.client.ClickListener;
import com.google.gwt.topspin.ui.client.Container;
import com.google.gwt.topspin.ui.client.Widget;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;

public class ComposeDialog extends Widget {

  public interface Delegate {
    void onClose(ComposeDialog dlg);

    void onPost(ComposeDialog dlg);
  }

  interface Binder extends UiBinder<Element, ComposeDialog> { }
  private static Binder binder = GWT.create(Binder.class);

  private Delegate delegate;

  @UiField AnchorElement closeAnchor, linkAnchor, commentAnchor;
  @UiField TextAreaElement messageBox;
  @UiField ButtonElement postButton;
  @UiField InputElement linkBox, commentBox;

  public ComposeDialog(Container container) {
    create(binder.createAndBindUi(this), container);

    Anchor close = new Anchor(closeAnchor);
    Anchor link = new Anchor(linkAnchor);
    Anchor comment = new Anchor(commentAnchor);
    Button post = new Button(postButton);

    link.addClickListener(new ClickListener() {
      public void onClick(ClickEvent event) {
        linkAnchor.getStyle().setProperty("display", "none");
        linkBox.getStyle().setProperty("display", "");
      }
    });

    comment.addClickListener(new ClickListener() {
      public void onClick(ClickEvent event) {
        commentAnchor.getStyle().setProperty("display", "none");
        commentBox.getStyle().setProperty("display", "");
      }
    });

    close.addClickListener(new ClickListener() {
      public void onClick(ClickEvent event) {
        if (delegate != null) {
          delegate.onClose(ComposeDialog.this);
        }
      }
    });

    post.addClickListener(new ClickListener() {
      public void onClick(ClickEvent event) {
        if (delegate != null) {
          delegate.onPost(ComposeDialog.this);
        }
      }
    });

    setStyleName("ComposeDialog");
  }

  public String getComment() {
    return commentBox.getValue();
  }

  public String getLink() {
    return linkBox.getValue();
  }

  public String getTitle() {
    return messageBox.getValue();
  }

  public void setDelegate(Delegate delegate) {
    this.delegate = delegate;
  }
}
