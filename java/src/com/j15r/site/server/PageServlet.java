package com.j15r.site.server;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class PageServlet extends HttpServlet {

  private static final String PAGE_TOP =
    "<!DOCTYPE html>" +
    "<html>" +
    "<head>" +
//      "<base href='/site/'>" +  // needed to make inline selection script work
      "<title>j15r</title>" +
      "<style>" +
      "body, * { font-family: Helvetica, Arial, sans-serif; overflow:hidden; }" +
      "a { text-decoration: none; color: #666; }" +
      ".bg { background: #dfe3e6 url(/bg.jpg) no-repeat bottom right; }" +
      ".page { padding: 1em 320px 1em 1em; }" +
      ".comments { }" +
      "</style>" +
    "</head>" +
    "<body>" +
      "<iframe src='javascript:''' id='__gwt_historyFrame' tabIndex='-1' style='position:absolute;width:0;height:0;border:0'></iframe>";

  private static final String PAGE_BOTTOM =
    "</body>" +
    "</html>";

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    resp.getWriter().print(PAGE_TOP);
    writeSelectionScript(resp.getWriter());
    writeInitScript(resp.getWriter());
    resp.getWriter().print(PAGE_BOTTOM);
  }

  private void writeSelectionScript(PrintWriter writer)
      throws FileNotFoundException {
//    // TODO: stick this on the classpath or something like that.
//    File scriptFile = new File("/Users/jgw/src/j15r/war/site/site.nocache.js");
//    String script = Util.convertStreamToString(new FileInputStream(scriptFile));
//
//    writer.print("<script>");
//    writer.print(script);
//    writer.print("</script>");
    writer.print("<script type='text/javascript' language='javascript' src='site/site.nocache.js'></script>");
  }

  private void writeInitScript(PrintWriter writer) {
    writer.print("<script>");
    writer.print("var s = document.createElement('script');");

    writer.print("var pageId = location.hash;");
    writer.print("if (!pageId.length) pageId = 0;");
    writer.print("else pageId = pageId.substring(1);");
    writer.print("s.src='/init?page=' + pageId;");

    writer.print("document.body.appendChild(s);");
    writer.print("</script>");
  }
}
