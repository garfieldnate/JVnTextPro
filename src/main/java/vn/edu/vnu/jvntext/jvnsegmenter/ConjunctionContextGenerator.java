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
package vn.edu.vnu.jvntext.jvnsegmenter;

import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import vn.edu.vnu.jvntext.jvntextpro.data.Sentence;

public class ConjunctionContextGenerator extends BasicContextGenerator {

    /**
     * Instantiates a new conjunction context generator.
     *
     * @param node the node
     */
    public ConjunctionContextGenerator(Element node) {
        readFeatureParameters(node);
    }

    /* (non-Javadoc)
     * @see ContextGenerator#getContext(Sentence, int)
     */
    @Override
    public String[] getContext(Sentence sent, int pos) {
        List<String> cps = new ArrayList<>();

        for (int it = 0; it < cpnames.size(); ++it) {
            String cp = cpnames.get(it);
            Vector<Integer> paras = this.paras.get(it);
            String cpvalue = "";

            if (cp.equals("syll_conj_gen")) {
                StringBuilder prefix = new StringBuilder("s:");
                StringBuilder suffix = new StringBuilder();
                for (Integer para : paras) {
                    if (pos + para < 0 || pos + para >= sent.size()) {
                        cpvalue = "";
                        continue;
                    }

                    prefix.append(para).append(":");
                    suffix.append(sent.getWordAt(pos + para)).append(":");
                }

                if (suffix.toString().endsWith(":")) cpvalue = prefix + suffix.substring(0, suffix.length() - 1);
            }
            if (!cpvalue.equals("")) cps.add(cpvalue);
        }
        String[] ret = new String[cps.size()];
        return cps.toArray(ret);
    }

}
