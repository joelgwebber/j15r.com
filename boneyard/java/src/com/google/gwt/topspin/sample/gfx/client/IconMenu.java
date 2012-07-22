package com.google.gwt.topspin.sample.gfx.client;

import com.google.gwt.animation.client.Animation;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.topspin.gfx.client.Canvas;
import com.google.gwt.topspin.gfx.client.CanvasGradient;
import com.google.gwt.topspin.gfx.client.CanvasRenderingContext2D;
import com.google.gwt.topspin.ui.client.ClickEvent;
import com.google.gwt.topspin.ui.client.ClickListener;
import com.google.gwt.topspin.ui.client.Composite;
import com.google.gwt.topspin.ui.client.Container;
import com.google.gwt.topspin.ui.client.Div;
import com.google.gwt.topspin.ui.client.MouseOverEvent;
import com.google.gwt.topspin.ui.client.MouseOverListener;
import com.google.gwt.topspin.ui.client.Panel;
import com.google.gwt.topspin.ui.client.Widget;

public class IconMenu extends Composite {

  public interface Listener {
    void onItemSelected(IconMenu sender, int index);
  }

  private class CrossFadeAnimation extends Animation {
    private Item item0;
    private Item item1;
    private boolean isRunning;

    public void run(Item item0, Item item1) {
      this.item0 = item0;
      this.item1 = item1;
      this.run(FADE_DURATION);
    }

    @Override
    protected void onCancel() {
    }

    @Override
    protected void onComplete() {
      drawIcon(item0, item1, 1);
      isRunning = false;
    }

    @Override
    protected void onStart() {
      isRunning = true;
    }

    @Override
    protected void onUpdate(double progress) {
      drawIcon(item0, item1, (float)progress);
    }

    public boolean isRunning() {
      return isRunning;
    }
  }

  private class Item extends Widget {
    private final String name;
    private ImageElement img;

    public Item(Container container, String name, String iconUrl, final int index) {
      super(container.getDocument().createDivElement(), container);
      this.name = name;

      getElement().setInnerHTML(name);
      getElement().getStyle().setProperty("cursor", "pointer");

      addMouseOverListener(new MouseOverListener() {
        public void onMouseOver(MouseOverEvent event) {
          showItem(index);
        }
      });

      addClickListener(new ClickListener() {
        public void onClick(ClickEvent event) {
          selectItem(index);
        }
      });

      Canvas.loadImage(iconUrl, new Canvas.ImageLoadCallback() {
        public void onImageLoaded(ImageElement elem) {
          img = elem;
          if ((index == curItem) && !crossFader.isRunning()) {
            drawIcon(null, Item.this, 1);
          }
        }
      });
    }
  }

  private static final int TRANSITION_DURATION = 500;
  private static final int FADE_DURATION = 500;

  private final CrossFadeAnimation crossFader = new CrossFadeAnimation();
  private final Panel panel;
  private final Panel itemPanel;
  private final Listener listener;
  private final Item[] items;
  private final Canvas icon;
  private final Canvas reflection;
  private final Div title;

  private boolean shrunk;
  private int curItem = -1;

  public IconMenu(Container container, String[] names, String[] iconUrls, Listener listener) {
    super(container);
    this.listener = listener;

    assert names.length == iconUrls.length : "Expecting the same number of names and icons";

    panel = new Panel(getCompositeContainer());
    getElement().getStyle().setProperty("font", "24pt Arial");
    getElement().getStyle().setProperty("color", "white");
    getElement().getStyle().setProperty("position", "relative");
    setHeight(80);

    title = new Div(panel.getContainer());
    title.getElement().getStyle().setProperty("position", "absolute");
    title.getElement().getStyle().setPropertyPx("left", 512);
    title.getElement().getStyle().setPropertyPx("top", 32);
    title.setHtml("title...");
    Util.setAlpha(title.getElement(), 0);

    itemPanel = new Panel(panel.getContainer());

    icon = new Canvas(panel.getContainer());
    reflection = new Canvas(panel.getContainer());

    icon.getElement().getStyle().setProperty("position", "absolute");
    icon.getElement().getStyle().setPropertyPx("left", 0);
    icon.getElement().getStyle().setPropertyPx("top", 0);
    icon.setWidth(384);
    icon.setHeight(384);

    reflection.getElement().getStyle().setProperty("position", "absolute");
    reflection.getElement().getStyle().setPropertyPx("left", 0);
    reflection.getElement().getStyle().setPropertyPx("top", 384);
    reflection.setWidth(384);
    reflection.setHeight(192);

    items = new Item[names.length];
    for (int i = 0; i < names.length; ++i) {
      items[i] = new Item(itemPanel.getContainer(), names[i], iconUrls[i], i);
      Util.setAlpha(items[i].getElement(), 0.5);
    }

    Style ipStyle = itemPanel.getElement().getStyle();
    ipStyle.setProperty("position", "absolute");
    ipStyle.setPropertyPx("left", 512);
    ipStyle.setPropertyPx("top", 64);
  }

  public void expand() {
    if (!shrunk) {
      return;
    }
    shrunk = false;

    new ScaleAnimation(icon.getElement(), 256, -148, 0, 0, 0.2f, 1.0f).run(TRANSITION_DURATION);
    new ScaleAnimation(reflection.getElement(), 256, 0, 0, 0, 0.2f, 1.0f).run(TRANSITION_DURATION);
    new FadeAnimation(reflection.getElement(), 0.0, 1.0).run(TRANSITION_DURATION);
    new FadeAnimation(itemPanel.getElement(), 0.0, 1.0).run(TRANSITION_DURATION);
    new FadeAnimation(title.getElement(), 1.0, 0.0).run(TRANSITION_DURATION);
  }

  public void shrink() {
    if (shrunk) {
      return;
    }
    shrunk = true;

    title.setHtml(items[curItem].name);
    new ScaleAnimation(icon.getElement(), 0, 0, 256, -148, 1.0f, 0.2f).run(TRANSITION_DURATION);
    new ScaleAnimation(reflection.getElement(), 0, 0, 256, 0, 1.0f, 0.2f).run(TRANSITION_DURATION);
    new FadeAnimation(reflection.getElement(), 1.0, 0.0).run(TRANSITION_DURATION);
    new FadeAnimation(itemPanel.getElement(), 1.0, 0.0).run(TRANSITION_DURATION);
    new FadeAnimation(title.getElement(), 0.0, 1.0).run(TRANSITION_DURATION);
  }

  public void selectItem(int item) {
    if (shrunk) {
      return;
    }

    listener.onItemSelected(this, item);
  }

  public void showItem(int item) {
    if (shrunk || (curItem == item)) {
      return;
    }

    Item item0 = null, item1 = null;
    if (curItem != -1) {
      item0 = items[curItem];
      new FadeAnimation(items[curItem].getElement(), 1, 0.5).run(FADE_DURATION);
    }

    if (item != -1) {
      item1 = items[item];
      new FadeAnimation(items[item].getElement(), 0.5, 1).run(FADE_DURATION);
    }
    crossFader.run(item0, item1);

    curItem = item;
  }

  private void drawIcon(Item item0, Item item1, float alpha) {
    CanvasRenderingContext2D ctx = icon.getContext2D();
    CanvasRenderingContext2D rctx = reflection.getContext2D();

    ctx.clearRect(0, 0, icon.getWidth(), icon.getHeight());
    rctx.clearRect(0, 0, icon.getWidth(), icon.getHeight());

    if (item0 != null && item0.img != null) {
      float x = (icon.getWidth() - item0.img.getWidth()) / 2;
      float y = icon.getHeight() - item0.img.getHeight();
      ctx.setGlobalAlpha(1.0f - alpha);
      ctx.drawImage(item0.img, x, y);

      rctx.setGlobalAlpha(1.0f - alpha);
      renderReflection(rctx, item0.img, x);
    }

    if (item1 != null && item1.img != null) {
      ctx.setGlobalAlpha(alpha);
      float x = (icon.getWidth() - item1.img.getWidth()) / 2;
      float y = icon.getHeight() - item1.img.getHeight();
      ctx.drawImage(item1.img, x, y);

      rctx.setGlobalAlpha(alpha);
      renderReflection(rctx, item1.img, x);
    }
  }

  private void renderReflection(CanvasRenderingContext2D ctx, ImageElement img, float x) {
    int h = img.getHeight();
    int w = img.getWidth();

    ctx.save();
    ctx.translate(0, h / 2);
    ctx.scale(1, -1);
    ctx.drawImage(img, 0, h / 2, w, h / 2, x, 0, w, h / 2);
    ctx.restore();

    ctx.save();
    ctx.setGlobalCompositeOperation(CanvasRenderingContext2D.COMPOSITE_DESTINATION_OUT);
    CanvasGradient alphaMask = ctx.createLinearGradient(0, 0, 0, h / 2);
    alphaMask.addColorStop(0, "rgba(0, 0, 0, 0.5)");
    alphaMask.addColorStop(1, "rgba(0, 0, 0, 1.0)");
    ctx.setFillStyleGradient(alphaMask);
    ctx.fillRect(x, 0, w + 1, h / 2);
    ctx.restore();
  }
}
