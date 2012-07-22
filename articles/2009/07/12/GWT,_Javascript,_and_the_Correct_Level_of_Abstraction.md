John Resig seems to have stirred up a bit of a hornet's nest with a [recent
post][ejohn] on JavaScript language abstractions. I don't want to wade into
the details of the argument; I think Francisco over at cappuccino.org does a
[great job][cappuccino] of explaining the value of language abstractions that I
largely agree with.

However, I've noticed a number of misunderstandings about GWT in this and other
articles, and I feel some clarification is in order.

## Why Java?
First and foremost, let me clarify yet again the reasons behind the decision to
use Java as a source language. Allow me to quote from the ["Making GWT Better"]
[gwtbetter] document:

> Why does GWT support the Java programming language instead of language X? In
> a word, tools. There are lots of good Java tools. That's the entire
> explanation. It isn't that we don't like language X or that we think the Java
> programming language is somehow superior. We just like the tools.

This point has perhaps been beaten to death, but there are a *lot* of good
tools for Java, whether you love the language itself or not. Code completion,
automated refactoring, static error detection, code coverage, testing, and so
forth, are pretty hard to give up once you're used to them.

But wait, there's more. Let's talk about compiler optimizations a bit. I'll
quote Ray Cromwell, from the comments on Resig's article:

> ... Aggressive compiler optimizations, that happen before code is sent down
> the wire. This is a problem that no amount of trace-jitting will solve. Far
> from being bloated, as I demonstrated in my [GWT Query
> presentation][gwtquery] at Google I/O, GWT can produce code an order of
> magnitude smaller than most selector libraries in specific circumstances. GWT
> can prune dead code far better than any JS optimizer, and can obfuscate code
> to reduce size in a way that helps maximum GZIP/DEFLATE algorithms.

What Ray's referring to here is the fact that a statically-typed language like
Java allows you to perform absolutely correct optimizations far in excess of
what is achievable without type information (There's a lot more to say on this
topic, which I'll save for a future post).

Why is this so important for JavaScript output? In a word (or two), *code
size*. Code that grows without bound is a serious problem for Javascript
applications, and static analysis gives us a *lot* of leverage over the
problem. The GWT compiler can determine precisely which classes, methods,
and fields are actually used and aggressively prune everything else. And this
pruning feeds back into the iterative compilation process, allowing still
more optimizations (e.g., type-tightening, devirtualization, and so forth)
to be performed.

Indeed, you can take this even further, with a concept we refer to as
runAsync(). With static whole-program analysis, it is possible to automatically
break a program into optimal fragments at user-defined cut points. This is
still experimental, but the preliminary results [look pretty good][runasync].

To put all this in concrete terms, check out this [great example]
[designpatterns] on Ray's blog showing the following transformation:

      public class MyArray implements Iterable<string> {  
        private String[] items = {"foo", "bar", "baz"};  
        
        public Iterator<string> iterator() {  
          return new StringArrayIterator(items);  
        }  
        
        private class StringArrayIterator implements Iterator<string> {  
          private String[] items;  
          private int index;  
        
          public StringArrayIterator(String[] items) {  
            this.items = items;  
            this.index = 0;  
          }  
        
          public boolean hasNext() {  
            return index < items.length;  
          }  
        
          public String next() {  
            return items[index++];  
          }  
        
          public void remove() {  
            throw new UnsupportedOperationException();  
          }  
        }  
      }  

      void iterate() {
        MyArray m = new MyArray();  
        for(String s : m)   
          Window.alert(s);  
      }

`iterate()` becomes

      function $iterate(m){  
        var s, s$iterator;  
        for (s$iterator = $MyArray$StringArrayIterator(new MyArray$StringArrayIterator(), m.items);  
           s$iterator.index < s$iterator.items.length;) {  
          s = s$iterator.items[s$iterator.index++];  
          $wnd.alert(s);  
        }  
      }  

which becomes something like

      function x(a){var b,c;for(c=y(new z(),a.a);c.b<c.a.length;){b=c.a[c.b++];$wnd.alert(b);}}


The point of going into all this detail about tools and optimization is that
choosing Java as a source language gives GWT leverage that would have been
provably impossible in JavaScript. It's most emphatically *not* about loving or
hating any given language, or providing Java programmers with a way to *avoid*
JavaScript -- it's a pragmatic decision based upon specific, measurable
benefits.

I've seen lots of assertions that various languages are either "higher level"
or "lower level" than others, without any clear definition of what metric is
being used to justify these statements. For example, "JavaScript is the
assembly language of the web" or "Java is a low-level language because it
doesn't have closures and dynamic typing". These sorts of arguments are
pointless at best, and at times simply disingenuous. What matters is not some
ill-defined notion of a language's "level of abstraction", but rather what you
can actually achieve with it.


## JavaScript Interop
So what if I *need* to write or use existing Javascript code? Most everyone
seems to finally be aware that it's possible to write JavaScript directly in
your Java source using [JavaScript Native Interface][jsni] methods, like so:

      // Java method declaration...
      native String flipName(String name) /*-{
        // ...implemented with JavaScript
        var re = /(\w+)\s(\w+)/;
        return name.replace(re, '$2, $1');
      }-*/;

What doesn't seem to be clear is just how fundamental a feature this is. I've
seen various people suggest that this is somehow "circumventing" the "normal"
way of doing things -- and while it's true that you lose some of the benefits
of Java type-checking, optimization, and debugging in these methods, they're
also the foundation upon which all the GWT libraries are built. And there's no
particular reason they have to be short snippets, either. They can be complex
methods and classes that reach back into Java code, pass exceptions around,
and so forth.

What about calling into existing JavaScript libraries, or exposing GWT classes
to JavaScript code? For the former, check out Sanjay Jivan's [SmartGWT]
[smartgwt] library, which wraps the massive [Isomorphic SmartClient]
[smartclient] library. For the latter, have a look at Ray Cromwell's
GWT-Exporter [gwtexporter] library.

It's also worth noting that this functionality, combined with [Overlay Types]
[overlay] makes it really easy to efficiently parse and operate on JSON
structures. Once again, you get a side-benefit: once you've written the overlay
type for a particular JSON type, everyone using it gets code completion and
documentation for free. For example:

      var customerJsonData = [
        { "FirstName" : "Jimmy", "LastName" : "Webber" },
        { "FirstName" : "Alan",  "LastName" : "Dayal" },
        { "FirstName" : "Keanu", "LastName" : "Spoon" },
        { "FirstName" : "Emily", "LastName" : "Rudnick" }
      ];

      class Customer extends JavaScriptObject {
        protected Customer() { } 

        public final native String getFirstName() /*-{ return this.FirstName; }-*/;
        public final native String getLastName()  /*-{ return this.LastName;  }-*/;
      }

which means, as a user of this class, if I get a Customer type from somewhere,
I don't have to ask what fields and methods are available. The IDE can simply
tell me.


## HTML DOM Integration
But don't GWT's abstractions keep you from just working with DOM elements?
Again, no. It has always been possible to work with the DOM from GWT (how else
do you think all those widgets get implemented?), but as of 1.5, it's gotten a
lot easier. See this [Google IO presentation][rockin] for details. Here's a
taste, though:

      TableElement table = ...;
      String s = table.getRows().getItem(0).getCells().getItem(0).getInnerText();

Which of course translates to:

      var s = table.rows[0].cells[0].innerText;

With the important addition that as you type "table.[ctrl-space]", the IDE can
actually tell you that "getRows()" is an option. Given that TextAreaElement
alone has nearly 100 methods, that starts to be pretty useful.

[The statement][ejohn] "You are likely to never see a DOM object or pieces of
the native JavaScript language" is actually only as true as you want it to be.
Most JavaScript frameworks provide some sort of higher-level abstraction than
simple DOM elements, and GWT is no exception, but you should pick the *right*
level of abstraction for the task at hand.


## Independently Useful Parts
Finally, we have the question of whether (GWT === GWT's libraries). This is
another common misconception -- GWT looks interesting, but I don't like the
way their widget library works. That's like saying you like JavaScript, but
not the way Prototype (or whatever) works.

GWT "eats its own dogfood" almost all the way down. That is to say, there's
practically no module you can't replace (including the JRE). Like the DOM and
JRE modules, but not all the widgetry? Write your own widgets. Don't like any
of the modules? Replace the whole bloody thing! The many problems of building
browser-based UIs are not yet well-solved in the state of the art, so it's
highly unlikely that GWT's widget library represents a "perfect" solution. For
a completely different take on how a widget library should work, have a look at
the [Ext-GWT][extgwt] library.


## To Sum Up
I hope I've managed to convey the pragmatic goals that underlie GWT's design
decisions, and the methods we've used to achieve them. There are still a
million things we'd like to build and/or change, but I feel like it's off to a
pretty good start. Nothing would make me happier than if we could stop arguing
about abstract notions of what language is "right" and focus on practical goals
and metrics.


[ejohn]: http://ejohn.org/blog/javascript-language-abstractions/
[cappuccino]: http://cappuccino.org/discuss/2008/12/08/on-leaky-abstractions-and-objective-j/
[gwtbetter]: http://code.google.com/webtoolkit/makinggwtbetter.html
[gwtquery]: http://timepedia.blogspot.com/2008/06/google-io-gwt-extreme-presentation.html
[designpatterns]: http://timepedia.blogspot.com/2008/06/design-patterns-vs-gwt-compiler.html
[runasync]: http://groups.google.com/group/Google-Web-Toolkit-Contributors/browse_thread/thread/eb9c8cf046cbaaf2
[jsni]: http://googlewebtoolkit.blogspot.com/2008/07/getting-to-really-know-gwt-part-1-jsni.html
[smartgwt]: http://code.google.com/p/smartgwt/
[smartclient]: http://www.smartclient.com/
[gwtexporter]: http://code.google.com/p/gwt-exporter/
[rockin]: http://sites.google.com/site/io/surprisingly-rockin-javascript-and-dom-programming-in-gwt
[overlay]: http://googlewebtoolkit.blogspot.com/2008/08/getting-to-really-know-gwt-part-2.html
[extgwt]: http://extjs.com/products/gxt/

