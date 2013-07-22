# webloc-util

CLI for working with .webloc files on Mac OS X. Currently just converts .webloc files to HTML (as list of anchor tags) or Markdown (as list of MD formatted links). May add other operations in the future. Suggestions welcome.

## Installation

 1. Copy wu.sample to wu and overide M2_HOME and VERSION
 2. Use install-wu.sh script to: (a) build uberjar bin; (b) install bin in local maven repo; and (c) copy shell script to ~/bin 
    $ ./scripts/install-wu.sh
 3. Put ~/bin on your PATH (if not already there)     
    $ PATH=$PATH:~/bin/

## Usage

From Installed Binary:
 
    $ wu "markdown" ~/Desktop

From Source:

    $ lein run $format $file

 * format = { html | md }
 * file = absolute or relative path to .webloc file

## Examples

    $ ls
    shortcut_to_mushrooms.webloc

    $ more shortcut_to_mushrooms.webloc
    <?xml version="1.0" encoding="UTF-8"?>
    <!DOCTYPE plist PUBLIC "-//Apple//DTD PLIST 1.0//EN" "http://www.apple.com/DTDs/PropertyList-1.0.dtd">
    <plist version="1.0">
    <dict>
    <key>URL</key>
    <string>http://google.com</string>
    </dict>
    </plist>

    $ wu "html" .    
    <html>
     <head></head>
     <body>
      <ul>
       <li><a href="http://google.com">shortcut_to_mushrooms</a></li>
      </ul>
     </body>
    </html>

    $ lein run "markdown" .
     * [shortcut_to_mushrooms](http://google.com)

## Tech Refs

I referred to the following online resources while coding:

 * [Building Command Line Applications with Clojure](http://www.beaconhill.com/blog/?p=283)
 * [jeremyheiler/xenopath](https://github.com/jeremyheiler/xenopath)<blockquote>Xenopath is an XPath and DOM library for Clojure.<br/><br/>The primary goal of this project is to provide a straightforward Clojure interface to the JDK's built-in XPath and DOM packages. The advantage is that it allows you to operate on sequences and maps instead of DOM collection objects. The disadvantage is that you're still working with a mutable DOM, so tread carefully.</blockquote><blockquote cite="http://clojure-log.n01se.net/date/2013-03-15.html#14:59"><strong>jeremyheiler:</strong> I just released an XPath library https://github.com/jeremyheiler/xenopath. I wasn't a fan of <a href="https://github.com/kyleburton/clj-xpath">clj-xpath</a>, so that was born.</blockquote>
 * [StackOverflow > Getting the value of an element in XML in Clojure? > Using Christophe Grand's great Enlive library](http://stackoverflow.com/a/6329574/1695506)
 * [StackOverflow > Listing files in a directory in Clojure](http://stackoverflow.com/questions/8566531/listing-files-in-a-directory-in-clojure)
 * [StackOverflow > Compojure HTML Formatting](http://stackoverflow.com/questions/1918901/compojure-html-formatting)
    * [HTML Cleaner](http://htmlcleaner.sourceforge.net/download.php)
    * [Jsoup](http://jsoup.org/download)
    * [TagSoup](http://ccil.org/~cowan/XML/tagsoup/)
 * [GNU Emacs Manual > 18.3.1 Commands for Saving Files](http://www.gnu.org/software/emacs/manual/html_node/emacs/Save-Commands.html)
 * [Maven OS X Application Bundle Plugin](http://mojo.codehaus.org/osxappbundle-maven-plugin/)
 * [Bundle Programming Guide > Application Bundles > Anatomy of an OS X Application Bundle](https://developer.apple.com/library/mac/#documentation/CoreFoundation/Conceptual/CFBundles/BundleTypes/BundleTypes.html#//apple_ref/doc/uid/10000123i-CH101-SW19)

## License

Copyright © 2013 Roan O'Sullivan

Distributed under the Apache License v2.0.
