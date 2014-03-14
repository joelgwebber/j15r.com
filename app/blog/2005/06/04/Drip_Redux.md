Wow.  Thanks for all the excellent feedback on Drip. It was really just a tool
that I needed for myself, but I'm glad that it may prove useful for others as
well.

There were a lot of comments, both here and on Slashdot, so I'm going to try to
put as many of my thoughts and responses as possible in this post. As such, it
may be a bit of a grab-bag.

## Exacerbating the problem
The first point I want to make is in response to one or two comments here, and
many on Slashdot: That is, that I am not particularly concerned about whether
or not I am exacerbating the problem by helping developers to "work around"
IE's issues.  Don't get me wrong; I find it just as unfortunate as everyone
else that these problems exist in the first place. It is truly awful that
developers using such a high-level tool as a web browser have to take memory
allocation issues into account. Particularly given the fact that they're not
really given the tools to effectively deal with them (window.CollectGarbage()
doesn't count, since it won't really fix the problem).

Anyone who's spent a significant amount of time developing software has to
realize that they will *always* be dealing with inadequacies of their tools and
platforms. This has always been the case. It doesn't mean that vendors
shouldn't fix their mistakes, but it *does* mean that you can't usually bitch
at your customers for their choice of platform. If you are going to make
software development your profession, then you must generally accept this
responsibility.  Certainly there are cases where you can dictate the details of
the client's platform, but this is not the case for most vendors.

I also want to point out two things about this specific problem. First, IE's
memory leak issues stem largely from the underlying model that allows scripting
languages to interface with native COM objects (that is, making all objects
accessible to scripting languages COM objects deriving from IDispatch). While
imperfect, this model is also quite efficient -- and given that it was
developed in the mid-90's, not an unreasonable compromise at the time. The
second point I want to make is that IE is *not* the only browser with this
problem. Mozilla had fairly severe memory leak issues until recently, and I've
been told that Safari does as well. So let's not use this as an excuse to jump
all over Microsoft.

## When do leaks matter?
This is another point that I think bears some discussion. If you've spent a
little time pointing Drip at existing sites, you've probably found that most
sites exhibit no issues at all. This is simply because most sites simply don't
use enough complex DHTML (with complex object graphs and the like) to create
the specific sort of circular references that cause leaks. Most sites that *do*
have a few leaks seem to be of PARAM objects passed to Java and/or Flash
components. I've gotten mixed reports on when this happens, and when it causes
a significant leak, so the jury's still out on whether this matters.

On the other hand, I saw one comment to the effect that Google Maps leaks a
*lot* of elements. This is exactly the sort of application that is in danger of
leaking enough to matter. If you look at the Maps code, you'll discover that
they've done an excellent job of abstracting the components that comprise the
application, and it's quite easy to follow (if you de-obfuscate it, anyway).
And I believe that the fact that it leaks so much is actually an indication
that its developers have done a *good* job.  The problem is that the very
abstractions that make a code base of that size manageable make it *really*
easy to create leaks. Because there are a lot of references among all of its
objects, and most DOM elements are wrapped in one way or another, even a single
leak can cause the entire reference graph to leak. Nasty, huh?

## How do I fix leaks?
This is a pretty complex question. So I've decided to punt this to a
forthcoming post. There are a lot of resources out there on this subject, but I
hope to gather as much of it as possible into one post so that I can provide a
reasonable framework for finding and dealing with them.

## What now?
I've gotten a lot of helpful suggestions and a couple of bug reports. What I
would like to do now is to list all of the fixes and enhancements that I can
think of, and solicit advice on how to prioritize them. Once I've had another
pass at the code, I will release the source as well so that you can all help
maintain it! This is my current list:

- Deal with deeply nested frames. This is a real issue for a lot of sites -- apparently Drip only hooks one level of nested frames, but fails to hook deeper windows.
- Hook the cloneNode() method.  This is simply an oversight on my part, but it's necessary to catch all possible leaks.
- Resizable window. This was just me being lazy. I've gotten really used to constraint-based layout in the Java world, and to be honest, I just didn't want to deal with doing this by hand in MFC.
- Sorted and expandable element properties.  'Nuff said.
- Hook new windows (via window.open).  I think this is feasible, and will do my best.
- Anything else you guys can think of!

