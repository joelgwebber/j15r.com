package com.j15r.gsearch.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.AnchorElement;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.FormElement;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.topspin.ui.client.Anchor;
import com.google.gwt.topspin.ui.client.ClickEvent;
import com.google.gwt.topspin.ui.client.ClickListener;
import com.google.gwt.topspin.ui.client.Container;
import com.google.gwt.topspin.ui.client.Form;
import com.google.gwt.topspin.ui.client.InputText;
import com.google.gwt.topspin.ui.client.SubmitEvent;
import com.google.gwt.topspin.ui.client.SubmitListener;
import com.google.gwt.topspin.ui.client.Widget;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;

public class SearchForm extends Widget {

  public interface Delegate {
    void onSearch(String query);
    void onWebTypeSelected();
    void onImageTypeSelected();
    void onVideoTypeSelected();
    void onNewsTypeSelected();
  }

  interface Binder extends UiBinder<Element, SearchForm> { }
  private static Binder binder = GWT.create(Binder.class);

  private InputText searchBox;
  private Anchor webAnchor, imageAnchor, videoAnchor, newsAnchor;

  @UiField FormElement formElem;
  @UiField InputElement searchInput;
  @UiField AnchorElement webElem, imageElem, videoElem, newsElem;

  public SearchForm(Container container, final Delegate delegate) {
    create(binder.createAndBindUi(this), container);

    Form form = new Form(formElem);
    form.addSubmitListener(new SubmitListener() {
      public void onSubmit(SubmitEvent event) {
        delegate.onSearch(searchBox.getText());
        event.preventDefault();
      }
    });

    searchBox = new InputText(searchInput);
    webAnchor = new Anchor(webElem);
    imageAnchor = new Anchor(imageElem);
    videoAnchor = new Anchor(videoElem);
    newsAnchor = new Anchor(newsElem);

    webAnchor.addClickListener(new ClickListener() {
      public void onClick(ClickEvent event) {
        delegate.onWebTypeSelected();
      }
    });
    imageAnchor.addClickListener(new ClickListener() {
      public void onClick(ClickEvent event) {
        delegate.onImageTypeSelected();
      }
    });
    videoAnchor.addClickListener(new ClickListener() {
      public void onClick(ClickEvent event) {
        delegate.onVideoTypeSelected();
      }
    });
    newsAnchor.addClickListener(new ClickListener() {
      public void onClick(ClickEvent event) {
        delegate.onNewsTypeSelected();
      }
    });
  }

  public String getSearchText() {
    return searchBox.getText();
  }
}
