package com.j15r.friendfeed.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.topspin.desktop.client.DeckPanel;
import com.google.gwt.topspin.ui.client.Root;
import com.google.gwt.user.client.Cookies;

import com.j15r.friendfeed.client.Feed.Entry;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class FriendFeed implements EntryPoint {

  private static FriendFeed instance;

  public static FriendFeed get() {
    return instance;
  }

  private DefaultFeedController feedController;
  private DefaultEntryController entryController;
  private DeckPanel deck;
  private DefaultFeedView feedView;
  private DetailEntryView entryView;
  private String nickname;
  private String key;
  private int working;
  private ImageElement spinner;
  private LoginDialog loginDialog;
  private FriendFeedService service;

  public String getUserKey() {
    return key;
  }

  public String getUserNickname() {
    return nickname;
  }

  public void incWorking() {
    if (working == 0) {
      spinner.getStyle().setProperty("display", "");
    }
    ++working;
  }

  public void decWorking() {
    --working;
    if (working == 0) {
      spinner.getStyle().setProperty("display", "none");
    }
  }

  public void onModuleLoad() {
    instance = this;

    Header header = new Header(Root.getContainer());
    header.setDelegate(new Header.Delegate() {
      public void refresh() {
        feedView.refresh();
      }

      public void prev() {
        feedView.prevPage();
      }

      public void next() {
        feedView.nextPage();
      }

      public void logout() {
        FriendFeed.this.logout();
      }

      public void compose() {
        feedView.compose();
      }
    });

    deck = new DeckPanel(Root.getContainer());
    spinner = Document.get().getElementById("spinner").cast();

    nickname = Cookies.getCookie("ffnick");
    key = Cookies.getCookie("ffkey");

    if ((nickname == null) || (key == null)) {
      showLogin();
    } else {
      // Initialize the service with the new nick/key pair.
      service = new FriendFeedService(nickname, key);
      showFeed();
    }
  }

  private void clearCookies() {
    Cookies.removeCookie("ffnick");
    Cookies.removeCookie("ffkey");
  }

  private void writeCookies() {
    Cookies.setCookie("ffnick", nickname);
    Cookies.setCookie("ffkey", key);
  }

  private void showFeed() {
    if (feedView == null) {
      feedController = new DefaultFeedController(service,
          new DefaultFeedController.Delegate() {
            public void selectEntry(FeedView view, Entry entry) {
              showEntry(entry);
            }
          }, nickname, key);

      feedView = new DefaultFeedView(deck.getContainer());
      feedController.addView(feedView);
    }

    deck.showWidget(feedView);
  }

  private void showEntry(Entry entry) {
    if (entryView == null) {
      entryController = new DefaultEntryController(service);
      entryView = new DetailEntryView(deck.getContainer());
      entryController.addView(entryView);
    }

    entryController.setEntry(entry);
    deck.showWidget(entryView);
  }

  private void showLogin() {
    if (loginDialog == null) {
      loginDialog = new LoginDialog(deck.getContainer());

      loginDialog.setDelegate(new LoginDialog.Delegate() {
        public void loginComplete(String nickname, String key) {
          FriendFeed.this.nickname = nickname;
          FriendFeed.this.key = key;
          writeCookies();

          // Initialize the service with the new nick/key pair.
          service = new FriendFeedService(nickname, key);

          loginDialog.destroy();
          showFeed();
        }
      });
    }

    deck.showWidget(loginDialog);
  }

  public void logout() {
    nickname = key = null;
    clearCookies();

    feedView.destroy();
    showLogin();
  }
}
