package com.j15r.friendfeed.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.dom.client.AnchorElement;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.topspin.ui.client.Container;
import com.google.gwt.topspin.ui.client.DefaultContainerImpl;
import com.google.gwt.topspin.ui.client.Widget;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;

import com.j15r.common.client.DateUtil;
import com.j15r.friendfeed.client.Feed.Comment;
import com.j15r.friendfeed.client.Feed.Entry;
import com.j15r.friendfeed.client.Feed.Like;
import com.j15r.friendfeed.client.Feed.Media;
import com.j15r.friendfeed.client.Feed.Service;
import com.j15r.friendfeed.client.Feed.Thumbnail;
import com.j15r.friendfeed.client.Feed.User;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DetailEntryView extends Widget implements EntryView {

  interface Binder extends UiBinder<Element, DetailEntryView> { }
  private static final Binder binder = GWT.create(Binder.class);

  private static final int MAX_BEFORE_OTHER_LIKES = 4;
  private static final int MAX_MEDIA_THUMBS = 4;

  private Entry entry;
  private NewCommentView newCommentView;
  private LikeCommentBox[] lcBoxes = new LikeCommentBox[2];
  private Delegate delegate;
  private final List<CommentView> commentViews = new ArrayList<CommentView>();
  private Container serviceContainer;
  private Container bottomContainer;
  private Container commentsContainer;

  @UiField ImageElement serviceIconElem;
  @UiField AnchorElement userAnchorElem, serviceAnchorElem;
  @UiField SpanElement whenElem;
  @UiField DivElement likesElem, titleElem, bottomElem, commentsElem;

  public DetailEntryView(Container container) {
    create(binder.createAndBindUi(this), container);
    setVisible(false);

    LikeCommentBox.Delegate lcDelegate = new LikeCommentBox.Delegate() {
      public void commentClicked() {
        newComment();
      }

      public void likeClicked() {
        if (delegate != null) {
          if (canLike(entry)) {
            delegate.like(DetailEntryView.this, entry);
          } else {
            delegate.unlike(DetailEntryView.this, entry);
          }
        }
      }
    };

    serviceContainer = new DefaultContainerImpl(serviceAnchorElem);
    bottomContainer = new DefaultContainerImpl(bottomElem);
    commentsContainer = new DefaultContainerImpl(commentsElem);
    lcBoxes[0] = new LikeCommentBox(serviceContainer, lcDelegate);
    lcBoxes[1] = new LikeCommentBox(bottomContainer, lcDelegate);
  }

  public Entry getData() {
    return entry;
  }

  public void setData(Entry entry) {
    if (this.entry != entry) {
      this.entry = entry;
      update();
    }
  }

  public void setDelegate(Delegate delegate) {
    this.delegate = delegate;
  }

  private boolean canLike(Entry entry) {
    return (findMe(entry.getLikes()) == -1);
  }

  private void createComments(Entry entry) {
    JsArray<Comment> comments = entry.getComments();

    int i = 0;
    for (; i < comments.length(); ++i) {
      CommentView commentView;
      if (i >= commentViews.size()) {
        commentView = new CommentView(commentsContainer);
        commentViews.add(commentView);
      } else {
        commentView = commentViews.get(i);
      }
      commentView.setData(comments.get(i));
    }

    while (i < commentViews.size()) {
      commentViews.remove(i).destroy();
    }
  }

  private int findMe(JsArray<Like> likes) {
    for (int i = 0; i < likes.length(); ++i) {
      if (isMe(likes.get(i).getUser())) {
        return i;
      }
    }
    return -1;
  }

  private String getLikesHtml(Entry entry) {
    JsArray<Like> likes = entry.getLikes();

    String html = "";
    if (likes.length() == 0) {
      return html;
    }

    if (!canLike(entry)) {
      html += "You";
      if (likes.length() > 1) {
        html += ", ";
      }
    }

    int meIndex = findMe(likes);
    int max = Math.min(likes.length(), MAX_BEFORE_OTHER_LIKES);
    int others = likes.length() - max;
    for (int i = 0; i < max; ++i) {
      if (i == meIndex) {
        continue;
      }

      User user = likes.get(i).getUser();

      String link = "<a target='_blank' href='" + user.getProfileUrl() + "'>"
          + user.getName() + "</a>";

      if (i == max - 1) {
        if (others > 0) {
          html += others + " other people";
        } else {
          html += link;
        }
      } else if (i == (max - 2)) {
        html += link + " and ";
      } else {
        html += link + ", ";
      }
    }

    return html + " liked this";
  }

  private String getTitleHtml(Entry entry) {
    String html;
    if (Services.getServiceInfo(entry.getService()).isLink) {
      html = "<a target='_blank' href='" + entry.getLink() + "'>"
          + entry.getTitle() + "</a>";
    } else {
      html = Util.linkify(entry.getTitle());
    }

    JsArray<Media> media = entry.getMedia();
    if (media.length() > 0) {
      html += "<br>";
    }

    for (int i = 0; i < Math.min(media.length(), MAX_MEDIA_THUMBS); ++i) {
      JsArray<Thumbnail> thumbs = media.get(i).getThumbnails();
      if ((thumbs != null) && (thumbs.length() > 0)) {
        Thumbnail thumb = thumbs.get(0);
        html += "<img width='" + thumb.getWidth() + "' height='"
            + thumb.getHeight() + "' src='" + thumb.getUrl() + "'>";
      }
    }

    return html;
  }

  private String getWhenText(String published) {
    Date date = DateUtil.parseRfc3339Date(published);
    return DateUtil.formatDateRelativeToNow(date);
  }

  private boolean isMe(User user) {
    return user.getNickname().equals(FriendFeed.get().getUserNickname());
  }

  private void newComment() {
    if (newCommentView != null) {
      return;
    }

    newCommentView = new NewCommentView(commentsContainer,
        new NewCommentView.Delegate() {
          public void onCancel(NewCommentView source) {
            newCommentView.destroy();
            newCommentView = null;
          }

          public void onPost(NewCommentView source) {
            if (delegate != null) {
              delegate.comment(DetailEntryView.this, entry,
                  newCommentView.getBody(), new CommentSavedCallback() {
                    public void onCommentSaved() {
                      newCommentView.destroy();
                      newCommentView = null;
                    }
                  });
            }
          }
        });

    newCommentView.focus();
  }

  private void update() {
    setVisible(this.entry != null);

    Service service = entry.getService();
    User user = entry.getUser();

    serviceIconElem.setSrc(service.getIconUrl());
    userAnchorElem.setHref(user.getProfileUrl());
    userAnchorElem.setInnerText(user.getName());
    serviceAnchorElem.setHref(service.getProfileUrl());
    serviceAnchorElem.setInnerText(service.getName());

    whenElem.setInnerText(getWhenText(entry.getPublished()));
    lcBoxes[0].setCanLike(canLike(entry));
    lcBoxes[1].setCanLike(canLike(entry));

    likesElem.getStyle().setProperty("display",
        (entry.getLikes().length() == 0) ? "none" : "");
    likesElem.setInnerHTML(getLikesHtml(entry));

    titleElem.setInnerHTML(getTitleHtml(entry));

    createComments(entry);
  }
}
