à la mode is a lightweight auto-update library for Java desktop applications.

PLEASE NOTE: a la mode update is still in ALPHA status. Do not use this in production without doing your own testing.

Please read ./manual.md for an overview of features, design, and use.


_Anticipated Questions_

Q. Does it work, in the sense that I can use it to update some software?

A. It appears to work.


Q. What does it lack?

A. Tracking/deleting deleted files. Access control for update clients. Compression. Robust updater updating. Reasonable support for symlinks (see: all Java ever).


Q. What does it do unreasonably to symlinks?

A. Follows them completely blindly, perhaps taking infinite time to complete because of loops. In the best case, though, they are simply ignored. Just don't try to distribute symlinks. That seems pretty reasonable, right?
