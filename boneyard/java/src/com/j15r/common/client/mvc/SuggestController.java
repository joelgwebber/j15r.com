package com.j15r.common.client.mvc;

public interface SuggestController<DataType> {

  void addView(SuggestView<DataType> view);
  void removeView(SuggestView<DataType> view);
}
