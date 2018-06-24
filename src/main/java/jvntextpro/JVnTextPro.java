/*
 Copyright (C) 2010 by
 *
 * 	Cam-Tu Nguyen
 *  ncamtu@ecei.tohoku.ac.jp or ncamtu@gmail.com
 *
 *  Xuan-Hieu Phan
 *  pxhieu@gmail.com
 *
 *  College of Technology, Vietnamese University, Hanoi
 * 	Graduate School of Information Sciences, Tohoku University
 *
 * JVnTextPro-v.2.0 is a free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published
 * by the Free Software Foundation; either version 2 of the License,
 * or (at your option) any later version.
 *
 * JVnTextPro-v.2.0 is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with  JVnTextPro-v.2.0); if not, write to the Free Software Foundation,
 * Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 */
package jvntextpro;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;

import jvnpostag.MaxentTagger;
import jvnsegmenter.CRFSegmenter;
import jvnsensegmenter.JVnSenSegmenter;
import jvntextpro.conversion.CompositeUnicode2Unicode;
import jvntextpro.util.VnSyllParser;
import jvntokenizer.PennTokenizer;
import vnu.jvntext.utils.InitializationException;

public class JVnTextPro {

    //==============================================
    // Instance Variables
    //==============================================
    /**
     * The vn sen segmenter.
     */
    JVnSenSegmenter vnSenSegmenter = null;

    /**
     * The vn segmenter.
     */
    CRFSegmenter vnSegmenter = null;

    /**
     * The vn pos tagger.
     */
    MaxentTagger vnPosTagger = null;

    /**
     * The is tokenization.
     */
    boolean isTokenization = false;

    /**
     * The convertor.
     */
    public CompositeUnicode2Unicode convertor;

    //==============================================
    // Constructors
    //==============================================

    /**
     * Instantiates a new j vn text pro.
     */
    public JVnTextPro() throws IOException {
        convertor = new CompositeUnicode2Unicode();
    }

    //==============================================
    // initial methods
    //==============================================

    /**
     * Initialize the sentence segmetation for Vietnamese return true if the initialization is successful and false
     * otherwise.
     *
     * @param modelDir the model dir
     * @return true, if successful
     */
    public void initSenSegmenter(Path modelDir) throws IOException {
        vnSenSegmenter = new JVnSenSegmenter();
        vnSenSegmenter.init(modelDir);
    }

    public void initSenSegmenter() throws IOException {
        vnSenSegmenter = new JVnSenSegmenter();
        vnSenSegmenter.init();
    }

    /**
     * Initialize the word segmetation for Vietnamese.
     *
     * @param modelDir the model dir
     * @return true if the initialization is successful and false otherwise
     */
    public void initSegmenter(Path modelDir) throws InitializationException {
        vnSegmenter = new CRFSegmenter(modelDir);
    }

    public void initSegmenter() throws InitializationException {
        vnSegmenter = new CRFSegmenter();
    }

    /**
     * Initialize the pos tagger for Vietnamese.
     *
     * @param modelDir the model dir
     * @return true if the initialization is successful and false otherwise
     */
    public void initPosTagger(Path modelDir) throws IOException, InitializationException {
        vnPosTagger = new MaxentTagger(modelDir);
    }

    public void initPosTagger() throws InitializationException {
        vnPosTagger = new MaxentTagger();
    }

    /**
     * Initialize the sentence tokenization.
     */
    public void initSenTokenization() {
        isTokenization = true;
    }
    //==============================================
    // public methods
    //==============================================

    /**
     * Process the text and return the processed text pipeline : sentence segmentation, tokenization, word segmentation,
     * part of speech tagging.
     *
     * @param text text to be processed
     * @return processed text
     */
    public String process(String text) {
        String ret = text;

        //Pipeline
        ret = convertor.convert(ret);
        ret = senSegment(ret);
        ret = senTokenize(ret);
        ret = wordSegment(ret);
        ret = postProcessing(ret);
        ret = posTagging(ret);
        return ret;
    }

    /**
     * Process a file and return the processed text pipeline : sentence segmentation, tokenization, tone recover, word
     * segmentation.
     *
     * @param infile data file
     * @return processed text
     */
    public String process(File infile) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(infile), "UTF-8"));

        String line;
        StringBuilder data = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            data.append(line).append("\n");
        }
        reader.close();

        return process(data.toString());
    }

    /**
     * Do sentence segmentation.
     *
     * @param text text to have sentences segmented
     * @return the string
     */
    public String senSegment(String text) {
        String ret = text;

        //Segment sentences
        if (vnSenSegmenter != null) {
            ret = vnSenSegmenter.senSegment(text);
        }

        return ret.trim();
    }

    /**
     * Do sentence tokenization.
     *
     * @param text to be tokenized
     * @return the string
     */
    public String senTokenize(String text) {
        String ret = text;

        if (isTokenization) {
            ret = PennTokenizer.tokenize(text);
        }

        return ret.trim();
    }

    /**
     * Do word segmentation.
     *
     * @param text to be segmented by words
     * @return text with words segmented, syllables in words are joined by '_'
     */
    public String wordSegment(String text) {
        String ret = text;

        if (vnSegmenter == null) return ret;
        ret = vnSegmenter.segmenting(ret);
        return ret;
    }

    /**
     * Do pos tagging.
     *
     * @param text to be tagged with POS of speech (need to have words segmented)
     * @return the string
     */
    public String posTagging(String text) {
        String ret = text;
        if (vnPosTagger != null) {
            ret = vnPosTagger.tagging(text);
        }

        return ret;
    }

    /**
     * Do post processing for word segmentation: break not valid vietnamese words into single syllables.
     *
     * @param text the text
     * @return the string
     */
    public String postProcessing(String text) {

        String[] lines = text.split("\n");
        String ret = "";

        for (String line : lines) {
            String[] words = line.split("[ \t]");
            String templine = "";

            for (String currentWord : words) {
                //break word into syllable and check if one of it is not valid vi syllable
                String[] syllables = currentWord.split("_");
                boolean isContainNotValidSyll = false;

                for (String syllable : syllables) {
                    VnSyllParser parser = new VnSyllParser(syllable.toLowerCase());

                    if (!parser.isValidVnSyllable()) {
                        isContainNotValidSyll = true;
                        break;
                    }
                }

                if (isContainNotValidSyll) {
                    String temp = "";

                    for (String syll : syllables) {
                        temp += syll + " ";
                    }

                    templine += temp.trim() + " ";
                } else templine += currentWord + " ";
            }

            ret += templine.trim() + "\n";
        }

        return ret.trim();
    }
}
