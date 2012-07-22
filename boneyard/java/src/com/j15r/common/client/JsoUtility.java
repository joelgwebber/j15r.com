package com.j15r.common.client;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;

public class JsoUtility {

  public static String dump(JavaScriptObject obj) {
    return dump(obj, 0);
  }

  public static native String dump(JavaScriptObject obj, int level) /*-{
  	function indent() {
  	  var s = '';
      for (var i = 0; i < level; ++i) {
        s += '  ';
      }
      return s;
  	}

    var msg = '';
    for (var key in obj) {
      if ((obj[key] == null) || (typeof(obj[key]) != 'object')) {
        msg += indent() + key + ' : ' + obj[key] + '\n';
      } else {
        msg += indent() + key + ' : {\n' +
          @com.j15r.common.client.JsoUtility::dump(Lcom/google/gwt/core/client/JavaScriptObject;I)(obj[key], level + 1) +
          indent() + '}\n';
      }
    }
    return msg;
  }-*/;

  public static native JavaScriptObject clone(JavaScriptObject obj) /*-{
    var temp = new obj.constructor();
    for (var key in obj) {
      if ((obj[key] == null) || (typeof(obj[key]) != 'object')) {
        temp[key] = obj[key];
      } else {
        temp[key] = @com.j15r.common.client.JsoUtility::clone(Lcom/google/gwt/core/client/JavaScriptObject;)(obj[key]);
      }
    }

    return temp;
  }-*/;

  /**
   * TODO(jgw): This should go into JsArray. It should also have overloads for
   * adding new items (bonus points for getting the compiler guys to add support
   * for Java varargs to make it cleaner).
   */
  public static native void splice(JsArray<? extends JavaScriptObject> array,
      int index, int numberToRemove) /*-{
    array.splice(index, numberToRemove);
  }-*/;
}
