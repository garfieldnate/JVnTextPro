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
package jvnsegmenter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import jvntextpro.data.DataReader;
import jvntextpro.data.Sentence;

public class WordDataReader extends DataReader {

    /**
     * The is train reading.
     */
    protected boolean isTrainReading = false;

    /**
     * The tags.
     */
    protected String[] tags = {"B-W", "I-W", "O"};

    //------------------------------------
    // Constructor
    //------------------------------------

    /**
     * Instantiates a new word data reader.
     */
    public WordDataReader() {
        // do nothing now
        isTrainReading = false;
    }

    /**
     * Instantiates a new word data reader.
     *
     * @param isTrainReading the is train reading
     */
    public WordDataReader(boolean isTrainReading) {
        this.isTrainReading = isTrainReading;
    }

    //-------------------------------------
    // Override methods
    //-------------------------------------
    /* (non-Javadoc)
     * @see jvntextpro.data.DataReader#readFile(java.lang.String)
	 */
    @Override
    public List<Sentence> readFile(String datafile) throws IOException {
        List<Sentence> data = new ArrayList<Sentence>();

        Files.lines(Paths.get(datafile)).forEach(line -> {
            Sentence sentence = new Sentence();

            if (line.startsWith("#")) return;

            StringTokenizer tk = new StringTokenizer(line, " ");
            while (tk.hasMoreTokens()) {
                String word = "", tag = null;
                if (!isTrainReading) {
                    word = tk.nextToken();
                    sentence.addTWord(word, tag);
                } else {
                    String token = tk.nextToken();
                    for (int i = 0; i < tags.length; ++i) {
                        String labelPart = "/" + tags[i];
                        if (token.endsWith(labelPart)) {
                            word = token.substring(0, token.length() - labelPart.length());
                            tag = tags[i];
                            break;
                        }
                    }
                    sentence.addTWord(word, tag);
                }
            }
            data.add(sentence);
        });
        return data;
    }

    /* (non-Javadoc)
     * @see jvntextpro.data.DataReader#readString(java.lang.String)
     */
    public List<Sentence> readString(String dataStr) {
        String[] lines = dataStr.split("\n");
        List<Sentence> data = new ArrayList<Sentence>();
        for (String line : lines) {
            Sentence sentence = new Sentence();

            if (line.startsWith("#")) continue;

            StringTokenizer tk = new StringTokenizer(line, " ");
            while (tk.hasMoreTokens()) {
                String word = "", tag = null;
                if (!isTrainReading) {
                    String token = tk.nextToken();
                    word = token;
                    sentence.addTWord(word, tag);
                } else {
                    String token = tk.nextToken();
                    StringTokenizer sltk = new StringTokenizer(token, "/");
                    word = sltk.nextToken();
                    tag = sltk.nextToken();
                    sentence.addTWord(word, tag);
                }
            }
            data.add(sentence);
        }

        return data;
    }
}
