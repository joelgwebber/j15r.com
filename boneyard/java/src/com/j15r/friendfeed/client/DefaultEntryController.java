package com.j15r.friendfeed.client;

import com.google.gwt.core.client.JsArray;
import com.google.gwt.topspin.ui.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

import com.j15r.common.client.JsoUtility;
import com.j15r.friendfeed.client.EntryView.CommentSavedCallback;
import com.j15r.friendfeed.client.Feed.Comment;
import com.j15r.friendfeed.client.Feed.Entry;
import com.j15r.friendfeed.client.Feed.Like;
import com.j15r.friendfeed.client.Feed.User;

import java.util.ArrayList;
import java.util.List;

public class DefaultEntryController implements EntryController, EntryView.Delegate {

  private final List<EntryView> views = new ArrayList<EntryView>();
  private final FriendFeedService service;

  public DefaultEntryController(FriendFeedService service) {
    this.service = service;
  }

  public void addView(EntryView view) {
    views.add(view);
    view.setDelegate(this);
  }

  public void comment(EntryView view, final Entry entry, String body,
      final CommentSavedCallback callback) {
    FriendFeed.get().incWorking();

    service.comment(entry.getId(), body, null, new AsyncCallback<Comment>() {
      public void onSuccess(Comment newComment) {
        FriendFeed.get().decWorking();

        Entry newEntry = (Entry) JsoUtility.clone(entry);
        newEntry.getComments().set(entry.getComments().length(), newComment);

        // TODO: How should we update this?
        // listController.replaceItem(entry, newEntry);

        callback.onCommentSaved();
      }

      public void onFailure(Throwable e) {
        FriendFeed.get().decWorking();
        Window.alert("something's rotten in denmark");
      }
    });
  }

  public void like(final EntryView view, final Entry entry) {
    FriendFeed.get().incWorking();

    service.like(entry.getId(), new AsyncCallback<SimpleResponse>() {
      public void onSuccess(SimpleResponse response) {
        if (response.success()) {
          FriendFeed.get().decWorking();

          // TODO: It seems kind of yucky to create a half-assed Like object,
          // but we don't have enough information to do better.
          Entry newEntry = (Entry) JsoUtility.clone(entry);

          Like like = Like.create("", User.create("empty", "empty",
              FriendFeed.get().getUserNickname(), "empty"));
          newEntry.getLikes().set(entry.getLikes().length(), like);

          // TODO: How should we update this?
          // listController.replaceItem(entry, newEntry);
        } else {
          Window.alert(response.errorCode());
        }
      }

      public void onFailure(Throwable e) {
        FriendFeed.get().decWorking();
        Window.alert("something's rotten in denmark");
      }
    });
  }

  public void removeView(EntryView view) {
    views.remove(view);
    view.setDelegate(null);
  }

  public void unlike(final EntryView view, final Entry entry) {
    FriendFeed.get().incWorking();

    service.unlike(entry.getId(), new AsyncCallback<SimpleResponse>() {
      public void onSuccess(SimpleResponse response) {
        if (response.success()) {
          FriendFeed.get().decWorking();

          Entry newEntry = (Entry) JsoUtility.clone(entry);
          JsArray<Like> likes = newEntry.getLikes();
          int toRemove = -1;
          for (int i = 0; i < likes.length(); ++i) {
            Like like = likes.get(i);
            if (like.getUser().getNickname().equals(
                FriendFeed.get().getUserNickname())) {
              toRemove = i;
              break;
            }
          }

          JsoUtility.splice(likes, toRemove, 1);

          // TODO: How should we update this?
          // listController.replaceItem(entry, newEntry);
        } else {
          Window.alert(response.errorCode());
        }
      }

      public void onFailure(Throwable e) {
        FriendFeed.get().decWorking();
        Window.alert("something's rotten in denmark");
      }
    });
  }

  public void setEntry(Entry entry) {
    for (EntryView view : views) {
      view.setData(entry);
    }
  }
}
