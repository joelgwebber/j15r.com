package com.j15r.friendfeed.client;

import com.j15r.common.client.mvc.ListView;

public interface FeedView extends ListView<Feed.Entry> {

  public interface Delegate {
    void newEntry(FeedView view, String title, String link, String comment);
    void selectEntry(FeedView view, Feed.Entry entry);
  }

  ListView<Feed.Entry> getListView();
  void setDelegate(Delegate delegate);
  void destroy();
}
