package com.j15r.common.client;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

public class SliceIterable<T> implements Iterable<T> {

  private final List<T> list;
  private final int start;
  private final int length;

  public SliceIterable(List<T> list, int start, int length) {
    this.list = list;
    this.start = start;
    this.length = length;
  }

  public Iterator<T> iterator() {
    return new Iterator<T>() {
      int i = start, last = -1;

      public boolean hasNext() {
        return i < start + length;
      }

      public T next() {
        if (!hasNext()) {
          throw new NoSuchElementException();
        }
        return list.get(last = i++);
      }

      public void remove() {
        throw new UnsupportedOperationException();
      }
    };
  }
}
