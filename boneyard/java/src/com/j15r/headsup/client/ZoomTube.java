package com.j15r.headsup.client;

import static com.google.gwt.query.client.GQuery.$;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.query.client.Function;
import com.google.gwt.query.client.GQuery;
import com.google.gwt.query.client.Selector;
import com.google.gwt.query.client.Selectors;

public class ZoomTube implements EntryPoint {

  private static class FindLargestElement extends Function {
    int maxSize;
    Element ret;

    @Override
    public void f(Element e) {
      int size = e.getOffsetWidth() * e.getOffsetHeight();
      if (size > maxSize) {
        maxSize = size;
        ret = e;
      }
    }
  }

  public interface ZoomSelector extends Selectors {
    @Selector("embed")
    GQuery findAllEmbeds();
  }

  public void onModuleLoad() {
    // Position the container and embed to cover the whole client area.
    Element mp = findLargestEmbed();
    Element div = mp.getParentElement();

    $(div).css("position", "fixed")
          .css("width", "100%")
          .css("height", "100%")
          .css("left", "0")
          .css("top", "0")
          .css("right", "0")
          .css("bottom", "0")
          .css("zIndex", "32767");
    $(mp).css("position", "absolute")
          .css("width", "100%")
          .css("height", "100%");
    $("body").css("overflow", "none");
  }

  private Element findLargestEmbed() {
    ZoomSelector zs = GWT.create(ZoomSelector.class);
    FindLargestElement lf = new FindLargestElement();
    zs.findAllEmbeds().each(lf);
    return lf.ret;
  }
}
