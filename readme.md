# vei
Pronounced "vay," `vei` is an extensible terminal-based text editor in Java. Inspired
heavily by `vim`, `vei` aims to be a lightweight, distraction-free, and high-octane
text editor that will make you feel like you're right out of a newly-released action movie.
`vei` shares many keybindings with `vim` - I'll make a full list once I actually have time -
but is focused on providing an easy-to-use interface so that anyone can customize it to their
likings. Namely, via `VeiPlugin` instances, you can do just about anything you'd like with
the editor.

## Features (and planned features)
There's not much as of now - I just started working on the project.
- Full text-based editing experience. This will make the editor significantly harder to use
  or pick up quickly, but you know what I have to say about that? Sucks to suck.
- Versatile command system. One of the downsides of `vim` is that you can do whatever you
  want, but you have to use vimscript - while it's not bad, it's a bit of an inconvenience.
  `vei` allows you to create completely custom commands in Java.
- Plugin system. You can create plugins that can do just about whatever you'd like them to
  do. There's not much more to say. As of now, plugin development will be fully supported in
  Java - in the future, I'm hoping to add support for JavaScript as well.
- Configurable. I haven't decided if I want to use XML or JSON for configuration, but I'm
  currently leaning towards JSON.
- Small codebase. Yep. It's not neat, but it's small.

## Purpose
Mostly for fun, to be honest - I'm a proud `vim` elitist, and I decided that I wanted to
try my hand at making a text editor that suits my needs perfectly. This is the result.
Written in Java, `vei` is also platform-independent and (relatively) fast (not compared
to compiled text editors, of course, but you get the point).

## Disclousre
I'm focusing on getting a functional editor before anything else, so the code is pretty
bad. I'm admitting that and saying this upfront so I'm essentially immune from any critique.
Yes, the code is bad. Yes, I know the code is bad.

# Licensing
This project is licensed under the GNU General Public License V3.
- This project depends on the Brownies Collections library, licensed under the Apache License 2.0.
  Only the required portions of this library are used. This library provides alternatives to the
  default `List` implementations. These alternatives are used to provide a boost to performance.
- This project depends on Lanterna, licensed under the GNU Lesser General Public License v3.0.
  Lanterna handles all of the UI stuff going on here - the terminal is all done throguh Lanterna.
