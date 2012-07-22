To anyone still following this site, my apologies for taking a millenium or two
between posts recently. Things have been a bit crazy of late, but I have
something to introduce that will hopefully make up for the radio silence:

## [Drip][drip] -- an Internet Explorer leak detector
Over the last few months, a number of people have written to me or left
comments asking questions about their memory leak issues with DHTML (or AJAX or
whatever-you-want-to-call-it-this-week) applications.  Unfortunately, there's
not much I could offer in the way of advice that most people don't already
know. Get rid of closures, unhook your event handlers, etc. This advice just
isn't all that helpful when you've got a giant mess of JavaScript (often
inherited) and visually detecting leak scenarios can be maddeningly subtle.

I did, however, find it quite surprising that no one had ever built a leak
detector for Internet Explorer (or apparently for any other browser with leak
problems; Mozilla has some, but they seem to be more for developers working on
Mozilla itself, and the browser does a pretty good job of cleaning up leaks
anyway). So I built one.

## What it Does
It's a pretty simple application. Basically, it lets you open an HTML page (or
pages, in succession) in a dialog box, mess around with it, then check for any
elements that were leaked.

The interface is currently rather spartan.  Here's what the main app looks
like:

[Sorry, I lost the image somewhere along the way]

On the top you'll notice what looks like a crude version of Explorer's
navigation bar. You've got the standard back and forward buttons, the URL box,
and the 'go' button. These behave exactly as you might expect.  To the right of
it, however, is a 'check leaks' button, which will be grayed out when you first
run the app. In order to try it out, you will first need to go to an HTML page
(preferably one that you suspect leaks). The test page at [sorry I lost this
page] will work.  When you load this page, the 'check leaks' button will become
enabled.  Click it to see the following report:

[Yet again, I lost the image somewhere along the way]

This simple page leaks two DOM elements, a DIV and a BUTTON. These two elements
are displayed in the top list, along with their source documents (useful if
you've loaded more than one document between leak tests, or if you have more
than one frame), the number of outstanding references on them, and their ID and
CLASS attributes.

If you click on one, you'll see a list of its enumerable attributes in the
bottom list. A particularly useful attribute for identifying the elements is
'innerHTML'.

## Blowing Memory
Back to the main dialog for a moment. You might also have noticed the
interestingly-titled 'blow memory' button. Its function is simple: to
constantly reload a page as fast as it can, and to report the process' memory
usage in the list box below. This is a helluva lot easier than pressing F5 for
hours to determine how fast a page leaks memory.

## How it Works
Fortunately, Internet Explorer's architecture made this app fairly easy to
build.  It's basically a simple MFC app with a browser COM component in it. The
strategy for catching leaked elements is as follows:

- When a document has been downloaded, sneakily override the document.createElement() function so that the application is notified of all dynamically-created elements.
- When the document is fully loaded, snag a reference to all static HTML elements.
- To detect leaks:  navigate to a blank HTML page (so that IE attempts to release all of the document's elements),
- force a garbage-collection pass (by calling window.CollectGarbage()),
- and look at each element to see if it has any outstanding references (by calling AddRef() and Release() in succession on it).

Within the leak dialog, each element's attributes are discovered and enumerated using the appropriate IDispatch/ITypeInfo methods.

## Caveats
This is basically an alpha release. The interface more or less blows, and I may
have left glaring holes in the leak-detection strategy or in the code itself.
It seems to work for me, but I would really like for anyone using it to keep an
eye out for any problems so that I can fix them. And please don't hesitate to
contact me, of course, if you have any ideas, praise, criticism, or even rants
to offer. I really want this to help people to stop dealing with these
god-awful leaks, and since Microsoft doesn't seem inclined to fix this design
flaw, we can at least try to make it more bearable.

## What Next?
Obviously, I would like any feedback I can get. There are definitely some
interface quirks I need to iron out. And I would like to do more to help
determine the actual *cause* of each leak.  There are a few things that I would
like to find out, and if anyone has any pointers, please share them:

- Can you perform similar tricks with Safari/KHTML or Opera? (I know you can with Mozilla, but since it doesn't really leak much, that seems rather pointless)
- Does anyone know if it's possible to enumerate variables on one of IE's JavaScript closures? (meaning the stack frame hanging off of the function reference)
- How about enumerating expandos on IE DOM objects from C++? (I only seem to get built-in properties from ITypeInfo)

I'm sure other questions will come up in the near future.  Oh, and I *will* be
releasing the source before too long, as soon as I get a few things cleaned up.

Happy leak hunting!

[drip]: http://code.google.com/p/iedrip/

