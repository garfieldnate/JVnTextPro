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
package vn.edu.vnu.jvntext.jvnpostag;

import org.xml.sax.SAXException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.xml.parsers.ParserConfigurationException;

import vn.edu.vnu.jvntext.jvntextpro.data.TaggingData;
import vn.edu.vnu.jvntext.jvntextpro.data.TrainDataGenerating;
import vn.edu.vnu.jvntext.utils.InitializationException;

public class POSTrainGenerating extends TrainDataGenerating {
    /**
     * The template file.
     */
    final Path templateFile;

    /**
     * Instantiates a new pOS train generating.
     *
     * @param templateFile the template file (in xml format) to generate context predicates using POSContextGenerator
     */
    public POSTrainGenerating(Path templateFile) throws InitializationException {
        this.templateFile = templateFile;
        init();
    }

    /* (non-Javadoc)
     * @see TrainDataGenerating#init()
     */
    @Override
    public void init() throws InitializationException {
        this.reader = new POSDataReader(true);
        this.tagger = new TaggingData();
        try {
            tagger.addContextGenerator(new POSContextGenerator(Files.newInputStream(templateFile)));
        } catch (ParserConfigurationException | SAXException | IOException e) {
            throw new InitializationException(e);
        }
    }

    /**
     * The main method.
     *
     * @param args the arguments
     */
    public static void main(String[] args) throws InitializationException, IOException {
        //tagging
        if (args.length != 2) {
            System.out.println("POSTrainGenerating [template File] [File/Folder]");
            System.out.println(
                "Generating training data for word segmentation with FlexCRFs++ or jvnmaxent (in JVnTextPro)");
            System.out.println("Template File: featuretemplate to generate context predicates");
            System.out.println("Input File/Folder: file/folder name containing data manually tagged for training");
            return;
        }

        POSTrainGenerating trainGen = new POSTrainGenerating(Paths.get(args[0]));
        trainGen.generateTrainData(args[1], args[1]);
    }
}
