package com.j15r.common.client.widgets;

import com.google.gwt.dom.client.Document;
import com.google.gwt.layout.client.Layout;
import com.google.gwt.layout.client.Layout.Layer;
import com.google.gwt.topspin.ui.client.ContainerImpl;
import com.google.gwt.topspin.ui.client.Widget;

import java.util.ArrayList;

public class LayoutPanel extends Widget implements HasLayoutContainer,
    RequiresLayout {

  private ArrayList<RequiresLayout> layoutChildren = new ArrayList<RequiresLayout>();

  private class MyContainer implements LayoutContainer, ContainerImpl {
    public void add(Widget widget) {
      widget.setLayoutData(layout.attachChild(widget.getElement()));
    }

    public Document getDocument() {
      return getElement().getOwnerDocument();
    }

    public void remove(Widget widget) {
      Object data = widget.getLayoutData();
      assert data instanceof Layer : "TODO";
      layout.removeChild((Layer) data);

      layoutChildren.remove(widget);
    }

    public void requestLayout(RequiresLayout child) {
      layoutChildren.add(child);
    }
  }

  private Layout layout;
  private LayoutContainer container;

  public LayoutPanel(LayoutContainer container) {
    super(container.getDocument().createDivElement(), container);
    layout = new Layout(getElement());
    this.container = new MyContainer();
  }

  public void animate(int duration) {
    layout.layout(duration);
  }

  public void animate(int duration, Layout.AnimationCallback callback) {
    layout.layout(duration, callback);
  }

  public void fillWindow() {
    layout.fillParent();
  }

  public LayoutContainer getContainer() {
    return container;
  }

  public Layer getLayer(Widget widget) {
    layout.assertIsChild(widget.getElement());
    return (Layer)widget.getLayoutData();
  }

  public void doLayout() {
    layout.layout();
  }

  public void setLayerVisible(Widget widget, boolean visible) {
    layout.assertIsChild(widget.getElement());
    Layer layer = (Layer)widget.getLayoutData();
    layer.getContainerElement().getStyle().setProperty("display", visible ? "" : "none");
  }
}
