package com.google.gwt.topspin.sample.gfx.client;

import com.google.gwt.topspin.ui.client.Container;

public class MusicMenu extends IconMenu {

  private static final String[] options = new String[] {
    "Bjork",
    "Dzihan & Kamien",
    "Foo Fighters",
    "Jordi Savall",
    "Smashing Pumpkins",
    "Tool",
  };

  private static final String[] icons = new String[] {
    "albums/Bjork.jpg",
    "albums/DzihanKamien.jpg",
    "albums/FooFighters.jpg",
    "albums/JordiSavall.jpg",
    "albums/SmashingPumpkins.jpg",
    "albums/Tool.jpg",
  };

  public MusicMenu(Container container) {
    super(container, options, icons, new Listener() {
      public void onItemSelected(IconMenu sender, int index) {
        // TODO
      }
    });
  }
}
