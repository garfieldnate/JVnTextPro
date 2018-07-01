# JVnTextPro

A Java-based Vietnamese Text Processing Tool

Original project URL: http://jvntextpro.sourceforge.net/

JVnTextPro is a Java open source tool, which is based on Conditional Random Fields (CRFs) and 
Maximum Entropy (Maxent), for Natural Language Processing (NLP) in Vietnamese. This tool 
consists of several steps (or sub-problem tools) for Vietnamese preprocessing and processing 
designed in a pipeline manner in which output of one step is used for the next step. The 
sub-problem tools are sentence segmentation tool, sentence tokenization tool, word segmentation 
tool and Part-of-Speech tagging tool. This tool would be useful for Vietnamese NLP community. 
We highly appreciate any bug report, comment, or suggestion that helps to fix errors and 
improve the accuracy.

## Usage

Requires [Java 8](https://java.com/en/download/) or later.

### Models

Starting with version 3.0.1, the models are released either as a separate jar or as part of the 
executable jar (see details below). If you would like to train your own models, please refer 
to the [original manual](http://jvntextpro.sourceforge.net/v2.0/JVnTextPro_Manual.pdf).

### Usage in Java

You should first add JVnTextPro as a dependency in your Maven, Ivy, or Gradle files. JVnTextPro is available from [JCenter](https://bintray.com/garfieldnate/general/JVnTextPro) and
[Maven Central](http://search.maven.org/#search|ga|1|JVnTextPro).

If you have not trained your own models (and most users have not!), then you should also download the jar containing the models.

You should also include an implementation for [slf4j](https://www.slf4j.org/). If you are just running some
experiments and don't care about where the logging statements go, you can just use the 
[slf4j Simple Binding](https://mvnrepository.com/artifact/org.slf4j/slf4j-simple).

For example, if you are using gradle and slf4j simple, you would add the following to your project:

    dependencies {
        compile 'vn.edu.vnu.jvntext.jvntextpro:JVnTextPro:3.0.1'
        classpath 'vn.edu.vnu.jvntext.jvntextpro:JVnTextPro:3.0.1:models'
        compile group: 'org.slf4j', name: 'slf4j-simple', version: '1.7.25'
    }

An example usage of the API is located in [src/main/java/vn/edu/vnu/jvntext/jvntextpro/Demo.java](src/main/java/vn/edu/vnu/jvntext/jvntextpro/Demo.java).
Just copy it into your project and edit as needed.

### Command line/terminal

Download the [executable jar](https://github.com/garfieldnate/JVnTextPro/releases/download/3.0.1/JVnTextPro-3.0.1-executable.jar)
from GitHub. Place the text you want to process into a text file or directory of text files. 
The text must be UTF-8 encoded. Then you can run JVnTextPro in your terminal like so:

    java -jar JVnTextPro-executable-X.X.X.jar -senseg -wordseg -postag -input <input file or directory>

If the input argument is a directory, it should contain `.txt` files to be analyzed. (You can changed
the expected file extension using the `-filetype` argument.) The output file(s) will have a `.pro` extension.

For more usage details, run the jar without any parameters.

## Downloads

* Version 3.0.1

    - [library jar](https://github.com/garfieldnate/JVnTextPro/releases/download/3.0.1/JVnTextPro-3.0.1.jar)
    - [models jar](https://github.com/garfieldnate/JVnTextPro/releases/download/3.0.1/JVnTextPro-3.0.1.jar)
    - [executable jar](https://github.com/garfieldnate/JVnTextPro/releases/download/3.0.1/JVnTextPro-3.0.1-executable.jar)
    - [sources jar](https://github.com/garfieldnate/JVnTextPro/releases/download/3.0.1/JVnTextPro-3.0.1-sources.jar)
    - [JavaDoc jar](https://github.com/garfieldnate/JVnTextPro/releases/download/3.0.1/JVnTextPro-3.0.1-javadoc.jar)

## Project Managers

* Cam-Tu Nguyen (1,2) (ncamtu at gmail dot com)
* Xuan-Hieu Phan (1) (pxhieu at gmail dot com)
* Thu-Trang Nguyen (1) (trangnt84 at gmail dot com)

1. College of Technology, Vietnam National University, Hanoi

2. Graduate School of Information Sciences (GSIS), Tohoku University, Japan

Version 3.0.1 was released by Nathan Glenn, an independent open source developer.

## Contributing

We gladly welcome and encourage contributions to the source code, models, training data,
documentation and any other aspect of the project. GitHub pull requests are welcome.
If you find issues or have a request, please file an issue on GitHub here:
https://github.com/garfieldnate/JVnTextPro/issues

If you would like to contribute but are unsure of how or what you can contribute, 
please contact the project managers.

## Related Work

* [JVnSegmenter](http://jvnsegmenter.sourceforge.net/): a Java-based Vietnamese Word Segmentation Tool
* [JNSP](http://jnsp.sourceforge.net/): a Java-based Ngram Statistic Package (for collocation detections - in English and Vietnamese)
* [FlexCRFs](http://flexcrfs.sourceforge.net/): Flexible Conditional Random Fields
* [GibbsLDA++](http://gibbslda.sourceforge.net/): A C/C++ and Gibbs Sampling-based Implementation of Latent Dirichlet Allocation (LDA)
* [CRFTagger](http://crftagger.sourceforge.net/): CRF English POS Tagger
* [CRFChunker](http://crfchunker.sourceforge.net/): CRF English Phrase Chunker
* [JTextPro](http://jtextpro.sourceforge.net/): A Java-based Text Processing Toolkit
* [JWebPro](http://jwebpro.sourceforge.net/): A Java-based Web Processing Toolkit

## Citation

If you use this tool in your research, please use the following citation:

    Cam-Tu Nguyen, Xuan-Hieu Phan and Thu-Trang Nguyen, "JVnTextPro: A Java-based Vietnamese Text Processing Tool", http://jvntextpro.sourceforge.net/, 2010.

## License

The project is released under the GPL version 2.0.
