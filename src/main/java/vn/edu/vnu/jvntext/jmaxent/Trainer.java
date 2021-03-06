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
import java.io.PrintWriter;
import java.nio.file.Paths;

import vn.edu.vnu.jvntext.utils.InitializationException;

public class Trainer {
    private static final Logger logger = LoggerFactory.getLogger(Trainer.class);

    /**
     * The main method.
     *
     * @param args the arguments
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public static void main(String[] args) throws IOException, InitializationException {
        if (!checkArgs(args)) {
            displayHelp();
            return;
        }

        boolean isAll = (args[0].compareToIgnoreCase("-all") == 0);
        boolean isTrn = (args[0].compareToIgnoreCase("-trn") == 0);
        boolean isTst = (args[0].compareToIgnoreCase("-tst") == 0);
        boolean isCont = (args[0].compareToIgnoreCase("-cont") == 0); //TUNC

        // create option object
        Option option = new Option(Paths.get(args[2]), args[4]);

        Data data;
        Dictionary dict;
        FeatureGen feaGen;
        Train train = null;
        Inference inference;
        Evaluation evaluation;
        Model model;
        PrintWriter foutModel;
        BufferedReader finModel;

        if (isAll) {
            // both training and testing

            PrintWriter flog = option.openTrainLogFile();
            if (flog == null) {
                logger.warn("Couldn't create training log file");
                return;
            }

            foutModel = option.createModelFile();
            if (foutModel == null) {
                logger.warn("Couldn't create model file");
                return;
            }

            data = new Data(option);
            data.readTrnData(option.getTrainReader());
            data.readTstData(option.getTestReader());

            dict = new Dictionary(option, data);
            dict.generateDict();

            feaGen = new FeatureGen(option, data, dict);
            feaGen.generateFeatures();

            data.writeCpMaps(dict, foutModel);
            data.writeLbMaps(foutModel);

            train = new Train();
            inference = new Inference();
            evaluation = new Evaluation();

            model = new Model(option, data, dict, feaGen, train, inference, evaluation);
            model.doTrain(flog);

            model.doInference(model.data.tstData);
            model.evaluation.evaluate(flog);

            dict.writeDict(foutModel);
            feaGen.writeFeatures(foutModel);

            foutModel.close();
        }

        if (isTrn) {
            // training only

            PrintWriter flog = option.openTrainLogFile();
            if (flog == null) {
                logger.warn("Couldn't create training log file");
                return;
            }

            foutModel = option.createModelFile();
            if (foutModel == null) {
                logger.warn("Couldn't create model file");
                return;
            }

            data = new Data(option);
            data.readTrnData(option.getTrainReader());

            dict = new Dictionary(option, data);
            dict.generateDict();

            feaGen = new FeatureGen(option, data, dict);
            feaGen.generateFeatures();

            data.writeCpMaps(dict, foutModel);
            data.writeLbMaps(foutModel);

            train = new Train();

            model = new Model(option, data, dict, feaGen, train, null, null);
            model.doTrain(flog);

            dict.writeDict(foutModel);
            feaGen.writeFeatures(foutModel);

            foutModel.close();
        } else if (isTst) {
            // testing only

            finModel = option.openModelFile();
            if (finModel == null) {
                logger.warn("Couldn't open model file");
                return;
            }

            data = new Data(option);
            data.readCpMaps(finModel);
            data.readLbMaps(finModel);
            data.readTstData(option.getTestReader());

            dict = new Dictionary(option, data);
            dict.readDict(finModel);

            feaGen = new FeatureGen(option, data, dict);
            feaGen.readFeatures(finModel);

            inference = new Inference();
            evaluation = new Evaluation();

            model = new Model(option, data, dict, feaGen, null, inference, evaluation);

            model.doInference(model.data.tstData);
            model.evaluation.evaluate(null);

            finModel.close();
        } else if (isCont) { //continue last training
            PrintWriter flog = option.openTrainLogFile(); //append
            if (flog == null) {
                logger.warn("Couldn't create training log file");
                return;
            }

            finModel = option.openModelFile();
            if (finModel == null) {
                logger.warn("Couldn't open model file");
                return;
            }

            data = new Data(option);
            data.readCpMaps(finModel);
            data.readLbMaps(finModel);
            data.readTstData(option.getTestReader());

            dict = new Dictionary(option, data);
            dict.readDict(finModel);

            feaGen = new FeatureGen(option, data, dict);
            feaGen.readFeatures(finModel);

            inference = new Inference();
            evaluation = new Evaluation();

            foutModel = option.createModelFile(); //overwrite the old model file
            if (foutModel == null) {
                logger.warn("Couldn't create model file");
                return;
            }

            model = new Model(option, data, dict, feaGen, train, inference, evaluation);
            model.doTrain(flog);

            model.doInference(model.data.tstData);
            model.evaluation.evaluate(flog);

            foutModel.close();
        }
    }

    /**
     * Check args.
     *
     * @param args the args
     * @return true, if successful
     */
    private static boolean checkArgs(String[] args) {
        return args.length >= 5 && (args[0].compareToIgnoreCase("-all") == 0 || args[0].compareToIgnoreCase("-trn") == 0
                                    || args[0].compareToIgnoreCase("-tst") == 0)
               && args[0].compareToIgnoreCase("-cont") != 0 && args[1].compareToIgnoreCase("-d") == 0
               && args[3].compareToIgnoreCase("-o") == 0;

    }

    /**
     * Display help.
     */
    private static void displayHelp() {
        System.out.println("Usage:");
        System.out.println("\tTrainer -all/-trn/-tst -d <model directory> -o <OPTION_FILE_NAME>");
    }
}

