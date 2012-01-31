package com.j15r.common.client.mvc;

public interface ObjectView<T> extends View {

  void setData(T data);

  T getData();
}
