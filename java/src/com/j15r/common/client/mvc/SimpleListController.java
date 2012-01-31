package com.j15r.common.client.mvc;

import com.j15r.common.client.mvc.ListView.Range;

import java.util.ArrayList;
import java.util.List;

public class SimpleListController<T> implements ListController<ListView<T>, T>,
    ListView.Delegate<T> {

  private List<ListView<T>> views = new ArrayList<ListView<T>>();
  private List<T> data = new ArrayList<T>();

  public void addItem(T item) {
    data.add(item);
    updateViews();
  }

  public void addView(ListView<T> view) {
    views.add(view);
    view.setListDelegate(this);
  }

  public boolean isRangeInCache(int start, int length) {
    return true;
  }

  public void rangeChanged(ListView<T> view) {
    if (data != null) {
      setViewData(view);
    }
  }

  public void refresh(ListView<T> view) {
    // TODO?
  }

  public void removeView(ListView<T> view) {
    assert views.contains(view) : "TODO";

    view.setListDelegate(null);
    views.remove(view);
  }

  public void replaceItem(T oldItem, T newItem) {
    // TODO: Gotta be a better way than linear search.
    for (int i = 0; i < data.size(); ++i) {
      if (data.get(i) == oldItem) {
        data.set(i, newItem);

        // TODO: Update only those views that actually care about the replaced
        // item.
        updateViews();
        return;
      }
    }

    assert false : "oldItem not found";
  }

  public void setData(Iterable<T> data) {
    this.data.clear();
    for (T item : data) {
      this.data.add(item);
    }

    updateViews();
  }

  private void setViewData(ListView<T> view) {
    view.setMaxItems(data.size());

    Range range = view.getRange();
    int end = Math.min(range.start + range.length, data.size());
    int length = end - range.start;

    ArrayList<T> data = new ArrayList<T>();
    for (int i = 0; i < length; ++i) {
      data.add(this.data.get(range.start + i));
    }
    view.setData(range.start, data);
  }

  private void updateViews() {
    for (ListView<T> view : views) {
      setViewData(view);
    }
  }
}
