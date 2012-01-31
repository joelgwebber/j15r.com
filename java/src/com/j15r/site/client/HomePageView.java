package com.j15r.site.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.ButtonElement;
import com.google.gwt.dom.client.Element;
import com.google.gwt.events.client.Event;
import com.google.gwt.events.client.EventListener;
import com.google.gwt.topspin.ui.client.Container;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;

public class HomePageView extends PageView {

  interface Binder extends UiBinder<Element, HomePageView> { }
  private static Binder binder = GWT.create(Binder.class);

  @UiField ButtonElement linkButton;

  public HomePageView(Container container, final Site site) {
    create(binder.createAndBindUi(this), container);

    Event.addEventListener("click", linkButton, new EventListener() {
      public void handleEvent(Event event) {
        site.showPage("11");
      }
    });
  }

  @Override
  public void setPage(Page page) {
    // Do nothing.
  }
}
