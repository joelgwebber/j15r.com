package com.j15r.friendfeed.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import sun.misc.BASE64Encoder;

public class AuthServlet extends HttpServlet {

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    boolean post = req.getParameter("_post") != null;
    String user = req.getParameter("_user");
    String token = req.getParameter("_token");
    String remoteUrl = req.getParameter("_url");

    String encodedParams = "";
    Map<?, ?> paramMap = req.getParameterMap();
    for (Object _key : paramMap.keySet()) {
      String key = (String) _key;
      if (!key.startsWith("_")) {
        String[] values = (String[]) paramMap.get(key);
        for (String value : values) {
          encodedParams += URLEncoder.encode(key, "UTF8") + "="
              + URLEncoder.encode(value, "UTF8") + "&";
        }
      }
    }

    URLConnection conn = null;
    if (post) {
      conn = new URL(remoteUrl).openConnection();
      conn.setDoOutput(true);
      auth(user, token, conn);

      OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream());
      out.write(encodedParams);
      out.close();

      conn.connect();
    } else {
      conn = new URL(remoteUrl + "?" + encodedParams).openConnection();
      auth(user, token, conn);
      conn.connect();
    }

    streamOut(conn.getInputStream(), resp.getOutputStream(), 4096);
  }

  private void auth(String user, String pass, URLConnection conn) {
    String userPass = user + ":" + pass;
    String encoding = new BASE64Encoder().encode(userPass.getBytes());
    conn.setRequestProperty("Authorization", "Basic " + encoding);
  }

  private void streamOut(InputStream in, OutputStream out, int bufferSize)
      throws IOException {
    assert (bufferSize >= 0);

    byte[] buffer = new byte[bufferSize];
    int bytesRead = 0;
    while (true) {
      bytesRead = in.read(buffer);
      if (bytesRead >= 0) {
        // Copy the bytes out.
        out.write(buffer, 0, bytesRead);
      } else {
        // End of input stream.
        return;
      }
    }
  }
}
