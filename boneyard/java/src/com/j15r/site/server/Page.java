package com.j15r.site.server;

import org.json.JSONException;
import org.json.JSONObject;

public class Page extends JSONObject {

  public Page() {
  }

  public Page(String source) throws JSONException {
    super(source);
  }

  public String id() throws JSONException { return getString("id"); }
  public String title() throws JSONException { return getString("title"); }
  public double date() throws JSONException { return getDouble("date"); }
  public String type() throws JSONException { return getString("type"); }

  public void setId(String id) throws JSONException { put("id", id); }
  public void setTitle(String title) throws JSONException { put("title", title); }
  public void setDate(double date) throws JSONException { put("date", date); }
  public void setType(String type) throws JSONException { put("type", type); }
}
