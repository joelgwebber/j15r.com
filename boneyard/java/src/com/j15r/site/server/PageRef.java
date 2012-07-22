package com.j15r.site.server;

import org.json.JSONException;
import org.json.JSONObject;

public class PageRef extends JSONObject {

  public String title() throws JSONException { return getString("title"); }
  public String id() throws JSONException { return getString("id"); }

  public void setTitle(String title) throws JSONException { put("title", title); }
  public void setId(String id) throws JSONException { put("id", id); }
}
