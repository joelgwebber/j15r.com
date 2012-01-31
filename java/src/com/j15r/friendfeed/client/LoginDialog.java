package com.j15r.friendfeed.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.FormElement;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.topspin.ui.client.Container;
import com.google.gwt.topspin.ui.client.Form;
import com.google.gwt.topspin.ui.client.InputText;
import com.google.gwt.topspin.ui.client.SubmitEvent;
import com.google.gwt.topspin.ui.client.SubmitListener;
import com.google.gwt.topspin.ui.client.Widget;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;

public class LoginDialog extends Widget {

  public interface Delegate {
    void loginComplete(String nickname, String key);
  }

  interface Binder extends UiBinder<Element, LoginDialog> { }
  private static Binder binder = GWT.create(Binder.class);

  private Delegate delegate;

  @UiField FormElement formElem;
  @UiField InputElement nickElem, keyElem;

  public LoginDialog(Container container) {
    create(binder.createAndBindUi(this), container);

    final InputText nickBox = new InputText(nickElem);
    final InputText keyBox = new InputText(keyElem);

    Form form = new Form(formElem);
    form.addSubmitListener(new SubmitListener() {
      public void onSubmit(SubmitEvent event) {
        if (delegate != null) {
          delegate.loginComplete(nickBox.getText(), keyBox.getText());
        }
      }
    });
  }

  public void setDelegate(Delegate delegate) {
    this.delegate = delegate;
  }
}
