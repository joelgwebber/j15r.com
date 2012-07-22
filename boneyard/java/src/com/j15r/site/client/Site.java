package com.j15r.site.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.layout.client.Layout.Layer;
import com.google.gwt.topspin.ui.client.Anchor;
import com.google.gwt.topspin.ui.client.ClickEvent;
import com.google.gwt.topspin.ui.client.ClickListener;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;

import com.j15r.common.client.widgets.LayoutPanel;
import com.j15r.common.client.widgets.LayoutRoot;

public class Site implements EntryPoint, SiteService.Listener,
    ValueChangeHandler<String> {

  public static final String HOMEPAGE_ID = "0";

  private LayoutPanel layout;
  private IndexView indexView;
  private PageHolder pageHolder;
  private CommentsView commentsView;
  private Anchor commentsButton;

  private String curId;
  private String initId;

  private boolean commentsShowing;

  public void onModuleLoad() {
    layout = new LayoutPanel(LayoutRoot.getContainer());
    layout.fillWindow();
    layout.setStyleName("bg");

    pageHolder = new PageHolder(layout.getContainer());

    indexView = new IndexView(layout.getContainer());
    Layer layer = layout.getLayer(indexView);
    layer.setTopBottom(1, Unit.EM, 320, Unit.PX);
    layer.setRightWidth(1, Unit.EM, 300, Unit.PX);
    layer.getContainerElement().getStyle().setZIndex(2);

    commentsView = new CommentsView(layout.getContainer());
    layer = layout.getLayer(commentsView);
    layer.setBottomHeight(-20, Unit.EM, 20, Unit.EM);
    layer.setLeftRight(0, Unit.PX, 0, Unit.PX);
    commentsView.setStyleName("comments");

    commentsButton = new Anchor(layout.getContainer());
    commentsButton.setText("Comments");
    layer = layout.getLayer(commentsButton);
    layer.setRightWidth(2, Unit.EM, 10, Unit.EM);
    layer.setBottomHeight(1, Unit.EM, 1.5, Unit.EM);
    layer.getContainerElement().getStyle().setZIndex(2);
    layer.getContainerElement().getStyle().setProperty("textAlign", "right");

    commentsButton.addClickListener(new ClickListener() {
      public void onClick(ClickEvent event) {
        showComments(!commentsShowing);
      }
    });

    layout.doLayout();

    SiteService.init(this, new SiteService.Callback() {
      public void done() {
        initId = Window.Location.getParameter("page");
        if (initId == null) {
          initId = HOMEPAGE_ID;
        }

        History.addValueChangeHandler(Site.this);
        History.fireCurrentHistoryState();
      }
    });

    // TODO: remove me (should be in init data)
    SiteService.getIndex();
  }

  private void showComments(boolean show) {
    if (show) {
      Layer layer = layout.getLayer(commentsView);
      layer.setBottomHeight(0, Unit.EM, 20, Unit.EM);

      layer = layout.getLayer(pageHolder);
      layer.setTopBottom(0, Unit.EM, 20, Unit.EM);
    } else {
      Layer layer = layout.getLayer(commentsView);
      layer.setBottomHeight(-20, Unit.EM, 20, Unit.EM);

      layer = layout.getLayer(pageHolder);
      layer.setTopBottom(0, Unit.EM, 0, Unit.EM);
    }

    layout.animate(500);
    commentsShowing = show;
  }

  public void onValueChange(ValueChangeEvent<String> event) {
    String pageId = event.getValue();
    if (pageId.length() == 0) {
      pageId = initId;
    }

    showPageNow(pageId);
  }

  public void onCommentsReceived(String pageId, JsArray<Comment> comments) {
    commentsView.setComments(pageId, comments);
  }

  public void onPageReceived(Page page) {
    String id = page.id();
    if (!id.equals(curId)) {
      return;
    }

    pageHolder.createPageView(page, this);
  }

  public void onIndexReceived(JsArray<PageRef> index) {
    indexView.setIndex(index);
  }

  public void showPage(String id) {
    History.newItem(id);
  }

  private void showPageNow(String pageId) {
    curId = pageId;
    commentsView.setPageId(pageId);
    SiteService.getPage(pageId);
    SiteService.getComments(pageId);
  }
}
