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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Element {

    /**
     * The count.
     */
    public int count = 0;    // the number of occurrences of this context predicate

    /**
     * The chosen.
     */
    public int chosen = 0;    // indicating whether or not it is incorporated into the model

    /**
     * The lb cnt fidxes.
     */
    Map<Integer, CountFeatureIdx> lbCntFidxes = null;    // map of labels to CountFeatureIdxes

    /**
     * The cp features.
     */
    List<Feature> cpFeatures = null;    // features associated with this context predicates

    /**
     * The is scanned.
     */
    boolean isScanned = false;    // be scanned or not

    /**
     * Instantiates a new element.
     */
    public Element() {
        lbCntFidxes = new HashMap<>();
        cpFeatures = new ArrayList<>();
    }

}
