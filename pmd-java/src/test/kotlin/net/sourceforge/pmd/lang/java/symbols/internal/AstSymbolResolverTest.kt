/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.internal

import io.kotest.matchers.types.shouldBeSameInstanceAs
import io.kotest.matchers.shouldBe
import net.sourceforge.pmd.lang.ast.test.shouldBeA
import net.sourceforge.pmd.lang.java.ast.ProcessorTestSpec
import net.sourceforge.pmd.lang.java.symbols.JClassSymbol
import net.sourceforge.pmd.lang.java.symbols.internal.ast.SymbolResolutionPass
import net.sourceforge.pmd.lang.java.symbols.table.internal.testProcessor

/**
 * @author Clément Fournier
 */
class AstSymbolResolverTest : ProcessorTestSpec({

    parserTest("Simple test") {

        val resolver = parser.withProcessing(false).parse("""
            package com.foo.bar;
            
            public class Foo {
                class Inner {}
            }
            
            class Other {}
        """.trimIndent()).let {
            SymbolResolutionPass.traverse(testProcessor(), it)
        }



        doTest("Test outer class") {
            resolver.resolveClassFromBinaryName("com.foo.bar") shouldBe null
            resolver.resolveClassFromBinaryName("com.foo.bar.Foo").shouldBeA<JClassSymbol> {  }
            resolver.resolveClassFromBinaryName("com.foo.bar.Other").shouldBeA<JClassSymbol> {  }
        }

        doTest("Test inner class") {
            val sym = resolver.resolveClassFromBinaryName("com.foo.bar.Foo\$Inner").shouldBeA<JClassSymbol> {  }
            val sym2 = resolver.resolveClassFromCanonicalName("com.foo.bar.Foo.Inner").shouldBeA<JClassSymbol> {  }

            sym.shouldBeSameInstanceAs(sym2)
        }
    }
})