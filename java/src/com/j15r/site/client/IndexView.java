package com.j15r.site.client;

import com.google.gwt.animation.client.Animation;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.topspin.ui.client.Composite;
import com.google.gwt.topspin.ui.client.Container;
import com.google.gwt.topspin.ui.client.MouseOutEvent;
import com.google.gwt.topspin.ui.client.MouseOutListener;
import com.google.gwt.topspin.ui.client.MouseOverEvent;
import com.google.gwt.topspin.ui.client.MouseOverListener;
import com.google.gwt.topspin.ui.client.Panel;

public class IndexView extends Composite {

  private abstract class OverOutAnimation extends Animation {
    private static final int TIME = 500;

    protected double range = 0.75f;
    protected double startProgress, curProgress = 1.0;

    protected void onUpdate(double progress) {
      this.curProgress = progress;
      setOpacity(progress);
    }

    public void run(double start) {
      this.startProgress = start;
      setOpacity(0);
      super.run((int) (TIME * (1.0 - start)));
    }

    @Override
    protected void onCancel() {
    }

    protected void setOpacity(double progress) {
      Style style = getElement().getStyle();
      style.setOpacity(0.25 + ((startProgress + progress * (1.0 - startProgress)) * 0.75));
    }
  }

  private class OverAnimation extends OverOutAnimation {
    protected void setOpacity(double progress) {
      Style style = getElement().getStyle();
      style.setOpacity((1.0 - range) + ((startProgress + progress * (1.0 - startProgress)) * range));
    }
  }

  private class OutAnimation extends OverOutAnimation {
    protected void setOpacity(double progress) {
      Style style = getElement().getStyle();
      style.setOpacity(1.0 -           ((startProgress + progress * (1.0 - startProgress)) * range));
    }
  }

  private OverAnimation overAnim = new OverAnimation();
  private OutAnimation outAnim = new OutAnimation();
  private Panel panel;

  public IndexView(Container container) {
    super(container);
    panel = new Panel(getCompositeContainer());

    addMouseOverListener(new MouseOverListener() {
      public void onMouseOver(MouseOverEvent event) {
        Element relatedTarget = event.getNativeEvent().getRelatedTarget();
        if ((relatedTarget != null) && !getElement().isOrHasChild(relatedTarget)) {
          overAnim.run(1.0 - outAnim.curProgress);
          outAnim.cancel();
        }
      }
    });

    addMouseOutListener(new MouseOutListener() {
      public void onMouseOut(MouseOutEvent event) {
        Element relatedTarget = event.getNativeEvent().getRelatedTarget();
        if ((relatedTarget != null) && !getElement().isOrHasChild(relatedTarget)) {
          outAnim.run(1.0 - overAnim.curProgress);
          overAnim.cancel();
        }
      }
    });

    getElement().getStyle().setOpacity(0.25);
  }

  public void setIndex(JsArray<PageRef> index) {
    panel.clear();

    for (int i = 0; i < index.length(); ++i) {
      IndexItemView itemView = new IndexItemView(panel.getContainer());
      itemView.setPageRef(index.get(i));
    }
  }
}
