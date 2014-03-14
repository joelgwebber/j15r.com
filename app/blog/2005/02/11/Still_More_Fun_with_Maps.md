Well, that was certainly an unexpected flood of responses. Apparently I wasn't
the only one that found Google Maps interesting &emdash; I was amazed at some
of the creative hacks that some commentors created. After sifting through this
deluge, I decided to summarize some of the findings, clarify a few points, and
add a few other comments on implementation details. This is kind of a grab bag
of points, so please bear with me.

## Cut Google some slack on Safari
I noticed a lot of people complaining both here and on Slashdot about the lack
of Safari support. And I'm not completely unsympathetic, as I'm running a shiny
new Mac Mini at home that I love. However, as anyone can attest that has ever
tried to build an even remotely complex web application, it just ain't easy.
And please don't blather on about who implements 'web standards' better
&emdash; no one really implements them, and even if they did, you'd still be
outta-luck if you wanted to do anything interesting in DHTML.

If you take some time to dig through Google's Javascript, you'll find that
there is proto-Safari support all over the place. They're clearly working on
it, and you really can't ask for much more than that.

## The route overlay
There's one really interesting facet of the route display that I totally failed
to notice the first time around &emdash; I was doing everything in Mozilla and
simply didn't notice that they were actually using Microsoft's VML to render
the route on IE. It may be non-standard, but you have to admit that it's very
fast and effective! And switching off between client-side and server-side
rendering in one code base is a pretty cool hack.

## Decoding polylines and levels
Also because I only noticed the server-side route rendering the first time
around, I failed to check whether the route's polylines were being decoded on
the client. Well, as several commentors pointed out, they are. In fact, the
decoding loop is fairly simple (just have a look at `decodePolyline()` for the
details). I originally assumed this stream was encoded so that you could just
grab it and send it back to the server for rendering (thus making the image
server stateless, and the rendered route cacheable). However, since they're
decoded on the client, it appears that it also served the purpose of keeping
the size reasonable &emdash; encoding all those points as XML would get pretty
fat.

I also glossed over the fact that there's another stream associated with the
route called 'levels'. This is an interesting trick that allowed them to encode
the route points at different zoom levels in the same stream (because there's
really no opportunity to easily go back to the server for a new route when you
change zoom levels &emdash; when you're rendering on the client).

## Flow of control for form submit and IFrame
Although it's something of a wacky implementation detail, it's interesting to
note how the search form at the top of the application works. It is actually
contained in a FORM element, although it has three separate DIVs whose
visibilities are swapped as you click on the different search links. However,
it can't be submitted directly, as that would cause the entire application to
reload. Instead, the event handler for the form's submit button suppresses the
reload, gathers the search parameters, and calls the application's `search()`
method. This method builds a query URL and sets a hidden IFrame's 'src'
parameter, which causes it to gather a new chunk of XML from the server.

As I believe I mentioned in the previous article, requesting the XML via this
IFrame has the additional benefit that it ties the browser's history perfectly
to the application's state. Although it would be nice if someone would fix
Mozilla such that the history titles are correct (this works properly in IE).

## `asynchronousTransform()`
It turns out that Google Maps communicates with the server both through the
hidden IFrame and the XMLHttpRequest object. I mentioned in the last article
that it transforms the downloaded XML using XSL.  Well, that XSL is not
actually hard-coded into the application. Instead, whenever it needs to perform
a transform, it requests the XSL via XMLHttpRequest, performs the transform,
and caches the XSL itself so that it won't need to download it again.

## Permalink and feedback
One of the potential downsides to building an application that runs entirely
within a single page is that the address bar never changes, so it's not
possible for the user to create links to the application's current state. Of
course, many 'normal' web applications don't really work properly when you try
to link to their internal pages, but people have still come to expect it to
work most of the time.

Google's solution to this problem was to create the 'link to this page' anchor
in the right panel. When you click on it, it refreshes the entire application
with a URL that encodes the entire application state.  Pretty nifty, as it
gives you the important parts of the behavior everyone likes to call 'REST'
without having to break the application up into a million little pieces.

You may have noticed that the 'feedback' link also encodes the application's
state. The map application actually updates the hrefs of both of the stateful
links every time the application state changes.

## Profiler
Often you can tell quite a bit from code that is left lying around unexecuted.
In this case, it appears that the Google Maps team may have had performance
problems in some methods (or may simply have been trying to head them off). You
can tell this because there are some prologue/epilogue functions being called
in various methods that definitely smell like a hand-rolled profiler. And if
you look at the list of methods that contain these hooks, it definitely makes
sense:

    Polyline.decodePolyline()
    Polyline.decodeLevels()
    Polyline.getVectors()
    Page.loadFromXML()
    Map.getVMLPathString()
    Map.createRawVML()
    Map.createVectorSegments()
    Map.createImageSegments()
    Map.drawDirections()

If this is indeed the case, I think it's worth noting that Mozilla's profiler
actually does a reasonably good job. And although performance on one browser is
not perfectly indicative of performance on the others, the Mozilla results have
been roughly indicative of results on IE in my experience.

## Tiles & longitude/latitude mapping
The mapping of tiles to longitude/latitude pairs is relatively straightforward.
The code for doing transformations in both directions can be found in the
functions `getBitmapCoordinate()`, `getTileCoordinate()`, and `getLatLng()`, and
several commentors have picked apart the transforms in more detail. As
mentioned before, the tiles' image source URLs encode the longitude, latitude,
and zoom level.

Several commentors also suggested that the entire map was pre-rendered at every
zoom level, so that the web server could simply deliver the tiles without
further consideration. While I believe this is partially true, I also am fairly
certain that some areas will be viewed far more often than others. Clearly,
Google is working from vector data at some level, and it would probably make
far more sense to render tiles on the fly and caching them. This would also
make a big difference when it came to dealing with updates to the vector data,
especially if those updates could be localized such that the entire tile cache
need not be invalidated.

## Crazy 'bookmarklet'
Finally, I have to give kudos to those who've been working on the 'bookmarklet'
that reaches into the running application and makes it dance. It's completely
novel to me that you can add bookmarks of the form 'javascript:' and have them
run in the context of the current page. It makes perfect sense, of course
&amp;emdash; I just never thought of it. It appears that the current state of
the art of this hack can be found [here][libgmail].

The only unfortunate part about this (through no fault of the authors') is that
it's likely to be brittle in the face of updates to Google Maps &emdash; the
Javascript has to reach into global variables within the running application,
which will probably change when Google's code obfuscator is run. It does give
us a glimpse of a possible future, however, when even web pages publish public
API's for developers to use. Very interesting!

[libgmail]: http://libgmail.sourceforge.net/googlemaps.html

