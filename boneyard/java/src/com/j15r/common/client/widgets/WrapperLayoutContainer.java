package com.j15r.common.client.widgets;

import com.google.gwt.dom.client.Document;
import com.google.gwt.topspin.ui.client.ContainerImpl;
import com.google.gwt.topspin.ui.client.Widget;

public class WrapperLayoutContainer implements ContainerImpl, LayoutContainer {

  private final ContainerImpl wrapee;

  /**
   * Creates a new instance that wraps an existing container.
   * 
   * @param wrapee the container to be wrapped
   */
  public WrapperLayoutContainer(LayoutContainer wrapee) {
    assert wrapee instanceof ContainerImpl;
    this.wrapee = (ContainerImpl) wrapee;
  }

  public void add(Widget widget) {
    wrapee.add(widget);
  }

  public Document getDocument() {
    return wrapee.getDocument();
  }

  public void remove(Widget widget) {
    wrapee.remove(widget);
  }

  public void requestLayout(RequiresLayout child) {
    assert wrapee instanceof LayoutContainer;
    ((WrapperLayoutContainer) wrapee).requestLayout(child);
  }
}
