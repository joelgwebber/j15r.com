## Most web browsers leak memory like a bloody sieve
You heard me: Like a sieve. Gobs and gobs of memory. Necessarily. "Gee, that's
funny", you might say, "My browser doesn't seem to leak noticably". And you're
probably right. However, the design of both major browsers (Internet Explorer
and Mozilla) leaks memory *necessarily* (To be honest, I'm not sure about
Safari and Opera, but it wouldn't surprise me).

Let's take a moment to think about that "necessarily" part.  What I mean by
this is that these browsers are *not* poorly implemented (I'm not going to pass
judgment on that), but rather that their design leads inexorably to memory
leaks. The reason you don't usually see this when browsing is that (a) most
individual pages are simple enough so as not to exhibit the leak and (b) the
few sites that use truly heavy JavaScript often work very hard to get around
these leaks.

If you've ever built a really complex site using lots of JavaScript, you've
probably seen this problem. You may even have some idea of where it comes from.
Well, I'm writing this explanation because (a) I think I fully understand the
problem and (b) there are a lot of confused (or simply wrong) explanations out
there.

## The Joy of Automatic Garbage Collection
The problem is not JavaScript.  Nor is it really the DOM.  It is the
*interface* between the two. JavaScript uses (sometime after Netscape 2.0, I
think) a fully garbage-collected memory allocator. For anyone who doesn't
understand this, this simply means that memory can *never* be truly leaked,
even when objects reference each other circularly (e.g. A-&gt;B-&gt;A). Both
Internet Explorer and Mozilla are built on roughly similar component models for
their native object layer (i.e.  the DOM). Internet Explorer uses the native
windows COM model, while Mozilla uses a very similar XPCOM model. One of the
things these two models have in common is that objects allocated within them
are *not* garbage collected &amp;emdash; they are reference counted.  Again,
for those unfamiliar with the vagaries of memory management systems, this means
that objects might *not* get freed if they take part in a circular reference
(as above).

Now the designers of these browsers have gone to some trouble to keep their COM
layers (I'll refer to both as COM for simplicity) from leaking during normal
usage. If you're careful, this is not too difficult &amp;emdash; you simply
have to be vigilant about potential circular references and use various hacks
to refactor them out of existence. And of course their JavaScript garbage
collectors can't really leak at all.  Where things start to go sour is when you
have circular references that involve both* JavaScript objects and COM objects.
Let me use an example to illustrate this point. Let's say you have a JavaScript
object 'jsComponent' with a reference to an underlying DIV. And the DIV
contains a reference to the jsComponent object. It might look something like
this:

    var someDiv = document.getElementById('someDiv');
    jsComponent.myDomObject = someDiv;
    someDiv.myComponent = jsComponent;

What's wrong with this? What basically appears to happen is that jsComponent
holds a reference to someDiv. In a reference-counted memory manager, this means
that it has a reference count of at least 1, and thus cannot be freed. Now
someDiv also holds a reference to jsComponent (because jsComponent *cannot* be
freed if it is still accessible via someDiv, or things could go *really* bad).
Because COM objects cannot truly participate in garbage collection, they must
create a 'global' reference to myComponent (I'm not sure what the actual
implementation looks like under the hood, because I haven't dug through the
source for either browser, but I imagine it's similar to the semantics of
Java's createGlobalRef() JNI call).  Thus begins the deadly embrace: someDiv's
reference count will stay at 1 as long as jsComponent is not freed, but
jsComponent will not be freed until someDiv drops its global reference to it.
Game over: that memory is irretrievable without human intervention.

At this point, you might be asking yourself how common circular references of
this sort really are. First, I would argue that they *ought* to be relatively
common, because building any sort of reusable component framework in JavaScript
requires this sort of structure to tie the component and DOM layers together.
However, there are many schools of thought on this subject, and if that were
the only problem, it wouldn't be so bad. However, there are two very common
cases that make this problem much more notable.

## Event Handlers
Perhaps the most common manifestation is in DOM event handlers. Most event
handlers take the form " onclick='this.doSomething()' ". This doesn't really
pose a problem. However, if the event handler references a JavaScript object in
any way (as in the aforementioned component scenario), then it serves as a
back-reference. This is why, in many posts and articles I've read about
avoiding memory leaks, the statement "don't forget to unhook all of your event
handlers" is often made.

## Closures
A much more subtle (and therefore nasty) situation where circular references
occur is in JavaScript closures. For those not familiar with the concept, a
closure binds stack variables to an object created in a local scope. You may
well have used them before without realizing it.  For example:

    function foo(buttonElement, buttonComponent) {
      buttonElement.onclick = new function() {
        buttonComponent.wasClicked();
      }
    }

At first glance, it may appear that this method of hooking events on a button
avoids the circular reference problem. After all, the button's 'onclick' event
doesn't directly reference any JavaScript object, and the button element itself
contains no such reference. So how does the event find its way back to the
component? The answer is that JavaScript creates a closure that wraps the
anonymous function and the local variables (in this case, 'buttonElement' and
'buttonComponent'). This allows the code in the anonymous function to call
buttonComponent.wasClicked().

Unfortunately, this closure is an implicitly created object that closes the
circular reference chain containing both the JavaScript button component and
the DOM button element. Thus, the memory leak exists here as well.

## So Now What?
Unfortunately, there really is no easy way around this problem. If you want to
build complex reusable objects in JavaScript, you are probably going to have to
deal with this and some point. And don't feel tempted to think "It's probably
not *that* bad; I'll just ignore it" -- neither Internet Explorer nor Mozilla
free these objects even *after* a page is unloaded, so the browser will just
blow more and more memory.  In fact, I first noticed this problem in my own
work when I saw IE blowing around 150 megabytes of memory!

The only real option I know of is to be extremely careful to clean up potential
circular references. This can be a little tricky if you're writing components
for others to use, because you have to ensure that they call some cleanup
method in the 'onunload' event. But at least by understanding the root cause of
the problem, you have at least some hope of getting your leaks cleaned up
before your users start complaining!

## One Further Note
When I said at the beginning of this article that the design of most web
browsers *necessarily* leaks, I was probably making too strong a statement.
While their reference-counting and mark-and-sweep garbage collection
implementations do not "play well" together, there is *lots* of research on
this subject out there, and I'm sure a way could be found to fix this problem
without throwing away most of the implementation.

