If you've dealt with HTTP much, you've probably at least *heard* that it
supports gzip compression. And under some circumstances, this even turns out to
be true! You might think that supporting something as simple as decoding
gzip-encoded content would be simple and straightforward, and you'd be right
(especially given that gzip code has been available freely since, well, about
forever).

It seems, however, that people working on most major browsers at various points
found ways to make this difficult. It would be one thing (albeit a silly thing)
if they simply didn't support gzip encoding, but it's another matter entirely
that a number of browsers *request* encoded content which they then proceed to
barf on. There are lots of articles out there describing, in varying degrees of
detail, the mess that is HTTP compression. IBM has a [good one][ibm].

## The Specific Problem
Myself, I don't have much cause to worry about this, except for one very
specific scenario: various difficult-to-discern versions and patches of
Internet Explorer do not *cache* compressed content correctly. Case in point:
if you reference an external .js file that IE then caches, it will usually
screw it up the *second* time you hit the page. It appears that what's
happening is that it somehow forgets the full size of the compressed .js file
in its cache, truncating the decompressed .js file to its *compressed* size.
Needless to say, this is not exactly conducive to your script actually working
at all.

I don't know about you, but I've seen some pretty damned big external scripts
out there (think hundreds of kilobytes, uncompressed), and it would be a real
waste to *not* be able to compress these things. But no matter how hard I
tried, I was never able to reliably detect the versions of IE that do and do
not handle this correctly (and to make matters worse, the most commonly
deployed versions *don't*, so it's kind of moot if you want to see any
real-world benefit).

## My Wacky Solution
But I'm going to share a trick with you.  It's *really* not pretty, but quite
functional in practice. If you want to compress external scripts reliably, what
you need to do is wrap 'em up in HTML and shove 'em into an IFrame. I see by
that incredulous look on your face that you think this is a strange idea.
Doesn't this mess with my `window` references?  You bet it does, but if you
make them all `parent` references, then everything works out just fine.

Basically, all you have to do is embed an IFrame in your HTML, like so:

    <iframe id="__scriptFrame" style="display: none;" src="bigAssScript.js">

Then you can access functions in your IFrame like so:

    var scriptFrame = document.getElementById('__scriptFrame');
    scriptFrame.contentWindow.foo();

There are two caveats that spring to mind when doing this. First, you can't set
your parent window's event handlers from within the external script. However,
this is easily fixed by creating a method within the outer HTML that allows the
external script to set these handlers:

    function setWindowOnResize(handler) {
      window.onresize = handler;
    }

The other caveat is that you need to ensure that the IFrame is done loading
before you try to access any methods within it. This is also relatively easy to
fix: use the IFrame's `onload` handler to handle any startup code you need to
run. This simply avoids the problem, because this event will not fire until the
IFrame is done loading -- and the IFrame won't finish loading until after the
*outer* window is done loading.

I *do* realize that this is a really strange solution, but once you
implement it, it's pretty easy to maintain, and most importantly it lets you
compress those giant scripts, usually to about 1/5 of their original size. That
can be a pretty significant bandwidth savings!

To give you an idea of the kinds of savings we're talking about, one of my own
external scripts was compressed from 140,769 bytes to 29,057 bytes, for a
compression ratio of nearly 80%. If you consider the fact that external .js
files almost *never* get compressed normally, this will add up
quickly.

[ibm]: http://www-106.ibm.com/developerworks/web/library/wa-httpcomp/

