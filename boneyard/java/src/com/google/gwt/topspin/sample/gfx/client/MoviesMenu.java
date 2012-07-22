package com.google.gwt.topspin.sample.gfx.client;

import com.google.gwt.topspin.ui.client.Container;
import com.google.gwt.topspin.ui.client.Root;

public class MoviesMenu extends IconMenu {

  private static final String[] options = new String[] {
    "Forbidden Kingdom",
    "Mongol",
    "Brick Lane",
    "The Wackness",
    "Hellboy 2",
    "Bangkok Dangerous"
  };

  private static final String[] icons = new String[] {
    "http://images.apple.com/moviesxml/s/lions_gate/posters/forbiddenkingdom_l200801241316.jpg",
    "http://images.apple.com/moviesxml/s/picturehouse/posters/mongol_l200804101616.jpg",
    "http://images.apple.com/moviesxml/s/sony/posters/bricklane_l200804161459.jpg",
    "http://images.apple.com/moviesxml/s/sony/posters/thewackness_l200804241723.jpg",
    "http://images.apple.com/moviesxml/s/universal/posters/hellboy2thegoldenarmy_l200801071541.jpg",
    "http://images.apple.com/moviesxml/s/lions_gate/posters/bangkokdangerous_l200804041607.jpg"
  };

  private static final String[] movies = new String[] {
    "http://movies.apple.com/movies/lionsgate/forbidden_kingdom/forbidden_kingdom_h.480.mov",
    "http://movies.apple.com/movies/picturehouse/mongol/mongol-tlr2-h.ref.mov",
    "http://movies.apple.com/movies/sony/brick_lane/brick_lane-h.ref.mov",
    "http://movies.apple.com/movies/sony/the_wackness/the_wackness-h.ref.mov",
    "http://movies.apple.com/movies/universal/hellboy_2/hellboy_2-tlr1_h.480.mov",
    "http://movies.apple.com/movies/lionsgate/bangkok_dangerous/bangkok_dangerous-h.ref.mov"
  };

  public MoviesMenu(Container container) {
    super(container, options, icons, new Listener() {
      public void onItemSelected(IconMenu sender, int index) {
        MoviePlayer player = new MoviePlayer(Root.getContainer(), movies[index]);
        player.getElement().getStyle().setProperty("position", "absolute");
        player.getElement().getStyle().setPropertyPx("left", 0);
        player.getElement().getStyle().setPropertyPx("top", 0);
        player.getElement().getStyle().setPropertyPx("right", 0);
        player.getElement().getStyle().setPropertyPx("bottom", 0);

        new FadeAnimation(player.getElement(), 0, 1).run(1000);
      }
    });
  }
}
