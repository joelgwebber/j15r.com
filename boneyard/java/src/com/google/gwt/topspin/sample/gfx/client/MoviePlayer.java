package com.google.gwt.topspin.sample.gfx.client;

import com.google.gwt.topspin.gfx.client.CanPlayThroughEvent;
import com.google.gwt.topspin.gfx.client.CanPlayThroughListener;
import com.google.gwt.topspin.gfx.client.ProgressEvent;
import com.google.gwt.topspin.gfx.client.ProgressListener;
import com.google.gwt.topspin.gfx.client.Video;
import com.google.gwt.topspin.ui.client.ClickEvent;
import com.google.gwt.topspin.ui.client.ClickListener;
import com.google.gwt.topspin.ui.client.Composite;
import com.google.gwt.topspin.ui.client.Container;
import com.google.gwt.topspin.ui.client.Div;
import com.google.gwt.topspin.ui.client.Panel;
import com.google.gwt.topspin.ui.client.Root;

public class MoviePlayer extends Composite {

  public MoviePlayer(Container container, String movieUrl) {
    super(container);

    Panel p = new Panel(getCompositeContainer());
    p.getElement().getStyle().setProperty("background-color", "black");

    final Video vid = new Video(p.getContainer());
    vid.getElement().getStyle().setProperty("width", "100%");
    vid.getElement().getStyle().setProperty("height", "100%");
    vid.setSrc(movieUrl);
    vid.load();

    BackButton back = new BackButton(p.getContainer());
    back.getElement().getStyle().setProperty("position", "absolute");
    back.getElement().getStyle().setPropertyPx("left", 8);
    back.getElement().getStyle().setPropertyPx("top", 8);
    back.addClickListener(new ClickListener() {
      public void onClick(ClickEvent event) {
        vid.pause();
        new FadeAnimation(getElement(), 1, 0) {
          @Override
          public void onComplete() {
            super.onComplete();
            MoviePlayer.this.destroy();
          }
        }.run(1000);
      }
    });

    final Div echo = new Div(Root.getContainer());
    echo.getElement().getStyle().setProperty("color", "white");
    echo.getElement().getStyle().setProperty("font", "24pt Arial");
    echo.getElement().getStyle().setProperty("position", "absolute");
    echo.getElement().getStyle().setPropertyPx("left", 64);
    echo.getElement().getStyle().setPropertyPx("top", 16);

    echo.setHtml("0% loaded");
    vid.addProgressListener(new ProgressListener() {
      public void onProgress(ProgressEvent event) {
        echo.setHtml("" + (100 * event.getBytesLoaded() / event.getTotalBytes()) + "% loaded");
      }
    });

    vid.addCanPlayThroughListener(new CanPlayThroughListener() {
      public void onCanPlayThrough(CanPlayThroughEvent event) {
        echo.setVisible(false);
        vid.play();
      }
    });
  }
}
