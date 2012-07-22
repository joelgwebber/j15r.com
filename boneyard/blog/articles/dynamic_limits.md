On the Limits of Optimizing Dynamic Languages

I've recently found myself re-reading the transcript of Steve Yegge's Dynamic Languages Strike
Back [yegge] talk, and it made me realize that I needed to go into some more detail on the specific
benefits that GWT achieves through static typing, and why they matter.

Let me first start by saying that Steve's points about dynamic optimization of dynamically-typed
code are correct -- modern (and in some cases, older) virtual machines can work wonders on
dynamically-typed code. Polymorphic inline caching [pic] alone can achieve very impressive results
on Javascript code, as witnessed in VMs like Chrome's V8 engine [v8] and others.

I suggested in my previous post [prev] that ...

[yegge] http://steve-yegge.blogspot.com/2008/05/dynamic-languages-strike-back.html
[pic]
[v8]
[prev] http://gwt-unofficial.blogspot.com/2008/12/gwt-javascript-and-correct-level-of.html

