# JVnTextPro

A Java-based Vietnamese Text Processing Tool

Project URL: http://jvntextpro.sourceforge.net/

JVnTextPro is a Java open source tool, which is based on Conditional Random Fields (CRFs) and Maximum Entropy (Maxent), for Natural Language Processing (NLP) in Vietnamese. This tool consists of several steps (or sub-problem tools) for Vietnamese preprocessing and processing designed in a pipeline manner in which output of one step is used for the next step. The sub-problem tools are sentence segmentation tool, sentence tokenization tool, word segmentation tool and Part-of-Speech tagging tool. This tool would be useful for Vietnamese NLP community. We highly appreciate any bug report, comment, and suggestion that help to fix errors and improve the accuracy.

Project Managers:

* Cam-Tu Nguyen (1,2) (ncamtu at gmail dot com)
* Xuan-Hieu Phan (1) (pxhieu at gmail dot com)
* Thu-Trang Nguyen (1) (trangnt84 at gmail dot com)

1. College of Technology, Vietnam National University, Hanoi

2. Graduate School of Information Sciences (GSIS), Tohoku University, Japan

Version 3.0.0 was released by Nathan Glenn.

JVnTextPro is developed by the project managers and under the terms of the GNU General Public License. 
We highly welcome anyone, who would like to develop this tool for the benifits of NLP community in general and Vietnamese NLP community in particular, to join us. 
Please contact the project managers for further details.

## Downloads

* Version 3.0.0

    - [library jar](https://github.com/garfieldnate/JVnTextPro/releases/download/3.0.0/JVnTextPro-3.0.0.jar)
    - [executable jar](https://github.com/garfieldnate/JVnTextPro/releases/download/3.0.0/JVnTextPro-executable-3.0.0.jar)
    - [sources jar](https://github.com/garfieldnate/JVnTextPro/releases/download/3.0.0/JVnTextPro-3.0.0-sources.jar)
    - [JavaDoc jar](https://github.com/garfieldnate/JVnTextPro/releases/download/3.0.0/JVnTextPro-3.0.0-javadoc.jar)

## Related Work

[JVnSegmenter](http://jvnsegmenter.sourceforge.net/): a Java-based Vietnamese Word Segmentation Tool
[JNSP](http://jnsp.sourceforge.net/): a Java-based Ngram Statistic Package (for collocation detections - in English and Vietnamese)
[FlexCRFs](http://flexcrfs.sourceforge.net/): Flexible Conditional Random Fields
[GibbsLDA++](http://gibbslda.sourceforge.net/): A C/C++ and Gibbs Sampling-based Implementation of Latent Dirichlet Allocation (LDA)
[CRFTagger](http://crftagger.sourceforge.net/): CRF English POS Tagger
[CRFChunker](http://crfchunker.sourceforge.net/): CRF English Phrase Chunker
[JTextPro](http://jtextpro.sourceforge.net/): A Java-based Text Processing Toolkit
[JWebPro](http://jwebpro.sourceforge.net/): A Java-based Web Processing Toolkit

## Citation

Researches using this tool for running experiments should include the following citation:

Cam-Tu Nguyen, Xuan-Hieu Phan and Thu-Trang Nguyen, "JVnTextPro: A Java-based Vietnamese Text Processing Tool", http://jvntextpro.sourceforge.net/, 2010.

## License

The project is released under the GPL version 3.0.
