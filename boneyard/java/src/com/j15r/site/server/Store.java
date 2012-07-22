package com.j15r.site.server;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Text;
import com.google.appengine.api.datastore.Query.SortDirection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Store {

  private static final String KIND_PAGE = "page";
  private static final String KIND_COMMENT = "comment";

  private static final String PROP_JSO = "jso";
  private static final String PROP_DATE = "date";

  private static DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

  public static JSONObject getPage(String pageId) {
    try {
      Entity entity = datastore.get(KeyFactory.createKey(KIND_PAGE, pageId));
      return new JSONObject(((Text) entity.getProperty(PROP_JSO)).getValue());
    } catch (Throwable e) {
      // TODO: log error.
      return null;
    }
  }

  public static void deletePage(String pageId) throws JSONException {
    Key key = KeyFactory.createKey(KIND_PAGE, pageId);
    datastore.delete(key);
  }

  public static void writePage(Page page) throws JSONException {
    Key key = KeyFactory.createKey(KIND_PAGE, page.id());
    Entity entity = new Entity(key);
    entity.setProperty(PROP_JSO, new Text(page.toString()));
    entity.setProperty(PROP_DATE, page.date());
    datastore.put(entity);
  }

  public static void addComment(String pageId, Comment comment) throws JSONException {
    Key pageKey = KeyFactory.createKey(KIND_PAGE, pageId);
    Key key = datastore.allocateIds(pageKey, KIND_COMMENT, 1).getStart();
    Entity entity = new Entity(key);
    entity.setProperty(PROP_JSO, new Text(comment.toString()));
    entity.setProperty(PROP_DATE, comment.date());
    datastore.put(entity);
  }

  public static JSONArray getComments(String pageId) throws JSONException {
    Key pageKey = KeyFactory.createKey(KIND_PAGE, pageId);
    Query q = new Query(KIND_COMMENT, pageKey);
    q.addSort(PROP_DATE, SortDirection.ASCENDING);
    PreparedQuery pq = datastore.prepare(q);

    JSONArray comments = new JSONArray();
    for (Entity entity : pq.asIterable()) {
      comments.put(new Comment(((Text)entity.getProperty(PROP_JSO)).getValue()));
    }

    return comments;
  }

  public static JSONArray getIndex() throws JSONException {
    Query q = new Query(KIND_PAGE);
    q.addSort(PROP_DATE, SortDirection.ASCENDING);
    PreparedQuery pq = datastore.prepare(q);

    JSONArray index = new JSONArray();
    for (Entity entity : pq.asIterable()) {
      Page page = new Page(((Text)entity.getProperty(PROP_JSO)).getValue());
      PageRef ref = new PageRef();
      ref.setId(page.id());
      ref.setTitle(page.title());
      index.put(ref);
    }

    return index;
  }
}
