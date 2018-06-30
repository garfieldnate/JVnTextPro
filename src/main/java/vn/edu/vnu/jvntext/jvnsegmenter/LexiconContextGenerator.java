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
package vn.edu.vnu.jvntext.jvnsegmenter;

import org.w3c.dom.Element;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import vn.edu.vnu.jvntext.jvntextpro.data.Sentence;

public class LexiconContextGenerator extends BasicContextGenerator {
    //------------------------------
    //Variables
    //------------------------------
    /**
     * The hs vietnamese dict.
     */
    private static Set<String> hsVietnameseDict;

    /**
     * The hs vi family names.
     */
    private static Set<String> hsViFamilyNames;

    /**
     * The hs vi middle names.
     */
    private static Set<String> hsViMiddleNames;

    /**
     * The hs vi last names.
     */
    private static Set<String> hsViLastNames;

    /**
     * The hs vi locations.
     */
    private static Set<String> hsViLocations;

    //------------------------------
    //Methods
    //------------------------------

    /**
     * Instantiates a new lexicon context generator.
     *
     * @param node the node
     */
    public LexiconContextGenerator(Element node) {
        readFeatureParameters(node);
    }

    /* (non-Javadoc)
     * @see ContextGenerator#getContext(Sentence, int)
     */
    @Override
    public String[] getContext(Sentence sent, int pos) {
        // get the context information from sequence
        List<String> cps = new ArrayList<>();

        for (int it = 0; it < cpnames.size(); ++it) {
            String cp = cpnames.get(it);
            Vector<Integer> paras = this.paras.get(it);
            String cpvalue = "";

            StringBuilder suffix = new StringBuilder();
            StringBuilder word = new StringBuilder();
            boolean outOfArrayIndex = false;
            //noinspection Duplicates
            for (Integer para : paras) {
                if (pos + para < 0 || pos + para >= sent.size()) {
                    cpvalue = "";
                    outOfArrayIndex = true;
                    break;
                }

                suffix.append(para).append(":");
                word.append(sent.getWordAt(pos + para)).append(" ");
            }
            word = new StringBuilder(word.toString().trim());
            if (suffix.toString().endsWith(":")) suffix = new StringBuilder(suffix.substring(0, suffix.length() - 1));

            if (outOfArrayIndex) continue;

            switch (cp) {
                case "vietnamese_dict":
                    word = new StringBuilder(word.toString().toLowerCase());
                    if (inVietnameseDict(word.toString())) {
                        cpvalue = "d:" + suffix;
                    }
                    break;
                case "family_name":
                    if (inViFamilyNameList(word.toString())) cpvalue = "fam:" + suffix;
                    break;
                case "middle_name":
                    if (inViMiddleNameList(word.toString())) cpvalue = "mdl:" + suffix;
                    break;
                case "last_name":
                    if (inViLastNameList(word.toString())) cpvalue = "lst:" + suffix;
                    break;
                case "location":
                    if (inViLocations(word.toString())) cpvalue = "loc:" + suffix;
                    break;
            }

            if (!cpvalue.equals("")) cps.add(cpvalue);
        }
        String[] ret = new String[cps.size()];
        return cps.toArray(ret);
    }

    //------------------------------
    // static methods
    //------------------------------

    /**
     * In vietnamese dict.
     *
     * @param word the word
     * @return true, if successful
     */
    public static boolean inVietnameseDict(String word) {
        return hsVietnameseDict.contains(word);
    }

    /**
     * In vi family name list.
     *
     * @param word the word
     * @return true, if successful
     */
    public static boolean inViFamilyNameList(String word) {
        return hsViFamilyNames.contains(word);
    }

    /**
     * In vi middle name list.
     *
     * @param word the word
     * @return true, if successful
     */
    public static boolean inViMiddleNameList(String word) {
        return hsViMiddleNames.contains(word);
    }

    /**
     * In vi last name list.
     *
     * @param word the word
     * @return true, if successful
     */
    public static boolean inViLastNameList(String word) {
        return hsViLastNames.contains(word);
    }

    /**
     * In vi locations.
     *
     * @param word the word
     * @return true, if successful
     */
    public static boolean inViLocations(String word) {
        return hsViLocations.contains(word);
    }

    /**
     * Load vietnamese dict.
     *
     * @param filename the filename
     */
    public static void loadVietnameseDict(Path filename) throws IOException {
        InputStream in = Files.newInputStream(filename);
        if (hsVietnameseDict == null) {
            hsVietnameseDict = new HashSet<>();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.substring(0, 2).equals("##")) {
                    String word = line.substring(2);
                    word = word.toLowerCase();
                    hsVietnameseDict.add(word);
                }
            }
        }
    }

    /**
     * Load vi personal names.
     *
     * @param filename the filename
     */
    public static void loadViPersonalNames(Path filename) throws IOException {
        InputStream in = Files.newInputStream(filename);
        if (hsViFamilyNames == null) {

            hsViFamilyNames = new HashSet<>();
            hsViLastNames = new HashSet<>();
            hsViMiddleNames = new HashSet<>();

            BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.equals("")) continue;

                //line = line.toLowerCase();
                int idxSpace = line.indexOf(' ');
                int lastIdxSpace = line.lastIndexOf(' ');

                if (idxSpace != -1) {
                    String strFamilyName = line.substring(0, idxSpace);
                    hsViFamilyNames.add(strFamilyName);
                }

                if ((idxSpace != -1) && (lastIdxSpace > idxSpace + 1)) {
                    String strMiddleName = line.substring(idxSpace + 1, lastIdxSpace - 1);
                    hsViMiddleNames.add(strMiddleName);
                }

                if (lastIdxSpace != -1) {
                    String strLastName = line.substring(lastIdxSpace + 1, line.length());
                    hsViLastNames.add(strLastName);
                }
            }
            in.close();
        }
    }

    /**
     * Load vi location list.
     *
     * @param filename the filename
     */
    public static void loadViLocationList(Path filename) throws IOException {
        InputStream in = Files.newInputStream(filename);
        if (hsViLocations == null) {
            hsViLocations = new HashSet<>();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            String line;
            while ((line = reader.readLine()) != null) {
                String word = line.trim();
                hsViLocations.add(word);
            }
        }
    }
}
