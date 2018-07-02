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
 *  JVnTextPro is a free software; you can redistribute it and/or modify
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

package vn.edu.vnu.jvntext.jflexcrf;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import vn.edu.vnu.jvntext.jvntextpro.data.DataReader;
import vn.edu.vnu.jvntext.jvntextpro.data.DataWriter;
import vn.edu.vnu.jvntext.jvntextpro.data.Sentence;
import vn.edu.vnu.jvntext.jvntextpro.data.TaggingData;
import vn.edu.vnu.jvntext.utils.InitializationException;

public class Labeling {
    //-----------------------------------------------
    // Member Variables
    //-----------------------------------------------
    /**
     * The tagger maps.
     */
    private final Maps taggerMaps;

    /**
     * The tagger model.
     */
    private final Model taggerModel;

    /**
     * The data tagger.
     */
    private final TaggingData dataTagger;

    /**
     * The data reader.
     */
    private final DataReader dataReader;

    /**
     * The data writer.
     */
    private final DataWriter dataWriter;

    /**
     * Instantiates a new labeling.
     *
     * @param taggerOpt  options for the model
     * @param dataTagger the data tagger
     * @param dataReader the data reader
     * @param dataWriter the data writer
     */
    public Labeling(Option taggerOpt, TaggingData dataTagger, DataReader dataReader, DataWriter dataWriter) throws IOException, InitializationException {
        this.dataTagger = dataTagger;
        this.dataWriter = dataWriter;
        this.dataReader = dataReader;
        taggerMaps = new Maps();

        Dictionary taggerDict = new Dictionary();
        FeatureGen taggerFGen = new FeatureGen(taggerMaps, taggerDict);
        taggerModel = new Model(taggerOpt, taggerMaps, taggerDict, taggerFGen, new Viterbi());
    }

    //---------------------------------------------------------
    // labeling methods
    //---------------------------------------------------------

    /**
     * labeling observation sequences.
     *
     * @param data list of sequences with specified format which can be read by DataReader
     * @return a list of sentences with tags annotated
     */
    private List<Sentence> seqLabeling(String data) {
        List<Sentence> obsvSeqs = dataReader.readString(data);
        return labeling(obsvSeqs);
    }

    /**
     * labeling observation sequences.
     *
     * @param file the file
     * @return a list of sentences with tags annotated
     */
    public List<Sentence> seqLabeling(File file) throws IOException {
        List<Sentence> obsvSeqs = dataReader.readFile(file.getPath());
        return labeling(obsvSeqs);
    }

    /**
     * labeling observation sequences.
     *
     * @param data the data
     * @return string representing label sequences, the format is specified by writer
     */
    public String strLabeling(String data) {
        List<Sentence> lblSeqs = seqLabeling(data);
        return dataWriter.writeString(lblSeqs);
    }

    /**
     * labeling observation sequences.
     *
     * @param file contains a list of observation sequence, this file has a format wich can be read by DataReader
     * @return string representing label sequences, the format is specified by writer
     */
    public String strLabeling(File file) throws IOException {
        List<Sentence> obsvSeqs = dataReader.readFile(file.getPath());
        List<Sentence> lblSeqs = labeling(obsvSeqs);
        return dataWriter.writeString(lblSeqs);
    }

    /**
     * Labeling.
     *
     * @param obsvSeqs the obsv seqs
     * @return the list
     */
    private List<Sentence> labeling(List<Sentence> obsvSeqs) {
        List<List<Observation>> labelSeqs = new ArrayList<>();

        for (Sentence obsvSeq : obsvSeqs) {//ith sentence
            List<Observation> sequence = new ArrayList<>();

            for (int j = 0; j < obsvSeq.size(); ++j) {//jth observation
                Observation obsv = new Observation();
                obsv.originalData = obsvSeq.getWordAt(j);

                String[] strCps = dataTagger.getContext(obsvSeq, j);

                ArrayList<Integer> tempCpsInt = new ArrayList<>();

                for (String strCp : strCps) {
                    Integer cpInt = taggerMaps.cpStr2Int.get(strCp);
                    if (cpInt == null) {
                        continue;
                    }
                    tempCpsInt.add(cpInt);
                }

                obsv.cps = new int[tempCpsInt.size()];
                for (int k = 0; k < tempCpsInt.size(); ++k) {
                    obsv.cps[k] = tempCpsInt.get(k);
                }
                sequence.add(obsv);
            }

            labelSeqs.add(sequence);
        }

        taggerModel.inferenceAll(labelSeqs);

        //assign labels to list of sentences
        for (int i = 0; i < obsvSeqs.size(); ++i) {
            Sentence sent = obsvSeqs.get(i);
            List<Observation> seq = labelSeqs.get(i);

            for (int j = 0; j < sent.size(); ++j) {
                Observation obsrv = seq.get(j);
                String label = taggerMaps.lbInt2Str.get(obsrv.modelLabel);

                sent.getTWordAt(j).setTag(label);
            }
        }

        return obsvSeqs;
    }

}
