ToyTCP
======

This is a toy TCP/IP stack implementation, in order to practice my
understanding of TCP/IP as I work through TCP/IP Illustrated.

To use
------

Build, and run "go.sh". This all assumes you're building with Idea,
and working on a platform that behaves like MacOS X. I've not tested
any cross-anything compatibility, I'm afraid.

Problems
--------

Here are some of the problems I've encountered:

* */etc/ppp/options: no such file or directory* The file must exist,
   even if it's empty.
