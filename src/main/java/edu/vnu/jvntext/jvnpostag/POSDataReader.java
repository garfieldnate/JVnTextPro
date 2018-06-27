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

package edu.vnu.jvntext.jvnpostag;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.StringTokenizer;

import edu.vnu.jvntext.jvntextpro.data.DataReader;
import edu.vnu.jvntext.jvntextpro.data.Sentence;
import edu.vnu.jvntext.jvntextpro.util.StringUtils;

public class POSDataReader extends DataReader {
    private static final Logger logger = LoggerFactory.getLogger(POSDataReader.class);
    private static final String[] tags = {
        "N", "Np", "Nc", "Nu", "V", "A", "P", "L", "M", "R", "E", "C", "I", "T", "B", "Y", "X", "Ny", "Nb", "Vb", "Mrk"
    };

    private boolean isTrainReading = false;

    //-------------------------------------
    // Constructor
    //-------------------------------------
    public POSDataReader() {
        // Do nothing
    }

    public POSDataReader(boolean isTrainReading) {
        this.isTrainReading = isTrainReading;
    }


    //-------------------------------------
    // Override methods
    //-------------------------------------
    @Override
    public List<Sentence> readFile(String datafile) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(datafile), "UTF-8"));

        String line;
        List<Sentence> data = new ArrayList<>();
        while ((line = reader.readLine()) != null) {
            Sentence sentence = new Sentence();
            boolean error = false;

            if (line.startsWith("#")) continue;

            StringTokenizer tk = new StringTokenizer(line, " ");

            while (tk.hasMoreTokens()) {
                StringBuilder word = new StringBuilder();
                String tag = null;
                String token = tk.nextToken();

                if (isTrainReading) {
                    if (Objects.equals(token, "/")) {
                        word = new StringBuilder("/");
                        tag = "Mrk";
                    } else if (Objects.equals(token, "///")) {
                        word = new StringBuilder("/");
                        tag = "Mrk";
                    } else {

                        String[] fields = token.split("/");
                        if (fields.length == 1) {
                            error = true;
                            break;
                        } else if (fields.length == 2) {
                            word = new StringBuilder(fields[0]);
                            tag = fields[1];
                        } else if (fields.length > 2) {//token = 20/9/08
                            tag = fields[fields.length - 1];
                            for (int i = 0; i < fields.length - 2; ++i)
                                word.append(fields[i]).append("/");
                            word.append(fields[fields.length - 2]);
                        }

                        if (tag != null) {
                            if (StringUtils.isPunc(tag)) sentence.addTWord(word.toString(), "Mrk");
                            else {
                                boolean found = false;
                                for (String tag1 : tags) {
                                    if (tag.equalsIgnoreCase(tag1)) {
                                        //sentence.addTWord(word, tags[i]);
                                        tag = tag1;
                                        found = true;
                                        break;
                                    }
                                }

                                if (!found) {
                                    error = true;
                                    logger.warn("problem with tag: " + tag);
                                }
                                sentence.addTWord(word.toString(), tag);
                            }
                        } else {
                            //sentence.addTWord(word, tag);
                            error = true; //uncomment this when reading data for training
                            break;
                        }
                    }
                } else {
                    word = new StringBuilder(token);
                    sentence.addTWord(word.toString(), null);
                }
            }

            if (!error) {
                data.add(sentence);
            }
        }

        reader.close();
        return data;
    }

    @Override
    public List<Sentence> readString(String dataStr) {
        String[] lines = dataStr.split("\n");

        List<Sentence> data = new ArrayList<>();
        for (String line : lines) {
            Sentence sentence = new Sentence();
            StringTokenizer tk = new StringTokenizer(line, " ");

            while (tk.hasMoreTokens()) {
                if (isTrainReading) {
                    String token = tk.nextToken();
                    String[] fields = token.split("/");

                    if (fields.length > 0) {
                        String word = fields[0];
                        String tag = null;
                        if (fields.length == 2) tag = fields[1];

                        sentence.addTWord(word, tag);
                    }
                } else {
                    String token = tk.nextToken();
                    sentence.addTWord(token, null);
                }
            }
            data.add(sentence);
        }

        return data;
    }
}
