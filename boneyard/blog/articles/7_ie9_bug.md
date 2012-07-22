An Ugly Bug in the IE9 Beta

Hot on the heels of my very happy discovery that IE9 finally [plugs its
leaks][ie9leaks], I've found a subtle-but-important bug in the IE9 beta. Bear
with me, as it's a little tricky to explain.

### The Good News
IE9 finally implements the standard HTML5 DOM element interfaces, which will
make many things simpler. Further good news: IE9 includes a nice debugger you
can use to explore these interfaces. [As I understand it][ie9dom], the IE team
has cleaned up all the bizarre old COM bindings that have been giving
developers fits for years. So when you inspect an element in their nifty new
debugger, you get something like this:

    document.body   {...}                     [Object, HTMLBodyElement]
    - accessKey     ""                        String
    - appendChild   function appendChild(...  Object, (Function)
    ...

### The Bad News
This is beautiful, and matches your expectations of the interfaces quite
nicely. But then I discovered this little gem:

    elem:           {...}                     DispHTMLImg
    - [Events]
    - [Expandos]
    - [Methods]
    - accessKey     ""                        String
    ...

What on earth is this? It sure looks like an IDispatch interface to an element
-- but I thought we weren't supposed to be seeing that sort of thing anymore.
But if you resolve properties on the object using the Javascript VM, most of
them resolve the same way, so no harm done, right?

Not so fast. When digging into a bug in my code, I kept running into this
bizarre situation where elements didn't seem to be comparing properly.
Specifically, I got into a situation where (ElemA == ElemB) *and* (ElemB !=
ElemA). These were two different elements, so they shouldn't have been equal to
one another anyway, but the asymmetric equality relation was a really big
surprise!

As you might have guessed, one of these two elements was an HTMLElement, while
the other was a DispHTMLDivElement. Ok, if one of them is a Disp interface two
an element and the other is a native DOM host object, you can imagine how the
comparison might get screwed up (I'm going on the assumption that IE didn't
expect to have those Disp objects exposed at all). Which begs the question of
how I got that reference in the first place.

When I tried to reproduce the bug in isolation, everything seemed to work fine
-- no Disp references in sight. I finally tracked it down to the fact that
my code was running in an iframe, while the DOM elements themselves were in the
outer frame (this is a not-uncommon technique for isolating code). Specifically,
it seems to be triggered by the following situation:

    Outer page:
      <div id='target'>...</div>

    IFrame:
      <script>
      var target = parent.document.getElementById('target');
      target.onclick = function(evt) {
        // both 'evt' and 'elem' will be Disp interfaces
        var elem = evt.currentTarget;
      };
      </script>

So it appears that something's going wrong when marshalling the event object
from one frame to the other. And once you get one of these funky Disp objects,
all references you get from it will be Disp objects as well. Which opens you
to these comparison failures.

### A couple of caveats
I'm assuming that the "Disp" part of these objects' names refers to IDispatch,
but if that's not correct it doesn't really change much. Also, you may have
noticed that I used the == comparison operator above -- it turns out that ===
behaves as expected. However, there's no good reason to use === when comparing
two objects.

### A possible explanation
If I understand IE's architecture correctly, older versions appeared to use
DCOM for cross-frame communication. If I'm correct about this (and it's still
the case in IE9), then it may be that something just went wrong in the
marshalling of references from one frame to another (hence my assumption that
"Disp" means "IDispatch").

### Does This Really Matter?
Yes. It might seem really subtle, but these are the kinds of bugs that can take
hours or days to track down when something goes wrong (and for which the fix is
non-obvious at best). And while putting your code in an iframe might seem like
a slightly odd thing to do, there are very good reasons for it under some
circumstances (I'll have more to say on precisely why this is important in a
follow-up post).

### Repro
I've posted a relatively simple reproduction case [here][repro]. It's a little
screwy, because it's a case hoisted out of a much more complex app, but it
should illustrate the issue reasonably well.

[ie9leaks]: http://blog.j15r.com/2010/09/ie9-memory-leaks-finally-declared-dead.html
[ie9dom]: http://blogs.msdn.com/b/ie/archive/2010/09/02/dup-exploring-ie9-s-enhanced-dom-capabilities.aspx
[repro]: http://j15r.com/example/ie9_disp.html

