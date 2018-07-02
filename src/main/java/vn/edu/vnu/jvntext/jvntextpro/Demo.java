package vn.edu.vnu.jvntext.jvntextpro;

import java.io.IOException;

import vn.edu.vnu.jvntext.jvntextpro.conversion.CompositeUnicode2Unicode;
import vn.edu.vnu.jvntext.utils.InitializationException;

/**
 * Use this class as an example for using JVnTextPro in your own project.
 */
public class Demo {
    private static CompositeUnicode2Unicode unicodeNormalizer;
    private static JVnTextPro vnTextPro;

    private static void init() throws IOException, InitializationException {
        vnTextPro = new JVnTextPro();
        vnTextPro.initSenTokenization();
        unicodeNormalizer = new CompositeUnicode2Unicode();

        vnTextPro.initSenSegmenter();
        // use this method instead if you have a custom model
        //        vnTextPro.initSenSegmenter(Paths.get("path", "to", "senSegModels"));

        vnTextPro.initSenTokenization();

        vnTextPro.initSegmenter();
        // use this method instead if you have a custom model
        //        vnTextPro.initSegmenter(Paths.get("path", "to", "segModels"));

        vnTextPro.initPosTagger();
        // use this method instead if you have a custom model
        //        vnTextPro.initPosTagger(Paths.get("path", "to", "posTagModels"));
    }

    public static void main(String[] args) throws IOException, InitializationException {
        init();
        String text = "Việt Nam (tên chính thức: Cộng hòa xã hội chủ nghĩa Việt Nam) là quốc "
                      + "gia nằm ở phía đông bán đảo Đông Dương thuộc khu vực Đông Nam Á.";

        // Normalize compound unicode characters, converting them to single characters
        String converted = unicodeNormalizer.convert(text);
        // perform actual annotation
        String annotated = vnTextPro.process(converted);
        System.out.println(annotated);
        // Should print:
        // Việt_Nam/Np (/( tên/N chính_thức/A :/: Cộng/Np hòa/A xã_hội_chủ_nghĩa/A Việt_Nam/Np )/)
        // là/C quốc_gia/N nằm/V ở/E phía/N đông/A bán_đảo/N Đông_Dương_thuộc/Np
        // khu_vực/N Đông_Nam_Á/Np ./.
    }
}
