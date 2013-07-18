# webloc-util

CLI for working with .webloc files on Mac OS X. Currently just converts .webloc files to HTML (as list of anchor tags) or Markdown (as list of MD formatted links). May add other operations in the future. Suggestions welcome.

## Installation

Once I write a wrapper shell script ...

    $ lein install
    $ cp scripts/webloc-util.sh ~/bin

## Usage

If running from source, use "lein run [args]":

    $ lein run $format $file

 * format = { html | md }
 * file = absolute or relative path to .webloc file

Until I write a shell script, need to run binaries the old-fashioned way:

    $ java -jar webloc-util-0.1.0-standalone.jar $format $file

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

    $ java -jar webloc-util-0.1.0-standalone.jar html shortcut_to_mushrooms.webloc
    <a href="http://google.com">shortcut_to_mushrooms</a>

    $ java -jar webloc-util-0.1.0-standalone.jar md shortcut_to_mushrooms.webloc
    [shortcut_to_mushrooms](http://google.com)

## Tech Refs

I referred to the following online resources while coding:

 * [Building Command Line Applications with Clojure](http://www.beaconhill.com/blog/?p=283)

## License

Copyright © 2013 Roan O'Sullivan

Distributed under the Eclipse Public License, the same as Clojure.
