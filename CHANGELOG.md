# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](http://keepachangelog.com/en/1.0.0/)
and this project adheres to [Semantic Versioning](http://semver.org/spec/v2.0.0.html).

## [3.0.3] - 2018-07-03

### Fixed

* Once again fixing a problem with external model loading. The previous release would have worked if the author had run "clean" before releasing D:
* Previous fix was not aggressive enough; updated code for configuring CRF and MaxEnt taggers.

## [3.0.2] - 2018-07-02

### Fixed

* Models loading from resource jar was fixed. Several internal APIs had to be changed for this fix.

## [3.0.1] - 2018-07-01

### Changed

* Models are now provided in a separate jar file

## [3.0.0] - 2018-06-30

This is the first release since 2010. The codebase has been cleaned and updated extensively, and the process of building and running has been improved dramatically.

### Changed

* All classes were moved into the package `vn.edu.vnu.jvntext`.
* Now requires Java 1.8 (released March, 2014). The current Java compiler no longer supports targeting versions previous to 1.7, and 1.8 contains many convenient functions.
* The library no longer prints all logging statements to standard out. Instead, messages are logged via the [slf4j](https://www.slf4j.org/) API. Clients including the library in their own project must pick their own implementation library (such as logback). The executable jar still prints to standard out.
* Removed C-style exception handling; many methods which returned `boolean` to indicate a failure are now `void` and have a `throws` declaration.
* Removed string paths from most public-facing methods. Java 7's NIO (Path, Files, etc.) is now used extensively.

### Added

* Now available as a package on [JCenter](https://bintray.com/bintray/jcenter) and [Maven Central](https://search.maven.org/).
* Gradle build script for generating and publishing all artifacts and publishing.
* `gradle fatJar` generates an executable jar which runs `JVnTextProTest`, a command line program for analyzing Vietnamese text. This will be available as a release artifact on GitHub.
* 0-argument `init()` method in all classes which require model or other files. The models directory is now included in the jar and loaded automatically when no other models are provided. For example, `JVnSenSegmenter.init()` loads pre-trained models. `JVnTextPro` has similarly been updated with 0-argument initialization methods for each submodule: `initSenSegmenter()`, `initSegmenter()`, etc.
