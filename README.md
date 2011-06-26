Geb is a tool for automating web browsers for web testing, screen scraping and process automation.

Project home: [http://www.gebish.org](http://www.gebish.org/)

## Build Instructions

To build Geb, you will need Git installed. Once you have cloned Geb…

    git clone git://github.com/geb/geb.git

You then need to get the submodules. To do this you need to run the following two commands:

    git submodule init
    git submodule update

You can now build Geb using the gradle wrapper…

    ./gradlew jar

