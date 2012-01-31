package com.google.gwt.topspin.sample.gfx.client;

import com.google.gwt.animation.client.Animation;
import com.google.gwt.dom.client.Element;

public class ScaleAnimation extends Animation {

  private final Element elem;
  private final float xEnd, yEnd;
  private final float scaleStart, scaleEnd;
  private final float xStart, yStart;

  public ScaleAnimation(Element elem, float xStart, float yStart, float xEnd,
      float yEnd, float scaleStart, float scaleEnd) {
    this.elem = elem;
    this.xStart = xStart;
    this.yStart = yStart;
    this.xEnd = xEnd;
    this.yEnd = yEnd;
    this.scaleStart = scaleStart;
    this.scaleEnd = scaleEnd;
  }

  @Override
  public void onCancel() {
  }

  @Override
  public void onComplete() {
    Util.setTransform(elem, xEnd, yEnd, 0, scaleEnd, scaleEnd);
  }

  @Override
  public void onStart() {
    Util.setTransform(elem, xStart, yStart, 0, scaleStart, scaleStart);
  }

  @Override
  public void onUpdate(double progress) {
    double scale = scaleStart + progress * (scaleEnd - scaleStart);
    double x = xStart + progress * (xEnd - xStart);
    double y = yStart + progress * (yEnd - yStart);

    Util.setTransform(elem, x, y, 0, scale, scale);
  }
}
