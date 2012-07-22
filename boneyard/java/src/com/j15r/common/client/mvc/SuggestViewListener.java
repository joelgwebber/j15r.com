package com.j15r.common.client.mvc;

public interface SuggestViewListener<DataType> {

  void onQueryChanged(SuggestView<DataType> view);
}
