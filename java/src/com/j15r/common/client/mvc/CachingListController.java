package com.j15r.common.client.mvc;

import com.j15r.common.client.SliceIterable;
import com.j15r.common.client.mvc.ListView.Range;

import java.util.ArrayList;

public abstract class CachingListController<T> implements ListController<ListView<T>, T>,
    ListView.Delegate<T> {

  private static class Page<T> {
    public ArrayList<T> items;
  }

  private final ArrayList<ListView<T>> views = new ArrayList<ListView<T>>();
  private final ArrayList<Page<T>> pageCache = new ArrayList<Page<T>>();
  private final int pageSize;
  private int maxItems;

  public CachingListController(int pageSize) {
    this.pageSize = pageSize;
  }

  public void addView(ListView<T> view) {
    views.add(view);
    view.setListDelegate(this);
    rangeChanged(view);
  }

  public void clearCache() {
    pageCache.clear();
    for (ListView<T> view : views) {
      view.refresh();
    }
  }

  public boolean isRangeInCache(int start, int length) {
    int firstPage = start / pageSize;
    int lastPage = ((start + length) / pageSize);
    if (lastPage > maxItems / pageSize) {
      lastPage = maxItems / pageSize;
    }

    for (int pageIdx = firstPage; pageIdx <= lastPage; ++pageIdx) {
      if (pageIdx < pageCache.size()) {
        Page<T> page = pageCache.get(pageIdx);
        if (page == null) {
          return false;
        }
      }
    }

    return true;
  }

  public void rangeChanged(ListView<T> view) {
    Range range = view.getRange();
    requestData(range.start, range.length);
  }

  public void refresh(ListView<T> view) {
    clearCache();
  }

  public void removeView(ListView<T> view) {
    assert views.contains(view) : "TODO";

    views.remove(view);
    view.setListDelegate(null);
  }

  public void replaceItem(T oldItem, T newItem) {
    // TODO: this linear search really sucks.
    for (int pageIdx = 0; pageIdx < pageCache.size(); ++pageIdx) {
      Page<T> p = pageCache.get(pageIdx);

      for (int itemIdx = 0; itemIdx < p.items.size(); ++itemIdx) {
        if (p.items.get(itemIdx) == oldItem) {
          p.items.set(itemIdx, newItem);
          setViewDataFromCache(pageIdx);
          return;
        }
      }
    }

    assert false : "oldItem not found";
  }

  protected abstract void doRequestPage(int page);

  protected void providePage(int pageIdx, Iterable<T> data) {
    Page<T> page = pageCache.get(pageIdx);
    page.items = new ArrayList<T>();
    for (T item : data) {
      page.items.add(item);
    }
    setViewDataFromCache(pageIdx);
  }

  protected void setMaxItems(int maxItems) {
    this.maxItems = maxItems;
    for (ListView<T> view : views) {
      view.setMaxItems(maxItems);
    }
  }

  private void ensureSlot(ArrayList<?> list, int index) {
    list.ensureCapacity(index);
    int size = list.size();
    for (int i = list.size(); i < size + index + 1; ++i) {
      list.add(null);
    }
  }

  private void requestData(int start, int length) {
    int firstPage = start / pageSize;

    // +1 for an extra page of lookahead.
    int lastPage = ((start + length) / pageSize) + 1;
    if (lastPage > maxItems / pageSize) {
      lastPage = maxItems / pageSize;
    }

    for (int pageIdx = firstPage; pageIdx <= lastPage; ++pageIdx) {
      if (pageIdx < pageCache.size()) {
        Page<T> page = pageCache.get(pageIdx);
        if (page == null) {
          // No page is loading. Begin fetching it.
          beginFetching(pageIdx);
        } else {
          // The page exists, so it's either loading or fully-cached.
          if (page.items != null) {
            // It's already cached, so just send the cached version.
            setViewDataFromCache(pageIdx);
            continue;
          }
        }
      } else {
        // No slot for the page. Begin fetching it.
        beginFetching(pageIdx);
      }
    }
  }

  private void beginFetching(int pageIdx) {
    ensureSlot(pageCache, pageIdx);
    pageCache.set(pageIdx, new Page<T>());
    doRequestPage(pageIdx);
  }

  private void setViewDataFromCache(int pageIdx) {
    Page<T> page = pageCache.get(pageIdx);

    for (ListView<T> view : views) {
      ListView.Range r = view.getRange();
      int pageStart = pageIdx * pageSize;
      int start = Math.max(r.start, pageStart);
      int end = Math.min(r.start + r.length, pageStart + pageSize);
      int length = Math.min(end - start, page.items.size());
      if (length > 0) {
        view.setData(start, new SliceIterable<T>(page.items, start - pageStart,
            length));
      }
    }
  }
}
