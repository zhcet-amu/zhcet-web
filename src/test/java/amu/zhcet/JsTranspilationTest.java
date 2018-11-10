package amu.zhcet;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class JsTranspilationTest {

    @Test
    public void testFileExists() throws IOException {
        String transpiled = "!function(){\"use strict\";console.log(\"test\")}();\n//# sourceMappingURL=test.min.js.map\n";
        String nonMinified = "(function () {\n" +
                "\t'use strict';\n" +
                "\n" +
                "\t// This file is used to test the transpilation of javascript\n" +
                "\t// Do not remove, rename or change the contents\n" +
                "\n" +
                "\tconsole.log(\"test\");\n" +
                "\n" +
                "}());" +
                "\n//# sourceMappingURL=test.min.js.map\n";
        String content = IOUtils.toString(new ClassPathResource("static/js/build/test.min.js").getInputStream(), "UTF-8");
        if (!content.equals(transpiled)) {
            System.out.println("WARNING: JS Not Minified");
            assertEquals(nonMinified, content);
        } else {
            assertEquals(transpiled, content);
        }
    }

}
