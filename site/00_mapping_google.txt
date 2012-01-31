By now, many of you will have gone and tried out the new Google Maps
application. By and large, you have to admit that it's pretty damned slick for
a DHTML web application -- even my wife was impressed, and that's not easy with
geek toys. So, in the spirit of Google Suggest and GMail, I've decided to have
a quick peek under the hood to figure out what makes it tick.

## Not quite like GMail
The first thing I noticed is that it doesn't quite work like GMail. Whereas
GMail uses XMLHttp to make calls back to the server, Google Maps uses a hidden
IFrame. Each method has its benefits, as I'll discuss below, but this
difference of approach does seem to imply that it may not be the same team
doing the work.

## The Graphics
Probably the most striking thing about Google Maps is the very impressive (for
DHTML, anyway) graphics. Now, I'm sure that many of you old JavaScript hacks
out there have known this sort of thing was possible for a long time, but it's
very cool to see it (a) actually being used for something real, and (b) where
normal users will see it.

For those to whom the implementation is less than obvious, here's a quick
breakdown. The top and side bars are (more or less) simply HTML. The center
pane with the map, however, is a different beast. First, let's address the map
itself. It is broken up into a grid of 128x128 images (basically like an old
tile-based scrolling console game). The dragging code is nothing new, but the
cool trick here is that each of these images is absolutely positioned -- and
the 'infinite' scrolling effect is achieved by picking up tiles that are
off-screen on one end and placing them down on the other end. The effect is
kind of like laying track for a train by picking up track from behind it.

<center>
  <img src="http://photos1.blogger.com/x/blogger/7678/661/320/219085/Tiles.png"/>

  *Google map, with tiles outlined*
</center>

The push-pins and info-popups are a different matter. Simply placing them is no
big trick; an absolutely-positioned transparent GIF does the trick nicely. The
shadows, however, are a different matter. They are PNGs with 8-bit alpha
channels. Personally, I didn't even realize you could depend upon the browser
to render these correctly, but apparently (at least with IE6 and Mozilla), you
can. And they actually render pretty quickly -- for proof, check out the
overlaid route image (at the end of the article), which is often as big as the
entire map view.

<center>
  <img src="http://photos1.blogger.com/x/blogger/7678/661/320/239629/Pin.gif"/>

  *The pushpin, with its two images outlined*
</center>

## Communicating with the Server
There are two ways in which Google Maps has to communicate with the server. The
first is to get map images, and the second is to get search results. It turns
out that getting map images is remarkably easy -- all you have to do is set an
image tile's URL. Because the coordinate system is known and fixed (each tile
represents a known area specified in longitude and latitude, at a given zoom
level), the client has all the information it needs to set tile URLs. Each tile
URL is of the following form:

    http://mt.google.com/mt?v=.1&x={x tile index}&{y tile index}=2&zoom={zoom level}

I'm not sure what the 'v' argument specifies, but it never seems to change. The
others are fairly self-explanatory. One nice side effect of this is that the
images have fixed URLs for a given chunk of the earth's surface, so they get
cached. If you're doing most of your searches in one region, then the app can
be quite snappy once everything gets cached.

Doing searches is another matter. Clearly, you can't 'submit' the entire page,
because that would destroy your map and other context. Google's solution is to
submit a hidden IFrame, then gather the search results from it. Let's say, for
example, that you simply wanted to go to Atlanta. You type 'Atlanta' in the
search area, and the following HTTP GET is made:

    http://maps.google.com/maps?q=atlanta&z=13&sll=37.062500%2C-95.677068&sspn=37.062500%2C80.511950&output=js

There are a couple of things to notice here. The 'question' is passed in the
'q' parameter (much like Google). The other arguments are 'z' for zoom, 'sll'
for longitude & latitude (your current focus, I believe), and 'sspn' to specify
the span/size of your viewing area. What's interesting is what comes back:

    <!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
    <html xmlns="http://www.w3.org/1999/xhtml">
      <head>
        <meta http-equiv="content-type" content="text/html; charset=UTF-8"/>
        <title>Google Maps - atlanta</title>
        <script type="text/javascript">
        //<![CDATA[
        function load() {
          if (window.parent && window.parent._load) {
            window.parent._load({big chunk of XML}, window.document);
          }
        }
        //]]>
        </script>
      </head>
      <body onload="load()">
        <input id="zoom" type="text" value=""/>
        <input id="centerlat" type="text" value=""/>
        <input id="centerlng" type="text" value=""/>
      </body>
    </html>

This HTML is loaded into the hidden IFrame which, when loaded, will punt a big
chunk of XML back up to the outer frame's _load() function. This is kind of a
cool trick, because it saves the outer frame from having to determine when the
IFrame is done loading.

I mentioned before that there was some advantage to be had by using a hidden
IFrame over making direct XMLHttp requests. One of these is that the IFrame's
state affects the back button. So every time you do a search, it creates a new
history entry. This creates an excellent user experience, because pressing the
back button always takes you back to the last major action you performed (and
the forward button works just as well).

## Big Hunks of XML
Ok, so now the outer frame's code has a big chunk of XMl. What can it do with
that? Well, it turns out that Google Maps depends upon two built-in browser
components: XMLHttpRequest and XSLTProcessor. Oddly enough, even though it
doesn't use XMLHttpRequest for making calls to the server, it _does_ use it for
parsing XML. I'll get to the XSLT later.

Here's an example of the XML response that comes back from the 'Atlanta'
request above:

    <?xml version="1.0"?>
    <page>
      <title>atlanta</title>
      <query>atlanta</query>
      <center lat="33.748889" lng="-84.388056"/>
      <span lat="0.089988" lng="0.108228"/>
      <overlay panelStyle="/mapfiles/geocodepanel.xsl">
        <location infoStyle="/mapfiles/geocodeinfo.xsl" id="A">
          <point lat="33.748889" lng="-84.388056"/>
          <icon class="noicon"/>
          <info>
            <title xml:space="preserve"></title>
            <address>
              <line>Atlanta, GA</line>
            </address>
          </info>
        </location>
      </overlay>
    </page>

Nothing surprising here -- we have a title, query, center & span, and the
location and name of the search result. For a slightly more interesting case,
let's look at the response when searching for 'pizza in atlanta':

    <pre>
    <?xml version="1.0" ?>
    <page>
      <title>pizza in atlanta</title>
      <query>pizza in atlanta</query>
      <center lat="33.748888" lng="-84.388056" />
      <span lat="0.016622" lng="0.017714" />
      <overlay panelStyle="/mapfiles/localpanel.xsl">
        <location infoStyle="/mapfiles/localinfo.xsl" id="A">
          <point lat="33.752099" lng="-84.391900" />
          <icon image="/mapfiles/markerA.png" class="local" />
          <info>
            <title xml:space="preserve">
              Kentucky Fried Chicken/Taco Bell/<b>Pizza</b> Hut
            </title>
            <address>
              <line>87 Peachtree St SW</line>
              <line>Atlanta, GA 30303</line>
            </address>
            <phone>(404) 658-1532</phone>
            <distance>0.3 mi NW</distance>
            <description>
              <references count="9">
                <reference>
                  <url>http://www.metroatlantayellowpages.com/pizzaatlanta.htm</url>
                  <domain>metroatlantayellowpages.com</domain>
                  <title xml:space="preserve">Atlanta<b>Pizza</b> Guide-Alphabetical Listings of Atlanta<b>...</b></title>
                </reference>
              </references>
            </description>
            <url>http://local.google.com/local?q=pizza&near=atlanta&latlng=33748889,-84388056,11825991348281990841</url>
          </info>
        </location>
        { lots more locations... }
      </overlay>
    </page>

Again, nothing too surprising when you think about the data that's going to
show in the map pane. But how do the results get shown in the search result
area to the right? This is where things get a little wacky. The JavaScript
actually uses the XSLTProcessor component I mentioned earlier to apply an XSLT
to the result XML. This generates HTML which is then shown in the right panel.
We've come to expect this sort of thing on the server, but this is the first
time I've ever seen it done on the client (I'm sure it saves Google lots of
cycles, but personally I didn't even know XSLTProcessor existed!)

## Driving Directions
There's one last case to discuss, and that's driving directions. This works
just like other searches, including XSLT to show the results, with one
exception: the result XML includes a <polyline> tag that two opaque values
encoding the geometric route to be taken. This data appears to be base 64
encoded (or something similar, anyway). Remember the giant transparent PNG I
mentioned earlier for rendering routes? This data is used to render that
sucker. The data looks like this:

    <polyline numLevels="4" zoomFactor="32">
      <points>k`dmEv`naOdGC??EtD??@|DAxL??hEFjJ@ ...</points>
      <levels>BBB???BB?BB??@??@?????BB??@?????@? ...</levels>
    </polyline>

This polyline data is then used to request the route PNG from the server using
a URL like so:

    http://www.google.com/maplinedraw?width=324&height=564&path=sRS?k@fB@?}As@e@CGAIA}@BwCEu@Bs@?E_@cACS@a@PaC ...

<center>
  <img src="http://photos1.blogger.com/x/blogger/7678/661/320/118745/Route.png"/>

  *The route overlay*
</center>

## In Summary
That's about it. I hope that demystifies this application a bit; the real
magic, of course, is all the work going on to enable this on the back-end. The
fact that Google's servers can handle all of these images requests, route
finding, line drawing, searches and the like so quickly is the real magic. I
also want to point out that their map renderer (or the one they purchased)
works _much_ better than all the other ones I've seen on Mapquest, Mapblast,
and the like. That alone makes it worth using, if only so you can actually
_read_ the map!

I also think it bears noting that Google is pulling out all the stops to build
rich web apps, no matter how weirdly they have to hack the browser to make them
go. And I strongly believe that this is a trend that is here to stay -- XHTML
Strict/CSS/etc be damned. At the end of the day, what really matters to users
is compelling apps that let them get their work done quickly.

