package com.j15r.site.server;

import org.json.JSONException;

public class ArticlePage extends Page {

  public ArticlePage() throws JSONException {
    setType("article");
  }

  public ArticlePage(String source) throws JSONException {
    super(source);
    assert "article".equals(type()) : "Unexpected type";
  }

  public String html() throws JSONException { return getString("html"); }
  public String md() throws JSONException { return getString("md"); }

  public void setHtml(String html) throws JSONException { put("html", html); }
  public void setMarkdown(String md) throws JSONException { put("md", md); }
}
