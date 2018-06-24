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

package jflexcrf;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import jvntextpro.data.DataReader;
import jvntextpro.data.DataWriter;
import jvntextpro.data.Sentence;
import jvntextpro.data.TaggingData;

public class Labeling {

    //-----------------------------------------------
    // Member Variables
    //-----------------------------------------------
    /**
     * The model dir.
     */
    private Path modelDir = Paths.get(".");

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
    private FeatureGen taggerFGen = null;

    /**
     * The tagger vtb.
     */
    private Viterbi taggerVtb = null;

    /**
     * The tagger model.
     */
    private Model taggerModel = null;

    /**
     * The data tagger.
     */
    private TaggingData dataTagger = null;

    /**
     * The data reader.
     */
    private DataReader dataReader = null;

    /**
     * The data writer.
     */
    private DataWriter dataWriter = null;

    //-----------------------------------------------
    // Initilization
    //-----------------------------------------------

    /**
     * Instantiates a new labeling.
     *
     * @param modelDir   the model dir
     * @param dataTagger the data tagger
     * @param dataReader the data reader
     * @param dataWriter the data writer
     */
    public Labeling(Path modelDir, TaggingData dataTagger, DataReader dataReader, DataWriter dataWriter) throws IOException {
        init(modelDir);
        this.dataTagger = dataTagger;
        this.dataWriter = dataWriter;
        this.dataReader = dataReader;
    }

    /**
     * Inits the.
     *
     * @param modelDir the model dir
     */
    public void init(Path modelDir) throws IOException {
        this.modelDir = modelDir;

        Option taggerOpt = new Option(this.modelDir);
        taggerOpt.readOptions();

        taggerMaps = new Maps();
        taggerDict = new Dictionary();
        taggerFGen = new FeatureGen(taggerMaps, taggerDict);
        taggerVtb = new Viterbi();

        taggerModel = new Model(taggerOpt, taggerMaps, taggerDict, taggerFGen, taggerVtb);
        taggerModel.init();
    }

    /**
     * Sets the data reader.
     *
     * @param reader the new data reader
     */
    public void setDataReader(DataReader reader) {
        dataReader = reader;
    }

    /**
     * Sets the data tagger.
     *
     * @param tagger the new data tagger
     */
    public void setDataTagger(TaggingData tagger) {
        dataTagger = tagger;
    }

    /**
     * Sets the data writer.
     *
     * @param writer the new data writer
     */
    public void setDataWriter(DataWriter writer) {
        dataWriter = writer;
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
    @SuppressWarnings("unchecked")
    public List<Sentence> seqLabeling(String data) {
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
    @SuppressWarnings("unchecked")
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
