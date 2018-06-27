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

package edu.vnu.jvntext.jvnsensegmenter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.StringTokenizer;

import edu.vnu.jvntext.jmaxent.Classification;
import edu.vnu.jvntext.utils.PathUtils;

public class JVnSenSegmenter {
    private static final Logger logger = LoggerFactory.getLogger(JVnSenSegmenter.class);

    /**
     * The positive label.
     */
    private static final String positiveLabel = "y";

    /**
     * The classifier.
     */
    private Classification classifier = null;

    /**
     * Creates a new instance of JVnSenSegmenter.
     *
     * @param modelDir the model dir
     */
    public void init(Path modelDir) throws IOException {
        logger.info("Initializing JVnSenSegmenter from " + modelDir + "...");
        classifier = new Classification(modelDir);
        /*
      The fea gen.
     */
        FeatureGenerator feaGen = new FeatureGenerator();
        classifier.init();
    }

    public void init() throws IOException {
        Path modelDir;
        try {
            modelDir = PathUtils.getResourceDirectory(JVnSenSegmenter.class);
        } catch (URISyntaxException e) {
            // this should never happen
            logger.error("problem getting the model path from resources", e);
            throw new RuntimeException(e);
        }
        init(modelDir);
    }

    /**
     * Sen segment.
     *
     * @param text the text
     * @return the string
     */
    public String senSegment(String text) {
        //text normalization
        text = text.replaceAll("([\t \n])+", "$1");

        //generate context predicates
        List<Integer> markList = new ArrayList<>();
        List<String> data = FeatureGenerator.doFeatureGen(new HashSet<>(), text, markList, false);

        if (markList.isEmpty()) return text + "\n";

        //classify
        List<String> labels = classifier.classify(data);

        StringBuilder result = new StringBuilder(text.substring(0, markList.get(0)));

        for (int i = 0; i < markList.size(); ++i) {
            int curPos = markList.get(i);

            if (labels.get(i).equals(positiveLabel)) {
                result.append(" ").append(text.charAt(curPos)).append("\n");
            } else result.append(text.charAt(curPos));

            if (i < markList.size() - 1) {
                int nexPos = markList.get(i + 1);
                result.append(text.substring(curPos + 1, nexPos));
            }
        }

        int finalMarkPos = markList.get(markList.size() - 1);
        result.append(text.substring(finalMarkPos + 1, text.length()));

        result = new StringBuilder(result.toString().replaceAll("\n ", "\n"));
        result = new StringBuilder(result.toString().replaceAll("\n\n", "\n"));
        result = new StringBuilder(result.toString().replaceAll("\\.\\. \\.", "..."));
        return result.toString();
    }

    /**
     * Sen segment.
     *
     * @param text    the text
     * @param senList the sen list
     */
    public void senSegment(String text, List<String> senList) {
        senList.clear();
        String resultStr = senSegment(text);

        StringTokenizer senTknr = new StringTokenizer(resultStr, "\n");
        while (senTknr.hasMoreTokens()) {
            senList.add(senTknr.nextToken());
        }
    }

    /**
     * main method of JVnSenSegmenter to use this tool from command line.
     *
     * @param args the arguments
     */
    public static void main(String args[]) throws IOException {
        if (args.length != 4) {
            displayHelp();
            System.exit(1);
        }

        JVnSenSegmenter senSegmenter = new JVnSenSegmenter();
        senSegmenter.init(Paths.get(args[1]));

        String option = args[2];
        if (option.equalsIgnoreCase("-inputfile")) {
            senSegmentFile(args[3], args[3] + ".sent", senSegmenter);
        } else if (option.equalsIgnoreCase("-inputdir")) {
            //segment only files ends with .txt
            File inputDir = new File(args[3]);
            File[] children = inputDir.listFiles((dir, name) -> name.endsWith(".txt"));

            for (File child : children) {
                logger.info("Segmenting sentences in " + child);
                senSegmentFile(child.getPath(), child.getPath() + ".sent", senSegmenter);
            }
        } else displayHelp();
    }

    /**
     * Segment sentences.
     *
     * @param infile       the infile
     * @param outfile      the outfile
     * @param senSegmenter the sen segmenter
     */
    private static void senSegmentFile(String infile, String outfile, JVnSenSegmenter senSegmenter) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(infile), "UTF-8"));
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outfile), "UTF-8"));

        String para;
        String line;
        StringBuilder text = new StringBuilder();
        while ((line = in.readLine()) != null) {
            if (!line.equals("")) {
                if (line.charAt(0) == '#') {
                    //skip comment line
                    text.append(line).append("\n");
                    continue;
                }

                para = senSegmenter.senSegment(line).trim();
                text.append(para.trim()).append("\n\n");
            } else {
                //blank line
                text.append("\n");
            }
        }
        text = new StringBuilder(text.toString().trim());

        out.write(text.toString());
        out.newLine();

        in.close();
        out.close();
    }

    /**
     * Display help.
     */
    public static void displayHelp() {
        System.out.println("Usage:");
        System.out.println("\tCase 1: JVnSenSegmenter -modeldir <model directory> -inputfile <input data file>");
        System.out.println("\tCase 2: JVnSenSegmenter -modeldir <model directory> -inputdir <input data directory>");
        System.out.println("Where:");
        System.out.println("\t<model directory> is the directory contain the model and option files");
        System.out.println("\t<input data file> is the file containing input text that need to");
        System.out.println("\thave sentences segmented (each sentence on a line)");
        System.out.println("\t<input data directory> is the directory containing multiple input .tkn files");
        System.out.println();
    }
}
