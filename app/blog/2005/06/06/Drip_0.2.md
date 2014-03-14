Happy Monday morning to everyone (or, depending upon where you may be,
evening). This is just a quick note to announce Drip 0.2! Here is a quick list
of changes in this version:
- The main window is now resizeable.
- The property list is sorted.
- Property lists are now separate from the leak dialog. You can double-click on an element to see its properties. And you can double-click on any object property to see its properties. Think of it as a poor-man's expandable property list.
- The source is also available here.

My current list of definitely known issues is as follows:
- Still need to hook node.cloneNode() to catch all possible leaks.
- Still need to hook new windows as they are created.
- It sometimes reports that leaks are coming from about:blank rather than their actual source.

And my current list of possible issues is:
- A couple of people have mentioned crashes occuring, which I have not yet been able to reproduce. If anyone having such a problem has a chance to build the source and catch this in a debugger, that would be wonderful.
- I've also heard mention of issues with deeply-nested frames. My demo leak test page should exhibit this issue, but seems to work fine. Again, any help appreciated.

As always, please let me know of any other issues you discover, suggestions,
and (even better) patches. And I haven't forgotten about my promise to provide
a solid overview of how to deal with leak issues. I'm still doing a bit of
research on the subject, but this will be forthcoming soon!
