package com.j15r.site.server;

import org.json.JSONException;

public class HomePage extends Page {

  public HomePage() throws JSONException {
    setType("home");
  }

  public HomePage(String source) throws JSONException {
    super(source);
    assert "home".equals(type()) : "Unexpected type";
  }
}
