Javascript WTF #0
-----------------

Welcome to Javascript WTF #0. In this series of brief articles, I'll address
some really surprising aspects of the Javascript language. I plan on sticking
to the language itself (and its implementations in various browsers), as
opposed to the DOM and other libraries found in various browsers (the WTF's
found in browsers warrant an entirely separate set of articles).

The purpose of these is neither to serve as reference material (that is best
left to entire books) nor to simply take pot-shots at the language (that's
pretty easy with most languages, not just Javascript). These are just things
that have bitten me personally that are probably documented elsewhere (the
spec, say), but you'd never know to look.

Let's take a look at the following code:

function foo(
  some, args,
  that, split, nicely,
  across, lines, because, otherwise,
  the, argument, list, would,
  be, too, bloody, long) {

  if (perhaps) {
    if (because) {
      if (of_a) {
        if (bunch_of) {
          if (nested_ifs) {
            return
              "Some really long string that contains " + numbers +
              " and would be ugly on the previous line...";
          }
        }
      }
    }
  }
}

alert(foo());

Assuming the values of all the little variables I created to try and make a
point are irrelevant, what does this code do? If you guessed that something
useful might show up in the alert box, you'd be exactly as wrong as I was when
I first ran into this.

The real answer is alert("undefined"). WTF?

When I first hit this problem, I thought I was perhaps rapidly descending into
insanity, perhaps driven by overindulgence in Absinthe or that creeping case of
syphilis. But no, it seems that this is the price we pay for being able to get
away with things like this:

function that_semicolon_is_too_hard_to_type() {
  return 42
}

If you want to be able to skip that semicolon (that's *so* useful, because it
allows me to save the approximately 1-2 ms it takes me to hit *one of my home
keys* -- non en-US keyboard users may even save a whopping 2-3 ms, since they
often put their semicolons in weird places on the keybaord), then the grammar
has to treat "return" as a single-line statement.

Think about that for a second -- that means that the Javascript grammar has to
treat a whitespace character (i.e. newline) as a special token that affects
the behavior of the return statement, effectively turning this:

{
  return
    42
}

into this:

{
  return;
    42;
}

Wait a minute! How can that not be an error? You can't have statements after a
return statement, can you? Certainly not raw expressions, right? Sorry, but it
seems you can -- after all, who likes to be told by a compiler what they can
and can't do?

