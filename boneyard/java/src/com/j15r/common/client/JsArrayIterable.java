package com.j15r.common.client;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class JsArrayIterable<T extends JavaScriptObject> implements Iterable<T> {
  private final JsArray<T> array;
  private final int start, length;

  public JsArrayIterable(JsArray<T> array) {
    this(array, 0, array.length());
  }

  public JsArrayIterable(JsArray<T> array, int start, int length) {
    this.array = array;
    this.start = start;
    this.length = length;
  }

  public Iterator<T> iterator() {
    return new Iterator<T>() {
      int i = start, last = -1;

      public boolean hasNext() {
        return i < length;
      }

      public T next() {
        if (!hasNext()) {
          throw new NoSuchElementException();
        }
        return array.get(last = i++);
      }

      public void remove() {
        throw new UnsupportedOperationException();
      }
    };
  }
}
