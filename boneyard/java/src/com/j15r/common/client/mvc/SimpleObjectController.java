package com.j15r.common.client.mvc;

import java.util.ArrayList;
import java.util.List;

public class SimpleObjectController<V extends ObjectView<T>, T> implements
    ObjectController<V, T> {

  private final List<V> views = new ArrayList<V>();
  private T data;

  public void addView(V view) {
    views.add(view);
    if (data != null) {
      view.setData(data);
    }
  }

  public T getData() {
    return data;
  }

  public void removeView(V view) {
    views.remove(view);
  }

  public void setData(T data) {
    this.data = data;
    for (ObjectView<T> view : views) {
      view.setData(data);
    }
  }
}
