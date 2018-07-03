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

package vn.edu.vnu.jvntext.jmaxent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.StringTokenizer;

import vn.edu.vnu.jvntext.utils.InitializationException;
import vn.edu.vnu.jvntext.utils.PathUtils;

public class Option {
    public static final String modelSeparator = "##########";

    private static final Logger logger = LoggerFactory.getLogger(Option.class);
    private final static String DEFAULT_FILE_NAME = "option.txt";
    private static final String FEATURETEMPLATE_FILE = "featuretemplate.xml";
    private final Path optionPath;

    private final Path modelDir;
    private String modelFileName = "model.txt";

    // training data, testing data file
    /**
     * The train data file.
     */
    private String trainDataFile = "train.labeled";

    /**
     * The test data file.
     */
    private String testDataFile = "test.labeled";

    /**
     * The label separator.
     */
    public static final String labelSeparator = "/";

    // training log
    /**
     * The train log file.
     */
    private String trainLogFile = "trainlog.txt";

    /**
     * The is logging.
     */
    public boolean isLogging = true;

    /**
     * The num train exps.
     */
    public int numTrainExps = 0; // number of training examples

    /**
     * The num test exps.
     */
    public int numTestExps = 0; // number of testing examples

    /**
     * The num labels.
     */
    public int numLabels = 0; // number of class labels

    /**
     * The num cps.
     */
    public int numCps = 0; // number of context predicates

    /**
     * The num features.
     */
    public int numFeatures = 0; // number of features

    // thresholds for context predicate and feature cut-off
    /**
     * The cp rare threshold.
     */
    public int cpRareThreshold = 1;

    /**
     * The rare threshold.
     */
    public int fRareThreshold = 1;

    // training options
    /**
     * The num iterations.
     */
    public int numIterations = 100; // number of training iterations

    /**
     * The init lambda val.
     */
    public double initLambdaVal = 0.0; // intial value for feature weights

    /**
     * The sigma square.
     */
    public double sigmaSquare = 100; // for smoothing

    /**
     * The eps for convergence.
     */
    public double epsForConvergence = 0.0001; // for checking training termination

    /**
     * The m for hessian.
     */
    public int mForHessian = 7;    // for L-BFGS corrections

    /**
     * The debug level.
     */
    public final int debugLevel = 1; // control output status information

    // evaluation options
    /**
     * The evaluate during training.
     */
    public boolean evaluateDuringTraining = true; // evaluate during training

    /**
     * The save best model.
     */
    public boolean saveBestModel = true; // save the best model with testing data
    private InputStream featureTemplateInputStream;

    /**
     * Instantiates a new option.
     *
     * @param optionPath the path to the options file
     */
    public Option(Path optionPath) throws IOException {
        this.optionPath = optionPath;
        this.modelDir = optionPath.getParent();
        init();
    }

    /**
     * Instantiates a new option.
     *
     * @param modelDir the model dir
     */
    public Option(Path modelDir, String optionFileName) throws IOException {
        this.modelDir = modelDir;
        optionPath = modelDir.resolve(optionFileName);
        init();
    }

    /**
     * Instantiates a new option.
     *
     * @param clazz to load option resource from
     */
    public Option(Class<?> clazz) throws InitializationException, IOException {
        try {
            optionPath = PathUtils.getPath(clazz.getResource(DEFAULT_FILE_NAME).toURI());
        } catch (URISyntaxException e) {
            throw new InitializationException(e);
        }
        modelDir = optionPath.getParent();
        init();
    }

    /**
     * Read options.
     */
    private void init() throws IOException {
        logger.info("Reading maxent options from " + optionPath + "...");
        for (String line : Files.readAllLines(optionPath)) {
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

            if (strOpt.compareToIgnoreCase("trainDataFile") == 0) {
                trainDataFile = strVal;

            } else if (strOpt.compareToIgnoreCase("testDataFile") == 0) {
                testDataFile = strVal;

            } else if (strOpt.compareToIgnoreCase("isLogging") == 0) {
                if (!(strVal.compareToIgnoreCase("true") == 0 || strVal.compareToIgnoreCase("false") == 0)) {
                    continue;
                }
                isLogging = Boolean.valueOf(strVal);

            } else if (strOpt.compareToIgnoreCase("cpRareThreshold") == 0) {
                cpRareThreshold = Integer.parseInt(strVal);

            } else if (strOpt.compareToIgnoreCase("fRareThreshold") == 0) {
                fRareThreshold = Integer.parseInt(strVal);

            } else if (strOpt.compareToIgnoreCase("numIterations") == 0) {
                numIterations = Integer.parseInt(strVal);

            } else if (strOpt.compareToIgnoreCase("initLambdaVal") == 0) {
                initLambdaVal = Double.parseDouble(strVal);

            } else if (strOpt.compareToIgnoreCase("sigmaSquare") == 0) {
                sigmaSquare = Double.parseDouble(strVal);

            } else if (strOpt.compareToIgnoreCase("epsForConvergence") == 0) {
                epsForConvergence = Double.parseDouble(strVal);

            } else if (strOpt.compareToIgnoreCase("mForHessian") == 0) {
                mForHessian = Integer.parseInt(strVal);

            } else if (strOpt.compareToIgnoreCase("evaluateDuringTraining") == 0) {
                if (!(strVal.compareToIgnoreCase("true") == 0 || strVal.compareToIgnoreCase("false") == 0)) {
                    continue;
                }
                evaluateDuringTraining = Boolean.parseBoolean(strVal);

            } else if (strOpt.compareToIgnoreCase("saveBestModel") == 0) {
                if (!(strVal.compareToIgnoreCase("true") == 0 || strVal.compareToIgnoreCase("false") == 0)) {
                    continue;
                }
                saveBestModel = Boolean.parseBoolean(strVal);

            } else if (strOpt.compareToIgnoreCase("trainLogFile") == 0) {
                trainLogFile = strVal;
                // for future use
            } else if (strOpt.compareToIgnoreCase("modelFileName") == 0) {
                modelFileName = strVal;
            } else {
                logger.warn("Unknown maxent option: " + strOpt);
            }
        }

        logger.info("Reading maxent options completed!");
    }

    //TODO: return input stream, not path
    public BufferedReader getTrainReader() throws IOException {
        return Files.newBufferedReader(optionPath.resolveSibling(trainDataFile));
    }

    public BufferedReader getTestReader() throws IOException {
        return Files.newBufferedReader(optionPath.resolveSibling(testDataFile));
    }

    /**
     * Open train log file.
     *
     * @return the prints the writer
     */
    public PrintWriter openTrainLogFile() throws IOException {
        return new PrintWriter(Files.newBufferedWriter(optionPath.resolveSibling(trainLogFile)));
    }

    /**
     * Open model file.
     *
     * @return the buffered reader
     */
    public BufferedReader openModelFile() throws IOException {
        return Files.newBufferedReader(optionPath.resolveSibling(modelFileName));
    }

    /**
     * Creates the model file.
     *
     * @return the prints the writer
     */
    public PrintWriter createModelFile() throws IOException {
        return new PrintWriter(Files.newBufferedWriter(optionPath.resolveSibling(modelFileName)));
    }

    /**
     * Write options.
     *
     * @param fout the fout
     */
    public void writeOptions(PrintWriter fout) {
        fout.println("OPTION VALUES:");
        fout.println("==============");
        fout.println("Model directory: " + optionPath.getParent());
        fout.println("Model file: " + modelFileName);
        fout.println("Option file: " + optionPath.getFileName());
        fout.println("Training log file: " + trainLogFile + " (this one)");
        fout.println("Training data file: " + trainDataFile);
        fout.println("Testing data file: " + testDataFile);
        fout.println("Number of training examples " + Integer.toString(numTrainExps));
        fout.println("Number of testing examples " + Integer.toString(numTestExps));
        fout.println("Number of class labels: " + Integer.toString(numLabels));
        fout.println("Number of context predicates: " + Integer.toString(numCps));
        fout.println("Number of features: " + Integer.toString(numFeatures));
        fout.println("Rare threshold for context predicates: " + Integer.toString(cpRareThreshold));
        fout.println("Rare threshold for features: " + Integer.toString(fRareThreshold));
        fout.println("Number of training iterations: " + Integer.toString(numIterations));
        fout.println("Initial value of feature weights: " + Double.toString(initLambdaVal));
        fout.println("Sigma square: " + Double.toString(sigmaSquare));
        fout.println("Epsilon for convergence: " + Double.toString(epsForConvergence));
        fout.println("Number of corrections in L-BFGS: " + Integer.toString(mForHessian));
        if (evaluateDuringTraining) {
            fout.println("Evaluation during training: true");
        } else {
            fout.println("Evaluation during training: false");
        }
        if (saveBestModel) {
            fout.println("Save the best model towards testing data: true");
        } else {
            fout.println("Save the best model towards testing data: false");
        }
        fout.println();
    }

    public InputStream getFeatureTemplateInputStream() throws IOException {
        return Files.newInputStream(modelDir.resolve(FEATURETEMPLATE_FILE));
    }
}
