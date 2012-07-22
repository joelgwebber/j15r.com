package com.j15r.friendfeed.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.AnchorElement;
import com.google.gwt.dom.client.Element;
import com.google.gwt.events.client.Event;
import com.google.gwt.events.client.EventListener;
import com.google.gwt.topspin.ui.client.Container;
import com.google.gwt.topspin.ui.client.Widget;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;

public class Header extends Widget {

  public interface Delegate {
    void logout();
    void prev();
    void next();
    void compose();
    void refresh();
  }

  interface Binder extends UiBinder<Element, Header> { }
  private static Binder binder = GWT.create(Binder.class);

  private class Listeners implements EventListener {
    public void handleEvent(Event event) {
      Element src = event.getCurrentTarget();

      if (src == logoutAnchor) {
        delegate.logout();
      } else if (src == prevAnchor) {
        delegate.prev();
      } else if (src == nextAnchor) {
        delegate.next();
      } else if (src == composeAnchor) {
        delegate.compose();
      } else if ((src == refreshAnchor) || (src == logoAnchor)) {
        delegate.refresh();
      }

    }
  }

  private Listeners listeners = new Listeners();
  private Delegate delegate;

  @UiField AnchorElement logoAnchor, logoutAnchor, prevAnchor, nextAnchor,
      composeAnchor, refreshAnchor;

  public Header(Container container) {
    create(binder.createAndBindUi(this), container);

    Event.addEventListener("click", logoAnchor, listeners);
    Event.addEventListener("click", prevAnchor, listeners);
    Event.addEventListener("click", composeAnchor, listeners);
    Event.addEventListener("click", refreshAnchor, listeners);
    Event.addEventListener("click", nextAnchor, listeners);
    Event.addEventListener("click", logoutAnchor, listeners);
  }

  public void setDelegate(Delegate delegate) {
    this.delegate = delegate;
  }
}
