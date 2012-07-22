package com.google.gwt.topspin.sample.gfx.client;

import com.google.gwt.topspin.ui.client.Container;

public class PhotosMenu extends IconMenu {

  private static final String[] options = new String[] {
    "Tortola",
    "Jaisalmer",
    "Khuri",
    "Varanasi",
    "Agra",
    "Asakusa",
  };

  private static final String[] icons = new String[] {
    "photos/Tortola.jpg",
    "photos/Jaisalmer.jpg",
    "photos/Khuri.jpg",
    "photos/Varanasi.jpg",
    "photos/Agra.jpg",
    "photos/Asakusa.jpg",
  };

  public PhotosMenu(Container container) {
    super(container, options, icons, new Listener() {
      public void onItemSelected(IconMenu sender, int index) {
        // TODO
      }
    });
  }
}
