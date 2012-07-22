package com.j15r.site.server;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class InitDataServlet extends HttpServlet {

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    try {
      JSONObject jso = Util.retrievePage(req.getParameter("page"));
      PrintWriter out = resp.getWriter();
      out.write("var __init = [");
      out.write(jso.toString());
      out.write("];");
      out.write("if (window['__initCallback']) { __initCallback(); }");
    } catch (JSONException e) {
      throw new ServletException(e);
    }
  }
}
