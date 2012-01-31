package com.j15r.common.client.mvc;

public interface Selectable<T> {

  void addToSelection(T item);

  void clearSelection();

  Iterable<T> getSelection();

  boolean isSelected(T item);

  void removeFromSelection(T item);
}
