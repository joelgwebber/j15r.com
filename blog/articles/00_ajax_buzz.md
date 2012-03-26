title:Ajax Buzz
date:2000.01.01

This wasn't intended to be the Google Maps blog. Really. But hey, since they
just released the Keyhole mode, I thought it might be worth going over the
structure of the data.

Actually the integration of the Keyhole images is quite straightforward. The
only things that had to change were (a) the functions that map between
longitude/latitude pairs and pixel coordinates and (b) the tile URL generator.

The first of these is very simple. It would appear that the Maps team simply
adhered to the coordinate system already in use by Keyhole, as it's completely
different than the Maps coordinate system. The mapping functions are as
follows:

    // Initialize the zoom levels.
    //
    var gZoomLevels = new Array();
    for (var zoom = 0; zoom &lt; 15; zoom++)
      gZoomLevels[zoom] = Math.pow(2, 25 - zoom) / 360;

    // Compute the longitude and latitude for a given pixel coordinate.
    //
    function getLngLat(pixelX, pixelY, zoom) {
      return new Point(
        pixelX / gZoomLevels[zoom] - 180,
        180 - pixelY / gZoomLevels[zoom]
      );
    }

    // Compute the pixel coordinate for a given longitude and latitude.
    //
    function getPixelPos(longitude, latitude, zoom) {
      return new Point(
        (longitude + 180) * gZoomLevels[zoom],
        (180 - latitude)  * gZoomLevels[zoom]
      );
    }

Nothing too surprising there. The part that seems to have people scratching
their heads is the image URL format. At first glance, it appears to be a wacky
series of characters with lots of 'q, r, s, and t'. Well, there is a method to
this madness. First, have a look at the de-obfuscated function below:

    // Compute the URL of the tile with the given coordinates.
    //   (note that these coordinates are not the same as in the
    //    two functions above:  they must be divided by the tile
    //    size, which is 256).
    //
    function tileUrl(x, y, zoom) {
      var range = Math.pow(2, 17 - zoom);

      // Wrap-around x coordinate.
      //
      if ((x &lt; 0) || (x &gt; range - 1)) {
        x = x % range;

        // The mod operator isn't quite the same on a computer as it is
        //   in your math class (negative isn't handled correctly).
        //
        if (x &lt; 0)
          x += range;
      }

      // Build the quadtree path, working our way down from 2^16
      //   to the current zoom level.
      //
      var Ac = "t";
      for (var pow = 16; pow &gt;= zoom; pow--) {
        // Drop to the next zoom level.
        //
        range = range / 2;

        if (y &lt; range) {
          if (x &lt; range) {
            // Upper-left quadrant.
            //
            Ac += "q";
          } else {
            // Upper-right quadrant.
            //
            Ac += "r";
            x -= range;
          }
        } else {
          if (x &lt; range) {
            // Lower-left quadrant.
            //
            Ac += "t";
            y -= range;
          } else {
            // Lower-right quadrant.
            //
            Ac += "s";
            x -= range;
            y -= range;
          }
        }
      }

      return "http://kh.google.com/kh?" + "t=" + Ac;
    }

Ok, so my comments were a bit of a giveaway. Basically, it looks like Keyhole
stores its data in a quadtree structure, and the URL describes the 'path'
through the tree to find a given tile. A quadtree, many of you may remember
from your undergraduate graphics class, is a nice little structure for
efficiently storing and accessing large tile arrays. It's particularly
effective when not all areas need to be stored with the same level of detail
(as is clearly the case with Keyhole).

I won't go into detail on the structure here, as others have already done a
better job of explaining it (just do a Google search on 'quadtree'). For our
purposes, suffice it to say that q, r, s, and t refer to the four quadrants at
each depth in the tree.  Thus, a sequence of these characters uniquely
identifies a tile at some depth.

