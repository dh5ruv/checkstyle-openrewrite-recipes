package org.checkstyle.autofix.recipe;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import org.checkstyle.autofix.PositionHelper;
import org.checkstyle.autofix.parser.CheckstyleViolation;
import org.openrewrite.ExecutionContext;
import org.openrewrite.Recipe;
import org.openrewrite.TreeVisitor;
import org.openrewrite.java.JavaIsoVisitor;
import org.openrewrite.java.tree.J;

public class EmptyStatement extends Recipe {
    private final List<CheckstyleViolation> violations;

    @JsonCreator
    public EmptyStatement(@JsonProperty("violations") List<CheckstyleViolation> violations) {
        this.violations = violations != null ? violations : Collections.emptyList();
    }

    @Override
    public String getDisplayName() { return "EmptyStatement recipe"; }

    @Override
    public String getDescription() { return "Removes standalone semicolons that match violations."; }

    @Override
    public TreeVisitor<?, ExecutionContext> getVisitor() {
        return new EmptyStatementVisitor();
    }

    private final class EmptyStatementVisitor extends JavaIsoVisitor<ExecutionContext> {
        private Path sourcePath;

        @Override
        public J.CompilationUnit visitCompilationUnit(J.CompilationUnit cu, ExecutionContext ctx) {
            this.sourcePath = cu.getSourcePath().toAbsolutePath();
            return super.visitCompilationUnit(cu, ctx);
        }

        @Override
        public J.Empty visitEmpty(J.Empty empty, ExecutionContext ctx) {
            J.Empty e = super.visitEmpty(empty, ctx);

            if (isAtViolationLocation(e)) {
                return null;
            }

            return e;
        }

        private boolean isAtViolationLocation(J.Empty empty) {
            final J.CompilationUnit cu = getCursor().firstEnclosing(J.CompilationUnit.class);
            final int line = PositionHelper.computeLinePosition(cu, empty, getCursor());

            return violations.stream().anyMatch(v -> 
                v.getLine() == line &&
                v.getFilePath().toAbsolutePath().equals(sourcePath)
            );
        }
    }
}