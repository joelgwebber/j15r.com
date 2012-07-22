package com.j15r.friendfeed.client;

import com.google.gwt.dom.client.TextAreaElement;
import com.google.gwt.topspin.ui.client.Anchor;
import com.google.gwt.topspin.ui.client.ClickEvent;
import com.google.gwt.topspin.ui.client.ClickListener;
import com.google.gwt.topspin.ui.client.Composite;
import com.google.gwt.topspin.ui.client.Container;
import com.google.gwt.topspin.ui.client.Form;
import com.google.gwt.topspin.ui.client.SubmitButton;
import com.google.gwt.topspin.ui.client.SubmitEvent;
import com.google.gwt.topspin.ui.client.SubmitListener;
import com.google.gwt.topspin.ui.client.TextArea;

public class NewCommentView extends Composite {

  public interface Delegate {

    void onCancel(NewCommentView source);

    void onPost(NewCommentView source);
  }

  private Form form;
  private TextArea body;
  private SubmitButton post;
  private Anchor cancel;

  public NewCommentView(Container container, final Delegate listener) {
    super(container);
    form = new Form(getCompositeContainer());
    body = new TextArea(form.getContainer());
    post = new SubmitButton(form.getContainer());
    cancel = new Anchor(form.getContainer());

    body.setWidth("100%");
    post.setText("Post");
    cancel.setText("Cancel");
    cancel.setHref("#");

    assert listener != null : "TODO";

    form.addSubmitListener(new SubmitListener() {
      public void onSubmit(SubmitEvent event) {
        listener.onPost(NewCommentView.this);
      }
    });

    cancel.addClickListener(new ClickListener() {
      public void onClick(ClickEvent event) {
        listener.onCancel(NewCommentView.this);
      }
    });

    setStyleName("NewCommentView");
  }

  public String getBody() {
    return body.getText();
  }

  public void focus() {
    // TODO: This needs to exist on TextArea
    body.getElement().<TextAreaElement>cast().focus();
  }
}
