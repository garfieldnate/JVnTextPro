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

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import jvntextpro.conversion.CompositeUnicode2Unicode;
import vnu.jvntext.utils.InitializationException;

public class JVnTextProTest {
    private static final String OUTPUT_EXTENSION = ".pro";
    private static CompositeUnicode2Unicode conversion;
    private static JVnTextPro vnTextPro = new JVnTextPro();

    private static void init(JVnTextProTestOption option) throws IOException, InitializationException {
        vnTextPro.initSenTokenization();
        conversion = new CompositeUnicode2Unicode();

        if (option.doSenSeg) {
            if (option.modelDir != null) {
                vnTextPro.initSenSegmenter(option.modelDir.resolve( "jvnsensegmenter"));
            } else {
                vnTextPro.initSenSegmenter();
            }
        }

        if (option.doSenToken) {
            vnTextPro.initSenTokenization();
        }

        if (option.doWordSeg) {
            if (option.modelDir != null) {
                vnTextPro.initSegmenter(option.modelDir.resolve( "jvnsegmenter"));
            } else {
                vnTextPro.initSegmenter();
            }
        }

        if (option.doPosTagging) {
            if (option.modelDir != null) {
                vnTextPro.initPosTagger(option.modelDir.resolve("jvnpostag").resolve( "maxent"));
            } else {
                vnTextPro.initPosTagger();
            }
        }
    }

    /**
     * Read and process an entire file, then print the results to another file with the extension ".pro".
     */
    private static void processFile(Path filePath) throws IOException {
        StringBuilder sb = new StringBuilder();
        for (String line : Files.readAllLines(filePath)) {
            line = conversion.convert(line);
            sb.append(vnTextPro.process(line).trim()).append("\n");
        }

        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(filePath + OUTPUT_EXTENSION))) {
            writer.write(sb.toString());
        }
    }

    /**
     * The main method.
     *
     * @param args the arguments
     */
    public static void main(String[] args) throws IOException, InitializationException {
        JVnTextProTestOption option = parseArguments(args);
        if (option == null) {
            return;
        }

        init(option);

        if (Files.isDirectory(option.inputPath)) {
            // TODO: would be better to just accept the blob, but can we break backwards compatibility?
            for(Path child: Files.newDirectoryStream(option.inputPath, "*" + option.fileType)) {
                processFile(child);
            }
        } else {
            //Read and process file
            processFile(option.inputPath);
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
    Path inputPath;

    @Option(name = "-filetype", usage = "Specify file types to process (in the case -input is a directory")
    String fileType = ".txt";

    @Option(name = "-modeldir", usage = "Specify model directory, which is the folder containing model directories of subproblem tools (Word Segmentation, POS Tag)")
    Path modelDir;

    @Option(name = "-senseg", usage = "Specify if doing sentence segmentation is set or not, not set by default")
    boolean doSenSeg = false;

    @Option(name = "-wordseg", usage = "Specify if doing word segmentation is set or not, not set by default")
    boolean doWordSeg = false;

    @Option(name = "-sentoken", usage = "Run pre-normalizations on the text equivalent to that done for the Penn Treebank. Default false.")
    boolean doSenToken = false;

    @Option(name = "-postag", usage = "Specify if doing pos tagging or not is set or not, not set by default")
    boolean doPosTagging = false;
}
