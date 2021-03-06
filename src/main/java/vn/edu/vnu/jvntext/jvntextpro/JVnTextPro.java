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
 * JVnTextPro is a free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published
 * by the Free Software Foundation; either version 2 of the License,
 * or (at your option) any later version.
 *
 * JVnTextPro is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with  JVnTextPro); if not, write to the Free Software Foundation,
 * Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 */
package vn.edu.vnu.jvntext.jvntextpro;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;

import vn.edu.vnu.jvntext.jvnpostag.MaxentTagger;
import vn.edu.vnu.jvntext.jvnsegmenter.CRFSegmenter;
import vn.edu.vnu.jvntext.jvnsensegmenter.JVnSenSegmenter;
import vn.edu.vnu.jvntext.jvntextpro.conversion.CompositeUnicode2Unicode;
import vn.edu.vnu.jvntext.jvntextpro.util.VnSyllParser;
import vn.edu.vnu.jvntext.jvntokenizer.PennTokenizer;
import vn.edu.vnu.jvntext.utils.InitializationException;

public class JVnTextPro {

    //==============================================
    // Instance Variables
    //==============================================

    private JVnSenSegmenter vnSenSegmenter = null;
    private CRFSegmenter vnSegmenter = null;
    private MaxentTagger vnPosTagger = null;
    private boolean isTokenization = false;
    private CompositeUnicode2Unicode unicodeNormalizer;

    //==============================================
    // Constructors
    //==============================================

    /**
     * Instantiates a new j vn text pro.
     */
    public JVnTextPro() throws IOException {
        unicodeNormalizer = new CompositeUnicode2Unicode();
    }

    //==============================================
    // initial methods
    //==============================================

    /**
     * Initialize the sentence segmetation for Vietnamese return true if the initialization is successful and false
     * otherwise.
     *
     * @param modelDir the model dir
     */
    public void initSenSegmenter(Path modelDir) throws IOException, InitializationException {
        vnSenSegmenter = new JVnSenSegmenter();
        vnSenSegmenter.init(modelDir);
    }

    public void initSenSegmenter() throws IOException, InitializationException {
        vnSenSegmenter = new JVnSenSegmenter();
        vnSenSegmenter.init();
    }

    /**
     * Initialize the word segmetation for Vietnamese.
     *
     * @param modelDir the model dir
     */
    public void initSegmenter(Path modelDir) throws InitializationException, IOException {
        vnSegmenter = new CRFSegmenter(modelDir);
    }

    public void initSegmenter() throws InitializationException, IOException {
        vnSegmenter = new CRFSegmenter();
    }

    /**
     * Initialize the pos tagger for Vietnamese.
     *
     * @param modelDir the model dir
     */
    public void initPosTagger(Path modelDir) throws InitializationException, IOException {
        vnPosTagger = new MaxentTagger(modelDir);
    }

    public void initPosTagger() throws InitializationException, IOException {
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
        ret = unicodeNormalizer.convert(ret);
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
        StringBuilder ret = new StringBuilder();

        for (String line : lines) {
            String[] words = line.split("[ \t]");
            StringBuilder templine = new StringBuilder();

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
                    StringBuilder temp = new StringBuilder();

                    for (String syll : syllables) {
                        temp.append(syll).append(" ");
                    }

                    templine.append(temp.toString().trim()).append(" ");
                } else templine.append(currentWord).append(" ");
            }

            ret.append(templine.toString().trim()).append("\n");
        }

        return ret.toString().trim();
    }
}
