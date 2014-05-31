I'm no distributed systems expert. But like anyone working on a large, modern application, I have to at least be able to
impersonate one to a first approximation.

[This article](http://www.infoq.com/articles/microservices-intro) does a good job justifying the move towards what
the author calls a "microservice architecture". This is, of course, just a fancy way of saying "distributed system composed of
many different services" or "what everyone dealing with significant scale does now". To its credit, it also describes the
non-trivial problems you have to solve to implement such a system, along with some approaches and patterns that can help.

## My own experience
In my own work on [FullStory](http://www.thefullstory.com/), we've certainly followed the "start monolothic, then
refactor" pattern. This is particularly important for a startup, because:

- you're trying to pin down product-market fit, so you need to be able to change quickly,
- you can't organize a small handful of engineers into separate teams, and
- you can't yet afford the ops overhead of all that deployment and communication.

But if you're diligent (and don't want to drown in monolithic complexity), you can evolve your system into services
over time. Thankfully, this is become a *lot* easier with platform providers like Google and Amazon. I can't
imagine how we'd get by without Google's [cloud services](http://cloud.google.com/) -- AppEngine, Compute Engine,
Task Queues, Cloud Storage, Datastore, and even CloudSQL. And of course there are equivalents to most of these on
Amazon's offerings. Deploying all this infrastructure by hand, would be an absolute nightmare, and a collosal waste
of time.

But when using these services it becomes much more important to have the flexibility provided by a microservice
architecture. Case-in-point: AppEngine frontend instances are extraordinarily convenient, offering you seamless
no-downtime deployment and automatic scaling. If you've ever tried to do this by hand on a large service, you know
how nasty a problem it is. But they don't come cheap, and they aren't flexible when it comes to CPU/memory balance.
In our original monolithic architecture, we had one task that needed a lot of memory, but wasn't very CPU
intensive. Our costs dropped dramatically when we moved this process to a Compute Engine instance (high-memory
configuration). But we couldn't have realistically done that without task queues and a distributed architecture.

## Go
We've also found that [Go](http://golang.org/) has removed a lot of the pain of building services and communicating
among them. There are many reasons, but here are a few:

### Concurrency primitives
[Goroutines and channels](http://golang.org/doc/effective_go.html#goroutines) are an incredibly effective and safe
way to coordinate external services efficiently. Threads can be complex and error prone, and async-io can quickly
turn into "callback spaghetti".

### Libraries
The libraries are straightforward, composable, and modern. This is a byproduct of both thoughtful design *and* the
fact that they were designed and implemented recently, so they don't bear the scars of decades of evolution. For
a great example of what such scars look like, just go try and implement anything crypto-related in Java -- the
pain therein isn't the language's fault, but simply the fact that they've sprouted all kinds of crazy abstractions
to deal with changing technology over a couple of decades.

### Performance
The code's efficient. The output might not be as fast as C++, and in some cases Java outperforms it as well. But it's
firmly in the "native performance" class of compilers and VMs. And it's a *lot* more memory-efficient than the JVM,
both in terms of baseline use and the control it gives you over memory layout and GC overhead, when you need it.
This control performance and over memory use makes a huge difference when you're trying to squeeze more traffic
into a smaller number of instances. And that translates directly into money saved.

## Further reading
If you're building a distributed system, you should also consider following up with the article's references to
articles and presentations by companies that have dealt with these problems:

- [Netflix](http://techblog.netflix.com/2013/01/optimizing-netflix-api.html)
- [Amazon](http://highscalability.com/amazon-architecture)
- [Groupon](https://engineering.groupon.com/2013/misc/i-tier-dismantling-the-monoliths/)
- [eBay](http://www.addsimplicity.com/downloads/eBaySDForum2006-11-29.pdf)
- [Gilt](http://www.slideshare.net/LappleApple/gilt-from-monolith-ruby-app-to-micro-service-scala-service-architecture)

There's also a reference in the article to the book [_The Art of Scalability_](http://theartofscalability.com/). I
haven't read it yet, but I'd be interested in your thoughts if you have.
