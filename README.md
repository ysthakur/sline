# sline

This is a cross-platform library for making interactive CLIs/REPLs in Scala.
It doesn't do the actual line editing itself: on the JVM, it uses [JLine], and
in Native, it uses [replxx].

To try the JVM demo:

```sh
./mill -i "demo[3.3.0]".jvm.runLocal
```

To try the Native demo:

```sh
./mill "demo[3.3.0]".native.nativeLink
./out/demo/3.3.0/native/nativeLink.dest/out
```

[JLine]: https://github.com/jline/jline3
[replxx]: https://github.com/AmokHuginnsson/replxx/
