package com.j15r.friendfeed.client;

import com.j15r.common.client.mvc.ObjectController;

public interface EntryController extends ObjectController<EntryView, Feed.Entry> {

  void addView(EntryView view);

  void removeView(EntryView view);

  void setEntry(Feed.Entry entry);
}
