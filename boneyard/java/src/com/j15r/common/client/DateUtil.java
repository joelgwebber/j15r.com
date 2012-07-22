package com.j15r.common.client;

import java.util.Date;

public class DateUtil {

  private static final int MINUTE_IN_MS = 60 * 1000;
  private static final int HOUR_IN_MS = 60 * MINUTE_IN_MS;
  private static final int DAY_IN_MS = 24 * HOUR_IN_MS;
  private static final int WEEK_IN_MS = 7 * DAY_IN_MS;

  private static final String[] months = new String[] {
      "January", "February", "March", "April", "May", "June", "July", "August",
      "September", "October", "November", "December"};
  private static final String[] days = new String[] {
      "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday",
      "Saturday"};

  public static String emitRfc3339Date(Date date) {
    // Assumes UTC.
    return
      (date.getYear() + 1900) + "-" +
      (date.getMonth() + 1) + "-" +
      (date.getDate()) + "T" +
      (date.getHours()) + ":" +
      (date.getMinutes()) + ":" +
      (date.getSeconds()) + "Z";
  }

  public static Date parseRfc3339Date(String rfcDate) {
    // Assumes UTC.
    String[] split = rfcDate.split("T");
    String[] ymd = split[0].split("-");
    String[] hms = split[1].split(":");
    hms[2] = hms[2].substring(0, hms[2].length() - 1);

    int year = Integer.parseInt(ymd[0]);
    int month = Integer.parseInt(ymd[1]);
    int day = Integer.parseInt(ymd[2]);
    int hours = Integer.parseInt(hms[0]);
    int minutes = Integer.parseInt(hms[1]);
    int seconds = Integer.parseInt(hms[2]);

    Date date = new Date(year - 1900, month - 1, day, hours, minutes, seconds);
    date.setMinutes(date.getMinutes() - date.getTimezoneOffset());
    return date;
  }

  public static String formatDateRelativeToNow(Date date) {
    Date now = new Date();
    long relTime = now.getTime() - date.getTime();
    if (relTime < WEEK_IN_MS) {
      if (relTime < 2 * DAY_IN_MS) {
        if (relTime < DAY_IN_MS) {
          if (relTime < HOUR_IN_MS) {
            int minutes = (int) (relTime / MINUTE_IN_MS);
            return minutes + ((minutes == 1) ? " minute ago" : " minutes ago");
          }
          int hours = (int) (relTime / HOUR_IN_MS);
          return hours + ((hours == 1) ? " hour ago" : " hours ago");
        }
        return "Yesterday at " + formatTime(date);
      }
      return days[date.getDay()] + " at " + formatTime(date);
    }

    return months[date.getMonth()] + " " + date.getDate() + " at " + formatTime(date);
  }

  public static String formatTime(Date date) {
    if (date.getHours() < 12) {
      return date.getHours() + " am";
    }
    return (date.getHours() - 12) + " pm";
  }
}
