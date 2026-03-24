///////////////////////////////////////////////////////////////////////////////////////////////
// checkstyle-openrewrite-recipes: Automatically fix Checkstyle violations with OpenRewrite.
// Copyright (C) 2025 The Checkstyle OpenRewrite Recipes Authors
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
///////////////////////////////////////////////////////////////////////////////////////////////

package org.checkstyle.autofix.recipe;

import static org.openrewrite.java.Assertions.java;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

import org.checkstyle.autofix.CheckFullName;
import org.checkstyle.autofix.CheckstyleCheck;
import org.checkstyle.autofix.parser.CheckstyleViolation;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openrewrite.test.RewriteTest;

class EmptyStatementTest implements RewriteTest {

    @Test
    void removeEmptyStatement() {
        final CheckstyleViolation fakeViolation = new CheckstyleViolation(
                1,
                11,
                "error",
                new CheckstyleCheck(CheckFullName.EMPTY_STATEMENT, null),
                "Empty statement.",
                Paths.get("Test.java").toAbsolutePath()
            );

        rewriteRun(
                spec -> spec.recipe(new EmptyStatement(List.of(fakeViolation))),
                java(
                    "class A { ; }",
                    "class A { }",
                    spec -> spec.path("Test.java")
                )
        );
    }

    @Test
    void doesNotRemoveWhenLineMismatch() {
        final CheckstyleViolation mismatchLine = new CheckstyleViolation(
            10, 1, "error",
            new CheckstyleCheck(CheckFullName.EMPTY_STATEMENT, null),
            "Empty statement.",
            Paths.get("Test.java").toAbsolutePath()
        );

        rewriteRun(
            spec -> spec.recipe(new EmptyStatement(List.of(mismatchLine))),
            java(
                "class Test { ; }",
                spec -> spec.path("Test.java")
            )
        );
    }

    @Test
    void doesNotRemoveWhenPathMismatch() {
        final Path relPath = Paths.get("WrongFile.java");
        final CheckstyleViolation mismatchPath = new CheckstyleViolation(
            1, 1, "error",
            new CheckstyleCheck(CheckFullName.EMPTY_STATEMENT, null),
            "Empty statement.",
            relPath.toAbsolutePath()
        );
        rewriteRun(
            spec -> spec.recipe(new EmptyStatement(List.of(mismatchPath))),
            java(
                "class Test { ; }",
                spec -> spec.path("Test.java")
            )
        );
    }

    @Test
    void onlyRemovesTargetViolationInMultiStatement() {
        final CheckstyleViolation target = new CheckstyleViolation(
            3, 5, "error",
            new CheckstyleCheck(CheckFullName.EMPTY_STATEMENT, null),
            "Empty statement.",
            Paths.get("Test.java").toAbsolutePath()
        );

        rewriteRun(
            spec -> spec.recipe(new EmptyStatement(List.of(target))),
            java(
                "class A {\n"
                + "    void m() {\n"
                + "        ;\n"
                + "        int x = 5;\n"
                + "    }\n"
                + "}",
                "class A {\n"
                + "    void m() {\n"
                + "        int x = 5;\n"
                + "    }\n"
                + "}",
                spec -> spec.path("Test.java")
            )
        );
    }

    @Test
    void killConstructorMutation() {
        final EmptyStatement recipe = new EmptyStatement(null);
        rewriteRun(
            spec -> spec.recipe(recipe),
            java("class A { ; }", spec -> spec.path("Test.java"))
        );
    }

    @Test
    void killMetadataMutations() {
        final EmptyStatement recipe = new EmptyStatement(Collections.emptyList());
        // REMOVED assert AND USED Assertions.assertEquals
        Assertions.assertEquals("EmptyStatement recipe", recipe.getDisplayName());
        Assertions.assertTrue(recipe.getDescription().contains("Removes standalone semicolons"));
    }

    @Test
    void killVisitEmptyMutation() {
        final EmptyStatement recipe = new EmptyStatement(Collections.emptyList());
        rewriteRun(
            spec -> spec.recipe(recipe),
            java("class A { ; }")
        );
    }

    @Test
    void killPathMutations() {
        final Path relPath = Paths.get("Test.java");
        final CheckstyleViolation v = new CheckstyleViolation(1, 1, "error",
            new CheckstyleCheck(CheckFullName.EMPTY_STATEMENT, null),
            "msg", relPath.toAbsolutePath());
        rewriteRun(
            spec -> spec.recipe(new EmptyStatement(List.of(v))),
            java("class Test { ; }", "class Test { }",
                 spec -> spec.path("Test.java"))
        );
    }

    @Test
    void killLineMismatchMutant() {
        final CheckstyleViolation v = new CheckstyleViolation(10, 1, "error",
            new CheckstyleCheck(CheckFullName.EMPTY_STATEMENT, null),
            "Empty statement.", Paths.get("Test.java").toAbsolutePath());
        rewriteRun(
            spec -> spec.recipe(new EmptyStatement(List.of(v))),
            java(
                "class Test { ; }",
                spec -> spec.path("Test.java")
            )
        );
    }

    @Test
    void killConstructorFinalMutant() {
        final EmptyStatement recipe = new EmptyStatement(null);
        rewriteRun(
            spec -> spec.recipe(recipe),
            java("class A { ; }")
        );
    }

    @Test
    void killPathAndReceiverMutations() {
        final Path p = Paths.get("Test.java");
        final CheckstyleViolation v = new CheckstyleViolation(1, 1, "error",
            new CheckstyleCheck(CheckFullName.EMPTY_STATEMENT, null),
            "msg", p.toAbsolutePath());
        rewriteRun(
            spec -> spec.recipe(new EmptyStatement(List.of(v))),
            java("class Test { ; }", "class Test { }",
                 spec -> spec.path("Test.java"))
        );
    }

    @Test
    void killLineNegationMutant() {
        final CheckstyleViolation v = new CheckstyleViolation(5, 1, "error",
            new CheckstyleCheck(CheckFullName.EMPTY_STATEMENT, null),
            "Empty statement.", Paths.get("Test.java").toAbsolutePath());
        rewriteRun(
            spec -> spec.recipe(new EmptyStatement(List.of(v))),
            java(
                "class Test { ; }",
                spec -> spec.path("Test.java")
            )
        );
    }
}
