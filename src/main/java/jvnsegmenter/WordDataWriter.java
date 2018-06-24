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

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;

import jvntextpro.data.DataWriter;
import jvntextpro.data.Sentence;

public class WordDataWriter extends DataWriter {
    /* (non-Javadoc)
     * @see jvntextpro.data.DataWriter#writeFile(java.util.List, java.lang.String)
     */
    @Override
    public void writeFile(List lblSeqs, String filename) throws IOException {
        String ret = writeString(lblSeqs);
        try (BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filename), "UTF-8"))) {
            out.write(ret);
        }
    }

    /* (non-Javadoc)
     * @see jvntextpro.data.DataWriter#writeString(java.util.List)
     */
    @Override
    public String writeString(List lblSeqs) {
        StringBuilder ret = new StringBuilder();
        for (int i = 0; i < lblSeqs.size(); ++i) {
            Sentence sent = (Sentence) lblSeqs.get(i);

            boolean start = true;
            String word = "";
            StringBuilder sentStr = new StringBuilder();
            for (int j = 0; j < sent.size(); ++j) {
                String curTag = sent.getTagAt(j);
                if (curTag.equalsIgnoreCase("B-W") || curTag.equalsIgnoreCase("O")) {
                    start = true;
                } else if (start && curTag.equalsIgnoreCase("I-W")) {
                    start = false;
                }

                if (start) {
                    sentStr.append(" ").append(word);
                    word = sent.getWordAt(j);
                } else {
                    word = word + "_" + sent.getWordAt(j);
                }
            }
            sentStr.append(" ").append(word);
            ret.append("\n").append(sentStr.toString().trim());
        }

        return ret.toString().trim();
    }

}
