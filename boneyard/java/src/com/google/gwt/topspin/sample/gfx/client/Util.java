package com.google.gwt.topspin.sample.gfx.client;

import com.google.gwt.dom.client.Element;

public class Util {

  public static native void setAlpha(Element elem, double alpha) /*-{
    elem.style.opacity = alpha;
  }-*/;

  public static native void setScale(Element elem, double scale) /*-{
    elem.style.webkitTransform = 'scale(' + scale + ')';
  }-*/;

  public static native void setTransform(Element elem, double x, double y, double rotation, double scaleX, double scaleY) /*-{
    elem.style.webkitTransform = 'translate(' + x + 'px,' + y + 'px) rotate(' + rotation + 'deg) scale(' + scaleX + ',' + scaleY + ')';
  }-*/;
}
