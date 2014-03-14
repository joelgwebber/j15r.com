IE9: Memory Leaks Finally Declared Dead

It is with great pleasure that I can finally declare the [infamous][leaks],
[painful][drip], [long-standing][moreleaks], [never][stillleaks]
[fixed][ie8leaks] IE memory leak bug fixed! With the release of IE9, I have
verified that every leak pattern I'm aware of is fixed. It's been a long-time
coming, but I'm starting to feel more confident that IE9 can be reasonably
called part of the "modern web" -- the web that is sufficiently powerful to
support complex applications, and not just lightly scripted documents.

One caveat: Do be aware that your "standard" pages need to explicitly request
"IE9 Standards" mode, using either an HTTP response header or a meta tag like
the following:

    <meta http-equiv='X-UA-Compatible' content='IE=9'/>

Failure to do so, in addition to giving you all the old crufty bugs and quirks
in previous IE versions, will continue to leak memory, presumably because it is
using the DLLs from the old rendering engine.

Now perhaps I can finally stop writing about this stupid bug!

[leaks]: http://blog.j15r.com/2005/01/dhtml-leaks-like-sieve.html
[drip]: http://blog.j15r.com/2005/05/drip-ie-leak-detector.html
[moreleaks]: http://blog.j15r.com/2005/06/another-word-or-two-on-memory-leaks.html
[stillleaks]: http://blog.j15r.com/2007/09/ies-memory-leak-fix-greatly-exaggerated.html
[ie8leaks]: http://blog.j15r.com/2009/07/memory-leaks-in-ie8.html

