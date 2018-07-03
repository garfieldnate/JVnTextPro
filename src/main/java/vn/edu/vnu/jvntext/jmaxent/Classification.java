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

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

import vn.edu.vnu.jvntext.utils.InitializationException;

public class Classification {

    /**
     * The option.
     */
    public Option option = null;

    /**
     * The data.
     */
    public Data data = null;

    /**
     * The dict.
     */
    public Dictionary dict = null;

    /**
     * The inference.
     */
    private Inference inference = null;

    /**
     * The model.
     */
    public Model model = null;

    /**
     * The initialized.
     */
    private boolean initialized = false;

    /**
     * The int cps.
     */
    private List<Integer> intCps;

    /**
     * Instantiates a new classification.
     */
    public Classification(Option option) throws IOException, InitializationException {
        this.option = option;
        init();
    }

    /**
     * Instantiates a new classification
     *
     * @param clazz to get model resources from
     */
    public Classification(Class<?> clazz) throws IOException, InitializationException {
        option = new Option(clazz);
        init();
    }

    /**
     * Checks if is initialized.
     *
     * @return true, if is initialized
     */
    public boolean isInitialized() {
        return initialized;
    }

    /**
     * Inits the.
     */
    public void init() throws IOException, InitializationException {
        BufferedReader finModel = option.openModelFile();

        data = new Data(option);
        // read context predicate map
        data.readCpMaps(finModel);
        // read label map
        data.readLbMaps(finModel);

        dict = new Dictionary(option, data);
        // read dictionary
        dict.readDict(finModel);


        FeatureGen feagen = new FeatureGen(option, data, dict);
        // read features
        feagen.readFeatures(finModel);

        // create an inference object
        inference = new Inference();

        // create a model object
        model = new Model(option, data, dict, feagen, null, inference, null);
        model.initInference();

        // close model file
        finModel.close();
        intCps = new ArrayList<>();

        initialized = true;
    }

    /**
     * classify an observation.
     *
     * @param cps contains a list of context predicates
     * @return label
     */
    public String classify(String cps) {
        // cps contains a list of context predicates

        String modelLabel = "";
        int i;

        intCps.clear();

        StringTokenizer strTok = new StringTokenizer(cps, " \t\r\n");
        int count = strTok.countTokens();

        for (i = 0; i < count; i++) {
            String cpStr = strTok.nextToken();
            Integer cpInt = data.cpStr2Int.get(cpStr);
            if (cpInt != null) {
                intCps.add(cpInt);
            }
        }

        Observation obsr = new Observation(intCps);

        // classify
        inference.classify(obsr);

        String lbStr = data.lbInt2Str.get(obsr.modelLabel);
        if (lbStr != null) {
            modelLabel = lbStr;
        }

        return modelLabel;
    }

    /**
     * Classify.
     *
     * @param cpArr the cp arr
     * @return the string
     */
    public String classify(String[] cpArr) {
        String modelLabel = "";
        //int i;

        intCps.clear();

        int curWordCp = -1;
        int dictLabel = -2;
        int dictCp = -1;
        Vector<Integer> dictCps = new Vector<>();

        for (String cpStr : cpArr) {
            Integer cpInt = data.cpStr2Int.get(cpStr);

            if (cpInt != null) {
                intCps.add(cpInt);

                if (cpStr.startsWith("w:0")) {
                    //current word
                    curWordCp = cpInt;
                } else if (cpStr.startsWith("dict:0")) {
                    //current labels
                    dictCp = cpInt;
                    dictCps.add(dictCp);

                    if (dictLabel == -1) {
                        //do nothing
                    } else if (dictLabel == -2) {
                        //initial state
                        String label = cpStr.substring("dict:0:".length());

                        dictLabel = data.lbStr2Int.getOrDefault(label, -1);
                    } else {//!=-1 && !=-2
                        dictLabel = -1;
                    }
                }
            }
        }

        //insert information about current cpid of w:0:<current_word>
        if (curWordCp != -1 && dictLabel >= 0) { //in training data
            for (int i = 0; i < 3; ++i)
                intCps.add(dictCp);
        } else {
            for (Integer dictCp1 : dictCps) {
                intCps.add(dictCp1);
                intCps.add(dictCp1);
            }
        }

        //create observation and start inference
        Observation obsr = new Observation(intCps);
        obsr.curWordCp = curWordCp;
        obsr.dictLabel = dictLabel;

        if (obsr.curWordCp == -1 && obsr.dictLabel >= 0) {
            //not in training data and
            //there is only one corresponding label in dict
            obsr.modelLabel = obsr.dictLabel;
        } else inference.classify(obsr);

        String lbStr = data.lbInt2Str.get(obsr.modelLabel);
        if (lbStr != null) {
            modelLabel = lbStr;
        }

        return modelLabel;
    }

    /**
     * classify a list of observation.
     *
     * @param data contains a list of cps
     * @return the list
     */
    public List<String> classify(List<String> data) {
        List<String> list = new ArrayList<>();

        for (String datum : data) {
            list.add(classify(datum));
        }

        return list;
    }

}
