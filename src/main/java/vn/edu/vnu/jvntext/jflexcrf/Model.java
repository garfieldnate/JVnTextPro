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
 *  JVnTextPro-v.2.0 is a free software; you can redistribute it and/or modify
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

package vn.edu.vnu.jvntext.jflexcrf;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

public class Model {
    private static final Logger logger = LoggerFactory.getLogger(Model.class);
    /**
     * The tagger opt.
     */
    public Option taggerOpt = null;

    /**
     * The tagger maps.
     */
    public Maps taggerMaps = null;

    /**
     * The tagger dict.
     */
    public Dictionary taggerDict = null;

    /**
     * The tagger f gen.
     */
    public FeatureGen taggerFGen = null;

    /**
     * The tagger vtb.
     */
    public Viterbi taggerVtb = null;

    // feature weight
    /**
     * The lambda.
     */
    double[] lambda = null;

    /**
     * Instantiates a new model.
     */
    public Model() {
    }

    /**
     * Instantiates a new model.
     *
     * @param taggerOpt  the tagger opt
     * @param taggerMaps the tagger maps
     * @param taggerDict the tagger dict
     * @param taggerFGen the tagger f gen
     * @param taggerVtb  the tagger vtb
     */
    public Model(Option taggerOpt, Maps taggerMaps, Dictionary taggerDict, FeatureGen taggerFGen, Viterbi taggerVtb) {
        this.taggerOpt = taggerOpt;
        this.taggerMaps = taggerMaps;
        this.taggerDict = taggerDict;
        this.taggerFGen = taggerFGen;
        this.taggerVtb = taggerVtb;
    }

    // load the model

    /**
     * Inits the.
     */
    public void init() throws IOException {
        // open model file to load model here ... complete later
        BufferedReader fin = Files.newBufferedReader(taggerOpt.modelDir.resolve(taggerOpt.modelFile));
        // read context predicate map and label map
        taggerMaps.readCpMaps(fin);

        System.gc();

        taggerMaps.readLbMaps(fin);

        System.gc();

        // read dictionary
        taggerDict.readDict(fin);

        System.gc();

        // read features
        taggerFGen.readFeatures(fin);

        System.gc();

        // close model file
        fin.close();


        // update feature weights
        if (lambda == null) {
            int numFeatures = taggerFGen.numFeatures();
            lambda = new double[numFeatures];
            for (int i = 0; i < numFeatures; i++) {
                Feature f = taggerFGen.features.get(i);
                lambda[f.idx] = f.wgt;
            }
        }

        // call init method of Viterbi object
        if (taggerVtb != null) {
            taggerVtb.init(this);
        }
    }

    /**
     * Inference.
     *
     * @param seq the seq
     */
    public void inference(List<Observation> seq) {
        taggerVtb.viterbiInference(seq);
    }

    /**
     * Inference all.
     *
     * @param data the data
     */
    public void inferenceAll(List<List<Observation>> data) {
        logger.info("Starting inference ...");

        long start, stop, elapsed;
        start = System.currentTimeMillis();

        for (int i = 0; i < data.size(); i++) {
            logger.debug("sequence " + Integer.toString(i + 1));
            List<Observation> seq = data.get(i);
            inference(seq);
        }

        stop = System.currentTimeMillis();
        elapsed = stop - start;

        logger.info("Inference " + Integer.toString(data.size()) + " sequences completed!");
        logger.info("Inference time: " + Double.toString((double) elapsed / 1000) + " seconds");
    }

}
