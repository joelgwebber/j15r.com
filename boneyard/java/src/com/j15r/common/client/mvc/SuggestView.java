package com.j15r.common.client.mvc;

public interface SuggestView<DataType> {

  String getQuery();
  void setListener(SuggestViewListener<DataType> listener);
  void setData(Iterable<DataType> data);
}
