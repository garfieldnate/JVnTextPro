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

package jvntextpro.service;

import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Paths;
import java.util.Vector;

import jvntextpro.JVnTextPro;
import vnu.jvntext.utils.InitializationException;

public class TaggingService extends Thread {

    //---------------------------
    // Data
    //---------------------------
    /**
     * The port at which the tagging service is listening.
     */
    private int port = 2929;

    /**
     * The server socket.
     */
    private ServerSocket socket;

    /**
     * The Constant maxNSession.
     */
    public static final int maxNSession = 5;

    /**
     * The pool of TaggingService threads.
     */
    public static Vector<Session> pool;

    /**
     * The vn text pro.
     */
    private JVnTextPro vnTextPro = null;

    /**
     * The option.
     */
    private final Options option;

    //---------------------------
    // Constructor
    //---------------------------

    /**
     * Instantiates a new tagging service.
     *
     * @param p      the port
     * @param option the service option (specifying which tools to be used for text processing)
     */
    public TaggingService(int p, Options option) {
        this.port = p;
        this.option = option;

    }

    /**
     * Instantiates a new tagging service.
     *
     * @param option the service option
     */
    public TaggingService(Options option) {
        this.option = option;
    }

    /**
     * Inits the.
     */
    private void init() throws IOException, InitializationException {
        vnTextPro = new JVnTextPro();

        if (option.doSenToken) vnTextPro.initSenTokenization();

        if (option.doSenSeg) vnTextPro.initSenSegmenter(Paths.get(option.modelDir, "jvnsegmenter"));

        if (option.doWordSeg) vnTextPro.initSegmenter(Paths.get(option.modelDir, "jvnsegmenter"));

        if (option.doPosTagging) vnTextPro.initPosTagger(Paths.get(option.modelDir, "jvnpostag", "maxent"));

			/* start session threads*/
        pool = new Vector<>();
        for (int i = 0; i < maxNSession; ++i) {
            Session w = new Session(vnTextPro);
            w.start(); //start a pool of session threads at start-up time rather than on demand for efficiency
            pool.add(w);
        }
    }

    //------------------------
    // main methods
    //------------------------
    /* (non-Javadoc)
     * @see java.lang.Thread#run()
	 */
    @Override
    public void run() {
        System.out.println("Starting tagging service!");
        try {
            this.socket = new ServerSocket(this.port);
        } catch (IOException ioe) {
            ioe.printStackTrace();
            System.exit(1);
        }

        try {
            init();
            System.out.println("Tagging service is started successfully");
            Socket incoming;
            incoming = this.socket.accept();
            Session w;
            synchronized (pool) {
                if (pool.isEmpty()) {
                    w = new Session(vnTextPro);
                    w.setSocket(incoming); //additional sessions
                    w.start();
                } else {
                    w = pool.elementAt(0);
                    pool.removeElementAt(0);
                    w.setSocket(incoming);
                }
            }
        } catch (IOException | InitializationException e) {
            e.printStackTrace();
        }
    }

    /**
     * The main method.
     *
     * @param args the arguments
     */
    public static void main(String[] args) {
        Options option = new Options();
        CmdLineParser parser = new CmdLineParser(option);

        if (args.length == 0) {
            System.out.println("TaggingService [options...] [arguments..]");
            parser.printUsage(System.out);
            return;
        }
        new TaggingService(option).run();
    }

    private static class Options {
        @Option(name = "-modeldir", usage = "Specify model directory, which is the folder containing model directories of subproblem tools (Word Segmentation, POS Tag)")
        String modelDir;

        @Option(name = "-senseg", usage = "Specify if doing sentence segmentation is set or not, not set by default")
        boolean doSenSeg = false;

        @Option(name = "-wordseg", usage = "Specify if doing word segmentation is set or not, not set by default")
        boolean doWordSeg = false;

        @Option(name = "-sentoken", usage = "Specify if doing sentence tokenization is set or not, not set by default")
        boolean doSenToken = false;

        @Option(name = "-postag", usage = "Specify if doing pos tagging or not is set or not, not set by default")
        boolean doPosTagging = false;
    }
}
