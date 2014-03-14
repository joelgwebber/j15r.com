For those unfamiliar with it, Box2D is a great 2D physics library written by
Erin Catto, which is at the core of a large number of casual games on consoles
and mobile devices. Angry Birds is one you might have heard of, but there are
many, many others.

It's also not a simple library by any means. When porting Angry Birds to
HTML5, we found that in some cases Box2D performance could be the limiting
factor in the game's frame-rate (on the more complex levels). It turns out
this little library is doing a lot of work under the hood. And the work it's
doing isn't limited to any one tight loop or hotspot. Rather, its work is
distributed all over the place -- matrix and vector math, creation of lots of
small objects, and general object-oriented logic distributed over a complex
code base.

# Motivation

Box2d makes a great general benchmark -- it's a bottleneck on real-world apps,
and there's no one thing that a VM or compiler can optimize to "fix it". It
also has the nice property that it's been ported to lots of different
platforms -- the original is written in C++, and it's been ported to
ActionScript, Java, Javascript, and several other systems. So I took it upon
myself to put together a little benchmark that measures the time it takes to
simulate one frame of a reasonably complex Box2D world.

The goal of this little experiment is not to add fuel to the flames of the
Internet's already-tiresome "Compiler and VM Wars" -- rather, my intention is
to get some hard data on what behavior real-world performance-sensitive code
can actually expect to see in practice on various platforms. Measuring the
performance of virtual machines in isolation is particularly tricky, but this
benchmark has the nice property that, if a VM or compiler improves it, then
real-world problems are actually solved in the wild, and everyone wins.

# The Platforms

My intention is to measure the performance of various platforms, not
particular Box2D ports. The ports themselves necessarily vary somewhat from
one-another, but they should still be broadly equivalent. The reason
Javascript VMs are represented four times in this list is that I wanted to
make sure that we compared Javascript VMs, at their best, to the JVM, NaCl,
and native code.

- Native : This is the standard Box2D code, compiled via gcc or clang/llvm
  (the latter on my test machine, as described below).
- NaCl : The same code, compiled via the NaCl SDK's custom gcc build, and
  running within Chrome.
- Java : The JRE (1.6), as currently shipped by Apple on Mac OS 10.6.
- Box2D-web : The hand- written Javascript Box2D port, on various browsers.
- Emscripten : The original C++ code, compiled via Emscripten to Javascript.
- Mandreel : The original C++ code, compiled via Mandreel to Javascript.
- GwtBox2D : The Java port, compiled via GWT to Javascript.

# The Test

## World
Picking the right world structure for this kind of benchmark is a bit
tricky, because it needs to have the following properties:

- A high per-frame running time.
- Not settle quickly: simulations that settle eventually stop doing work, as
  the physics engine skips calculations for settled objects.
- Stable: Subtle variations in the behavior of floating-point math can cause
  the behavior on different VMs to diverge badly, invalidating comparisons.

I eventually settled on a simple pyramid of boxes with 40 boxes at the base,
for a total of about 800 boxes. I manually verified that this simulation is
stable on all the systems tested, and that it doesn't settle within the number
of frames simulated for each test. It takes at least 3-4ms to simulate in
native code, and only gets slower from there, so the running time is
sufficient to avoid problems with timer resolution.

## Code
 All the test code is available on GitHub. Some of it requires a bit of care
and feeding to get running (especially the C++ version), but should allow you
to reproduce these results if so desired. There are also copies of the
Mandreel and Emscripten Javascript output checked in, so you won't have to go
through the pain of building those yourself.

## Test System
I used my MacBook Pro 2.53 GHz Intel Core i5 as a test machine. It seems a
fairly middle-of-the road machine (maybe a bit on the fast side for a laptop).
As always, your mileage may vary.

# Data
The raw data I collected is in the following spreadsheet. I ran each test
several times, to mitigate spikes and hiccups that might be caused by other
activity, and to give each system a fair chance. Each run warms up over 64
frames, and then runs for 256 frames -- I've confirmed that the simulation is
stable over this duration on all the platforms under test.

First, let's look at all the results together, on a log-scale graph:

<script src="//ajax.googleapis.com/ajax/static/modules/gviz/1.0/chart.js" type="text/javascript"> {"dataSourceUrl":"//docs.google.com/spreadsheet/tq?key=0Ag3_0ZPxr2HrdEdoUy1RVDQtX2k3a0ZISnRiZVZBaEE&transpose=0&headers=1&merge=COLS&range=B1%3AB257%2CC1%3AC257%2CD1%3AD257%2CE1%3AE257%2CF1%3AF257%2CG1%3AG257%2CH1%3AH257%2CI1%3AI257%2CJ1%3AJ257%2CK1%3AK257&gid=0&pub=1","options":{"reverseCategories":false,"titleX":"samples","pointSize":0,"backgroundColor":"#FFFFFF","lineWidth":2,"logScale":true,"hAxis":{"maxAlternations":1},"hasLabelsColumn":false,"vAxes":[{"title":"ms / frame","minValue":0,"viewWindowMode":"pretty","viewWindow":{"min":0,"max":150},"maxValue":150},{"viewWindowMode":"pretty","viewWindow":{}}],"title":"Box2D Performance (All, Log Scale)","interpolateNulls":false,"legend":"right","reverseAxis":false,"width":800,"height":600},"state":{},"view":"{\"columns\":[0,1,2,3,4,5,6,7,8,9]}","chartType":"LineChart","chartName":"Chart 1"} </script>

The first thing you'll notice is that there are three clusters of results,
centered roughly around 5ms, 10ms, and 100ms. These clusters are associated
with native code, the JVM, and Javascript VMs.

Now let's compare the best of the best of each of these three groups.

<script src="//ajax.googleapis.com/ajax/static/modules/gviz/1.0/chart.js" type="text/javascript"> {"dataSourceUrl":"//docs.google.com/spreadsheet/tq?key=0Ag3_0ZPxr2HrdEdoUy1RVDQtX2k3a0ZISnRiZVZBaEE&transpose=0&headers=1&merge=COLS&range=B1%3AB257%2CD1%3AD257%2CJ1%3AJ257&gid=0&pub=1","options":{"reverseCategories":false,"titleX":"samples","pointSize":0,"backgroundColor":"#FFFFFF","width":800,"lineWidth":2,"logScale":false,"hAxis":{"maxAlternations":1},"hasLabelsColumn":false,"vAxes":[{"title":"ms / frame","minValue":0,"viewWindowMode":"pretty","viewWindow":{"min":0,"max":0},"maxValue":0},{"viewWindowMode":"pretty","viewWindow":{}}],"title":"Box2D Performance (VMs vs. Native)","height":600,"interpolateNulls":false,"legend":"right","reverseAxis":false},"state":{},"view":"{\"columns\":[0,1,2]}","chartType":"LineChart","chartName":"Chart 1"} </script>

This is similar to the first graph, except that we've removed the noise of
NaCl (which is within 20-30% of raw native performance) and all but the best
of the various Javascript approaches. This looks a bit better for Javascript,
clocking in at a mean of around 50ms (interestingly, this number is achieved
by the Mandreel C++ cross-compiler running on Chrome 17; more on this below).

# Analysis

Looking at the last graph above, I believe the most important observation is
that, while Javascript implementations have improved drastically in recent
years, the best result achieved by any Javascript implementation is still more
than an order-of-magnitude slower than native code. This is perhaps an
unsurprising result to many, but some have begun suggesting that modern
Javascript VMs are "nearing native performance". While this may be true for
some workloads, they still have a long way to go in this case.

This also demonstrates that native code compiled via NaCl stays within 20-30%
of the performance of un-sandboxed native code, which is in line with what
I've been told to expect.

Finally, it's somewhat interesting to see that the JVM is running 3x slower
than native code. I've been told, anecdotally, that OpenJDK on Mac OS X is
producing numbers closer to 6ms/frame, which would be ~50-60% slower than
native, but I need to confirm this. I don't think it can be proven, but I
suspect the JVM's performance can be taken as a rough lower-bound on what one
can expect from any dynamic VM. Thus, Javascript VM implementors can take the
JVM's performance as a reasonable target to shoot for.

# Appendix: Javascript Implementations and Compilers

While it wasn't a primary goal of these benchmarks, they do give some
interesting data about Javascript VMs, and the various compilers that use
Javascript as a target language.

The following is a graph of the Javascript VMs in Chrome, Safari, Firefox, and
Opera (IE9's Chakra VM is not yet included because it seems that Box2D-Web is
using Javascript properties, which it doesn't support yet).

<script src="//ajax.googleapis.com/ajax/static/modules/gviz/1.0/chart.js" type="text/javascript"> {"dataSourceUrl":"//docs.google.com/spreadsheet/tq?key=0Ag3_0ZPxr2HrdEdoUy1RVDQtX2k3a0ZISnRiZVZBaEE&transpose=0&headers=1&merge=COLS&range=E1%3AE257%2CF1%3AF257%2CG1%3AG257%2CH1%3AH257&gid=0&pub=1","options":{"reverseCategories":false,"titleX":"samples","pointSize":0,"backgroundColor":"#FFFFFF","lineWidth":2,"logScale":false,"hAxis":{"maxAlternations":1},"hasLabelsColumn":false,"vAxes":[{"title":"ms / frame","minValue":0,"viewWindowMode":"pretty","viewWindow":{"min":0,"max":150},"maxValue":150},{"viewWindowMode":"pretty","viewWindow":{}}],"title":"Box2D Performance (Javascript VMs)","interpolateNulls":false,"legend":"right","reverseAxis":false,"width":800,"height":600},"state":{},"view":"{\"columns\":[0,1,2,3]}","chartType":"LineChart","chartName":"Chart 1"} </script>

First off, all the VMs tested are well within 3x of each other, which is
wonderful, because wildly variant performance across browsers would make it
exceedingly difficult to depend upon them for any heavy lifting. V8 and JSCore
are quite close to one-another, but JSCore has an edge in variance. It's not
immediately obvious what's causing this, but GC pauses are a likely culprit
given the regularly-periodic spikes we see in this graph.

Now we move to the various Javascript compilers. Here we have Box2D-Web
(representing "raw" Javascript), Emscripten, Mandreel, and GWT (Closure
Compiler should give performance roughly in line with "raw" Javacript on any
modern Javascript VM).

<script src="//ajax.googleapis.com/ajax/static/modules/gviz/1.0/chart.js" type="text/javascript"> {"dataSourceUrl":"//docs.google.com/spreadsheet/tq?key=0Ag3_0ZPxr2HrdEdoUy1RVDQtX2k3a0ZISnRiZVZBaEE&transpose=0&headers=1&merge=COLS&range=F1%3AF257%2CI1%3AI257%2CJ1%3AJ257%2CK1%3AK257&gid=0&pub=1","options":{"reverseCategories":false,"titleX":"samples","pointSize":0,"backgroundColor":"#FFFFFF","lineWidth":2,"logScale":false,"hAxis":{"maxAlternations":1},"hasLabelsColumn":false,"vAxes":[{"title":"ms / frame","minValue":0,"viewWindowMode":"pretty","viewWindow":{"min":0,"max":0},"maxValue":0},{"viewWindowMode":"pretty","viewWindow":{}}],"title":"Box2D Performance (JS Compilers)","interpolateNulls":false,"legend":"right","reverseAxis":false,"width":800,"height":600},"state":{},"view":"{\"columns\":[0,1,2,3]}","chartType":"LineChart","chartName":"Chart 1"} </script>

The real shocker here is that Mandreel outperforms all the others fairly
consistently, given that it's translating C++ to Javascript (!). Note that the
Emscripten results are not as good as they could be -- the author is currently
finishing up a new optimizer. I also believe the GWT compiler should be doing
a better job than it is; there are a couple likely culprits in the compiled
output involving static initializers and array initialization overhead. It
should be possible to optimize these out and improve the results.

Note: See the update below about Emscripten performance

# Caveats

As with all benchmarks, especially ones as fuzzy as this, there are a lot of
caveats.

## The code's not identical
These are all ports of the same C++ source code, so by their nature they must
vary from one another. It may be the case that a particular port is unfairly
negatively biased, because of particular idioms used in the code. If you
suspect this is the case, please say so and preferably offer a patch to the
maintainer. These aren't being used in the same way as, e.g., the V8 or Kraken
benchmarks, so it's entirely fair to optimize the code to get better numbers.

## I may have made mistakes
I suppose this goes without saying, but there are a lot of knobs to be tweaked
here, and there could easily be something sub-optimal in my configuration,
makefiles, or what-have you. If you notice anything amiss, please say so and
I'll try to address it.

## This is just one machine
As described above, I ran these tests on my MacBook Pro. The relative
performance of these tests might not be the same on different machines.

# Update

Based on feedback from comments and elsewhere, I've updated a few of the
numbers in the linked spreadsheet, along with the graphs.

Emscripten: I got an updated build with a better compiler, and the numbers are
now much more in line with the other Javascript implementations. Java: I
updated the JVM implementation to use a faster sin()/cos() implementation
(there was a flag for this in the JBox2D code that I simply missed. It's no
about 2.5x the speed of the native implementation. I also wasn't entirely
clear about what I meant by the "JVM" -- so to be clear, this means the
standard Mac OS X JDK 1.6 server JVM (there is no standard client JVM on the
Mac). None of this changes the core conclusions in any substantive way,
however.
