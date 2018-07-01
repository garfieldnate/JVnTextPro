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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.xml.parsers.ParserConfigurationException;

import vn.edu.vnu.jvntext.jflexcrf.Labeling;
import vn.edu.vnu.jvntext.jvntextpro.data.DataReader;
import vn.edu.vnu.jvntext.jvntextpro.data.DataWriter;
import vn.edu.vnu.jvntext.jvntextpro.data.TaggingData;
import vn.edu.vnu.jvntext.utils.InitializationException;

public class CRFTagger implements POSTagger {
    private static final Logger logger = LoggerFactory.getLogger(CRFTagger.class);
    DataReader reader = new POSDataReader();
    DataWriter writer = new POSDataWriter();

    final TaggingData dataTagger = new TaggingData();

    Labeling labeling = null;

    public CRFTagger(Path modelDir) throws InitializationException {
        init(modelDir);
    }

    @Override
    public void init(Path modelDir) throws InitializationException {
        try {
            dataTagger.addContextGenerator(new POSContextGenerator(modelDir.resolve("featuretemplate.xml")));
            labeling = new Labeling(modelDir, dataTagger, reader, writer);
        } catch (SAXException | ParserConfigurationException | IOException e) {
            throw new InitializationException(e);
        }
    }

    public void init() throws InitializationException {
        Path modelDir;
        try {
            modelDir = Paths.get(CRFTagger.class.getResource("/" + CRFTagger.class.getPackage().getName() + "/crf")
                                                .toURI());
        } catch (URISyntaxException e) {
            // this should never happen
            logger.error("problem getting the model path from resources", e);
            throw new RuntimeException(e);
        }
        init(modelDir);
    }

    public String tagging(String instr) {
        return labeling.strLabeling(instr);
    }

    public String tagging(File file) throws IOException {
        return labeling.strLabeling(file);
    }

    public void setDataReader(DataReader reader) {
        this.reader = reader;
    }

    public void setDataWriter(DataWriter writer) {
        this.writer = writer;
    }

}
