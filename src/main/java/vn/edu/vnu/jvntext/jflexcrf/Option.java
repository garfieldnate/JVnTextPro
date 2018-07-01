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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.StringTokenizer;

public class Option {
    private static final Logger logger = LoggerFactory.getLogger(Option.class);

    /**
     * The Constant FIRST_ORDER.
     */
    static public final int FIRST_ORDER = 1;
    // second-order Markov is not supported currently
    /**
     * The Constant SECOND_ORDER.
     */
    static public final int SECOND_ORDER = 2;

    /**
     * The Constant inputSeparator.
     */
    public static final String inputSeparator = "|";

    /**
     * The Constant outputSeparator.
     */
    public static final String outputSeparator = "|";

    // model directory, default is current dir
    /**
     * The model dir.
     */
    public Path modelDir = Paths.get(".");
    // model file (mapping, dictionary, and features)
    /**
     * The model file.
     */
    public final String modelFile = "model.txt";
    // option file
    /**
     * The option file.
     */
    public final String optionFile = "option.txt";

    /**
     * The order.
     */
    public int order = FIRST_ORDER;    // 1: first-order Markov; 2: second-order Markov

    /**
     * Instantiates a new option.
     */
    public Option() {
    }

    /**
     * Instantiates a new option.
     *
     * @param modelDir the model dir
     */
    public Option(Path modelDir) {
        this.modelDir = modelDir;
    }

    /**
     * Read options.
     */
    public void readOptions() throws IOException {
        Path optionsPath = modelDir.resolve(optionFile);
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

}
