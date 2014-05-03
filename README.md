[![Build Status](https://snap-ci.com/geb/geb/branch/master/build_image)](https://snap-ci.com/geb/geb/branch/master)

Geb (pronounced “jeb”) is a browser automation solution. It brings together the power of WebDriver, the elegance of jQuery content selection, the robustness of Page Object modelling and the expressiveness of the Groovy language.

For more information about the project, see the [http://www.gebish.org](http://www.gebish.org/).

## How to contribute

### Build Environment

Geb builds with [Gradle](http://www.gradle.org/ "Home - Gradle"). You do not need to have Gradle installed to work with the Geb build as Gradle provides an executable wrapper that you use to drive the build.

On UNIX type environments this is `gradlew` and is `gradlew.bat` on Windows.

For example to run the Geb test suite for the entire project you would run…

    ./gradlew test

### Contributing Documentation

Geb documentation comes in two forms: the manual and the API (i.e. the Groovydoc amongst the source).

#### The Manual

The manual project can be found at `doc/manual` within the project tree. The [Markdown](http://daringfireball.net/projects/markdown/ "Daring Fireball: Markdown") source files, HTML templates, CSS and JavaScript that make up the manual can be found at `doc/manual/src` (the manual is compiled using a tool called [markdown2book](https://github.com/geb/markdown2book)).

Most documentation contributions are simply modifications to these files.

To compile the manual in or to see any changes made, simply run (from the root of the geb project)…

    ./gradlew :doc:manual:compileManual

You will then find the compiled HTML in the directory `doc/manual/build/manual`

#### The API reference

The API reference is made up of the Groovydoc (like Javadoc) that annotates the Groovy files for the different modules in `module/`. To make a change to the reference API documentation, find the corresponding file in `module/«module»/src/main/groovy` and make the change.

You can then generate the API reference HTML by running…

    ./gradlew :doc:manual:compileApi

You will then find the compiled HTML in the directory `doc/manual/build/manual/api`

> Note that you can build the manual chapters and reference API in one go with `./gradlew doc:manual:compile`

### Contributing features/patches

The source code for all of the modules is contained in the `module/` directory.

To run the tests after making your change to a module, you can run…

    ./gradlew :module:«module-name»:test

There are lots of example tests in the `geb-core` module that use the classes from the `test-support` module for running against an in memory HTTP server.

To run the entire test suite, run…

    ./gradlew test
    
## Development Mailing List

If you want to do some work on Geb and want some help, you can join the `dev@geb.codehaus.org` mailing list via [Codehaus' Xircles](http://xircles.codehaus.org/projects/geb/lists "Codehaus: Geb: Lists").
