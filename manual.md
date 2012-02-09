__Overview__

a la mode software update is essentially a one-way synchronization protocol. It attempts to make a client-local directory tree resemble an authoritative server-side tree. There is no concept of a monotonic version number. The update software does *not* advance the target software atomicly from one version to the next. However, if updates are only put online in monolithic blocks, and there is no need to support multiple software versions simultaneously, a la mode can easily function to push discrete versions.

The only network protocol necessary is http. There is no server software component other than an http server, or a content distribution network such as Amazon Cloudfront. You are welcome to complicate the server-side architecture with load-balancers and reverse proxies and whatnot; a la mode should handle it.


__Update Site__

First, let us define 'target' or 'target software' as the primary software package that a la mode is going to update. 

A server-side 'update site' consists of a file distribution, an index, and a version file.

The distribution is literally a copy of how you want the target software's directory to look after a client's next update. These are all of your program files, laid out just as they would be on an installed instance of the software.

The index is also rooted in the same directory as the distribution and is called, imaginatively, 'alamode.index'. Conceptually, this is a list of all files in the distribution, plus their length and an md5 hash of the contents. However, when the index is created, its size can be managed by restricting which subdirectories are actually targeted for update. For instance, if you have a 'lib' directory containing 3rd party libraries, and that has never changed over your entire version history, there is no need to index it. If you do change it in the future, simply add that directory to the next index.

The version file, called 'version', is the md5 hash of the index file.

The update site should be on an http server publicly accessible to all of the client machines you expect to update. For a publicly distributed program, the update site must be publicly accessible. At the present time, there is no support for access control. [However, it would be straight-forward to implement access control using client-side certificates and server-side cert authentication, at the cost of some simplicity and performance.]

An update site is identified by a URL fragment pointing to the root directory of the update site. For instance, "http://updates.htssoft.com/just_tactics". Distribution files then live at, for example, "http://updates.htssoft.com/just_tactics/assets/materials/GlowParticle.j3md". The index and version would be at "http://updates.htssoft.com/just_tactics/alamode.index" and "http://updates.htssoft.com/just_tactics/version" respectively.


__Update Process__

a la mode is written to be run at every target software startup. It can be run on-demand instead if silent or continuous update is not necessary for your application.

Each time a la mode is invoked, it expects to be handed a root directory to synchronize (this is typically the install directory of your application) and a path to a properties file. Currently, the only property in the file is "update.site = [update_site]".

a la mode then uses the update site's url to retrieve the remote version file.

If there is no local alamode.index, or its hash differs from the remote version, then the remote alamode.index is downloaded.

A la mode now scans all files listed in the local alamode.index. For each file whose signature differs from the indexed signature, a la mode downloads that file from the remote site. This scan should complete quickly--in testing, 30,000 files totalling about 2.7GB takes around 30 seconds.

And that's it. A crash anywhere along the way simply results in either redownloading the index, or rescanning some already-downloaded files before continuing with the update.


__Building an Update Site__

This is easy on *nix. If you're somewhere else, that sucks...

Add the scripts directory of this project to your path. cd into build and run 'ant ud', which will build alamode.jar and place it in the scripts directory. Congratulations, you've acquired the alamode_index; you'll use it on the dungeon boss.

Copy your target software's distribution files into an empty directory. cd into that directory and run "alamode_index [...]", where [...] should be replaced with a space-separated list of targets. If every subdirectory and root file has potentially changed, then just doing "alamode_index ." should work the treat.

Now, copy that whole directory to the location corresponding to the URL that you distributed in your users' alamode.props.
