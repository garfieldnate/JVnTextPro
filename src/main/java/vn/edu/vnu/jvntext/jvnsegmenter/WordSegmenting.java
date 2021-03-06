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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.file.Paths;

import vn.edu.vnu.jvntext.utils.InitializationException;

public class WordSegmenting {
    private static final Logger logger = LoggerFactory.getLogger(WordSegmenting.class);

    /**
     * The main method.
     *
     * @param args the arguments
     */
    public static void main(String[] args) throws IOException, InitializationException {
        displayCopyright();
        if (!checkArgs(args)) {
            displayHelp();
            return;
        }

        //get model dir
        CRFSegmenter segmenter = new CRFSegmenter(Paths.get(args[1]));

        //tagging
        if (args[2].equalsIgnoreCase("-inputfile")) {
            File inputFile = new File(args[3]);
            String outputPath = inputFile.getPath() + ".wseg";
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(outputPath),
                "UTF-8"
            ));

            logger.info("Segmenting " + inputFile + ", writing to " + outputPath);
            String result = segmenter.segmenting(inputFile);

            writer.write(result);
            writer.close();
        } else { //input dir
            String inputDir = args[3];
            if (inputDir.endsWith(File.separator)) {
                inputDir = inputDir.substring(0, inputDir.length() - 1);
            }

            File dir = new File(inputDir);
            String[] children = dir.list((dir1, name) -> name.endsWith(".tkn"));

            for (String child : children) {
                String filename = inputDir + File.separator + child;
                if ((new File(filename)).isDirectory()) {
                    continue;
                }
                String outputPath = filename + ".wseg";
                logger.info("Segmenting " + filename + ", writing to " + outputPath);
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(outputPath),
                    "UTF-8"
                ));

                writer.write(segmenter.segmenting(new File(filename)));

                writer.close();
            }
        }
    }

    /**
     * Check args.
     *
     * @param args the args
     * @return true, if successful
     */
    public static boolean checkArgs(String[] args) {
        if (args.length < 4) {
            return false;
        }

        return args[0].compareToIgnoreCase("-modeldir") == 0 && (args[2].compareToIgnoreCase("-inputfile") == 0
                                                                 || args[2].compareToIgnoreCase("-inputdir") == 0);

    }

    /**
     * Display copyright.
     */
    public static void displayCopyright() {
        System.out.println("Vietnamese Word Segmentation:");
        System.out.println("\tusing Conditional Random Fields");
        System.out.println("\ttesting our dataset of 8000 sentences with the highest F1-measure of 94%");
        System.out.println("Copyright (C) by Cam-Tu Nguyen {1,2} and Xuan-Hieu Phan {2}");
        System.out.println("{1}: College of Technology, Hanoi National University");
        System.out.println("{2}: Graduate School of Information Sciences, Tohoku University");
        System.out.println("Email: {ncamtu@gmail.com ; pxhieu@gmail.com}");
        System.out.println();
    }

    /**
     * Display help.
     */
    public static void displayHelp() {
        System.out.println("Usage:");
        System.out.println("\tCase 1: WordSegmenting -modeldir <model directory> -inputfile <input data file>");
        System.out.println("\tCase 2: WordSegmenting -modeldir <model directory> -inputdir <input data directory>");
        System.out.println("Where:");
        System.out.println("\t<model directory> is the directory contain the model and option files");
        System.out.println("\t<input data file> is the file containing input sentences that need to");
        System.out.println("\tbe tagged (each sentence on a line)");
        System.out.println("\t<input data directory> is the directory containing multiple input data files (.tkn)");
        System.out.println();
    }
}
