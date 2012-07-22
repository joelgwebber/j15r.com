package com.j15r.common.client.widgets;

import com.google.gwt.topspin.ui.client.ClickEvent;
import com.google.gwt.topspin.ui.client.ClickListener;
import com.google.gwt.topspin.ui.client.Composite;
import com.google.gwt.topspin.ui.client.Container;
import com.google.gwt.topspin.ui.client.HasText;
import com.google.gwt.topspin.ui.client.InputText;
import com.google.gwt.topspin.ui.client.KeyPressEvent;
import com.google.gwt.topspin.ui.client.KeyPressListener;
import com.google.gwt.topspin.ui.client.Panel;
import com.google.gwt.topspin.ui.client.Root;

import com.j15r.common.client.mvc.ListView;
import com.j15r.common.client.mvc.SimpleListController;
import com.j15r.common.client.mvc.SuggestView;
import com.j15r.common.client.mvc.SuggestViewListener;

public class SuggestBox<DataType> extends Composite implements
    SuggestView<DataType>, HasText {

  public interface ListViewCreator<DataType> {
    ListView<DataType> createListView(LayoutContainer container);
  }

  private class Listeners implements KeyPressListener, ClickListener {
    public void onClick(ClickEvent event) {
      // TODO
    }

    public void onKeyPress(KeyPressEvent event) {
      queryMaybeChanged();
    }
  }

  private InputText input;
  private Listeners listeners = new Listeners();
  private SuggestViewListener<DataType> listener;
  private String query;

  private Panel popup;
  private SimpleListController<DataType> listController;
  private ListView<DataType> listView;

  public SuggestBox(Container container, ListViewCreator<DataType> listCreator) {
    super(container);

    input = new InputText(getCompositeContainer());
    input.addKeyPressListener(listeners);

    popup = new Panel(Root.getContainer());
    popup.getElement().getStyle().setProperty("position", "absolute");
    popup.setVisible(false);

// TODO: Fix this. This type of list view needs a layout container.
//    listView = listCreator.createListView(popup.getContainer());
    listController = new SimpleListController<DataType>();
    listController.addView(listView);
  }

  public String getQuery() {
    return query;
  }

  public String getText() {
    return input.getText();
  }

  public void setData(Iterable<DataType> data) {
    listController.setData(data);
    popup.getElement().getStyle().setPropertyPx("left", getAbsoluteLeft());
    popup.getElement().getStyle().setPropertyPx("top",
        getAbsoluteTop() + getOffsetHeight());
    popup.setWidth(getOffsetWidth());
    popup.setVisible(true);
  }

  public void setListener(SuggestViewListener<DataType> listener) {
    this.listener = listener;
  }

  public void setText(String text) {
    input.setText(text);
  }

  private void queryMaybeChanged() {
    String newQuery = input.getText();
    if (!newQuery.equals(query)) {
      query = newQuery;
      if (listener != null) {
//        listener.onQueryChanged(this);
      }
    }
  }
}
