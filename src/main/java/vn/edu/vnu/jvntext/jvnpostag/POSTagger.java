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

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import vn.edu.vnu.jvntext.jvntextpro.data.DataReader;
import vn.edu.vnu.jvntext.jvntextpro.data.DataWriter;
import vn.edu.vnu.jvntext.utils.InitializationException;

public interface POSTagger {

    //--------------------------------
    // initialization
    //--------------------------------
    void init(Path modelfile) throws InitializationException;

    //-------------------------------
    //tagging methods
    //-------------------------------

    /**
     * Annotate string with part-of-speech tags
     *
     * @param instr string has been done word segmentation
     * @return string annotated with part-of-speech tags
     */
    String tagging(String instr);


    /**
     * Annotate content of file with part-of-speech tags
     *
     * @param file of which content has been done word segmentation
     * @return string annotated with part-of-speech tags
     */
    String tagging(File file) throws IOException;

    /**
     * Set data writer and reader to this pos tagger this is used to be adaptable to different format of input/output
     * data
     */
    void setDataReader(DataReader reader);

    void setDataWriter(DataWriter writer);
}
