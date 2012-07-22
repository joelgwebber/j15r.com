package com.google.gwt.topspin.sample.gfx.client;

import com.google.gwt.animation.client.Animation;
import com.google.gwt.dom.client.Element;

public class ExplodeAnimation extends Animation {

  private final Element elem;
  private final boolean out;

  public ExplodeAnimation(Element elem, boolean out) {
    this.elem = elem;
    this.out = out;
  }

  @Override
  public void onCancel() {
  }

  @Override
  public void onComplete() {
  }

  @Override
  public void onStart() {
    if (!out) {
      Util.setAlpha(elem, 0.0);
      Util.setScale(elem, 5.0);
    }
  }

  @Override
  public void onUpdate(double progress) {
    Util.setAlpha(elem, 1.0 - progress);
    Util.setScale(elem, 1.0 + (progress * 4.0));
  }
}
