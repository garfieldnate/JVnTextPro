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

package vn.edu.vnu.jvntext.jmaxent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

public class FeatureGen {
    Logger logger = LoggerFactory.getLogger(FeatureGen.class);

    /**
     * The features.
     */
    List<Feature> features = null;    // list of features

    /**
     * The fmap.
     */
    private Map<String, Integer> fmap = null;        // feature map

    /**
     * The option.
     */
    Option option = null;    // option object

    /**
     * The data.
     */
    Data data = null;        // data object

    /**
     * The dict.
     */
    private Dictionary dict = null;    // dictionary object

    // for scan feature only
    /**
     * The current features.
     */
    private List<Feature> currentFeatures = null;

    /**
     * The current feature idx.
     */
    private int currentFeatureIdx = 0;

    /**
     * Instantiates a new feature gen.
     *
     * @param option the option
     * @param data   the data
     * @param dict   the dict
     */
    public FeatureGen(Option option, Data data, Dictionary dict) {
        this.option = option;
        this.data = data;
        this.dict = dict;
    }

    // adding a feature

    /**
     * Adds the feature.
     *
     * @param f the f
     */
    public void addFeature(Feature f) {
        f.strId2IdxAdd(fmap);
        features.add(f);
    }

    // generating features

    /**
     * Generate features.
     */
    public void generateFeatures() {
        if (features != null) {
            features.clear();
        } else {
            features = new ArrayList<>();
        }

        if (fmap != null) {
            fmap.clear();
        } else {
            fmap = new HashMap<>();
        }

        if (currentFeatures != null) {
            currentFeatures.clear();
        } else {
            currentFeatures = new ArrayList<>();
        }

        if (data.trnData == null || dict.dict == null) {
            logger.warn("No data or dictionary for generating features");
            return;
        }

        // scan over data list
        for (int i = 0; i < data.trnData.size(); i++) {
            Observation obsr = data.trnData.get(i);

            for (int j = 0; j < obsr.cps.length; j++) {
                Element elem;
                CountFIdx cntFIdx;

                elem = dict.dict.get(obsr.cps[j]);
                if (elem != null) {
                    if (elem.count <= option.cpRareThreshold) {
                        // skip this context predicate, it is too rare
                        continue;
                    }

                    cntFIdx = elem.lbCntFidxes.get(obsr.humanLabel);
                    if (cntFIdx != null) {
                        if (cntFIdx.count <= option.fRareThreshold) {
                            // skip this feature, it is too rare
                            continue;
                        }

                    } else {
                        // not found in the dictionary, then skip
                        continue;
                    }

                } else {
                    // not found in the dictionary, then skip
                    continue;
                }

                // update the feature
                Feature f = new Feature(obsr.humanLabel, obsr.cps[j]);
                f.strId2Idx(fmap);
                if (f.idx < 0) {
                    // new feature, add to the feature list
                    addFeature(f);

                    // update the feature index in the dictionary
                    cntFIdx.fidx = f.idx;
                    elem.chosen = 1;
                }
            }
        }

        option.numFeatures = features.size();
    }

    /**
     * Num features.
     *
     * @return the int
     */
    public int numFeatures() {
        if (features == null) {
            return 0;
        } else {
            return features.size();
        }
    }

    /**
     * Read features.
     *
     * @param fin the fin
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public void readFeatures(BufferedReader fin) throws IOException {
        if (features != null) {
            features.clear();
        } else {
            features = new ArrayList<>();
        }

        if (fmap != null) {
            fmap.clear();
        } else {
            fmap = new HashMap<>();
        }

        if (currentFeatures != null) {
            currentFeatures.clear();
        } else {
            currentFeatures = new ArrayList<>();
        }

        String line;

        // get the number of features
        if ((line = fin.readLine()) == null) {
            logger.warn("Unknown number of features");
            return;
        }
        int numFeatures = Integer.parseInt(line);
        if (numFeatures <= 0) {
            logger.warn("Invalid number of features");
            return;
        }

        logger.info("Reading features ...");

        // main loop for reading features
        for (int i = 0; i < numFeatures; i++) {
            line = fin.readLine();
            if (line == null) {
                // invalid feature line, ignore it
                continue;
            }

            StringTokenizer strTok = new StringTokenizer(line, " ");
            if (strTok.countTokens() != 4) {
                logger.warn(i + " invalid feature line ");
                // invalid feature line, ignore it
                continue;
            }

            // create a new feature by parsing the line
            Feature f = new Feature(line, data.cpStr2Int, data.lbStr2Int);

            Integer fidx = fmap.get(f.strId);
            if (fidx == null) {
                // insert the feature into the feature map
                fmap.put(f.strId, f.idx);
                features.add(f);
            } else {
                fmap.put(f.strId, f.idx);
                features.add(f);
            }
        }

        logger.info("Reading " + Integer.toString(features.size()) + " features completed!");

        // read the line ###...
        fin.readLine();

        option.numFeatures = features.size();
    }

    /**
     * Write features.
     *
     * @param fout the fout
     */
    public void writeFeatures(PrintWriter fout) {
        // write the number of features
        fout.println(Integer.toString(features.size()));

        for (Feature feature : features) {
            fout.println(feature.toString(data.cpInt2Str, data.lbInt2Str));
        }

        // write the line ###...
        fout.println(Option.modelSeparator);
    }

    /**
     * Scan reset.
     */
    public void scanReset() {
        currentFeatureIdx = 0;
    }

    /**
     * Start scan features.
     *
     * @param obsr the obsr
     */
    public void startScanFeatures(Observation obsr) {
        currentFeatures.clear();
        currentFeatureIdx = 0;

        // scan over all context predicates
        for (int i = 0; i < obsr.cps.length; i++) {
            Element elem = dict.dict.get(obsr.cps[i]);
            if (elem == null) {//this context predicate doesn't appear in the dictionary of training data
                continue;
            }

            if (!(elem.isScanned)) {
                // scan all labels for features
                for (Integer labelInt : elem.lbCntFidxes.keySet()) {
                    CountFIdx cntFIdx = elem.lbCntFidxes.get(labelInt);

                    if (cntFIdx.fidx >= 0) {
                        Feature f = new Feature();
                        f.FeatureInit(labelInt, obsr.cps[i]);
                        f.idx = cntFIdx.fidx;

                        elem.cpFeatures.add(f);
                    }
                }

                elem.isScanned = true;
            }

            currentFeatures.addAll(elem.cpFeatures);
        }
    }

    /**
     * Checks for next feature.
     *
     * @return true, if successful
     */
    public boolean hasNextFeature() {
        return (currentFeatureIdx < currentFeatures.size());
    }

    /**
     * Next feature.
     *
     * @return the feature
     */
    public Feature nextFeature() {
        Feature f = currentFeatures.get(currentFeatureIdx);
        currentFeatureIdx++;
        return f;
    }

}
