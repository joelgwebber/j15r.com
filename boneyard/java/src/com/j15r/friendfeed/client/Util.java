package com.j15r.friendfeed.client;

import com.j15r.common.client.MatchResult;
import com.j15r.common.client.RegExp;

public class Util {

  private static final int MAX_VISIBLE_URL_LENGTH = 32;
  private static final RegExp HTTP_REGEX = RegExp.create(
      "(http|https|ftp|email):\\/\\/[\\S]+", "g");

  /**
   * Returns an HTML string with apparent links turned into anchors.
   * 
   * TODO: The regex that does this is broken. It grabs extra characters (like
   * parens) at the end of the URL that it shouldn't.
   */
  public static String linkify(String text) {
    StringBuilder sb = new StringBuilder();

    int last = 0;
    MatchResult m;
    while ((m = HTTP_REGEX.exec(text)) != null) {
      String url = m.group(0);
      String displayUrl = (url.length() < MAX_VISIBLE_URL_LENGTH) ? url
          : url.substring(0, MAX_VISIBLE_URL_LENGTH) + "...";

      sb.append(text.substring(last, m.index()));
      sb.append("<a target='_blank' href='" + url + "'>" + displayUrl + "</a>");
      last = m.index() + url.length();
    }
    sb.append(text.substring(last));

    return sb.toString();
  }
}
