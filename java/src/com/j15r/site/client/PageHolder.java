package com.j15r.site.client;

import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.layout.client.Layout;
import com.google.gwt.layout.client.Layout.Layer;
import com.google.gwt.topspin.ui.client.Widget;

import com.j15r.common.client.widgets.LayoutContainer;
import com.j15r.common.client.widgets.LayoutPanel;
import com.j15r.common.client.widgets.WrapperLayoutContainer;

// TODO(jgw): Turn this into a proper composite once we work out the
// interaction between Composite and LayoutContainer.
public class PageHolder extends LayoutPanel {

  private Widget curWidget;

  public PageHolder(LayoutContainer container) {
    super(container);

    // HACK(jgw):
    getElement().getStyle().setPosition(Position.ABSOLUTE);
  }

  public void createPageView(Page data, Site site) {
    String type = data.type();
    PageView page = null;

    if ("home".equals(type)) {
      page = new HomePageView(getContainer(), site);
    } else if ("article".equals(type)) {
      page = new ArticlePageView(getContainer(), site);
    } else {
      assert false : "Invalid data type: " + type;
    }

    page.setPage(data);
  }

  @Override
  public LayoutContainer getContainer() {
    return new WrapperLayoutContainer(super.getContainer()) {
      @Override
      public void add(Widget widget) {
        super.add(widget);

        if (curWidget != null) {
          Layer oldLayer = getLayer(curWidget);
          Layer newLayer = getLayer(widget);

          int width = getOffsetWidth();
          newLayer.setLeftWidth(width, Unit.PX, width, Unit.PX);
          doLayout();

          oldLayer.setLeftWidth(-width, Unit.PX, width, Unit.PX);
          newLayer.setLeftRight(0, Unit.PX, 0, Unit.PX);

          final Widget oldWidget = curWidget, newWidget = widget;
          animate(500, new Layout.AnimationCallback() {
            public void onAnimationComplete() {
              oldWidget.destroy();
            }

            public void onLayout(Layer layer, double progress) {
              oldWidget.getElement().getStyle().setOpacity(1.0 - progress);
              newWidget.getElement().getStyle().setOpacity(progress);
            }
          });
        } else {
          doLayout();
        }

        curWidget = widget;
      }
    };
  }
}
