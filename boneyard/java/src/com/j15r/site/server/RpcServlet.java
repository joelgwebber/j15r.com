package com.j15r.site.server;

import org.json.JSONException;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class RpcServlet extends HttpServlet {

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    try {
      if (req.getParameter("index") != null) {
        resp.getWriter().write(Store.getIndex().toString());
        return;
      } else if (req.getParameter("page") != null) {
        String pageId = req.getParameter("page");
        resp.getWriter().write(Util.retrievePage(pageId).toString());
        return;
      } else if (req.getParameter("comments") != null) {
        String pageId = req.getParameter("comments");
        resp.getWriter().write(Util.retrieveComments(pageId).toString());
        return;
      }
    } catch (JSONException e) {
      throw new ServletException(e);
    }
  }
}
