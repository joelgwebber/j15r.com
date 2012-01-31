package com.google.gwt.topspin.sample.gfx.client;

import com.google.gwt.topspin.ui.client.Container;
import com.google.gwt.topspin.ui.client.Image;

public class BackButton extends Image {

  public BackButton(Container container) {
    super(container);
    setSourceUrl("back.png");
    getElement().getStyle().setProperty("cursor", "pointer");
  }
}
