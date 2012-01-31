package com.j15r.site.server;

import org.json.JSONException;

import java.io.IOException;
import java.util.Date;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class TestDataServlet extends HttpServlet {

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {

    try {
      // 0: homepage
      HomePage page = new HomePage();
      page.setId("0");
      page.setTitle("Home");
      page.setDate(0);
      Store.writePage(page);

      putEntry("1", "The insanity of HTTP compression", "com/j15r/site/server/00_http_compression.txt");
      putEntry("2", "DHTML Leaks Like a Sieve", "com/j15r/site/server/00_dhtml_leaks.txt");
      putEntry("3", "Mapping Google", "com/j15r/site/server/00_mapping_google.txt");
      putEntry("4", "Still more fun with maps",  "com/j15r/site/server/00_more_fun_with_maps.txt");
      putEntry("5", "Making the back button dance", "com/j15r/site/server/00_back_button_dance.txt");
      putEntry("6", "Ajax Buzz", "com/j15r/site/server/00_ajax_buzz.txt");
      putEntry("7", "More maps", "com/j15r/site/server/00_more_maps.txt");
      putEntry("8", "Drip: IE leak detector", "com/j15r/site/server/00_drip.txt");
      putEntry("9", "Drip Redux", "com/j15r/site/server/00_drip_redux.txt");
      putEntry("10", "Another word or two on memory leaks", "com/j15r/site/server/00_another_word_on_leaks.txt");
      putEntry("11", "IE's memory leak fix greatly exaggerated", "com/j15r/site/server/00_memory_leaks_exaggerated.txt");
      putEntry("12", "GWT, Javascript, and the correct level of abstraction", "com/j15r/site/server/0_resig_gwt.txt");
      putEntry("13", "Memory leaks in IE8", "com/j15r/site/server/1_ie8_still_leaks.txt");
      putEntry("14", "Where should I define Javascript variables?", "com/j15r/site/server/3_varSpeed.txt");
      putEntry("15", "Javascript variables, continued", "com/j15r/site/server/4_varSpeedContinued.txt");

      addComment("1", "Test comment");
      addComment("1", "Test comment response");
    } catch (JSONException e) {
      throw new ServletException(e);
    }
  }

  private void addComment(String pageId, String md) throws JSONException {
    Comment comment = new Comment();
    comment.setMd(md);
    comment.setDate(new Date().getTime());
    comment.setName("Some Guy");
    comment.setEmail("some@guy.com");
    comment.setSite("http://someguy.blogspot.com");
    Store.addComment(pageId, comment);
  }

  private void putEntry(String id, String title, String resource) throws IOException, JSONException {
    String md = Util.readStream(resource);

    ArticlePage page = new ArticlePage();
    page.setId(id);
    page.setTitle(title);
    page.setDate(new Date().getTime());
    page.setMarkdown(md);

    Store.writePage(page);
  }
}
