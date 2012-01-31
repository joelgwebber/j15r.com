Ok, I promised to explain in more detail how to get rid of memory leaks once
you've found them. Though I haven't had time to gather all of the information
and examples I would have liked, I have run across a few external resources
that might be of help.

The first of these is a new [Microsoft Technical Article][msdn] that discusses
the various forms that IE memory leaks can take in some detail. Particularly
interesting is the fact that it discusses an even more obscure type of leak
that's not even a DOM element. It's definitely worth a read.

A bit more information on JavaScript closures can be found on Eric Lippert's
blog (which I highly recommend) [here][lippert].

For a nice, straightforward library that does an excellent job helping you
avoid the problem altogether, take a look at Mark Wubben's [Event
Cache][novemberborn].  I particularly like the fact that if you follow a simple
set of rules, then you cannot easily leak elements.

## On Another Note
I suggested earlier that the slowdown associated with leaking large amounts of
memory in IE might be associated with hash tables or something similar getting
full and therefore more inefficient. Eric Lippert left the following comment,
which makes perfect sense to me and seems more likely to characterize the
problem:

> The symbol tables are very search-efficient. What's more
likely is that the non-generational mark and sweep garbage collector is getting
more and more full, and therefore taking longer and longer to walk each time a
collection happens. A generational GC, like the .NET framework's GC, solves
this problem by not GCing long-lived networks of objects very often.

And don't worry, I haven't forgotten about Drip at all. As time allows, I will
be adding the features that I mentioned earlier. Of course, if anyone else
wants to play with the [source][ieleak] and make their own additions, please
feel free!

[msdn]: http://msdn.microsoft.com/library/default.asp?url=/library/en-us/IETechCol/dnwebgen/ie_leak_patterns.asp
[lippert]: http://blogs.msdn.com/ericlippert/archive/2003/09/17/53028.aspx
[novemberborn]: http://novemberborn.net/javascript/event-cache
[ieleak]: http://sourceforge.net/projects/ieleak/

