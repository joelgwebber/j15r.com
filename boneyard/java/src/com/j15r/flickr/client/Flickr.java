package com.j15r.flickr.client;

import static com.google.gwt.dom.client.Style.Unit.PX;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.dom.client.TableCellElement;
import com.google.gwt.dom.client.TableElement;
import com.google.gwt.dom.client.TableRowElement;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.topspin.ui.client.Button;
import com.google.gwt.topspin.ui.client.ClickEvent;
import com.google.gwt.topspin.ui.client.ClickListener;
import com.google.gwt.topspin.ui.client.Container;
import com.google.gwt.topspin.ui.client.Div;
import com.google.gwt.topspin.ui.client.DoubleClickEvent;
import com.google.gwt.topspin.ui.client.DoubleClickListener;
import com.google.gwt.topspin.ui.client.Form;
import com.google.gwt.topspin.ui.client.InputText;
import com.google.gwt.topspin.ui.client.SubmitEvent;
import com.google.gwt.topspin.ui.client.SubmitListener;
import com.google.gwt.topspin.ui.client.Widget;
import com.google.gwt.user.client.History;

import com.j15r.common.client.mvc.ObjectView;
import com.j15r.common.client.widgets.GridScroller;
import com.j15r.common.client.widgets.LayoutPanel;
import com.j15r.common.client.widgets.LayoutRoot;
import com.j15r.common.client.widgets.ListScroller;
import com.j15r.common.client.widgets.Scroller;

public class Flickr implements EntryPoint {

  private class PhotoView extends Widget implements ObjectView<Photo> {
    private TableCellElement td;
    private int thumbWidth;
    private ImageElement img;
    private int thumbHeight;
    private Photo photo;

    protected PhotoView(Container container) {
      super(container.getDocument().createDivElement(), container);

      getElement().getStyle().setProperty("overflow", "auto");
      getElement().getStyle().setProperty("backgroundColor", "black");

      Document document = container.getDocument();
      TableElement table = document.createTableElement();
      table.getStyle().setProperty("width", "100%");
      table.getStyle().setProperty("height", "100%");
      getElement().appendChild(table);
      TableRowElement tr = table.insertRow(0);
      td = tr.insertCell(0);
      td.getStyle().setProperty("textAlign", "center");
      td.getStyle().setProperty("vAlign", "middle");

      img = document.createImageElement();
      td.appendChild(img);

      addClickListener(new ClickListener() {
        public void onClick(ClickEvent event) {
          hidePhoto();
        }
      });
    }

    public Photo getData() {
      return photo;
    }

    public void setData(Photo data) {
      this.photo = data;
      img.setSrc(data.getPhotoUrl());
    }

    public void setThumbnailSize(int width, int height) {
      this.thumbWidth = width;
      this.thumbHeight = height;
    }
  }

  private class TagView extends Div implements Scroller.ItemView<Tag> {
    private Tag tag;

    public TagView(Container container) {
      super(container);
      setWidth("100%");

      addClickListener(new ClickListener() {
        public void onClick(ClickEvent event) {
          if (tag != null) {
            searchForTag(tag.getCooked());
          }
        }
      });
    }

    public Tag getData() {
      return tag;
    }

    public void setData(Tag data) {
      this.tag = data;
      setHtml("<span style='white-space: nowrap'>" + tag.getRaw() + "</span>");
    }

    public void setLoading() {
      this.tag = null;
      setText("");
    }

    public void setSelected(boolean selected) {
      getElement().getStyle().setProperty("backgroundColor",
          selected ? "lightblue" : "");
    }
  }

  private class ThumbnailView extends Widget implements
      GridScroller.ItemView<Photo> {

    private static final int WIDTH = 256;
    private static final int HEIGHT = 256;

    private Photo photo;
    private TableCellElement td;
    private ImageElement img;

    public ThumbnailView(Container container) {
      super(container.getDocument().createTableElement(), container);
      getElement().getStyle().setProperty("border", "2px solid white");

      TableElement table = getElement().cast();
      TableRowElement tr = table.insertRow(0);
      td = tr.insertCell(0);
      td.getStyle().setProperty("textAlign", "center");
      td.getStyle().setProperty("border", "1px solid gray");

      img = container.getDocument().createImageElement();
      td.appendChild(img);

      setWidth(WIDTH);
      setHeight(HEIGHT);

      addClickListener(new ClickListener() {
        public void onClick(ClickEvent event) {
          tagsController.setPhotoId(photo.getId());
          event.preventDefault();
        }
      });

      addDoubleClickListener(new DoubleClickListener() {
        public void onDoubleClick(DoubleClickEvent event) {
          showPhoto(photo, img.getWidth(), img.getHeight());
        }
      });
    }

    public Photo getData() {
      return photo;
    }

    public void setData(Photo data) {
      if (photo == data) {
        return;
      }
      photo = data;
      img.setSrc(data.getSmallUrl());
    }

    public void setLoading() {
      photo = null;
      img.setSrc("clear.cache.gif");
      img.getStyle().setProperty("background", "");
    }

    public void setSelected(boolean selected) {
      getElement().getStyle().setProperty("border",
          "2px solid " + (selected ? "green" : "white"));
    }
  }

  private FlickrSearchController searchController;
  private FlickrTagsController tagsController;
  private InputText searchBox;
  private PhotoView photoView;
  private LayoutPanel layout;

  public void onModuleLoad() {
    layout = new LayoutPanel(LayoutRoot.getContainer());
    layout.fillWindow();

    Form form = new Form(layout.getContainer());
    form.setStyleName("flickr-form");

    searchBox = new InputText(form.getContainer());
    Button searchButton = new Button(form.getContainer());
    searchButton.setText("Flickr Search");
    searchButton.addClickListener(new ClickListener() {
      public void onClick(ClickEvent event) {
        doSearch(true);
      }
    });
    form.addSubmitListener(new SubmitListener() {
      public void onSubmit(SubmitEvent event) {
        doSearch(true);
      }
    });
    Button interestingButton = new Button(form.getContainer());
    interestingButton.setText("Interesting");
    interestingButton.addClickListener(new ClickListener() {
      public void onClick(ClickEvent event) {
        doInteresting();
      }
    });

    GridScroller<Photo> searchView = new GridScroller<Photo>(
        layout.getContainer(), ThumbnailView.WIDTH, ThumbnailView.HEIGHT) {
      @Override
      protected ItemView<Photo> doCreateView(Container container) {
        return new ThumbnailView(container);
      }
    };
    searchController = new FlickrSearchController();
    searchView.setMaxItems(500);

    ListScroller<Tag> tagsView = new ListScroller<Tag>(layout.getContainer(), 16) {
      @Override
      protected ItemView<Tag> doCreateView(Container container) {
        return new TagView(container);
      }
    };
    tagsController = new FlickrTagsController();
    tagsView.setMaxItems(20);

    searchBox.setWidth(384);

    photoView = new PhotoView(layout.getContainer());

    layout.getLayer(tagsView).setLeftWidth(0, PX, 200, PX);
    layout.getLayer(tagsView).setTopBottom(32, PX, 0, PX);

    layout.getLayer(form).setLeftRight(0, PX, 0, PX);
    layout.getLayer(form).setTopBottom(0, PX, 32, PX);

    layout.getLayer(searchView).setLeftRight(200, PX, 0, PX);
    layout.getLayer(searchView).setTopBottom(32, PX, 0, PX);

    layout.getLayer(photoView).setLeftRight(0, PX, 0, PX);
    layout.getLayer(photoView).setTopBottom(0, PX, 0, PX);
    layout.setLayerVisible(photoView, false);

    layout.animate(0);

    tagsController.addView(tagsView);
    searchController.addView(searchView);

    History.addValueChangeHandler(new ValueChangeHandler<String>() {
      public void onValueChange(ValueChangeEvent<String> event) {
        searchBox.setText(event.getValue());
        doSearch(false);
      }
    });

    String initToken = History.getToken();
    searchBox.setText(initToken);
    doSearch(false);
  }

  protected void hidePhoto() {
    layout.setLayerVisible(photoView, false);
  }

  private void doInteresting() {
    searchBox.setText("");
    doSearch(true);
  }

  private void doSearch(boolean addHistory) {
    String tagText = searchBox.getText();
    String[] tags = tagText.split(" ");
    if (tags.equals(searchController.getTags())) {
      return;
    }

    if (addHistory) {
      History.newItem(tagText, false);
    }

    if (tagText.trim().length() == 0) {
      searchController.setTags(null);
    } else {
      searchController.setTags(tags);
    }
  }

  private void searchForTag(String tag) {
    searchBox.setText(tag);
    doSearch(true);
  }

  private void showPhoto(Photo photo, int width, int height) {
    photoView.setThumbnailSize(width, height);
    photoView.setData(photo);
    layout.setLayerVisible(photoView, true);
  }
}
