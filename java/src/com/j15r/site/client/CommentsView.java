package com.j15r.site.client;

import com.google.gwt.core.client.JsArray;
import com.google.gwt.topspin.ui.client.Composite;
import com.google.gwt.topspin.ui.client.Container;
import com.google.gwt.topspin.ui.client.Panel;

public class CommentsView extends Composite {

  private Panel panel;
  private String pageId;

  public CommentsView(Container container) {
    super(container);
    panel = new Panel(getCompositeContainer());
  }

  public void setPageId(String pageId) {
    this.pageId = pageId;
  }

  public void setComments(String pageId, JsArray<Comment> comments) {
    if (pageId.equals(this.pageId)) {
      panel.clear();
      for (int i = 0; i < comments.length(); ++i) {
        new CommentView(panel.getContainer()).setComment(comments.get(i));
      }
    }
  }
}
