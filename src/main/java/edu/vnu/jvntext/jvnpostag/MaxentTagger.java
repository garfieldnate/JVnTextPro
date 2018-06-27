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

package edu.vnu.jvntext.jvnpostag;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import edu.vnu.jvntext.jmaxent.Classification;
import edu.vnu.jvntext.jvntextpro.data.DataReader;
import edu.vnu.jvntext.jvntextpro.data.DataWriter;
import edu.vnu.jvntext.jvntextpro.data.Sentence;
import edu.vnu.jvntext.jvntextpro.data.TaggingData;
import edu.vnu.jvntext.jvntextpro.util.StringUtils;
import edu.vnu.jvntext.utils.InitializationException;
import edu.vnu.jvntext.utils.PathUtils;

public class MaxentTagger implements POSTagger {
    private static final Logger logger = LoggerFactory.getLogger(MaxentTagger.class);
    DataReader reader = new POSDataReader();
    DataWriter writer = new POSDataWriter();
    final TaggingData dataTagger = new TaggingData();

    Classification classifier = null;

    public MaxentTagger(Path modelDir) throws InitializationException {
        init(modelDir);
    }

    public MaxentTagger() throws InitializationException {
        init();
    }

    @Override
    public void init(Path modeldir) throws InitializationException {
        logger.info("Initializing MaxentTagger from " + modeldir + "...");
        try {
            dataTagger.addContextGenerator(new POSContextGenerator(modeldir.resolve("featuretemplate.xml")));
            classifier = new Classification(modeldir);
        } catch (SAXException | ParserConfigurationException | IOException e) {
            throw new InitializationException(e);
        }
    }

    public void init() throws InitializationException {
        Path modelDir;
        try {
            modelDir = PathUtils.getResourceDirectory(MaxentTagger.class).resolve("maxent");
            //            modelDir = Paths.get(MaxentTagger.class.getResource(
            //                "/" + MaxentTagger.class.getPackage().getName() + "/maxent").toURI());
        } catch (URISyntaxException | IOException e) {
            // this should never happen
            logger.error("problem getting the model path from resources", e);
            throw new InitializationException(e);
        }
        init(modelDir);
    }

    public String tagging(String instr) {
        logger.debug("tagging ....");
        List<Sentence> data = reader.readString(instr);
        tagging(data);

        return writer.writeString(data);
    }

    public String tagging(File file) throws IOException {
        List<Sentence> data = reader.readFile(file.getPath());
        tagging(data);

        return writer.writeString(data);
    }

    private void tagging(List<Sentence> data) {
        for (Sentence sent : data) {
            for (int j = 0; j < sent.size(); ++j) {
                String[] cps = dataTagger.getContext(sent, j);
                String label = classifier.classify(cps);

                if (label.equalsIgnoreCase("Mrk")) {
                    if (StringUtils.isPunc(sent.getWordAt(j))) label = sent.getWordAt(j);
                    else label = "X";
                }

                sent.getTWordAt(j).setTag(label);
            }
        }
    }

    public void setDataReader(DataReader reader) {
        this.reader = reader;
    }

    public void setDataWriter(DataWriter writer) {
        this.writer = writer;
    }
}
