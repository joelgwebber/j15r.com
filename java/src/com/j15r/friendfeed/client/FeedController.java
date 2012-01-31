package com.j15r.friendfeed.client;

import com.j15r.common.client.mvc.ListController;

public interface FeedController extends ListController<FeedView, Feed.Entry> {

  public interface Delegate {
    void selectEntry(FeedView view, Feed.Entry entry);
  }

  void addView(FeedView view);

  void removeView(FeedView view);
}
