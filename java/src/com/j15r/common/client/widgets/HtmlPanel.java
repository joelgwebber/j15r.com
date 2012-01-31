package com.j15r.common.client.widgets;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.topspin.ui.client.Container;
import com.google.gwt.topspin.ui.client.ContainerImpl;
import com.google.gwt.topspin.ui.client.Widget;

public class HtmlPanel extends Widget {

  private static class ReplacingContainerImpl implements ContainerImpl {
    private Element parent;
    private Element elementToReplace;

    public ReplacingContainerImpl(Element elementToReplace) {
      this.elementToReplace = elementToReplace;
      parent = elementToReplace.getParentElement();
    }

    public void add(Widget widget) {
      assert (elementToReplace != null) : "TODO";

      parent.insertBefore(widget.getElement(), elementToReplace);
      parent.removeChild(elementToReplace);

      elementToReplace = null;
    }

    public void remove(Widget widget) {
      Element element = widget.getElement();
      assert parent == element.getParentElement() : "TODO";
      parent.removeChild(element);
    }

    public Document getDocument() {
      return elementToReplace.getOwnerDocument();
    }
  }

  public HtmlPanel(Container container, String html) {
    super(container.getDocument().createDivElement(), container);
    getElement().setInnerHTML(html);
  }

// TODO: getElementsByClassName may not be the best idea here.
//
//  public Container getContainer(String className) {
//    return new DefaultContainerImpl(getElementByClassName(className));
//  }
//
//  public Container getReplacingContainer(String classNameToReplace) {
//    NodeList<Element> children = getElement().getElementsByClassName(classNameToReplace);
//    assert children.getLength() == 1 : "TODO";
//
//    return new ReplacingContainerImpl(children.getItem(0));
//  }
//
//  public Element getElementByClassName(String className) {
//    NodeList<Element> children = getElement().getElementsByClassName(className);
//    assert children.getLength() == 1 : "TODO";
//
//    return children.getItem(0);
//  }
//
//  public NodeList<Element> getElementsByClassName(String className) {
//    return getElement().getElementsByClassName(className);
//  }
}
