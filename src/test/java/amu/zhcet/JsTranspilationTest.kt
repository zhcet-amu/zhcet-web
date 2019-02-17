package amu.zhcet

import org.apache.commons.io.IOUtils
import org.junit.Test
import org.springframework.core.io.ClassPathResource

import java.io.IOException

import org.junit.Assert.assertEquals

class JsTranspilationTest {

    @Test
    @Throws(IOException::class)
    fun testFileExists() {
        val transpiled = "!function(){\"use strict\";console.log(\"test\")}();\n//# sourceMappingURL=test.min.js.map\n"
        val nonMinified = "(function () {\n" +
                "\t'use strict';\n" +
                "\n" +
                "\t// This file is used to test the transpilation of javascript\n" +
                "\t// Do not remove, rename or change the contents\n" +
                "\n" +
                "\tconsole.log(\"test\");\n" +
                "\n" +
                "}());\n"
        val content = IOUtils.toString(ClassPathResource("static/js/build/test.min.js").inputStream, "UTF-8")
        if (content != transpiled) {
            println("WARNING: JS Not Minified")
            assertEquals(nonMinified, content)
        } else {
            assertEquals(transpiled, content)
        }
    }

}
