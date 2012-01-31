package com.j15r.common.client.mvc;

public interface ListView<T> extends View {

  public class Range {
    public int start, length;

    public Range() {
    }

    public Range(int start, int length) {
      this.start = start;
      this.length = length;
    }
  }

  public interface Delegate<DataType> {

    boolean isRangeInCache(int start, int length);

    void rangeChanged(ListView<DataType> view);

    void refresh(ListView<DataType> view);
  }

  Range getRange();

  void refresh();

  void setData(int start, Iterable<T> data);

  void setListDelegate(Delegate<T> delegate);

  void setMaxItems(int maxItems);
}
