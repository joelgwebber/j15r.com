The [following][pike] is a quote from Rob Pike on the subject of interactive debuggers:

  A year or two after I'd joined the Labs, I was pair programming with Ken
  Thompson on an on-the-fly compiler for a little interactive graphics
  language designed by Gerard Holzmann. I was the faster typist, so I was at
  the keyboard and Ken was standing behind me as we programmed. We were
  working fast, and things broke, often visibly—it was a graphics language,
  after all. When something went wrong, I'd reflexively start to dig in to the
  problem, examining stack traces, sticking in print statements, invoking a
  debugger, and so on. But Ken would just stand and think, ignoring me and the
  code we'd just written. After a while I noticed a pattern: Ken would often
  understand the problem before I would, and would suddenly announce, "I know
  what's wrong." He was usually correct. I realized that Ken was building a
  mental model of the code and when something broke it was an error in the
  model. By thinking about *how* that problem could happen, he'd intuit where
  the model was wrong or where our code must not be satisfying the model.

  Ken taught me that thinking before debugging is extremely important. If you
  dive into the bug, you tend to fix the local issue in the code, but if you
  think about the bug first, how the bug came to be, you often find and
  correct a higher-level problem in the code that will improve the design and
  prevent further bugs.

  I recognize this is largely a matter of style. Some people insist on line-
  by-line tool-driven debugging for everything. But I now believe that
  thinking—without looking at the code—is the best debugging tool of all,
  because it leads to better software.

This is the standard advice that one tends to receive, certainly in academic
settings, and often in industry (I knew plenty of people at Google who felt
this way). There's a certain appeal to the idea that your brain is the best
debugger there is, and that you should simply be able to look at your code and
reason directly about its behavior well enough that you don't really need
interactive debugging.

On the other side of the debate, we have chapter 4 of Steve Maguire's
excellent (if somewhat dated) book _Writing Solid Code_, which is entitled
"Step Through Your Code", and comes with the following summary:

  The best way to find bugs is to step through all new code in a debugger. By
  stepping through each instruction with your focus on the data flow, you
  canquickly detect problems in your expressions and algorithms. Keeping the
  focus on the data, not the instructions, gives you a second, very different,
  view of the code. Stepping through code takes time, but not nearly as muchas
  most programmers would expect it to.

He goes on to describe how effective stepping through code can be in finding
the bugs you didn't even know to look for, the states you didn't realize it
could get into, and the tests you would not otherwise have written.

I have a great deal of respect for both Pike and Maguire. But there's a pretty
big gulf between these points of view. To be fair, Pike suggests that the
question is a matter of style, but I've heard a more extreme version of this
view expressed more times than I can count -- that one *shouldn't* ever rely
on interactive debugging, and you're forming dangerous mental habits if you do
so. Implicit in this view is the suggestion that developers who don't use
debuggers are more productive than those who do, because they're more careful
and thoughtful.

So how can we explain this difference of opinion? I believe to a large extent
it can be explained by the following tongue-in-cheek [description][cuthbert]
of two debugging methodologies:

  How to debug (our version which has worked brilliantly so far):
  1st pass) use brain
  2nd pass) use eyes
  3rd pass) think a bit, repeat the above a bit
  4th pass) use debugger if all else fails

  How to debug ("bad workers blame their tools version")
  1st pass) boot vs debugger
  2nd pass) single step through lots of functions
  3rd pass) still single stepping
  4th pass) stare at the code you could have been staring at in the 1st pass
  5th pass) still miss the obvious and glaring typo in your code because you
  think it must be the debugger playing up or the compiler, or the machine
  itself.. or perhaps it is solar flare magnetic-electronic interference. (all
  have been suggested to me in the past!!)

I don't mean to pick on Dylan -- his post explicitly asks us not to take this
description too seriously. But I do believe the "bad workers blame their tools
version" captures the way many developers believe heavy debugger users
actually work. And there is no doubt that there are people who behave
precisely this way -- we've all seen it, and it's painful to watch. But to use
this as an argument *against* actively using a debugger as part of your
workflow is nothing more than a strawman, as it's obvious that there are
highly effective developers like Maguire who use them heavily.


# What is debugging?

Let's take a moment to discuss what we actually mean by the process of
debugging. First, I'd like to propose a more expansive definition that
includes both the attempt to define the cause of a known error, and a process
for discovering as-yet-unknown bugs. Debugging, therefore, is a set of
techniques for forming and testing hypotheses about the behavior of a program.
One could think of it as "experimental computer science" -- we have a complex
system (our program) about whose behavior we form hypotheses, and perform
experiments to confirm or disprove them.

...


# Whence Unit Tests?

...

# Merging the Views of Debugging

...




[cuthbert] https://plus.google.com/109603191376504998413/posts/cHeke4TaUCw
[pike] http://www.informit.com/articles/article.aspx?p=1941206
