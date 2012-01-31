package com.j15r.common.client.widgets;

import com.google.gwt.topspin.ui.client.Container;

public interface LayoutContainer extends Container {

  void requestLayout(RequiresLayout child);
}
