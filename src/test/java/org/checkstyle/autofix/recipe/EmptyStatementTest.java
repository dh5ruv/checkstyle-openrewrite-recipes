package org.checkstyle.autofix.recipe;

import org.checkstyle.autofix.CheckFullName;
import org.checkstyle.autofix.CheckstyleCheck;
import org.checkstyle.autofix.parser.CheckstyleViolation;
import org.junit.jupiter.api.Test;
import org.openrewrite.test.RewriteTest;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import static org.openrewrite.java.Assertions.java;
import java.util.Collections;

class EmptyStatementTest implements RewriteTest {

    @Test
    void removeEmptyStatement() {
        CheckstyleViolation fakeViolation = new CheckstyleViolation(
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
        // Trap for PIT: Logic should NOT delete if line is different
        CheckstyleViolation mismatchLine = new CheckstyleViolation(
            10, 1, "error", 
            new CheckstyleCheck(CheckFullName.EMPTY_STATEMENT, null), 
            "Empty statement.", 
            Paths.get("Test.java").toAbsolutePath()
        );

        rewriteRun(
            spec -> spec.recipe(new EmptyStatement(List.of(mismatchLine))),
            java(
                "class Test { ; }", // MUST STAY because violation is for Line 10
                spec -> spec.path("Test.java")
            )
        );
    }

    @Test
    void doesNotRemoveWhenPathMismatch() {
        // Trap for PIT: Logic should NOT delete if file path is different
        CheckstyleViolation mismatchPath = new CheckstyleViolation(
            1, 1, "error", 
            new CheckstyleCheck(CheckFullName.EMPTY_STATEMENT, null), 
            "Empty statement.", 
            Paths.get("Other.java").toAbsolutePath()
        );

        rewriteRun(
            spec -> spec.recipe(new EmptyStatement(List.of(mismatchPath))),
            java(
                "class Test { ; }", // MUST STAY because violation is for Other.java
                spec -> spec.path("Test.java")
            )
        );
    }

    @Test
    void onlyRemovesTargetViolationInMultiStatement() {
        CheckstyleViolation target = new CheckstyleViolation(
            3, 5, "error", 
            new CheckstyleCheck(CheckFullName.EMPTY_STATEMENT, null), 
            "Empty statement.", 
            Paths.get("Test.java").toAbsolutePath()
        );

        rewriteRun(
            spec -> spec.recipe(new EmptyStatement(List.of(target))),
            java(
                "class A {\n" +
                "    void m() {\n" +
                "        ;\n" +
                "        int x = 5;\n" +
                "    }\n" +
                "}",
                "class A {\n" +
                "    void m() {\n" +
                "        int x = 5;\n" +
                "    }\n" +
                "}",
                spec -> spec.path("Test.java")
            )
        );
    }
    @Test
    void killConstructorMutation() {
        EmptyStatement recipe = new EmptyStatement(null);
        rewriteRun(
            spec -> spec.recipe(recipe),
            java("class A { ; }", spec -> spec.path("Test.java"))
        );
    }

    @Test
    void killMetadataMutations() {
        EmptyStatement recipe = new EmptyStatement(Collections.emptyList());
        assert recipe.getDisplayName().equals("EmptyStatement recipe");
        assert recipe.getDescription().contains("Removes standalone semicolons");
    }
}