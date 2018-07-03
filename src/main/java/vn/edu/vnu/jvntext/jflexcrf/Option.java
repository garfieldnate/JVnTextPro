/*
 Copyright (C) 2010 by
 *
 * 	Cam-Tu Nguyen	ncamtu@ecei.tohoku.ac.jp ncamtu@gmail.com
 *  Xuan-Hieu Phan  pxhieu@gmail.com

 *  College of Technology, Vietnamese University, Hanoi
 *
 * 	Graduate School of Information Sciences
 * 	Tohoku University
 *
 *  JVnTextPro is a free software; you can redistribute it and/or modify
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

package vn.edu.vnu.jvntext.jflexcrf;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.StringTokenizer;

import vn.edu.vnu.jvntext.utils.InitializationException;
import vn.edu.vnu.jvntext.utils.PathUtils;

public class Option {
    public static final int FIRST_ORDER = 1;
    // second-order Markov is not supported currently
    public static final int SECOND_ORDER = 2;
    public static final String INPUT_SEPARATOR = "|";
    public static final String OUTPUT_SEPARATOR = "|";

    private static final Logger logger = LoggerFactory.getLogger(Option.class);
    private static final String OPTIONS_FILE = "option.txt";
    // the model file contains the mapping, dictionary, and features
    private static final String MODEL_FILE = "model.txt";
    public static final String FEATURETEMPLATE_FILE = "featuretemplate.xml";

    private final Path modelDir;

    /**
     * The order.
     */
    public int order = FIRST_ORDER;    // 1: first-order Markov; 2: second-order Markov

    /**
     * Instantiates a new option.
     *
     * @param optionsPath the options file path
     */
    public Option(Path optionsPath) throws IOException {
        this.modelDir = optionsPath.getParent();
        readOptions(optionsPath);
    }

    /**
     * Instantiates a new option.
     *
     * @param clazz class to load option resource from
     */
    public Option(Class<?> clazz) throws InitializationException, IOException {
        Path optionsPath;
        try {
            optionsPath = PathUtils.getPath(clazz.getResource(OPTIONS_FILE).toURI());
        } catch (URISyntaxException e) {
            throw new InitializationException(e);
        }
        modelDir = optionsPath.getParent();
        readOptions(optionsPath);
    }

    /**
     * Read options.
     *
     * @param optionsPath
     */
    public void readOptions(Path optionsPath) throws IOException {
        logger.info("Reading options from " + optionsPath + "...");
        // read option lines
        for (String line : Files.readAllLines(optionsPath)) {
            String trimLine = line.trim();
            if (trimLine.startsWith("#")) {
                // comment line
                continue;
            }

            StringTokenizer strTok = new StringTokenizer(line, "= \t\r\n");
            int len = strTok.countTokens();
            if (len != 2) {
                // invalid parameter line, ignore it
                continue;
            }

            String strOpt = strTok.nextToken();
            String strVal = strTok.nextToken();

            if (strOpt.compareToIgnoreCase("order") == 0) {
                order = Integer.parseInt(strVal);
            }
        }

        logger.info("Reading options completed!");
    }

    public Path getModelDir() {
        return modelDir;
    }

    public BufferedReader getModelReader() throws IOException {
        return Files.newBufferedReader(modelDir.resolve(MODEL_FILE));
    }

    public InputStream getFeatureTemplateInputStream() throws IOException {
        return Files.newInputStream(modelDir.resolve(FEATURETEMPLATE_FILE));
    }
}
