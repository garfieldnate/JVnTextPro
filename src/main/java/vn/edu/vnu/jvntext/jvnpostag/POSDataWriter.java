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

package vn.edu.vnu.jvntext.jvnpostag;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;

import vn.edu.vnu.jvntext.jvntextpro.data.DataWriter;
import vn.edu.vnu.jvntext.jvntextpro.data.Sentence;

public class POSDataWriter extends DataWriter {

    public void writeFile(List<Sentence> lblSeqs, String filename) throws IOException {
        String ret = writeString(lblSeqs);
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filename), "UTF-8"));
        out.write(ret);
        out.close();
    }

    public String writeString(List<Sentence> lblSeqs) {
        StringBuilder ret = new StringBuilder();
        for (Sentence lblSeq : lblSeqs) {

            for (int j = 0; j < lblSeq.size(); ++j) {
                ret.append(lblSeq.getWordAt(j)).append("/").append(lblSeq.getTagAt(j)).append(" ");
            }
            ret = new StringBuilder(ret.toString().trim() + "\n");
        }

        return ret.toString().trim();
    }

}
