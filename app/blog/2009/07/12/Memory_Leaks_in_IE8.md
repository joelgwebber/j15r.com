Now that IE8's out, it seems I get to revisit this topic once again, which is
getting quite tedious. When Microsoft first began touting IE8 features, I
noticed a [couple][ms1] of [pages][ms2] pointing out that they had done a great
deal of work to "mitigate" memory leaks in IE. The word "mitigate" sounds a bit
fishy, as the source of the problem is [pretty fundamental][leaks] to the
design of the COM interface that their script engine uses to access the DOM and
other native objects.

As [you may recall][ie7], IE7 contained a rough attempt to solve this problem
by walking the DOM on unload, cleaning up leaks on any elements still there.
This helped somewhat, but left many common leak patterns unresolved (in
particular, any element removed from the DOM could still leak easily).

From my tests, IE8 appears to have resolved all of the most common leak
patterns (as described in the two IE8 links above). In particular, I can't
uncover a single leak that doesn't at least get cleaned up on unload. This is
good news for IE users, because under most circumstances it means that the
browser won't get slow and bloated over time.

### How IE8 leaks
With some cursory testing, however, I have uncovered at least one pattern that
still leaks memory on IE8. Consider the following code (which you can run
[here][spew]):

    // This approach hangs a massive js object from a dynamically created DOM
    // element that is attached to the DOM, then removed. This pattern leaks
    // memory on IE8 (in "IE8 Standards" mode).
    function spew() {
      // Create a new div and hang it on the body.
      var elem = document.createElement('div');
      document.body.appendChild(elem);

      // Hang a *really* big-ass javascript object from it.
      var reallyBigAss = {};
      for (var i = 0; i < 5; ++i) {
        reallyBigAss[i] = createBigAssObject();
      }
      elem.__expando = reallyBigAss;

      // Complete the circular reference.
      // Comment out the following line, & the leaks disappear.
      elem.__expando.__elem = elem;

      // Remove it from the DOM. The element should become garbage as soon as
      // this function returns.
      elem.parentElement.removeChild(elem);

      // Just to give it a fighting chance to collect this garbage.
      CollectGarbage();
    }

    function createBigAssObject() {
      var o = {};
      for (var i = 0; i < 100000; ++i) {
        o[i] = 'blah';
      }
      return o;
    }

This will leak at runtime on IE8. It <em>does</em> get cleaned up when the page
is unloaded, but it can still be a serious problem for long-running pages
(complex Ajax applications, for example).

This particular example is admittedly somewhat contrived, but it is actually
isomorphic to a common use pattern:

- Create an element.
- Attach it to the DOM.
- Create some event handlers that result in circular refs.
- At some point in the future, after the user's done with it, remove it.

Sounds like a popup, a menu bar, or just about any interactive element that
gets created and removed in an application, n'est-ce pas?

### What should I do about it?

Honestly, I would advise that you continue to do whatever you always have done.
Most Java\[script\] libraries (GWT, Dojo, jQuery, Prototype, etc.) already have
code in place to clean up these sorts of leaks, and they will continue to work
as advertised (I've personally checked GWT for leaks on IE8). It is unfortunate
that we have to continue doing these things, because they have a non-trivial
performance cost; and although it's taken a while, WebKit and Gecko seemed to
have finally nailed their own memory leak issues.

### Aside: Drip is dead
I wrote [Drip][drip] some years back in order to help track down memory leaks
on Internet Explorer. I incorrectly assumed that it would be useful for a year
or two, as the problem would eventually be dealt with by Microsoft.

Well, they did finally deal with the problem, primarily by building [their own
memory leak detector][detector]. The good news is that it works quite well, and
is probably much more comprehensive than Drip ever was (and I haven't had much
time to maintain it). The first caveat I would add is that you almost
invariably want to change it to report "actual leaks" -- I don't find the IE6
and IE7 options to be useful in practice. The really bad news is that it
isn't useful on IE8 -- it will install, but doesn't catch any actual leaks, as
far as I can tell.

[ms1]: http://msdn.microsoft.com/en-us/library/dd361842(VS.85).aspx
[ms2]: http://blogs.msdn.com/ie/archive/2008/08/26/ie8-performance.aspx
[leaks]: http://blog.j15r.com/2005/01/dhtml-leaks-like-sieve.html
[drip]: http://blog.j15r.com/2005/05/drip-ie-leak-detector.html
[detector]: http://blogs.msdn.com/gpde/pages/javascript-memory-leak-detector.aspx
[ie7]: http://blog.j15r.com/2007/09/ies-memory-leak-fix-greatly-exaggerated.html
[spew]: http://j15r.com/example/spew.html

