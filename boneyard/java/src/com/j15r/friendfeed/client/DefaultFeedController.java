package com.j15r.friendfeed.client;

import com.google.gwt.topspin.ui.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

import com.j15r.common.client.JsArrayIterable;
import com.j15r.common.client.mvc.CachingListController;
import com.j15r.friendfeed.client.Feed.Entry;

public class DefaultFeedController implements FeedController {

  private class FeedListController extends CachingListController<Feed.Entry> {
    public FeedListController() {
      super(PAGE_SIZE);
    }

    @Override
    protected void doRequestPage(final int page) {
      FriendFeed.get().incWorking();

      service.getHomeFeed(page * PAGE_SIZE, PAGE_SIZE,
          new AsyncCallback<Feed>() {

            public void onSuccess(Feed response) {
              FriendFeed.get().decWorking();

              providePage(page, new JsArrayIterable<Feed.Entry>(response
                  .getEntries()));

              // TODO: how do we find this?
              setMaxItems(64);
            }

            public void onFailure(Throwable e) {
              FriendFeed.get().decWorking();
              Window.alert("Failed to get home feed");
              // FriendFeed.get().logout();
            }
          });
    }
  }

  private static final int PAGE_SIZE = 8;

  private final FeedListController listController = new FeedListController();
  private final FriendFeedService service;

  private final FeedView.Delegate feedDelegate = new FeedView.Delegate() {
    public void newEntry(FeedView view, String title, String link,
        String comment) {
      FriendFeed.get().incWorking();

      service.share(title, link, comment, new AsyncCallback<Feed>() {
        public void onSuccess(Feed result) {
          FriendFeed.get().decWorking();

          // The 'share' api response returns a feed with a single new entry,
          // but we're not going to use it because it's a PITA to insert it in
          // the caching list controller.
          Window.alert("Post complete");
        }

        public void onFailure(Throwable e) {
          FriendFeed.get().decWorking();
          Window.alert("something's rotten in denmark");
        }
      });
    }

    public void selectEntry(FeedView view, Entry entry) {
      delegate.selectEntry(view, entry);
    }
  };

  private final Delegate delegate;

  public DefaultFeedController(FriendFeedService service, Delegate delegate,
      String nickname, String key) {
    this.delegate = delegate;
    this.service = service;
  }

  public void addView(FeedView view) {
    listController.addView(view.getListView());
    view.setDelegate(feedDelegate);
  }

  public void removeView(FeedView view) {
    listController.removeView(view.getListView());
    view.setDelegate(null);
  }
}
