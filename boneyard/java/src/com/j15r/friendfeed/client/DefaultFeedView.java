package com.j15r.friendfeed.client;

import com.google.gwt.topspin.ui.client.ClickEvent;
import com.google.gwt.topspin.ui.client.ClickListener;
import com.google.gwt.topspin.ui.client.Composite;
import com.google.gwt.topspin.ui.client.Container;
import com.google.gwt.topspin.ui.client.InsertingContainerImpl;

import com.j15r.common.client.mvc.ListView;
import com.j15r.common.client.widgets.PagedListView;
import com.j15r.friendfeed.client.Feed.Entry;

public class DefaultFeedView extends Composite implements FeedView {

  private PagedListView<Entry> listView;
  private Delegate delegate;
  private ComposeDialog composeDlg;
  private ListView.Delegate<Entry> listDelegate;

  public DefaultFeedView(Container container) {
    super(container);

    listView = new PagedListView<Entry>(getCompositeContainer(), 16) {
      @Override
      protected PagedListView.ItemView<Entry> doCreateItem(Container container) {
        final SummaryEntryView view = new SummaryEntryView(container);
        view.addClickListener(new ClickListener() {
          public void onClick(ClickEvent event) {
            delegate.selectEntry(DefaultFeedView.this, view.getData());
          }
        });
        return view;
      }
    };
  }

  public void compose() {
    if ((composeDlg != null) || (listDelegate == null)) {
      return;
    }

    composeDlg = new ComposeDialog(new InsertingContainerImpl(getElement(),
        listView.getElement()));

    composeDlg.setDelegate(new ComposeDialog.Delegate() {
      public void onClose(ComposeDialog dlg) {
        composeDlg.destroy();
        composeDlg = null;
      }

      public void onPost(ComposeDialog dlg) {
        delegate.newEntry(DefaultFeedView.this, composeDlg.getTitle(),
            composeDlg.getLink(), composeDlg.getComment());
        composeDlg.destroy();
        composeDlg = null;
      }
    });
  }

  public ListView.Range getRange() {
    return listView.getRange();
  }

  public void refresh() {
    listView.refresh();
  }

  public void setData(int start, Iterable<Feed.Entry> data) {
    listView.setData(start, data);
  }

  public void setDelegate(Delegate delegate) {
    this.delegate = delegate;
  }

  public void setListDelegate(ListView.Delegate<Feed.Entry> delegate) {
    listDelegate = delegate;
    listView.setListDelegate(delegate);
  }

  public void setMaxItems(int maxItems) {
    listView.setMaxItems(maxItems);
  }

  public ListView<Entry> getListView() {
    return listView;
  }

  public void nextPage() {
    listView.nextPage();
  }

  public void prevPage() {
    listView.prevPage();
  }
}
