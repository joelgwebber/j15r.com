I recently stumbled across this [article][sitepen] by Mike Wilcox at SitePen,
which suggests that defining Javascript variables in the global scope causes
reads and writes to them to be much slower than if they are defined as fields
on an object. While Javascript VMs never fail to surprise in their strange and
disparate behavior, something didn't quite smell right about this, so I decided
to dig in a bit further.

As I understand it, the investigation was triggered by Mike noticing a large
number of global variables in the Google Docs graphics editor. The variables in
question are defined something like this:

    var rs=parseFloat;
    var us="shape",vs="activeElement", ...

In all likelihood, a combination of normal static variables, interned strings,
and such. In order to test the hypothesis that defining these variables in the
global scope could be a performance problem, he created a test that looks
something like this:

    // Assume that v[i] is a randomly-created variable name.
    obj = {};
    setLocals = function(){
      for(var i=0;i<v.length;i++){
        obj[v[i]] = true;
      }
    }

    setGlobals = function(){
      for(var i=0;i<v.length;i++){
        window[v[i]] = true;
      }
    }

Tests for reads follow basically the same form. You can see the results in the
linked article, but the upshot is that accessing "globals" in this manner is
anywhere from 1x to 10x slower than the equivalent object field access.

There are two main problems with this test:

- It always accesses globals via the window object explicitly.
- It doesn't explicitly declare the variables in either scope.
  - This conflates creation with assignment.

Let's rewrite these tests to be closer to normal, idiomatic Javascript usage.
We will declare global variables explicitly, and reference them implicitly. We
will also explicitly declare the object fields explicitly, for as close an
apples-to-apples comparison as possible. Note that the random variables, as
well as the accumulator and return value, are probably-unnecessary attempts to
normalize for any static optimization tricks that newer Javascript VMs might be
playing.

    // Allocate a bunch of random values to be used later in assignments.
    var r0 = Math.random(), r1 = Math.random(), ... ;

    // Test assignment and reading of global variables.
    var g0 = 0, g1 = 0, ... ;
    function globals() {
      var a = 0;
      g0 = r0; g1 = r1; ... ;
      a += g0; a += g1; ... ;
      return a;
    }

    // Test assignment and reading of fields on a local object.
    var obj = {f0 : 0, f1: 0, ... };
    function locals() {
      var a = 0, o = obj;
      o.f0 = r0; o.f1 = r1; ... ;
      a += o.f0; a += o.f1; ... ;
      return a;
    }

In addition, we'll test the the performance of the increasingly common pattern
of scoping variables via closure.

    // Test assignment and reading of locals via closure.
    function closures() {
      var c0 = 0; c1 = 1; ... ;

      return function() {
        var a = 0;
        c0 = r0; c1 = r1; ... ;
        a += c0; a += c1; ... ;
        return a;
      };
    }

This is all far from "real-world" Javascript, but it's a lot closer than
referencing everything by name, using the array notation. I generated the above
code such that there were 1000 variables, assignments, and reads. I then
averaged the times over 100 runs, getting the following results:

MacBook Pro 2.4 GHz Core 2 Duo:

- Safari 4:
  - Globals:  15ms 
  - Fields:   30ms 
  - Closures: 30ms 

- Firefox 3.0.12:
  - Globals:  60ms 
  - Fields:   43ms 
  - Closures: 78ms 

- Firefox 3.5.2:
  - Globals:  43ms 
  - Fields:   46ms 
  - Closures: 52ms 

VMWare on aforementioned Mac:

- IE8:
  - Globals:  31ms 
  - Fields:   47ms 
  - Closures: 47ms 

- Chrome 2:
  - Globals:  31ms 
  - Fields:   47ms 
  - Closures: 47ms 

- Firefox 3.0.8:
  - Globals:  35ms 
  - Fields:   29ms 
  - Closures: 68ms 

The absolute values aren't as important as the relative values for each
browser.  The most notable pattern is that globals and fields don't perform all
that differently from one-another, and referencing variables via closure is
almost invariably slower. This is a random smattering of browsers, and
doubtless others will perform differently. But most importantly, there's no
clear-cut advantage to either one.

This has limited implications for hand-written code; you're almost always going
to want to write whatever makes the most sense to you, because the differences
aren't all that great either way. It *does* have strong implications for tools
(like [GWT][gwt]) that *generate* Javascript. Such tools have a high degree of
latitude in how they define variables, and these small effects can be amplified
for large programs.

I'd say the moral of this story is that you must be careful to measure
precisely what you *think* you're measuring, especially in as complex and
unpredictable a doman as Javascript VMs. I can understand how Mike arrived at
this point -- in any sane world, `(window[name])` would be the same as
`(name)`, since they do precisely the same thing. But we're not in a sane world
here, are we?

[sitepen]: http://www.sitepen.com/blog/2009/08/10/web-page-global-variable-performance/
[gwt]: http://code.google.com/webtoolkit

