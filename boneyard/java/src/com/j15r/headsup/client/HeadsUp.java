package com.j15r.headsup.client;

import static com.google.gwt.query.client.GQuery.$;
import static com.google.gwt.query.client.GQuery.$$;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.query.client.Effects;
import com.google.gwt.query.client.Function;
import com.google.gwt.query.client.GQuery;
import com.google.gwt.user.client.Event;

public class HeadsUp implements EntryPoint {

  private static final String BOOKMARK_READABILITY =
    "(function(){readStyle='style-newspaper';readSize='size-large';readMargin='margin-wide';" +
    "_readability_script=document.createElement('SCRIPT');_readability_script.type='text/javascript';" +
    "_readability_script.src='http://lab.arc90.com/experiments/readability/js/readability.js?x='+(Math.random());" +
    "document.getElementsByTagName('head')[0].appendChild(_readability_script);" +
    "_readability_css=document.createElement('LINK');_readability_css.rel='stylesheet';" +
    "_readability_css.href='http://lab.arc90.com/experiments/readability/css/readability.css';" +
    "_readability_css.type='text/css';document.getElementsByTagName('head')[0].appendChild(_readability_css);" +
    "_readability_print_css=document.createElement('LINK');_readability_print_css.rel='stylesheet';" +
    "_readability_print_css.href='http://lab.arc90.com/experiments/readability/css/readability-print.css';" +
    "_readability_print_css.media='print';_readability_print_css.type='text/css';" +
    "document.getElementsByTagName('head')[0].appendChild(_readability_print_css);})();";

  private GQuery popup;

  public void onModuleLoad() {
    $("body").append(createPopup());
  }

  private GQuery createPopup() {
    popup = $("<div/>")
      .css("position", "fixed")
      .css("backgroundColor", "#888")
      .css("opacity", "0.75")
      .css("left", "0")
      .css("right", "0")
      .css("top", "0")
      .css("height", "32px");

    popup.append(createItem("Readability", BOOKMARK_READABILITY));

    popup.append(createItem("ZoomTube", new Function() {
      @Override
      public boolean f(Event e) {
        close();
        zoomTube();
        return true;
      }
    }));

    return popup.css("top", "-32px")
        .as(Effects.Effects)
        .animate($$("top: '+=32'"), 400, Effects.Easing.SWING, null);
  }

  private void close() {
    popup.fadeOut().remove();
  }

  private GQuery createItem(String name, final String js) {
    return createItem(name, new Function() {
      @Override
      public boolean f(Event e) {
        close();
        eval(js);
        return true;
      }
    });
  }

  private GQuery createItem(String name, Function f) {
    return $("<button/>").css("margin", "4px").text(name).click(f);
  }

  private void zoomTube() {
    new ZoomTube().onModuleLoad();
  }

  private native void eval(String js) /*-{
    $wnd.eval(js);
  }-*/;
}
