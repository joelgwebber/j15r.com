package com.j15r.site.server;

import org.json.JSONException;
import org.json.JSONObject;

public class Comment extends JSONObject {

  public Comment() {
  }

  public Comment(String source) throws JSONException {
    super(source);
  }

  public String md() throws JSONException { return getString("md"); }
  public double date() throws JSONException { return getDouble("date"); }
  public double name() throws JSONException { return getDouble("name"); }
  public double email() throws JSONException { return getDouble("email"); }
  public double site() throws JSONException { return getDouble("site"); }

  public void setMd(String md) throws JSONException { put("md", md); }
  public void setDate(double date) throws JSONException { put("date", date); }
  public void setName(String name) throws JSONException { put("name", name); }
  public void setEmail(String email) throws JSONException { put("email", email); }
  public void setSite(String site) throws JSONException { put("site", site); }
}
