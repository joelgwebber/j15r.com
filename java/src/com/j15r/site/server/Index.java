package com.j15r.site.server;

import org.json.JSONException;
import org.json.JSONObject;

public class Index extends JSONObject {

  public JSONObject pageRefs() throws JSONException { return getJSONObject("pageRefs"); }
  public void setPageRefs(JSONObject pageRefs) throws JSONException { put("pageRefs", pageRefs); }
}
