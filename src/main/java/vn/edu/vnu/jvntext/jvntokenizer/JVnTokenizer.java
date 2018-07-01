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

package vn.edu.vnu.jvntext.jvntokenizer;


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

/**
 * @author Nguyen Cam Tu
 */
public class JVnTokenizer {
    private static final Logger logger = LoggerFactory.getLogger(JVnTokenizer.class);

    /**
     * The main method.
     *
     * @param args the arguments
     */
    public static void main(String[] args) throws IOException {
        if (args.length != 2) {
            displayHelp();
            return;
        }

        //Read the input data
        String option = args[0];
        if (option.equalsIgnoreCase("-inputfile")) {
            BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(args[1]), "UTF-8"));
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(args[1] + ".tkn"),
                                                                           "UTF-8"
            ));
            logger.info("tokenizing " + args[1]);

            String line;
            while ((line = in.readLine()) != null) {
                out.write(PennTokenizer.tokenize(line));
                out.write("\n");
            }

            in.close();
            out.close();
        } else if (option.equalsIgnoreCase("-inputdir")) {
            //segment only files ends with .sent
            File inputDir = new File(args[1]);
            File[] children = inputDir.listFiles((dir, name) -> name.endsWith(".sent"));

            for (File child : children) {
                BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(child), "UTF-8"));
                BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(child + ".tkn"),
                                                                               "UTF-8"
                ));
                logger.info("tokenizing " + child);

                String line;
                while ((line = in.readLine()) != null) {
                    out.write(PennTokenizer.tokenize(line));
                    out.write("\n");
                }

                in.close();
                out.close();
            }
        }
    }

    /**
     * Display help.
     */
    public static void displayHelp() {
        System.out.println("Usage:");
        System.out.println("\tCase 1: JVnTokenizer -inputfile <input data file>");
        System.out.println("\tCase 2: JVnTokenizer -inputdir <input data directory>");
        System.out.println("Where:");
        System.out.println("\t<input data file> is the file containing input text that need to");
        System.out.println("\thave sentences tokenized (each sentence on a line)");
        System.out.println("\t<input data directory> is the directory containing multiple input .sent files");
        System.out.println();
    }

}
