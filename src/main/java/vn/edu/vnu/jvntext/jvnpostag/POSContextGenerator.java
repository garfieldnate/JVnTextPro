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

package vn.edu.vnu.jvntext.jvnpostag;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import vn.edu.vnu.jvntext.jvntextpro.data.ContextGenerator;
import vn.edu.vnu.jvntext.jvntextpro.data.Sentence;
import vn.edu.vnu.jvntext.jvntextpro.util.StringUtils;
import vn.edu.vnu.jvntext.jvntextpro.util.VnSyllParser;

public class POSContextGenerator extends ContextGenerator {

    //----------------------------------------------
    // Member variables
    //----------------------------------------------
    private static final String DEFAULT_E_DICT = "ComputerDict.txt";
    private final Map<String, List<String>> word2dictags = new HashMap<>();
    private Vector<String> cpnames;
    private Vector<Vector<Integer>> paras;

    //----------------------------------------------
    // Constructor and Override methods
    //----------------------------------------------
    public POSContextGenerator(Path featureTemplateFile) throws IOException, SAXException, ParserConfigurationException {
        readDict();
        readFeatureTemplate(featureTemplateFile);
    }

    @Override
    public String[] getContext(Sentence sent, int pos) {
        // TODO Auto-generated method stub
        List<String> cps = new ArrayList<>();

        for (int it = 0; it < cpnames.size(); ++it) {
            String cp = cpnames.get(it);
            Vector<Integer> paras = this.paras.get(it);
            String cpvalue = "";
            switch (cp) {
                case "w":
                    cpvalue = w(sent, pos, paras.get(0));
                    break;
                case "wj":
                    cpvalue = wj(sent, pos, paras.get(0), paras.get(1));
                    break;
                case "prf":
                    cpvalue = prf(sent, pos, paras.get(0));
                    break;
                case "sff":
                    cpvalue = sff(sent, pos, paras.get(0));
                    break;
                case "an":
                    cpvalue = an(sent, pos, paras.get(0));
                    break;
                case "hn":
                    cpvalue = hn(sent, pos, paras.get(0));
                    break;
                case "hyph":
                    cpvalue = hyph(sent, pos, paras.get(0));
                    break;
                case "slash":
                    cpvalue = slash(sent, pos, paras.get(0));
                    break;
                case "com":
                    cpvalue = com(sent, pos, paras.get(0));
                    break;
                case "ac":
                    cpvalue = ac(sent, pos, paras.get(0));
                    break;
                case "ic":
                    cpvalue = ic(sent, pos, paras.get(0));
                    break;
                case "mk":
                    cpvalue = mk(sent, pos, paras.get(0));
                    break;
                case "dict":
                    cps.add(dict(sent, pos, paras.get(0)));
                    break;
                case "rr":
                    cpvalue = rr(sent, pos, paras.get(0));
                    break;
            }
            if (!cpvalue.equals("")) cps.add(cpvalue);
        }
        String[] ret = new String[cps.size()];
        return cps.toArray(ret);
    }

    //----------------------------------------------
    // IO methods
    //----------------------------------------------
    public void readDict() throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(POSContextGenerator.class.getResourceAsStream(
            DEFAULT_E_DICT), "UTF-8"));
        word2dictags.clear();

        String line, temp = null;
        while ((line = reader.readLine()) != null) {
            String[] tokens = line.split("\t");

            String word, tag;

            if (tokens.length != 2) {
                continue;
            } else {
                if (tokens[0].equals("")) {
                    if (temp == null) continue;
                    else {
                        word = temp;
                        tag = tokens[1];
                    }
                } else {
                    word = tokens[0].trim().toLowerCase();
                    tag = tokens[1].trim();
                    temp = word;
                }
            }

            word = word.replace(" ", "_");
            List<String> dictags = word2dictags.get(word);
            if (dictags == null) {
                dictags = new ArrayList<>();
            }
            dictags.add(tag);
            word2dictags.put(word, dictags);
        }

        reader.close();
    }

    public void readFeatureTemplate(Path file) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();

        InputStream stream = Files.newInputStream(file);
        Document doc = builder.parse(stream);

        Element root = doc.getDocumentElement();
        NodeList childrent = root.getChildNodes();
        cpnames = new Vector<>();
        paras = new Vector<>();

        for (int i = 0; i < childrent.getLength(); i++)
            if (childrent.item(i) instanceof Element) {
                Element child = (Element) childrent.item(i);
                String value = child.getAttribute("value");

                //parse the value and get the parameters
                String[] parastr = value.split(":");
                Vector<Integer> para = new Vector<>();
                for (int j = 1; j < parastr.length; ++j) {
                    para.add(Integer.parseInt(parastr[j]));
                }

                cpnames.add(parastr[0]);
                paras.add(para);
            }
    }

    //-----------------------------------------------
    // feature generating methods
    //-----------------------------------------------

    private String w(Sentence sent, int pos, int i) {
        String cp = "w:" + Integer.toString(i) + ":";
        //if (pos + i == -1)
        //	cp += "BS";
        //else if (pos + i == sent.size())
        //	cp += "ES";
        if (0 <= (pos + i) && (pos + i) < sent.size()) cp += sent.getWordAt(pos + i);
        else cp = "";

        return cp;
    }

    private String wj(Sentence sent, int pos, int i, int j) {
        String cp = "wj:" + Integer.toString(i) + ":" + Integer.toString(j) + ":";
        if ((pos + i) >= sent.size() || (pos + i) < 0 || (pos + j) < 0 || (pos + j) >= sent.size()) cp = "";
        else {
            cp += sent.getWordAt(pos + i) + ":" + sent.getWordAt(pos + j);
        }
        return cp;
    }

    private String prf(Sentence sent, int pos, int i) {
        String cp;
        if (0 <= (pos + i) && (pos + i) < sent.size()) {
            cp = "prf:" + Integer.toString(i) + ":";

            String word = sent.getWordAt(pos + i);
            String[] sylls = word.split("_");
            if (sylls.length >= 2) {
                cp += sylls[0];
            } else cp = "";
        } else cp = "";

        return cp;
    }

    private String sff(Sentence sent, int pos, int i) {
        String cp;
        if (0 <= (pos + i) && (pos + i) < sent.size()) {
            cp = "sff:" + Integer.toString(i) + ":";

            String word = sent.getWordAt(pos + i);
            String[] sylls = word.split("_");
            if (sylls.length >= 2) {
                cp += sylls[sylls.length - 1];
            } else cp = "";
        } else cp = "";

        return cp;
    }

    private String an(Sentence sent, int pos, int i) {
        String cp;
        if (0 <= (pos + i) && (pos + i) < sent.size()) {
            cp = "an:" + Integer.toString(i);

            String word = sent.getWordAt(pos + i);
            if (!StringUtils.isAllNumber(word)) cp = "";
        } else cp = "";

        return cp;
    }

    private String hn(Sentence sent, int pos, int i) {
        String cp;
        if (0 <= (pos + i) && (pos + i) < sent.size()) {
            cp = "hn:" + Integer.toString(i);

            String word = sent.getWordAt(pos + i);
            if (!StringUtils.containNumber(word)) cp = "";
        } else cp = "";

        return cp;
    }

    private String hyph(Sentence sent, int pos, int i) {
        String cp;
        if (0 <= (pos + i) && (pos + i) < sent.size()) {
            cp = "hyph:" + Integer.toString(i);

            String word = sent.getWordAt(pos + i);
            if (!word.contains("-")) cp = "";
        } else cp = "";

        return cp;
    }

    private String slash(Sentence sent, int pos, int i) {
        String cp;
        if (0 <= (pos + i) && (pos + i) < sent.size()) {
            cp = "hyph:" + Integer.toString(i);

            String word = sent.getWordAt(pos + i);
            if (!word.contains("/")) cp = "";
        } else cp = "";

        return cp;
    }

    private String com(Sentence sent, int pos, int i) {
        String cp;
        if (0 <= (pos + i) && (pos + i) < sent.size()) {
            cp = "hyph:" + Integer.toString(i);

            String word = sent.getWordAt(pos + i);
            if (!word.contains(":")) cp = "";
        } else cp = "";

        return cp;
    }

    private String ac(Sentence sent, int pos, int i) {
        String cp;
        if (0 <= (pos + i) && (pos + i) < sent.size()) {
            cp = "ac:" + Integer.toString(i);

            String word = sent.getWordAt(pos + i);
            boolean isAllCap = true;

            for (int j = 0; j < word.length(); ++j) {
                if (word.charAt(j) == '_' || word.charAt(j) == '.') continue;

                if (!Character.isUpperCase(word.charAt(j))) {
                    isAllCap = false;
                    break;
                }
            }

            if (!isAllCap) cp = "";
        } else cp = "";
        return cp;
    }

    private String ic(Sentence sent, int pos, int i) {
        String cp;
        if (0 <= (pos + i) && (pos + i) < sent.size()) {
            cp = "ic:" + Integer.toString(i);

            String word = sent.getWordAt(pos + i);
            if (!StringUtils.isFirstCap(word)) cp = "";
        } else cp = "";

        return cp;
    }

    private String mk(Sentence sent, int pos, int i) {
        String cp;
        if (0 <= (pos + i) && (pos + i) < sent.size()) {
            cp = "mk:" + Integer.toString(i);
            String word = sent.getWordAt(pos + i);
            if (!StringUtils.isPunc(word)) cp = "";
        } else cp = "";

        return cp;
    }

    private String dict(Sentence sent, int pos, int i) {
        StringBuilder cp = new StringBuilder();

        if (0 <= (pos + i) && (pos + i) < sent.size()) {
            String word = sent.getWordAt(pos + i);
            if (word2dictags.containsKey(word)) {
                List<String> tags = word2dictags.get(word);

                for (String tag : tags) {
                    cp.append("dict:").append(Integer.toString(i)).append(":").append(tag).append(" ");
                }
            }
        }

        return cp.toString().trim();
    }

    private String rr(Sentence sent, int pos, int i) {
        String cp = "";

        if (0 <= (pos + i) && (pos + i) < sent.size()) {
            String word = sent.getWordAt(pos + i);
            String[] sylls = word.split("_");

            if (sylls.length == 2) { //consider 2-syllable words
                VnSyllParser parser1 = new VnSyllParser(sylls[0]);
                VnSyllParser parser2 = new VnSyllParser(sylls[1]);

                if (parser1.isValidVnSyllable() && parser2.isValidVnSyllable()) {
                    if (parser1.getNonToneSyll().equalsIgnoreCase(parser2.getNonToneSyll())) {
                        cp += "fr:" + Integer.toString(i) + " ";
                    } else if (parser1.getRhyme().equalsIgnoreCase(parser2.getRhyme())) {
                        cp += "pr:" + Integer.toString(i) + " ";
                    }
                }
            }
        }


        return cp.trim();
    }
}
