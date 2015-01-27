ToyTCP
======

This is a toy TCP/IP stack implementation, in order to practice my
understanding of TCP/IP as I work through TCP/IP Illustrated.

To use
------

Build, and run "go.sh". This all assumes you're building with Idea,
and working on a platform that behaves like MacOS X. I've not tested
any cross-anything compatibility, I'm afraid.

Architecture
------------

Performance is not really the aim. After all, I'm writing a TCP/IP
stack in Java. I'm mostly looking for simplicity, and ease of testing
(lots of unit tests, etc.). Since the basic structure is a stack with
things being passed along, the general model for each component is for
something that takes in events and sends out events. Lots of callback
interfaces.

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

Problems
--------

Here are some of the problems I've encountered:

* */etc/ppp/options: no such file or directory* The file must exist,
   even if it's empty.
