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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import vn.edu.vnu.jvntext.jvntextpro.data.Sentence;

public class RegexContextGenerator extends BasicContextGenerator {
    //----------------------------
    //variables
    //----------------------------
    // Regular Expression Pattern string
    /**
     * The str number pattern.
     */
    private static final String strNumberPattern = "[+-]?\\d+([,.]\\d+)*";

    /**
     * The str short date pattern.
     */
    private static final String strShortDatePattern = "\\d+[/-:]\\d+";

    /**
     * The str long date pattern.
     */
    private static final String strLongDatePattern = "\\d+[/-:]\\d+[/-:]\\d+";

    /**
     * The str percentage pattern.
     */
    private static final String strPercentagePattern = strNumberPattern + "%";

    /**
     * The str currency pattern.
     */
    private static final String strCurrencyPattern = "\\p{Sc}" + strNumberPattern;

    /**
     * The str vi currency pattern.
     */
    private static final String strViCurrencyPattern = strNumberPattern + "[ \t]*\\p{Sc}";

    // Regular Expression Pattern
    /**
     * The ptn number.
     */
    private static Pattern ptnNumber = Pattern.compile(strNumberPattern);

    /**
     * The ptn short date.
     */
    private static Pattern ptnShortDate = Pattern.compile(strShortDatePattern);

    /**
     * The ptn long date.
     */
    private static Pattern ptnLongDate = Pattern.compile(strLongDatePattern);

    /**
     * The ptn percentage.
     */
    private static Pattern ptnPercentage = Pattern.compile(strPercentagePattern);

    /**
     * The ptn currency.
     */
    private static Pattern ptnCurrency = Pattern.compile(strCurrencyPattern);

    /**
     * The ptn vi currency.
     */
    private static Pattern ptnViCurrency = Pattern.compile(strViCurrencyPattern);

    //----------------------------
    //methods
    //----------------------------

    /**
     * Instantiates a new regex context generator.
     *
     * @param node the node
     */
    public RegexContextGenerator(Element node) {
        readFeatureParameters(node);
    }

    /* (non-Javadoc)
     * @see ContextGenerator#getContext(Sentence, int)
     */
    @Override
    public String[] getContext(Sentence sent, int pos) {
        // generate context predicates
        List<String> cps = new ArrayList<>();


        // get the context information from sequence
        for (int it = 0; it < cpnames.size(); ++it) {
            String cp = cpnames.get(it);
            Vector<Integer> paras = this.paras.get(it);
            String cpvalue = "";

            StringBuilder suffix = new StringBuilder();
            String regex;
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
            if (outOfArrayIndex) continue;

            word = new StringBuilder(word.toString().trim().toLowerCase());
            suffix = new StringBuilder(suffix.substring(0, suffix.length() - 1));
            suffix.insert(0, ":");

            // Match to a specific pattern
            regex = patternMatching(cp, word.toString());
            if (!regex.equals("")) {
                cpvalue = "re" + suffix + regex;
            }

            if (!cpvalue.equals("")) cps.add(cpvalue);
        }
        String[] ret = new String[cps.size()];
        return cps.toArray(ret);
    }

    //----------------------------
    // utility methods
    //----------------------------

    /**
     * Pattern matching.
     *
     * @param ptnName the ptn name
     * @param input   the input
     * @return the string
     */
    private static String patternMatching(String ptnName, String input) {
        String suffix = "";

        Matcher matcher;
        switch (ptnName) {
            case "number":
                matcher = ptnNumber.matcher(input);
                if (matcher.matches()) suffix = ":number";
                break;
            case "short_date":
                matcher = ptnShortDate.matcher(input);
                if (matcher.matches()) suffix = ":short-date";
                break;
            case "long_date":
                matcher = ptnLongDate.matcher(input);
                if (matcher.matches()) suffix = ":long-date";
                break;
            case "percentage":
                matcher = ptnPercentage.matcher(input);
                if (matcher.matches()) suffix = ":percentage";
                break;
            case "currency":
                matcher = ptnCurrency.matcher(input);
                if (matcher.matches()) suffix = ":currency";
                else {
                    matcher = ptnViCurrency.matcher(input);
                    if (matcher.matches()) {
                        suffix = ":currency";
                    }
                }
                break;
        }
        return suffix;
    }
}
