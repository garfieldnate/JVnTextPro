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
package jvnsegmenter;

import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.Vector;

import javax.xml.parsers.ParserConfigurationException;

import jflexcrf.Labeling;
import jvntextpro.data.DataReader;
import jvntextpro.data.DataWriter;
import jvntextpro.data.TaggingData;
import vnu.jvntext.utils.InitializationException;
import vnu.jvntext.utils.PathUtils;

public class CRFSegmenter {

    /**
     * The reader.
     */
    DataReader reader = new WordDataReader();

    /**
     * The writer.
     */
    DataWriter writer = new WordDataWriter();

    /**
     * The data tagger.
     */
    final TaggingData dataTagger = new TaggingData();

    /**
     * The labeling.
     */
    Labeling labeling = null;

    /**
     * Instantiates a new cRF segmenter.
     *
     * @param modelDir the model dir
     */
    public CRFSegmenter(Path modelDir) throws InitializationException, IOException {
        init(modelDir);
    }

    /**
     * Instantiates a new cRF segmenter.
     */
    public CRFSegmenter() throws InitializationException, IOException {
        init();
    }

    /**
     * Inits the.
     *
     * @param modelDir the model dir
     */
    private void init(Path modelDir) throws InitializationException, IOException {
        System.out.println("Initializing JVnSegmenter from " + modelDir + "...");
        //Read feature template file
        Path templateFile = modelDir.resolve("featuretemplate.xml");
        try {
            Vector<Element> nodes = BasicContextGenerator.readFeatureNodes(templateFile);

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

            //create context generators
            labeling = new Labeling(modelDir, dataTagger, reader, writer);
        } catch (ParserConfigurationException | SAXException e) {
            throw new InitializationException(e);
        }
    }

    public void init() throws InitializationException, IOException {
        Path modelDir;
        try {
            modelDir = PathUtils.getPath(CRFSegmenter.class.getResource("/" + CRFSegmenter.class.getPackage().getName())
                                                           .toURI());
        } catch (URISyntaxException | IOException e) {
            // this should never happen
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        init(modelDir);
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
