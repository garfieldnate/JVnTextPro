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

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import jvntextpro.data.DataReader;
import jvntextpro.data.Sentence;
import jvntextpro.data.TWord;

// TODO: Read data in IOB2 format which is the same as training data provided
// in the previous version of JVnSegmenter (v.1.0)

/**
 * The Class IOB2DataReader.
 */
public class IOB2DataReader extends DataReader {

    /* (non-Javadoc)
     * @see jvntextpro.data.DataReader#readFile(java.lang.String)
     */
    @Override
    public List<Sentence> readFile(String datafile) throws IOException {
        String dataString = FileUtils.readFileToString(new File(datafile), StandardCharsets.UTF_8);
        return readString(dataString);
    }

    /* (non-Javadoc)
     * @see jvntextpro.data.DataReader#readString(java.lang.String)
     */
    @Override
    public List<Sentence> readString(String dataStr) {
        dataStr = dataStr + "$";
        //TODO: won't work with Windows newlines
        String[] lines = dataStr.split("\\n");

        List<Sentence> data = new ArrayList<Sentence>();
        Sentence sent = new Sentence();
        for (String line : lines) {
            System.out.println(".");
            if (line.isEmpty() || line.equalsIgnoreCase("$")) {
                data.add(sent);
                sent = new Sentence();
            } else {
                StringTokenizer tk = new StringTokenizer(line, "\t");
                if (tk.countTokens() == 2) {
                    String token = tk.nextToken();
                    String tag = tk.nextToken();
                    TWord tw = new TWord(token, tag);
                    sent.addTWord(tw);
                }
            }
        }
        return data;
    }

}
