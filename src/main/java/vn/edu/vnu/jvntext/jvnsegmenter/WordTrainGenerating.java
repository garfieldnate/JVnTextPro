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

import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Vector;

import javax.xml.parsers.ParserConfigurationException;

import vn.edu.vnu.jvntext.jvntextpro.data.TaggingData;
import vn.edu.vnu.jvntext.jvntextpro.data.TrainDataGenerating;
import vn.edu.vnu.jvntext.utils.InitializationException;

public class WordTrainGenerating extends TrainDataGenerating {

    /**
     * The model dir.
     */
    private final Path modelDir;

    /**
     * Instantiates a new word train generating.
     *
     * @param modelDir the model dir
     */
    public WordTrainGenerating(Path modelDir) throws InitializationException {
        this.modelDir = modelDir;
        init();
    }

    /* (non-Javadoc)
     * @see TrainDataGenerating#init()
     */
    @Override
    public void init() throws InitializationException {
        reader = new IOB2DataReader();
        tagger = new TaggingData();

        //Read feature template file
        Path templateFile = modelDir.resolve("featuretemplate.xml");
        try {
            Vector<Element> nodes = BasicContextGenerator.readFeatureNodes(templateFile);
            CRFSegmenter.initContextGenerators(modelDir, nodes, tagger);
        } catch (IOException | SAXException | ParserConfigurationException e) {
            throw new InitializationException(e);
        }
    }

    /**
     * The main method.
     *
     * @param args the arguments
     */
    public static void main(String[] args) throws IOException, InitializationException {
        //tagging
        if (args.length != 2) {
            System.out.println("WordTrainGenerating [Model Dir] [File/Folder]");
            System.out.println(
                "Generating training data for word segmentation with FlexCRFs++ or jvnmaxent (in JVnTextPro)");
            System.out.println("Model Dir: directory containing featuretemple file");
            System.out.println("Input File/Folder: file/folder name containing data manually tagged for training");
            return;
        }

        WordTrainGenerating trainGen = new WordTrainGenerating(Paths.get(args[0]));
        trainGen.generateTrainData(args[1], args[1]);
    }

}
