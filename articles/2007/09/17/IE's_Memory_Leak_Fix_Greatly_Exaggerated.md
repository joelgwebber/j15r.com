So Microsoft (as reported [here][ie-memory-leaks-be-gone] and
[here][memory-leaks-gone]) recently released a "cumulative security update" for
IE that fixes its egregious memory leaks.  Sounds great.  Even if it takes a
while to get everybody updated, at least the problem is fixed and we can all
stop bending over backwards to work around this problem in our libraries,
right.

## Not So Fast
Let's have a look at the [actual knowledge-base article][kb] to see exactly
what it says:

> "... a Web page that uses JScript scripting code, a memory leak occurs in
Internet Explorer. When you visit a different Web page, the leaked memory is
not released."

So far so good. It even references the original ["circular-reference"
knowledge-base article][kb2], implying that this is indeed what is fixed.

When I saw this article, I nearly spilled tea all over the keyboard. They
really fixed this issue? You mean I can untangle all the painful code in GWT
that works around this issue, diligently cleaning up all its circular DOM
references under all sorts of circumstances?

## Settle Down, Beavis
Before I got too excited, I had to do a little gut-check. Did they really go
back and make it possible for their garbage collector to chase references
through COM objects?  That would be wonderful, but I'm not holding my breath.

And it's a good thing, because there's basically no way in hell they did that.
In fact, it turns out that all they did was write a little code to sweep the
DOM on unload and clean up all the extant circular references on those
elements. This means that *all elements not still attached on unload are still
leaked, along with the transitive closure over all references Javascript
objects*. In even marginally complex applications, that means you're still
going to leak like a bloody sieve!

I put together a [little test script][spew] to show this in action. Have a look
in any version of IE, and watch its spew memory!

## I'm With Alex ...
... [on this one][dojo]. This is more like a bad joke than anything else. I
recognize that fixing IE's memory leaks is a really complex problem, but the
fact that it's not being done is still more evidence that Microsoft is
abandoning IE, at least as far as any real progress is concerned. I just wish
they would come out and say it.

## In the Meantime
Don't go ripping out that memory-leak cleanup code. And keep checking for leaks (perhaps with
[Drip][iedrip]).

[ie-memory-leaks-be-gone]: http://ajaxian.com/archives/ie-memory-leaks-be-gone
[memory-leaks-gone]: http://novemberborn.net/javascript/memory-leaks-gone
[kb]: http://support.microsoft.com/kb/929874/
[kb2]: http://support.microsoft.com/kb/830555/
[spew]: http://j15r.com/example/spew.html
[dojo]: http://alex.dojotoolkit.org/?p=620
[iedrip]: http://code.google.com/p/iedrip/

