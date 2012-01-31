package com.j15r.common.client.widgets;

import com.google.gwt.dom.client.Document;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.topspin.ui.client.DefaultContainerImpl;
import com.google.gwt.topspin.ui.client.Widget;
import com.google.gwt.user.client.Window;

import java.util.ArrayList;

public class LayoutRoot {

  private static class LayoutContainerImpl extends DefaultContainerImpl
      implements LayoutContainer {
    private ArrayList<RequiresLayout> children = new ArrayList<RequiresLayout>();
    private final Document document;

    public LayoutContainerImpl(Document document) {
      super(document.getBody());

      this.document = document;
      Window.addResizeHandler(new ResizeHandler() {
        public void onResize(ResizeEvent event) {
          for (RequiresLayout child : children) {
            child.doLayout();
          }
        }
      });
    }

    @Override
    public void add(Widget widget) {
      super.add(widget);
      if (widget instanceof RequiresLayout) {
        children.add((RequiresLayout) widget);
      }
    }

    @Override
    public void remove(Widget widget) {
      super.remove(widget);
      if (widget instanceof RequiresLayout) {
        children.remove((RequiresLayout) widget);
      }
    }

    public void requestLayout(RequiresLayout child) {
      children.add(child);
    }

    public Document getDocument() {
      return document;
    }
  }

  // TODO(jgw): This is incorrect. If you use more than one document, this
  // will get clobbered.
  private static LayoutContainer bodyContainer;

  /**
   * Gets the default container, which will attach widgets to the default
   * document's body.
   * 
   * @return the container
   */
  public static LayoutContainer getContainer() {
    return getContainer(Document.get());
  }

  /**
   * Gets a container representing the body of the specified {@link Document}.
   * 
   * @param document the document to whose body widgets will be attached
   * @return the container
   */
  public static LayoutContainer getContainer(Document document) {
    if (bodyContainer == null) {
      bodyContainer = new LayoutContainerImpl(document);
    }
    return bodyContainer;
  }

  private LayoutRoot() {
  }
}
