In my [previous post][prev], I addressed the question of the speed of variable
access, as originally explored by Mike Wilcox [here][sitepen]. In the comment
section of his blog, he posted a cogent response to my concerns that I failed
to address at the time (about a month ago). Sorry about that, Mike -- I blame
the 10-month-old girl who consumes most of my time and attention lately :)

# Terminology

A commenter on that post appears to take serious offense with my choice of
terminology, so let's start by clearing that up:

- Array Notation: What I am referring to here is the array-like syntax for
accessing properties on Javascript objects. I am fully aware that there is a
difference between accessing string-keyed and integer-keyed properties in this
way. But it is not uncommon to refer to this as array-access notation. Call it
what you will, but I suspect anyone reading this will have little trouble
understanding what I'm referring to.

- Globals and Fields: Perhaps I'm letting my Java-centric view of the world
poke through a bit here, but I thought it was fairly clear from the examples
I gave.  I was using the term "global" to refer to a globally-scoped variable
defined with an explicit "var" statement. The term "field" I used somewhat
loosely to refer to an explicitly-defined property on an object. Again, sorry
if this wasn't clear from context. Henceforth, I'll refer to the latter as
"properties", which is more accurate in a Javascript context.

Now allow me a moment to clarify speicifically why I bring up the question of
"array access" notation vs. "dot" notation (call them what you will). The problem
with using them interchangeably in performance tests is that while logically
they both refer to properties on an object, we have no reason to believe that
their performance will be identical in all Javascript engines. In addition,
there is good reason to suspect that accessing globals via `window[name]` may
be significantly different from simply accessing them via an unadorned `name`
expression.

# Mike's Concerns

From Mike's comment:

> I think we were going for slightly different goals, and that makes your tests
> very complimentary to mine.

This is an entirely fair point. Let me attempt to justify the particular forms
of variable access that I chose to test. In a nutshell, I wanted to test the
precise form of usage that I see in much, if not all, idiomatic Javascript, and
that we produce from the GWT compiler. This involves global variables that are
declared explicitly, as in:

    var g0 = 0, g1 = 'foo', ...;

And objects whose fields (or properties, if you prefer), are explicitly
declared and initialized (usually in a constructor function, but the expression
form below is common as well):

    {
      f0: 0,
      f1: 'foo',
      ...
    }

These correspond to static and instance variables, respectively, in Java source
code.

> One thing to note, and I’m pretty sure neither of us did, is that the JS
> engine may possibly resolve those variables in the code block before the code
> is executed. Keeping them in functions (and not in the global scope) should
> prevent that.
>
> setLVars = function(){
>   var a0=0;var a1=1;var a2=2;var a3=3;var a4=4; … 5000
> }

I believe this is referring to the above test of initializing local variables,
and it doesn't sound inplausible. In an attempt to work around this possibility,
the "locals" test I added separates variable declarations from their assignments,
and uses the same random values as all the other tests to initialize them.

# A word on closures

When I added the test for variable access "via closure", it was to test a structure
I've seen used a lot to isolate the "global" namespaces used by separate chunks
of Javascript code. This pattern tends to look something like this:

    (function() {
      var g0, g1, ...;

      function init() {
        // Do stuff with g0, g1, ...
      }

      init();
    )();

This is a really useful pattern when you want to keep separately-loaded scripts
from stepping on each-others' toes, and I thought it worthwhile to see if using
such a structure imposed any significant overhead on access to globals.

You may note that my results for closures are much more flattering on some
browsers than before. This is because I made a bad typo in the test and failed
to declare the variables properly. This led to them being declared implicitly
in the global scope.

# Test Code

It also appears that both my dear commenter and Mike Wilcox himself had some
difficulty reproducing my results with the simple snippets I provided, so I am
publishing the test page [here][test]. I've made a few changes in the process:

1. I have changed references to "fields" to refer to "properties" for clarity.
1. I added a test for "locals" that should be self-explanatory.
1. I fixed a bug in the "closures" case that was skewing the results fairly badly.

If anyone notices anything amiss with this code, please do let me know. I am
aware that the tests conflate read and write times, and that the accumulator
code makes their absolute values irrelevant. What I'm attempting to tease out
here is only the relative costs of defining variables in each scope.

# Results

(Note that for simplicity's sake, I've simplified these results to only cover
Safari 4, IE8, Firefox 3.5, and Chrome 2)

MacBook Pro 2.4 GHz Core 2 Duo:

- Safari 4:
  - Globals: 11ms
  - Properties: 24ms
  - Locals: 12ms
  - Closures: 11ms

- Firefox 3.5:
  - Globals: 44ms
  - Properties: 47ms
  - Locals: 14ms
  - Closures: 91ms

VMWare on aforementioned Mac:

- IE8:
  - Globals: 31ms
  - Properties: 47ms
  - Locals: 15ms
  - Closures: 31ms

- Chrome 2:
  - Globals: 28ms
  - Properties: 30ms
  - Locals: 5ms
  - Closures: 6ms

- Firefox 3.5:
  - Globals: 32ms
  - Properties: 35ms
  - Locals: 16ms
  - Closures: 69ms

Again, please note that the results for closures are rather different than in
my previous post, because of the aforementioned bug in my original test.

Regardless, though, the most clear patterns that emerge here are as follows:
- In none of these cases are properties on a local object faster to access than
  globally-scoped variables.
- Locals are almost invariably faster than (or the same speed as) global or
  property accesses, which is not terribly surprising.
- Accessing variables via closure *can* be as fast as accessing variables in
  the global scope, but on Chrome it's actually *faster*, while on Firefox 3.5
  it's about twice as slow.


[prev]: http://blog.j15r.com/2009/08/where-should-i-define-javascript.html
[sitepen]: http://www.sitepen.com/blog/2009/08/10/web-page-global-variable-performance/
[test]: http://j15r.com/test/varSpeed.html

