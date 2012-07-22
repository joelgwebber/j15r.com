package com.j15r.site.server;

import com.j15r.common.client.DateUtil;
import com.petebevin.markdown.MarkdownProcessor;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;

public class Util {

  private static MarkdownProcessor mdproc = new MarkdownProcessor();

  public static JSONObject retrievePage(String pageId) throws JSONException {
    JSONObject jso = Store.getPage(pageId);
    mdToHtml(jso);
    return jso;
  }

  public static JSONArray retrieveComments(String pageId) throws JSONException {
    JSONArray comments = Store.getComments(pageId);
    for (int i = 0; i < comments.length(); ++i) {
      JSONObject comment = comments.getJSONObject(i);
      mdToHtml(comment);
      dateToRfc3339(comment);
    }
    return comments;
  }

  public static void mdToHtml(JSONObject jso) throws JSONException {
    // Convert the markdown source to html, removing the source.
    if (jso.has("md")) {
      String md = jso.getString("md");
      jso.put("html", mdproc.markdown(md));
      jso.remove("md");
    }
  }

  public static void dateToRfc3339(JSONObject jso) throws JSONException {
    if (jso.has("date")) {
      Date date = new Date(jso.getLong("date"));
      jso.put("date", DateUtil.emitRfc3339Date(date));
    }
  }

  public static String readStream(String path) {
    return convertStreamToString(TestDataServlet.class.getClassLoader().getResourceAsStream(
        path));
  }

  public static String convertStreamToString(InputStream is) {
    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
    StringBuilder sb = new StringBuilder();

    String line = null;
    try {
      while ((line = reader.readLine()) != null) {
        sb.append(line + "\n");
      }
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      try {
        is.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    return sb.toString();
  }
}
