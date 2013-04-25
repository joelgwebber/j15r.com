When you think of slick, fast, modern applications, what comes to mind? Web
apps, or native mobile apps? There are, of course, very polished web apps, and
terrible native mobile apps, but by and large most developers and designers
would tend to think of the latter. Hell, even the iPad "Settings" app is much
more smooth and polished than most modern web apps.

There are several reasons for this, however, and not all are fundamental to either
platform. For our purposes, let's take the following as given:

- There's no reason that native UX and static visual design should differ.
- Some kinds of apps (and especially games) can be extremely inefficient on
  the web, simply because of app code performance. Let's not consider these
  for the moment.
- For the purposes of "slickness", let us ignore problems of native hardware
  access, like cameras and such.
- Let's define "the web" as "most recent releases of Firefox, Chrome, and
  Safari, IE, and their mobile equivalents". If you care at all about old
  versions of these browsers, especially IE, there's no point in even
  discussing the comparison to modern native apps.

Taking these assumptions as given, is there any significant difference between
native and web applications? I suggest that there usually is, but it's a bit
hard to express -- most web apps just feel kind of... klunky. What does that
mean, exactly? They just don't seem to respond quickly, the UI often hangs for
no obvious reason, frame rates are unpredictable, and so forth. If you take an
acceptably-performing desktop web app and run it on a mobile device such as an
iPad, the problem is usually much worse.

But must it be this way? After all, they're running on the same hardware, most
of the rendering is being done in native code in both cases, often hardware-
accelerated. And while some apps are bound by Javascript VM performance (which,
while it has improved drastically in recent years, it still easily an [order-
of-magnitude slower][1] than C in many cases), for most apps this is not
usually the bottleneck.

No, in the cases I've carved out, which account for a huge proportion of apps,
the bottleneck is painting, painting, and more painting. Getting the right
pixels on the screen is the most intensive calculation most apps will ever
perform, and the mechanisms for doing so have changed significantly over the
last decade. Understanding why web apps often perform so poorly on this front
requires a bit of background, which I'll cover now.

## A Brief History of Pixel Pushing

To understand where we are today, we need to start by looking back at the
way GUI pixels have been pushed up till now. I'm going to start at a point in
time roughly in the mid-80s, when the modern GUI started to really take shape
on consumer devices. The architecture that we'll examine was roughly similar
on early Windows and Macintosh machines, and quite simple:

- The system has a single, memory-mapped framebuffer that drives the display.
- Applications paint into this buffer by writing pixels into the framebuffer.
- In shared windowing environments, the system "owns" the framebuffer, and
  gives apps sub-rectangles of the framebuffer to paint into when they become
  exposed.

Probably the worst-performing operation in this world was scrolling -- at a
minimum, the system was forced to copy every single pixel in the scrolling
region to a new target, which often involved two trips over the bus (remember
that video memory was usually on an external card with no processor of its
own).

Eventually we started to see "video accelerator" cards that could do some
processing locally. Most started with simple blitting operations, so that at
least scrolling wouldn't suck. But memory was *very* expensive throughout the
80s and 90s, so most operating systems were quite parsimonious when it came to
handing out regions of video memory for buffering of images and partially-
rendered output. Games would, of course, take every ounce of available video
memory and use the card's blitter heavily, but by and large applications stuck
to the "render pixels by hand whenever the window's exposed" model.

### Compositors

By the time the Macintosh got a reboot with OS X, things had changed
drastically in the hardware world. Memory became an order of magnitude or more
cheaper, and video hardware had gotten a lot smarter. CPUs and buses, however,
hadn't seen anywhere near the same performance improvement. The result of this
shift is that it started to make sense to cache rendered output for each
window in video memory. OS X was designed to take advantage of this shift,
allocating dedicated video memory for each window, and having the video card
do the work of "compositing" the windows together. This had the nice side-
effect of making it easy to add effects like transparency, translucency,
shadows, and scaling effects. Other operating systems eventually followed
suit, and today this is the norm in both desktop and mobile systems.

Eventually, the compositor migrated from the window manager to application
code, giving apps the ability to take advantage of hardware compositing. The
trick is that they have to organize their rendering code in a particular way,
which we'll talk about next.

### Layers and Animation

Which brings us to today. Modern operating systems typically provide a
construct such as a "layer" (this term is used by CoreAnimation on OS X, and
in WebKit internals). Each component is contained within exactly one layer,
and in the default, degenerate case, all components are in a single layer. The
point of the layer is to group components together that will be composited
together. Each layer gets a single, fixed-size pixel buffer in video memory,
and is known to the system's compositor. The advantage of this is that each
layer can have certain operations performed on it *extremely* efficiently.
Typically, these would be:

- Translation
- Scale
- Rotation,
- Alpha (translucency)
- Shadow

Note that resizing and layout are *not* among these operations. This is
because it would require repainting the pixel buffer (to be precise, some
systems such as OS X's CoreAnimation allow for some simple layout to be
performed by the compositor, but restricted such that the buffers don't have
to be repainted). But if you stick to these strictures, you can usually get
very smooth 60 f/s animations by simply poking at layer parameters.

## Pushing Pixels on The Web

I promised this post would actually be about web browsers, so here we are. Now
before we get into layers on the web, we need to briefly cover how painting
works in most web browsers. There's a lot to this subject, which I'll cover
only briefly, leaving the details to other articles.

For all their differences, web rendering doesn't differ *that* much from
traditional UI toolkits. The main difference is that web pages start with the
DOM tree (essentially comprised of HTML elements), which must be laid out to
create the "render" tree. Unlike traditional UI toolkits, application code
never paints pixels directly (with the exception of `<canvas>`, but even
that element behaves more like a custom image than anything else). It's the
render tree that actually drives painting to the screen, and this process is
very much analogous to that in other UI toolkits. For a great (and
extraordinarily detailed) explanation of this process, see Tali Garsiel's [How
Browsers Work][2] (especially the [Render-Tree Construction][3] section).

### Layers on the Web

If you read through the entirety of the _How Browsers Work_ article I linked
above, you'll find almost no mention of layers. But they're there, in one form
or another, in all modern browsers. In the WebKit internals, they're referred
to as `RenderLayer`s, and each node in the render tree is associated with
precisely one of them. Normally, they're used as an internal construct for the
browser to optimize certain paint operations. For example, if you place a
`position:fixed` `<div>` over the main scrollable area of a page, that element
and its children will likely end up in a separate layer, so that scrolling
remains smooth.

I believe the *intent* of this design was that the browser would always do the
"right thing", choosing to create layers where appropriate, but never exposing
this functionality directly to developers. But there are a few cases where
developers need to be aware of the existence of layers in order to get the
best possible performance. And indeed it turns out that there are certain CSS
properties that will cause an element to get its own layer in WebKit. Examples
include `-webkit-transform: translateZ(0)` (specifically any 3D transform) and
`alpha: 0.5` (any value other than 0 or 1). In practice the set of heuristics
used to determine whether any element gets its own layer is evolving, and can
change without notice, but the properties I just mentioned are pretty
reliable.

So why would you *want* to explicitly force an element to have its own layer?
Let's explore this by way of example. I've created a very simple mockup
version of my favorite iPad RSS applications, [Reeder][4]. It's a fairly
straightforward iPad app, visually notable for its nice, smooth transitions.
...

layers_slow_st.png

[1]: http://j15r.com/blog/2011/12/15/Box2D_as_a_Measure_of_Runtime_Performance
[2]: http://www.html5rocks.com/en/tutorials/internals/howbrowserswork/
[3]: http://www.html5rocks.com/en/tutorials/internals/howbrowserswork/#Render_tree_construction
[4]: http://reederapp.com/ipad/

https://developers.google.com/web-toolkit/speedtracer/
http://addyosmani.com/blog/devtools-visually-re-engineering-css-for-faster-paint-times/
http://kellegous.com/j/2013/01/26/layout-performance/
http://aerotwist.com/blog/on-translate3d-and-layer-creation-hacks/
http://updates.html5rocks.com/2013/02/Profiling-Long-Paint-Times-with-DevTools-Continuous-Painting-Mode
https://plus.google.com/u/0/115133653231679625609/posts/gv92WXBBkgU
