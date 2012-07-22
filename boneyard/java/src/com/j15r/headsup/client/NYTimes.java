package com.j15r.headsup.client;

import static com.google.gwt.query.client.GQuery.$;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.query.client.Function;
import com.google.gwt.query.client.GQuery;
import com.google.gwt.user.client.Window;

public class NYTimes implements EntryPoint {

  private Function fHide = new Function() {
    @Override
    public void f(Element e) {
      e.getStyle().setDisplay(Display.NONE);
    }
  };

  private Function fAddToBody = new Function() {
    @Override
    public void f(Element e) {
      $("body").append(e);
    }
  };

  private GQuery container;
  private GQuery paras;
  private GQuery imageBox;
  private GQuery image;

  public void onModuleLoad() {
    imageBox = $("#wideImage");
    paragraphCss(imageBox);
    imageBox.css("width", "195%");
    image = $("#wideImage img");
    image.css("width", "100%");

    GQuery nyt_text = $("NYT_TEXT").remove();
    paras = paragraphCss(nyt_text.children("p").clone());

    $("body").children().each(fHide);

    container = $(Document.get().createDivElement())
      .css("position", "absolute")
      .css("left", "0")
      .css("top", "0")
      .css("right", "0")
      .css("bottom", "0")
    ;
    $("body").append(container);

    Window.addResizeHandler(new ResizeHandler() {
      public void onResize(ResizeEvent event) {
        NYTimes.this.onResize();
      }
    });
    onResize();
  }

  private void onResize() {
    container.html("");

    paras.each(new Function() {
      int row, col;
      Element div = createColumn(0, 0);

      @Override
      public void f(Element protop) {
        Element p = (Element) protop.cloneNode(true);
        div.appendChild(p);

        if (p.getOffsetTop() + p.getOffsetHeight() > div.getOffsetHeight()) {
          Element newp = splitParagraph(p, div.getOffsetHeight());
          paragraphCss($(newp));
          nextColumn();
          div.appendChild(newp);
        }
      }

      private Element createColumn(int row, int col) {
        Element div = Document.get().createDivElement();
        container.append($(div)
          .css("position", "absolute")
          .css("textAlign", "justify")
          .css("border-bottom", "1px solid lightgrey")
          .css("width", "25%")
          .css("top", (row * 100) + "%")
          .css("left", (col * 25) + "%")
          .css("bottom", (row * -100) + "%")
        );
        return div;
      }

      private void nextColumn() {
        ++col;
        if (col == 4) {
          col = 0;
          ++row;
        }
        div = createColumn(row, col);

        if ((imageBox != null) && (row == 0)) {
          if (col == 1) {
            $(div).append(imageBox);
          }
          if (col == 2) {
            $(div).append(
                imageBox.clone().css("visibility", "hidden").css("width",
                    "200%"));
          }
        }
      }
    });
  }

  private GQuery paragraphCss(GQuery paras) {
    return paras
      .css("margin-left", "0.5em")
      .css("margin-right", "0.5em")
      .css("font-size", "15px")
      .css("font-family", "serif")
    ;
  }

  private Element splitParagraph(Element p, int maxHeight) {
    String text = p.getInnerText();
    int splitPoint = text.length();

    int top = p.getOffsetTop();
    while ((splitPoint > 0) && (top + p.getOffsetHeight() > maxHeight)) {
      splitPoint = text.lastIndexOf(' ', splitPoint - 1);
      if (splitPoint == -1) {
        splitPoint = 0;
      }
      p.setInnerText(text.substring(0, splitPoint));
    }

    Document doc = p.getOwnerDocument();
    Element newp = doc.createElement("p");
    newp.setInnerText(text.substring(splitPoint));

    return newp;
  }
}
