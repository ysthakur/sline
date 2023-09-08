# sline

This is a cross-platform library for making interactive CLIs/REPLs in Scala
(JVM/Native).

Note that it doesn't do the actual line editing itself: on the JVM, it uses
[JLine], and in Native, it uses [replxx]. sline itself doesn't include all the
features that JLine and replxx provide, but you can always access the underlying
`LineReader`/`Replxx` instances to do JLine/replxx stuff directly.

A facade to replxx is provided in the Native version.

## Demo

There's a demo in the [`demo`](./demo) folder.

To try it on the JVM:

```sh
./mill -i "demo[3.3.0]".jvm.runLocal
```

To try it using Native:

```sh
./mill "demo[3.3.0]".native.nativeLink
./out/demo/3.3.0/native/nativeLink.dest/out
```

[JLine]: https://github.com/jline/jline3
[replxx]: https://github.com/AmokHuginnsson/replxx/
