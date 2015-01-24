It's 2014, for god's sake. Why on earth are we still using a text editor built by Bill Joy
in 1976 that was optimized to make life better for people working on [terminals attached to
minicomputers over slow serial networks][1]?

By what metrics can we judge the utility and efficacy of a text editor? The most obvious (if
not the most quantitative) answer is how quickly it enables you to write and edit text (For
our purposes, let us consider only the case of writing source code, because it has very different
properties to natural language text.)  How can we make this metric more quantitative? Perhaps
we could ask, for a given set of operations, how many keystrokes are required to enact them?
That sounds reasonable on its face, but if taken to its logical extreme, produces some absurd
edge cases. For example, one could then argue that the most effective text editor is one in which
you write the shortest possible program to produce the desired text. That would certainly
optimize for keystrokes, but fails to consider the effort required to decide *which* keystrokes.
So a good metric for a text editor's efficacy must balance keystroke minimization with mental effort.

We are not going to try and actual produce such a metric for this essay -- that would almost
certainly be overkill. What we *are* going to do, however, is consider how different editor
styles might fare when considered in terms of such a hypothetical metric. I believe our
intuition is a decent guide on this question, even if it can't easily be rationalized.

Consider the two main classes of text editor. I think of the first (and most common) as
"word processors". That is, their designs are derived from traditional word processing tools.
Most keys produce exactly what you see on the key cap (possibly shifted), and you use
specialized keys for moving the cursor, selecting text, deleting, copy/paste, and so forth.
Statistically speaking, `P = 1.0` that you're familiar with these. The other class is...
well, pretty much just VI. This editor (and its descendents) are "modal" editors,
meaning that they have two modes, one for typing text, and another for operating on it
with editor commands (triggered by standard character and punctuation keys).

Given that you're likely familiar with the standard keys used by most text editors, I'm
going to focus on VI, and how it differs from and (IMHO) improves upon them. When I make
comparisons, I'll use [Sublime Text][2], as it's a pretty solid modern editor.

## VI's power lies in brevity

You will find plenty of articles suggesting that VI's power stems from its macros and other
complex features. But if that were really the measure of what I want most from an editor,
I'd be using Emacs. No, what I get from VI is not a powerful macro system (I never use the
scripting language, and only rarely use anything more complex than simple recorded macros) --
instead, I get an incredible economy of expression.

"How so?", you might ask. This is best explained through example. Let's say I want to do
something relatively common like "replace this parenthesized expression with something else".
This is a simple command: move the cursor over one of the parentheses and hit "c%", which
means "change" (operation) and "balance parentheses" (movement). Then just start typing.

// c%
// shift-ctrl-M backspace

Let's do some more, starting with simple ones. Most decent editors have keys for indent
and outdent operations, as does VI, which are "<", and ">". Not earth-shattering, but
combine it with movement commands, and it becomes much more powerful -- ">%" becomes
"indent all lines between these matching braces".

// >%
// shift-ctrl-M ctrl+9

What happens when we search for strings? Well, a search is just another kind of movement
command, so it can be composed with operations. So if you wanted to delete everything
from the cursor position to the next instance of some string, you simply type "d" (delete),
"/" (search), then the string you want to match (then enter). "n" searches for the next
instance of the last thing you searched for.

// d/, dn
// no equivalent

There are, of course, other movement keys, such as "{" / "}" (prev/next paragraph/block),
and "(" / ")" (prev/next sentence), which can be composed like the others.

// d}, d)
// no equivalent

And lots of little convenient movements like "0" (beginning of line) and "$" (end of line),
the latter of which comes up so frequently that we have shortcuts like "C" (change to end
of line) and "D" (delete to end of line).

// c$ (C), d$ (D)
// shift-end, [type]
// shift-end, backspace

Finally, we have a few useful odds and ends for dealing with lines make sense only in the
context of a programming editor. "o" and "O" start appending on the next/prev lines (the
latter inserts a new line above the current one), and "J" with conveniently joins the
next line to the current one. These are trivial, but come up all the time in programming.

// o, O, J
// ctrl-shift-enter
// ctrl-enter,
// ctrl-J

There are a few things I find particularly compelling about these examples. The first is
simply that there aren't a huge number of specialized commands. Rather, you take a small
number of operations and movements and compose them into a wide array of useful commands.
The second is simply that none of these are overly "clever" -- they're simply convenient,
powerful commands that come up all the time in day-to-day programming tasks. I might use
an editor's scripting language *once or twice* to perform some crazy hack that turns
Markdown into HTML (though tasks like that are, IMHO, why we have actual programming
languages), but I'm far more concerned with the fluidity of handling day-to-day tasks.

Finally, we have the fact that these are incredibly terse ways of expressing many of the
most common operations. In most editors, these operations would comprise at least a few
extra keystrokes, often more, and usually not on the home keys (if you're a touch typist
-- if not, go learn for god's sake). So for example, "d}" (delete to end of paragraph/block)
would require something like "shift, down down down ..., backspace" or "futz with the
mouse for a few seconds, backspace". "D" or "d$" (delete to end of line) would require
"shift end backspace". "J" (join lines) is "end delete space". The absolute numbers aren't
incredibly different (though they count when it's something you easily do thousands of
times a day), but the fact that you have to leave the home keys is an absolute killer.

## The almighty dot

Existing VI users will wonder why I haven't brought up "." (repeat last command) yet.
This is because it doesn't make much sense until you understand what a command *is*.
Any operation/movement pair (with an optional repeat count) is a single command. But
this also includes text typed (e.g., in the case of a "c" (change) or "i" (insert)
operation). Being able to repeat commands is an incredibly powerful way to simplify
many repetitive daily programming tasks.

// Good "n." example

"Repeat" is by far the most common command I use in VI, and it has no simple equivalent
in any other editor. As I'll explain below, this is also at the heart of what people
find obscure about VI.

## Still, that's pretty damned obscure

There's an old joke about VI. "It has two modes: 'edit' mode, and 'beep' mode." There's
an element of truth to this, of course. New users are confronted with a text editor
that doesn't appear to support, well, typing text. And they can't figure out how to
act upon their immediate desire, which is to quit the bloody thing.

This confusion is caused by the existence of VI's *actual* modes, "command" (where all
the aforementioned commands, that squat on commonly used characters, live) and "insert"
(for actual typing). Without these modes, there would be nothing to delineate the end of
a command such as "Insert the string 'wat' at the end of this line". Without that, you
have no "." command, and you lose most of the editor's expressive power.

These modes also allow VI to use easily reachable keys for commands, which in turn allows
you to keep your hands where they belong, on the home keys. Again, this makes a big
difference in editing speed. I assume that, if you tested it, you'd discover a keyboard
equivalent to Fitt's Law for mouse movement that accounts for this.

Still, I'll admit the command keys can be pretty obscure and arbitrary. They were, after
all, mostly invented in the 70s. I'm sure that if we redesigned VI today, we'd create
something a bit more coherent, but that ship has sailed, and once you internalize the
keys, it all starts to make sense (though there's probably a touch of Stockholm Syndrome
in there, too).

## VI and modern IDEs

Unfortunately, modern IDEs put VI users into a difficult situation. On one hand, we
can operate much more effectively on common text editing tasks in VI. On the other,
a good IDE can make some more complex tasks orders of magnitude more efficient --
formatting, semantic rename, jump to definition, find callers, etc. At one point in
my life, I was literally switching back and forth between VI and Visual Studio in
order to try and get the best of both worlds on C++ code, which was pretty much a
recipe for lost changes, as the editors kept stepping on each other's metaphorical
toes.

Obviously, simple key mapping can't come close to reproducing the behavior of a modal
editor like VI. But at least some have tried. Eclipse and IntelliJ have passable VI
plugins. And Visual Studio has a plugin that I'm told works well, though I haven't
tried it. The good news is that at least they're trying, and most give you something
like 75% fidelity for the core commands. The bad news is that every one I've tried
has serious flaws that can be frustrating (e.g., in IntelliJ's don't dare move the
cursor manually while typing text if you want the command to repeat cleanly).

What would be truly amazing is if IDEs started taking VI mode seriously and proving
all their built-in commands to VI, so that they can be repeated and used in macros.
But at present, I'm still waiting for a mostly-not-buggy VI mode. Hope springs
eternal, as they say.

## Is a better VI possible?

...


[1]: http://web.cecs.pdx.edu/~kirkenda/joy84.html
[2]: http://sublimetext.com/
Fitt's Law
Stockholm Syndrome







Arguing about text editors is one of the least fruitful endeavors that programmers engage
in. My goal here is not to tell you why *you* should use VI, but simply to help people who
don't use it much or at all understand why so many of us persist in using such a seemingly
odd program.

