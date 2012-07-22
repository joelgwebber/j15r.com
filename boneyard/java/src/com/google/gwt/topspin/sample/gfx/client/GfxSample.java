package com.google.gwt.topspin.sample.gfx.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.topspin.ui.client.ClickEvent;
import com.google.gwt.topspin.ui.client.ClickListener;
import com.google.gwt.topspin.ui.client.Root;

public class GfxSample implements EntryPoint {

  private static final String[] mainOptions = new String[] {
    "Movies", "Music", "Photos", "Settings"};
  private static final String[] mainUrls = new String[] {
    "icons/Movies.png", "icons/Music.png", "icons/Photos.png", "icons/Settings.png"};

  private IconMenu mainMenu;
  private IconMenu curSubMenu;

  private IconMenu moviesMenu;
  private IconMenu musicMenu;
  private IconMenu photosMenu;
  private IconMenu settingsMenu;

  private BackButton backButton;

  public void onModuleLoad() {
    backButton = new BackButton(Root.getContainer());
    backButton.addClickListener(new ClickListener() {
      public void onClick(ClickEvent event) {
        hideCurSubMenu();
        mainMenu.expand();
      }
    });
    Util.setAlpha(backButton.getElement(), 0);

    mainMenu = new IconMenu(Root.getContainer(), mainOptions, mainUrls,
        new IconMenu.Listener() {
          public void onItemSelected(IconMenu sender, int index) {
            sender.shrink();
            switch (index) {
              case 0:
                showMoviesMenu();
                break;
              case 1:
                showMusicMenu();
                break;
              case 2:
                showPhotosMenu();
                break;
              case 3:
                showSettingsMenu();
                break;
            }
          }
        });
    mainMenu.showItem(0);
    mainMenu.getElement().getStyle().setProperty("position", "absolute");
    mainMenu.getElement().getStyle().setPropertyPx("top", 16);
  }

  private void showMoviesMenu() {
    if (moviesMenu == null) {
      moviesMenu = new MoviesMenu(Root.getContainer());
    }

    showSubMenu(moviesMenu);
  }

  private void showMusicMenu() {
    if (musicMenu == null) {
      musicMenu = new MusicMenu(Root.getContainer());
    }

    showSubMenu(musicMenu);
  }

  private void showPhotosMenu() {
    if (photosMenu == null) {
      photosMenu = new PhotosMenu(Root.getContainer());
    }

    showSubMenu(photosMenu);
  }

  private void showSettingsMenu() {
    if (settingsMenu == null) {
      settingsMenu = new SettingsMenu(Root.getContainer());
    }

    showSubMenu(settingsMenu);
  }

  private void showSubMenu(IconMenu subMenu) {
    subMenu.showItem(0);
    subMenu.setVisible(true);
    new FadeAnimation(backButton.getElement(), 0, 1).run(500);
    new FadeAnimation(subMenu.getElement(), 0, 1).run(500);
    curSubMenu = subMenu;
  }

  private void hideCurSubMenu() {
    final IconMenu hidingMenu = curSubMenu;
    curSubMenu = null;

    new FadeAnimation(backButton.getElement(), 1, 0).run(500);
    new FadeAnimation(hidingMenu.getElement(), 1, 0) {
      @Override
      public void onComplete() {
        super.onComplete();
        hidingMenu.setVisible(false);
      }
    }.run(500);
  }
}
