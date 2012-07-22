Panels in GWT are much like their layout counterparts in other user interface
libraries. The main difference is that GWT panels use HTML elements to lay out
their child widgets.

Panels contain widgets and other panels. They are used to define the layout of
the user interface in the browser.


# Basic Panels

## [RootPanel]
A [RootPanel] is the top-most panel to which all other widgets are ultimately
attached. [RootPanel.get()] gets a singleton panel that wraps the HTML document's
`<body>` element. Use [RootPanel.get(String id)] to get a panel for any other
element on the page.

## [FlowPanel]
A [FlowPanel] is the simplest panel. It creates a single `<div>` element and
attaches children directly to it without modification. Use it in cases where
you want the natural HTML flow to determine the layout of child widgets.

## [HTMLPanel]
This panel provides a simple way to define an HTML structure, within which
widgets will be embedded at defined points. While you may use it directly, it
is most commonly used in [UiBinder templates][uibinder].

## [FormPanel]
When you need to reproduce the behavior of an HTML form (e.g., for interacting
with servers that expect form POST requests, or simply to get the default form
keyboard behavior in the browser), you can use a [FormPanel]. Any widgets wrapped
by this panel will be wrapped in a `<form>` element.

## [ScrollPanel]
When you wish to create a scrollable area within another panel, you should use
a [ScrollPanel]. This panel works well in layout panels (see below), which
provide it with the explicit size it needs to scroll properly.

## [PopupPanel] and [DialogBox]
Use these two panels to create simple popups and modal dialogs. They overlap
existing content in browser window, and can be stacked over one-another.

## [Grid] and [FlexTable]
These two widgets are used to create traditional HTML `<table>` elements, and can
also be used as panels, in that arbitrary widgets may be added to their cells.


# Layout Panels
GWT 2.0 introduces a number of new panels, which together form a stable basis
for fast and predictable application-level layout. For background and details,
see <a href='#design'>"Design of the GWT 2.0 layout system"</a> below.

The bulk of the layout system is embodied in a series of panel widgets. Each
of these widgets uses the underlying layout system to position its children
in a dependable manner.

## [RootLayoutPanel]
This panel is a singleton that serves as a root container to which all other
layout panels should be attached (see RequiresResize and ProvidesResize <a
href='#RequiresAndProvidesResize'>below</a> for details). It extends
[LayoutPanel], and can thus you can position any number of children with
arbitrary constraints.

You most commonly use [RootLayoutPanel] as a container for another panel, as in
the following snippet, which causes a [DockLayoutPanel] to fill the browser's
client area:

    DockLayoutPanel appPanel = new DockLayoutPanel(Unit.EM);
    RootLayoutPanel.get().add(appPanel);

## [LayoutPanel]
Think of [LayoutPanel] as the most general layout mechanism, and often one upon
which other layouts are built. Its closest analog is [AbsolutePanel], but it is
significantly more general in that it allows its children to be positioned
using arbitrary constraints, as in the following example:

    Widget child0, child1, child2;
    LayoutPanel p = new LayoutPanel();
    p.add(child0); p.add(child1); p.add(child2);

    p.setWidgetLeftWidth(child0, 0, PCT, 50, PCT);  // Left panel
    p.setWidgetRightWidth(child1, 0, PCT, 50, PCT); // Right panel

    p.setWidgetLeftRight(child2, 5, EM, 5, EM);     // Center panel
    p.setWidgetTopBottom(child2, 5, EM, 5, EM);

![LayoutPanel example](images/LayoutPanel.png)

## [DockLayoutPanel]
[DockLayoutPanel] serves the same purpose as the existing [DockPanel] widget,
except that it uses the layout system to achieve this structure without using
tables, and in a predictable manner. You would often use to build
application-level structure, as in the following example:

    DockLayoutPanel p = new DockLayoutPanel(Unit.EM);
    p.addNorth(new HTML("header"), 2);
    p.addSouth(new HTML("footer"), 2);
    p.addWest(new HTML("navigation"), 10);
    p.add(new HTML(content));

![DockLayoutPanel example](images/DockLayoutPanel.png)

Note that [DockLayoutPanel] requires the use of consistent units for all
children, specified in the constructor. It also requires that the size of each
child widget (except the last, which consumes all remaining space) be specified
explicitly along its primary axis.

## SplitLayoutPanel
The [SplitLayoutPanel] is identical to the [DockLayoutPanel] (and indeed
extends it), except that it automatically creates a user-draggable splitter
between each pair of child widgets. It also supports only the use of pixel
units. Use this instead of [HorizontalSplitPanel] and [VerticalSplitPanel].

    SplitLayoutPanel p = new SplitLayoutPanel();
    p.addWest(new HTML("navigation"), 128);
    p.addNorth(new HTML("list"), 384);
    p.add(new HTML("details"));

![SplitLayoutPanel example](images/SplitLayoutPanel.png)

## StackLayoutPanel
[StackLayoutPanel] replaces the existing [StackPanel] (which does not work very
well in standards mode). It displays one child widget at a time, each of which is
associated with a single "header" widget. Clicking on a header widget shows its
associated child widget.

    StackLayoutPanel p = new StackLayoutPanel(Unit.EM);
    p.add(new HTML("this content"), new HTML("this"), 4);
    p.add(new HTML("that content"), new HTML("that"), 4);
    p.add(new HTML("the other content"), new HTML("the other"), 4);

![StackLayoutPanel example](images/StackLayoutPanel.png)

Note that, as with [DockLayoutPanel], only a single unit type may be used on a
given panel. The length value provided to the [add()][DockLayoutPanel.add()]
method specifies the size of the header widget, which must be of a fixed size.

## TabLayoutPanel
As with the existing [TabPanel], [TabLayoutPanel] displays a row of clickable
tabs. Each tab is associated with another child widget, which is shown when a
user clicks on the tab.

    TabLayoutPanel p = new TabLayoutPanel(1.5, Unit.EM);
    p.add(new HTML("this content"), "this");
    p.add(new HTML("that content"), "that");
    p.add(new HTML("the other content"), "the other");
    
![TabLayoutPanel example](images/TabLayoutPanel.png)

The length value provided to the [TabLayoutPanel] constructor specifies the
height of the tab bar, which you must explicitly provide.

## When should I *not* use layout panels?
The panels described above are best used for defining your application's outer
structure &mdash; that is, the parts that are the least "document-like". You
should continue to use basic widgets and HTML structure for those parts for
which the HTML/CSS layout algorithm works well. In particular, consider using
[UiBinder templates][uibinder] to directly use HTML wherever that makes sense.


# Animation
The GWT 2.0 layout system has direct, built-in support for animation. This is
necessary to support a number of use-cases, because the layout system must
properly handle animation among sets of layout constraints.

Panels that implement [AnimatedLayout], such as [LayoutPanel],
[DockLayoutPanel], and [SplitLayoutPanel], can animate their child widgets from
one set of constraints to another. Typically this is done by setting up the
state towards which you wish to animate, then calling
[animate()][AnimatedLayout.animate(int)]. See <a href='#Recipes'>"Recipes"</a>
below for specific examples.


# RequiresResize and ProvidesResize
Two new characteristic interfaces were introduced in GWT 2.0: [RequiresResize]
and [ProvidesResize]. These are used to propagate notification of resize events
throughout the widget hierarchy.

[RequiresResize] provides a single method,
[onResize()][RequiresResize.onResize()], which is called by the widget's parent
whenever the child's size has changed. [ProvidesResize] is simply a tag
interface indicating that a parent widget will honor this contract. The purpose
of these two interfaces is to form an unbroken hierarchy between all widgets
that implement RequiresResize and the [RootLayoutPanel], which listens for any
changes (such as the browser window resizing) that could affect the size of
widgets in the hierarchy.

## When to use [onResize()][RequiresResize.onResize()]
Most widgets should *not* need to know when they've been resized, as the
browser's layout engine should be doing most of the work. However, there are
times when a widget *does* need to know. This comes up, for example, when a
widget contains a dynamic list of items depending upon how much room is
available to display them. Because it's almost always faster to let the layout
engine do its work than to run code, you should not lean upon
[onResize()][RequiresResize.onResize()] unless you have no alternative.

## When and how to implement ProvidesResize
A panel that implements ProvidesResize is expected to propagate resize events
to any of its child widgets that implement RequiresResize. For a canonical
example of this, see the implementation of [LayoutPanel.onResize()]. Most
custom widgets will want to composite an existing layout panel using
[ResizeComposite], however, as described next.

## ResizeComposite
When creating a custom [Composite] widget that wrap a widget that implements
[RequiresResize], you should use [ResizeComposite] as its base class. This
subclass of [Composite] automatically propagates resize events to its wrapped
widget.


# Moving to Standards Mode
The GWT 2.0 layout system is intended to work only in "standards mode". This
means that you should always place the following declaration at the top of your
HTML pages:
    `<!DOCTYPE html>`

## What won't work in standards mode?
As mentioned above, some of the existing GWT panels do not behave entirely as
expected in standards mode. This stems primarily from differences between the
way standards and quirks modes render tables.

### CellPanel (HorizontalPanel, VerticalPanel, DockPanel)
These panels all use table cells as their basic structural units. While they
still work in standards mode, they will lay out their children somewhat
differently. The main difference is that their children will not respect width
and height properties (it is common to set children of CellPanels explicitly to
100% width and height). There are also differences in the way that the browser
allocates space to individual table rows and columns that can lead to
unexpected behavior in standards mode.

You should use [DockLayoutPanel] in place of [DockPanel]. [VerticalPanel] can
usually be replaced by a simple [FlowPanel] (since block-level elements will
naturally stack up vertically).

[HorizontalPanel] is a bit trickier. In some cases, you can simply replace it
with a [DockLayoutPanel], but that requires that you specify its childrens'
widths explicitly. The most common alternative is to use [FlowPanel], and to
use the `float: left;` CSS property on its children. And of course, you can
continue to use [HorizontalPanel] itself, as long as you take the caveats above
into account.

### StackPanel
StackPanels do not work very well in standards mode. Because of the differences
in table rendering mentioned above, StackPanel will almost certainly not do
what you expect in standards mode, and you should replace them with
[StackLayoutPanel].

### SplitPanel (HorizontalSplitPanel and VerticalSplitPanel)
SplitPanels are very unpredictable in standards mode, and you should almost
invariably replace them with [SplitLayoutPanel].


# Design of the GWT 2.0 layout system
Prior to 2.0, GWT's mechanisms for handling application-level layout have a
number of significant problems:

* They're unpredictable.
* They often require extra code to fix up their deficiencies:
  * For example, causing an application to fill the browser's client area with
  interior scrolling is nearly impossible without extra code.
* They don't all work well in standards mode.

Their underlying motivation was sound &mdash; the intention was to let the
browser's native layout engine do almost all of the work. But the above
deficiencies can be crippling.

## Goals
* Perfectly predictable layout behavior. Precision layout should be possible.
  * It should also work in the presence of CSS decorations (border, margin, and
  padding) with arbitrary units.
* Work correctly in standards-mode.
* Get the browser to do almost all of the work in its layout engine.
  * Manual adjustments should occur only when strictly necessary.
* Smooth, automatic animation.

## Non-Goals
* Work in quirks-mode.
* Swing-style layout based on "preferred size". This is effectively
intractable in the browser.
* Take over all layout. This design is intended to handle coarse-grained
"desktop-like" layout. The individual bits and pieces, such as form elements,
buttons, tables, and text should still be laid out naturally.

## The Layout class
The [Layout] class contains all the underlying logic used by the layout system,
along with all the implementation details used to normalize layout behavior on
various browsers.

It is actually widget-agnostic, operating directly on DOM elements. Most
applications will have no reason to work directly with this class, but it should
prove useful to alternate widget library writers.

## Constraint-based Layout
The GWT 2.0 layout system is built upon the simple constraint system that
exists natively in CSS. This uses four properties: `left`, `top`, `width`,
`height`, `right`, and `bottom`. While most developers are familiar with these
properties, it is less well-known that they can be combined in various ways to
form a simple constraint system. Take the following CSS example:

    .parent {
      position: relative; /* to establish positioning context */
    }

    .child {
      position: absolute; left:1em; top:1em; right:1em; bottom:1em;
    }

In this example, the child will automatically consume the parent's entire
space, minus 1em of space around the edge. Any two of these properties (on each
axis) forms a valid constraint pair (three would be degenerate), producing lots
of interesting possibilities. This is especially true when you consider various
mixtures of relative units, such as "em" and "%".


# Recipes
  TODO: I'll come back and build all of these once the main doc is done.

## Basic application layout

## Splitters

## Implementing a [Composite] that [RequiresResize]

## Overlapping children

## Sliding windows

## Showing and hiding a footer with animation

## Using a [LayoutPanel] somewhere other than at the root

## Using a [LayoutPanel] in a [DialogBox] or [PopupPanel]

## Fake document-level scrolling


[uibinder]: DevGuideUiBinder.html

[AbsolutePanel]: http://google-web-toolkit.googlecode.com/svn/javadoc/2.0/com/google/gwt/user/client/ui/AbsolutePanel.html
[RootPanel]: http://google-web-toolkit.googlecode.com/svn/javadoc/2.0/com/google/gwt/user/client/ui/RootPanel.html
[RootPanel.get()]: http://google-web-toolkit.googlecode.com/svn/javadoc/2.0/com/google/gwt/user/client/ui/RootPanel.html#get()
[RootPanel.get(String id)]: http://google-web-toolkit.googlecode.com/svn/javadoc/2.0/com/google/gwt/user/client/ui/RootPanel.html#get(java.lang.String)
[DockPanel]: http://google-web-toolkit.googlecode.com/svn/javadoc/2.0/com/google/gwt/user/client/ui/DockPanel.html
[FlowPanel]: http://google-web-toolkit.googlecode.com/svn/javadoc/2.0/com/google/gwt/user/client/ui/FlowPanel.html
[FlowPanel]: http://google-web-toolkit.googlecode.com/svn/javadoc/2.0/com/google/gwt/user/client/ui/FlowPanel.html
[HTMLPanel]: http://google-web-toolkit.googlecode.com/svn/javadoc/2.0/com/google/gwt/user/client/ui/HTMLPanel.html
[FormPanel]: http://google-web-toolkit.googlecode.com/svn/javadoc/2.0/com/google/gwt/user/client/ui/FormPanel.html
[ScrollPanel]: http://google-web-toolkit.googlecode.com/svn/javadoc/2.0/com/google/gwt/user/client/ui/ScrollPanel.html
[PopupPanel]: http://google-web-toolkit.googlecode.com/svn/javadoc/2.0/com/google/gwt/user/client/ui/PopupPanel.html
[DialogBox]: http://google-web-toolkit.googlecode.com/svn/javadoc/2.0/com/google/gwt/user/client/ui/DialogBox.html
[Grid]: http://google-web-toolkit.googlecode.com/svn/javadoc/2.0/com/google/gwt/user/client/ui/Grid.html
[FlexTable]: http://google-web-toolkit.googlecode.com/svn/javadoc/2.0/com/google/gwt/user/client/ui/FlexTable.html
[SplitPanel]: http://google-web-toolkit.googlecode.com/svn/javadoc/2.0/com/google/gwt/user/client/ui/SplitPanel.html
[HorizontalSplitPanel]: http://google-web-toolkit.googlecode.com/svn/javadoc/2.0/com/google/gwt/user/client/ui/HorizontalSplitPanel.html
[VerticalSplitPanel]: http://google-web-toolkit.googlecode.com/svn/javadoc/2.0/com/google/gwt/user/client/ui/VerticalSplitPanel.html
[StackPanel]: http://google-web-toolkit.googlecode.com/svn/javadoc/2.0/com/google/gwt/user/client/ui/StackPanel.html
[TabPanel]: http://google-web-toolkit.googlecode.com/svn/javadoc/2.0/com/google/gwt/user/client/ui/TabPanel.html

[RequiresResize]: http://google-web-toolkit.googlecode.com/svn/javadoc/2.0/com/google/gwt/user/client/ui/RequiresResize.html
[RequiresResize.onResize()]: http://google-web-toolkit.googlecode.com/svn/javadoc/2.0/com/google/gwt/user/client/ui/RequiresResize.html#onResize()
[ProvidesResize]: http://google-web-toolkit.googlecode.com/svn/javadoc/2.0/com/google/gwt/user/client/ui/ProvidesResize.html
[AnimatedLayout]: http://google-web-toolkit.googlecode.com/svn/javadoc/2.0/com/google/gwt/user/client/ui/AnimatedLayout.html
[AnimatedLayout.animate(int)]: http://google-web-toolkit.googlecode.com/svn/javadoc/2.0/com/google/gwt/user/client/ui/AnimatedLayout.html#animate(int)

[Composite]: http://google-web-toolkit.googlecode.com/svn/javadoc/2.0/com/google/gwt/user/client/ui/Composite.html
[ResizeComposite]: http://google-web-toolkit.googlecode.com/svn/javadoc/2.0/com/google/gwt/user/client/ui/ResizeComposite.html

[LayoutPanel]: http://google-web-toolkit.googlecode.com/svn/javadoc/2.0/com/google/gwt/user/client/ui/LayoutPanel.html
[LayoutPanel.onResize()]: http://google-web-toolkit.googlecode.com/svn/javadoc/2.0/com/google/gwt/user/client/ui/LayoutPanel.html#onResize()
[RootLayoutPanel]: http://google-web-toolkit.googlecode.com/svn/javadoc/2.0/com/google/gwt/user/client/ui/RootLayoutPanel.html
[DockLayoutPanel]: http://google-web-toolkit.googlecode.com/svn/javadoc/2.0/com/google/gwt/user/client/ui/DockLayoutPanel.html
[DockLayoutPanel.add()]: http://google-web-toolkit.googlecode.com/svn/javadoc/2.0/com/google/gwt/user/client/ui/DockLayoutPanel.html#add(com.google.gwt.user.client.ui.Widget)
[SplitLayoutPanel]: http://google-web-toolkit.googlecode.com/svn/javadoc/2.0/com/google/gwt/user/client/ui/SplitLayoutPanel.html
[StackLayoutPanel]: http://google-web-toolkit.googlecode.com/svn/javadoc/2.0/com/google/gwt/user/client/ui/StackLayoutPanel.html
[TabLayoutPanel]: http://google-web-toolkit.googlecode.com/svn/javadoc/2.0/com/google/gwt/user/client/ui/TabLayoutPanel.html

[Layout]: http://google-web-toolkit.googlecode.com/svn/javadoc/2.0/com/google/gwt/layout/client/Layout.html
