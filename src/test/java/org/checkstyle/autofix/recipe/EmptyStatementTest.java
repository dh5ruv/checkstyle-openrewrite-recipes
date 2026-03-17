package org.checkstyle.autofix.recipe;

import org.checkstyle.autofix.CheckFullName;
import org.checkstyle.autofix.CheckstyleCheck;
import org.checkstyle.autofix.parser.CheckstyleViolation;
import org.junit.jupiter.api.Test;
import org.openrewrite.test.RewriteTest;
import java.nio.file.Paths;
import java.util.List;
import static org.openrewrite.java.Assertions.java;

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
                    "class A { ; }", // Semicolon at 11
                    "class A { }",   // Exactly ONE space between the braces
                    spec -> spec.path("Test.java")
                )
            );
    }
}