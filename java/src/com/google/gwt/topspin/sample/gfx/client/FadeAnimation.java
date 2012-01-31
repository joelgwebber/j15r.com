package com.google.gwt.topspin.sample.gfx.client;

import com.google.gwt.animation.client.Animation;
import com.google.gwt.dom.client.Element;

public class FadeAnimation extends Animation {

  private final Element elem;
  private final double startOpacity;
  private final double endOpacity;

  public FadeAnimation(Element elem, double startOpacity, double endOpacity) {
    this.elem = elem;
    this.startOpacity = startOpacity;
    this.endOpacity = endOpacity;
  }

  @Override
  public void onCancel() {
  }

  @Override
  public void onComplete() {
    Util.setAlpha(elem, endOpacity);
  }

  @Override
  public void onStart() {
    Util.setAlpha(elem, startOpacity);
  }

  @Override
  public void onUpdate(double progress) {
    Util.setAlpha(elem, startOpacity + progress * (endOpacity - startOpacity));
  }
}
