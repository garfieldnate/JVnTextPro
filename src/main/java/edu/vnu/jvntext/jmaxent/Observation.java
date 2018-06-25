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

package edu.vnu.jvntext.jmaxent;

import java.util.List;
import java.util.Map;

public class Observation {

    /**
     * The original data.
     */
    public final String originalData = "";

    /**
     * The cps.
     */
    public int[] cps = null;

    /**
     * The human label.
     */
    public int humanLabel = -1;

    /**
     * The model label.
     */
    public int modelLabel = -1;

    //context predicate id representing the identity of current word
    //curWordCp == -1 means this word is not in the training data
    /**
     * The cur word cp.
     */
    public int curWordCp = -1;

    //curWordCp == -1 && dictLabel != -1, modelLabel = dictLabel
    /**
     * The dict label.
     */
    public int dictLabel = -1;

    /**
     * Instantiates a new observation.
     */
    public Observation() {
        // do nothing
    }

    /**
     * Instantiates a new observation.
     *
     * @param cps the cps
     */
    public Observation(int[] cps) {
        this.cps = new int[cps.length];

        System.arraycopy(cps, 0, this.cps, 0, cps.length);
    }

    /**
     * Instantiates a new observation.
     *
     * @param intCps the int cps
     */
    public Observation(List<Integer> intCps) {
        this.cps = new int[intCps.size()];

        for (int i = 0; i < intCps.size(); i++) {
            Integer intCp = intCps.get(i);

            this.cps[i] = intCp;
        }
    }

    /**
     * Instantiates a new observation.
     *
     * @param humanLabel the human label
     * @param cps        the cps
     */
    public Observation(int humanLabel, int[] cps) {
        this.humanLabel = humanLabel;
        this.cps = new int[cps.length];

        System.arraycopy(cps, 0, this.cps, 0, cps.length);
    }

    /**
     * To string.
     *
     * @param lbInt2Str the lb int2 str
     * @return the string
     */
    public String toString(Map<Integer, String> lbInt2Str) {
        String res = originalData;

        String modelLabelStr = lbInt2Str.get(modelLabel);
        if (modelLabelStr != null) {
            res += Option.labelSeparator + modelLabelStr;
        }

        return res;
    }

}
