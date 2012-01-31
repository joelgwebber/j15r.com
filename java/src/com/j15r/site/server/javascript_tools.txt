Whither JavaScript Tools?
-------------------------
So why don't we just build better tools for Javascript? Let's think for a
moment about what that would mean. Take, for example, something as simple as
code completion (i.e. typing foo.[ctrl-space] and getting a list of options).
In order for that list to be correct, it must be possible to statically analyze
the code to determine foo's type. This is often impossible in Javascript,
because there *are* no static types. Take the following example, from jQuery:

      function(selector, context) {
        selector = selector || document;
        if (selector.nodeType) {
          // ...
        }
        if (typeof selector == "string") {
          // ...
        } else if (jQuery.isFunction(selector)) {
          // ...
        }
      }

Now, pick a random spot in this function to type "selector.[ctrl-space]" in our
hypothetical Javascript IDE. What's the right answer? It's obviously
undecidable. Now try to imagine what happens with an automated refactoring, say
something as simple as "rename method". There is no way this an be done
correctly in an automated fashion, because there's no way to know for certain
which expressions reference the method to be renamed.

Now this is in no way meant to take a pot-shot at jQuery. It seems to be a very
well-written library, and this sort of practice is very common in Javascript
code, often to simulate method overloading. But it's just one very small
example of the benefit of moving up a level of abstraction.

