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
package vn.edu.vnu.jvntext.jvnsegmenter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.Vector;

import javax.xml.parsers.ParserConfigurationException;

import vn.edu.vnu.jvntext.jflexcrf.Labeling;
import vn.edu.vnu.jvntext.jflexcrf.Option;
import vn.edu.vnu.jvntext.jvntextpro.data.DataReader;
import vn.edu.vnu.jvntext.jvntextpro.data.DataWriter;
import vn.edu.vnu.jvntext.jvntextpro.data.TaggingData;
import vn.edu.vnu.jvntext.utils.InitializationException;
import vn.edu.vnu.jvntext.utils.PathUtils;

public class CRFSegmenter {
    private static final Logger logger = LoggerFactory.getLogger(CRFSegmenter.class);
    private static final String FEATURE_TEMPLATE_FILE_NAME = "featuretemplate.xml";

    /**
     * The reader.
     */
    private DataReader reader = new WordDataReader();

    /**
     * The writer.
     */
    private DataWriter writer = new WordDataWriter();

    /**
     * The data tagger.
     */
    private final TaggingData dataTagger = new TaggingData();

    /**
     * The labeling.
     */
    private Labeling labeling = null;

    /**
     * Instantiates a new cRF segmenter.
     *
     * @param modelDir the model dir
     */
    public CRFSegmenter(Path modelDir) throws InitializationException, IOException {
        logger.info("Initializing CRFSegmenter from " + modelDir + "...");
        //Read feature template file
        Path templateFile = modelDir.resolve(FEATURE_TEMPLATE_FILE_NAME);
        try {
            Vector<Element> nodes = BasicContextGenerator.readFeatureNodes(templateFile);

            initContextGenerators(modelDir, nodes, dataTagger);

            //create context generators
            Option options = new Option(modelDir);
            labeling = new Labeling(options, dataTagger, reader, writer);
        } catch (ParserConfigurationException | SAXException e) {
            throw new InitializationException(e);
        }
    }

    /**
     * Instantiates a new cRF segmenter.
     */
    public CRFSegmenter() throws IOException, InitializationException {
        logger.info("Initializing CRFSegmenter from class resources");
        //Read feature template file
        Path templateFile;
        try {
            templateFile = PathUtils.getPath(CRFSegmenter.class.getResource(FEATURE_TEMPLATE_FILE_NAME).toURI());
        } catch (URISyntaxException e) {
            throw new InitializationException(e);
        }
        Vector<Element> nodes;
        try {
            nodes = BasicContextGenerator.readFeatureNodes(templateFile);
        } catch (SAXException | ParserConfigurationException e) {
            throw new InitializationException(e);
        }
        Option crfOptions = new Option(CRFSegmenter.class);
        initContextGenerators(crfOptions.getModelDir(), nodes, dataTagger);
        //create context generators
        labeling = new Labeling(crfOptions, dataTagger, reader, writer);
    }

    public static void initContextGenerators(Path modelDir, Vector<Element> nodes, TaggingData dataTagger) throws IOException {
        for (Element node : nodes) {
            String cpType = node.getAttribute("value");
            BasicContextGenerator contextGen = null;

            switch (cpType) {
                case "Conjunction":
                    contextGen = new ConjunctionContextGenerator(node);
                    break;
                case "Lexicon":
                    contextGen = new LexiconContextGenerator(node);
                    LexiconContextGenerator.loadVietnameseDict(modelDir.resolve("VNDic_UTF-8.txt"));
                    LexiconContextGenerator.loadViLocationList(modelDir.resolve("vnlocations.txt"));
                    LexiconContextGenerator.loadViPersonalNames(modelDir.resolve("vnpernames.txt"));
                    break;
                case "Regex":
                    contextGen = new RegexContextGenerator(node);
                    break;
                case "SyllableFeature":
                    contextGen = new SyllableContextGenerator(node);
                    break;
                case "ViSyllableFeature":
                    contextGen = new VietnameseContextGenerator(node);
                    break;
            }

            if (contextGen != null) dataTagger.addContextGenerator(contextGen);
        }
    }

    /**
     * Segmenting.
     *
     * @param instr the instr
     * @return the string
     */
    public String segmenting(String instr) {
        return labeling.strLabeling(instr);
    }

    /**
     * Segmenting.
     *
     * @param file the file
     * @return the string
     */
    public String segmenting(File file) throws IOException {
        return labeling.strLabeling(file);
    }

    /**
     * Sets the data reader.
     *
     * @param reader the new data reader
     */
    public void setDataReader(DataReader reader) {
        this.reader = reader;
    }

    /**
     * Sets the data writer.
     *
     * @param writer the new data writer
     */
    public void setDataWriter(DataWriter writer) {
        this.writer = writer;
    }

}
