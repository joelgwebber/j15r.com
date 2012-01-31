package com.j15r.site.client;

import com.google.gwt.topspin.ui.client.Container;

public class ArticlePageView extends PageView {

  public ArticlePageView(Container container, Site site) {
    create(container.getDocument().createDivElement(), container);
  }

  @Override
  public void setPage(Page page) {
    ArticlePage article = page.cast();
    getElement().setInnerHTML(article.html());
  }
}
