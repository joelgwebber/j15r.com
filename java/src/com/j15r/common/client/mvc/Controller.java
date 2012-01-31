package com.j15r.common.client.mvc;

public interface Controller<V extends View> {

  void addView(V view);

  void removeView(V view);
}
