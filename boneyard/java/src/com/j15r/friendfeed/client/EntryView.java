package com.j15r.friendfeed.client;

import com.j15r.common.client.mvc.ObjectView;
import com.j15r.friendfeed.client.Feed.Entry;

public interface EntryView extends ObjectView<Entry> {

  interface CommentSavedCallback {
    void onCommentSaved();
  }

  interface Delegate {
    void like(EntryView view, Entry entry);
    void unlike(EntryView view, Entry entry);
    void comment(EntryView view, Entry entry, String body, CommentSavedCallback callback);
  }

  void setDelegate(Delegate delegate);
}
