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
package vn.edu.vnu.jvntext.jvntextpro.conversion;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * This class provides functionality to convert from a composite unicode string in vietnamese to a unicode string.
 *
 * @author TuNC
 */
public class CompositeUnicode2Unicode {
    /**
     * The Constant DEFAULT_MAP_RESOURCE.
     */
    private static final String DEFAULT_MAP_RESOURCE = "Composite2Unicode.txt";

    /**
     * The cps uni2 uni.
     */
    private final Map<String, String> cpsUni2Uni = new HashMap<>();

    //---------------------------------------------------------------
    //Constructor
    //----------------------------------------------------------------

    /**
     * Instantiates a new composite unicode2 unicode.
     */
    public CompositeUnicode2Unicode() throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(CompositeUnicode2Unicode.class.getResourceAsStream(
            DEFAULT_MAP_RESOURCE)));
        String line;
        while ((line = reader.readLine()) != null) {
            String[] onemap = line.split("\t");
            if (onemap.length != 2) continue;
            cpsUni2Uni.put(onemap[0], onemap[1]);
        }

        reader.close();
    }

    //---------------------------------------------------------------
    //Public method
    //----------------------------------------------------------------

    /**
     * Convert a vietnamese string with composite unicode encoding to unicode encoding.
     *
     * @param text string in vietnamese with composite unicode encoding
     * @return string with unicode encoding
     */
    public String convert(String text) {
        String ret = text;

        for (String cpsChar : cpsUni2Uni.keySet()) {
            ret = ret.replaceAll(cpsChar, cpsUni2Uni.get(cpsChar));
        }

        return ret;

    }
}
