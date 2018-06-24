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
package jvntextpro;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import jvntextpro.conversion.CompositeUnicode2Unicode;

public class JVnTextProTest {

    /**
     * The main method.
     *
     * @param args the arguments
     */
    public static void main(String[] args) throws IOException {
        JVnTextProTestOption option = parseArguments(args);
        if (option == null) {
            return;
        }

        JVnTextPro vnTextPro = new JVnTextPro();
        vnTextPro.initSenTokenization();
        CompositeUnicode2Unicode conversion = new CompositeUnicode2Unicode();

        if (option.doSenSeg) {
            vnTextPro.initSenSegmenter(option.modelDir.getPath() + File.separator + "jvnsensegmenter");
        }

        if (option.doSenToken) {
            vnTextPro.initSenTokenization();
        }

        if (option.doWordSeg) {
            vnTextPro.initSegmenter(option.modelDir.getPath() + File.separator + "jvnsegmenter");
        }


        if (option.doPosTagging) {
            vnTextPro.initPosTagger(
                option.modelDir.getPath() + File.separator + "jvnpostag" + File.separator + "maxent");
        }


        if (option.inFile.isFile()) {
            //REad file, process and write file
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(option.inFile),
                                                                             "UTF-8"
            ));

            String line;
            StringBuilder sb = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                line = conversion.convert(line);
                sb.append(vnTextPro.process(line).trim()).append("\n");
            }

            reader.close();

            //write file
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(
                option.inFile.getPath() + ".pro"), "UTF-8"));
            writer.write(sb.toString());
            writer.close();
        } else if (option.inFile.isDirectory()) {
            File[] children = option.inFile.listFiles();

            for (File child : children) {
                if (!child.getName().endsWith(option.fileType)) continue;

                //REad file, process and write file
                BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(child), "UTF-8"));

                String line;
                StringBuilder sb = new StringBuilder();
                while ((line = reader.readLine()) != null) {
                    line = conversion.convert(line);
                    sb.append(vnTextPro.process(line).trim()).append("\n");
                }

                reader.close();

                //write file
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(
                    child.getPath() + ".pro"), "UTF-8"));
                writer.write(sb.toString());
                writer.close();
            }
        }
    }

    private static JVnTextProTestOption parseArguments(String[] args) {
        JVnTextProTestOption option = new JVnTextProTestOption();
        CmdLineParser parser = new CmdLineParser(option);
        try {
            parser.parseArgument(args);
        } catch (CmdLineException cle) {
            System.out.println("Error: " + cle.getLocalizedMessage());
            System.out.println("JVnTextProTest [options...] [arguments..]");
            parser.printUsage(System.out);
            return null;
        }
        return option;
    }
}

class JVnTextProTestOption {
    @Option(name = "-input", required = true, usage = "(required) Specify input file/directory")
    File inFile;

    @Option(name = "-filetype", usage = "Specify file types to process (in the case -input is a directory")
    String fileType = ".txt";

    @Option(name = "-modeldir", required = true, usage = "Specify model directory, which is the folder containing model directories of subproblem tools (Word Segmentation, POS Tag)")
    File modelDir;

    @Option(name = "-senseg", usage = "Specify if doing sentence segmentation is set or not, not set by default")
    boolean doSenSeg = false;

    @Option(name = "-wordseg", usage = "Specify if doing word segmentation is set or not, not set by default")
    boolean doWordSeg = false;

    @Option(name = "-sentoken", usage = "Run pre-normalizations on the text equivalent to that done for the Penn Treebank. Default false.")
    boolean doSenToken = false;

    @Option(name = "-postag", usage = "Specify if doing pos tagging or not is set or not, not set by default")
    boolean doPosTagging = false;
}
