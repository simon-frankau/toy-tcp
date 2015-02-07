ToyTCP
======

This is a toy TCP/IP stack implementation, in order to practice my
understanding of TCP/IP as I work through TCP/IP Illustrated. It hooks
into the rest of the system through PPP.

Yes, I could have gone for TUN/TAP, but I'd learn less, and might end
up somewhat more system-specific.

It's not even alpha yet. It doesn't even establish an IP-level link
right now.

To use
------

Build, and run "go.sh". This all assumes you're building with Idea,
and working on a platform that behaves like MacOS X. I've not tested
any cross-anything compatibility, I'm afraid.

TODO: There are some nasty hardwired paths in there for now.

Architecture
------------

Performance is not really the aim. After all, I'm writing a TCP/IP
stack in Java. I'm mostly looking for simplicity, and ease of testing
(lots of unit tests, etc.). Since the basic structure is a stack with
things being passed along, the general model for each component is for
something that takes in events and sends out events. Lots of callback
interfaces.

It's all a bit "How a network stack might be written in Enterprise
Java Style".

### Threading

I can see two simple and reliable threading/locking models:
 * Single thread, global lock
 * Multiple threads, one per layer, with queues between the layers.

(Previously I've used lock hierarchies, but if you're going up and
down the stack getting a lock ordering is error-prone, so I'm not
doing that)

Keeping it simple, and not wanting the overhead of lots of interacting
threads (blah, blah, yes, Disruptor, blah, blah - I don't care), I'm
going for the first option. So, we have a big lock on the core of the
stack.

(TODO: Install a lock, once we actually have more event sources than
reading from the connection!)

### IPv6

I was originally hoping to support this as I went along, to understand
this all the better. Having read some of TCP/IP Illustrated, I'm
seeing serious Second System Effect, and the complexity is boggling me
slightly. So, I'll probably come back to this.

### TODO

There are plenty of loose ends with the code. It's got to the point
where it can just scrape an LCP Up, but there are many things that
should be corrected:

* Build with Maven (remove classpath hacks)
* Get sensible and consistent logging behaviour
* Get the state machine to handle identifiers appropriately
* Have the config checker deal with acks/rejects/re-initialising
* Generally polish up state machine
* Implement time-outs! This will finally require setting up timers...
* WriteBuffer should derive from Buffer, Buffer should be read-only
* WriteBuffer should efficiently (zero copy) expand its header
* Re-read the RFC and go through the implementation with a
  fine-toothed comb

Problems
--------

Here are some of the problems I've encountered:

* */etc/ppp/options: no such file or directory* The file must exist,
   even if it's empty.
