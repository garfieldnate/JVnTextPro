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

package vn.edu.vnu.jvntext.jvntextpro.data;

import java.util.ArrayList;

public class TaggingData {

    /**
     * The cntx gen vector.
     */
    protected ArrayList<ContextGenerator> cntxGenVector = null;

    /**
     * Instantiates a new tagging data.
     */
    public TaggingData() {
        cntxGenVector = new ArrayList<>();
    }

    /**
     * Instantiates a new tagging data.
     *
     * @param _cntxGenVector the _cntx gen vector
     */
    public TaggingData(ArrayList<ContextGenerator> _cntxGenVector) {
        cntxGenVector = _cntxGenVector;
    }

    /**
     * Instantiates a new tagging data.
     *
     * @param cntxGen the cntx gen
     */
    public TaggingData(ContextGenerator cntxGen) {
        cntxGenVector = new ArrayList<>();
        cntxGenVector.add(cntxGen);
    }

    /**
     * Adds the context generator.
     *
     * @param cntxGen the cntx gen
     */
    public void addContextGenerator(ContextGenerator cntxGen) {
        cntxGenVector.add(cntxGen);
    }

    /**
     * Gets the context.
     *
     * @param sent    the sent
     * @param wordIdx the word idx
     * @return the context
     */
    public String[] getContext(Sentence sent, int wordIdx) {
        ArrayList<String> tempCps = new ArrayList<>();

        for (ContextGenerator aCntxGenVector : cntxGenVector) {
            String[] context = aCntxGenVector.getContext(sent, wordIdx);
            if (context != null) {
                for (String ctxt : context) {
                    if (ctxt.trim().equals("")) continue;
                    tempCps.add(ctxt);
                }
            }
        }

        String[] tempCpsArray = new String[tempCps.size()];
        return tempCps.toArray(tempCpsArray);
    }

    /**
     * Gets the context str.
     *
     * @param sent    the sentence
     * @param wordIdx the word index
     * @return the string representing contexts extracted at wordIdx of the sentence sent
     */
    public String getContextStr(Sentence sent, int wordIdx) {
        StringBuilder cpStr = new StringBuilder();

        for (ContextGenerator aCntxGenVector : cntxGenVector) {
            String[] context = aCntxGenVector.getContext(sent, wordIdx);
            if (context != null) {
                for (String ctxt : context) {
                    if (ctxt.trim().equals("")) continue;
                    cpStr.append(ctxt).append(" ");
                }
            }
        }

        return cpStr.toString().trim();
    }
}
