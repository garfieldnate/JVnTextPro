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

import java.util.Map;

public class Observation {

    /**
     * The original data.
     */
    public String originalData = "";    // original data (before generating context predicates)

    /**
     * The cps.
     */
    public int[] cps = null;        // array of context predicates

    /**
     * The model label.
     */
    public int modelLabel = -1;        // label predicted by model

    /**
     * Instantiates a new observation.
     */
    public Observation() {
        // do nothing currently
    }

    /**
     * To string.
     *
     * @param lbInt2Str the lb int2 str
     * @return the string
     */
    public String toString(Map<Integer, String> lbInt2Str) {
        String res = originalData;

        String labelStr = lbInt2Str.get(modelLabel);
        if (labelStr != null) {
            res += Option.OUTPUT_SEPARATOR + labelStr.toUpperCase();
        }

        return res;
    }

}
