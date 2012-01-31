package com.google.gwt.topspin.sample.gfx.client;

import com.google.gwt.topspin.ui.client.Container;

public class SettingsMenu extends IconMenu {

  private static final String[] options = new String[] {
      "Movies", "Music", "Photos", "Network"};
  private static final String[] icons = new String[] {
      "icons/Settings.png", "icons/Settings.png", "icons/Settings.png",
      "icons/Settings.png"};

  public SettingsMenu(Container container) {
    super(container, options, icons, new Listener() {
      public void onItemSelected(IconMenu sender, int index) {
        // TODO
      }
    });
  }
}
