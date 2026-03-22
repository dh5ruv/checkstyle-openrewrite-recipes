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
import org.openrewrite.Cursor;

public class EmptyStatement extends Recipe {
    private final List<CheckstyleViolation> violations;

    @JsonCreator
    public EmptyStatement(@JsonProperty("violations") List<CheckstyleViolation> violations) {
        if (violations == null) {
            this.violations = Collections.emptyList();
        } else {
            this.violations = violations;
        }
    }

    @Override
    public String getDisplayName() { 
        return "EmptyStatement recipe"; 
    }

    @Override
    public String getDescription() { 
        return "Removes standalone semicolons that match violations."; 
    }

    @Override
    public TreeVisitor<?, ExecutionContext> getVisitor() {
        return new EmptyStatementVisitor();
    }

    private final class EmptyStatementVisitor extends JavaIsoVisitor<ExecutionContext> {
        private Path sourcePath;

        @Override
        public J.CompilationUnit visitCompilationUnit(J.CompilationUnit cu, ExecutionContext ctx) {
            Path p = cu.getSourcePath();
            this.sourcePath = p.isAbsolute() ? p : p.toAbsolutePath(); 
            return super.visitCompilationUnit(cu, ctx);
        }

        @Override
        public J.Empty visitEmpty(J.Empty empty, ExecutionContext ctx) {
            J.Empty e = super.visitEmpty(empty, ctx);
            
            // Critical: Line and Path check to kill PIT mutations
            if (isAtViolationLocation(e)) {
                return null; // Delete it (Kills NULL_RETURNS)
            }
            return e;
        }

        private boolean isAtViolationLocation(J.Empty empty) {
            Cursor cursor = getCursor();
            J.CompilationUnit cu = cursor.firstEnclosing(J.CompilationUnit.class);
            Path currentPath = cu.getSourcePath().toAbsolutePath();
            int currentLine = PositionHelper.computeLinePosition(cu, empty, cursor);

            for (CheckstyleViolation violation : violations) {
                if (violation.getLine() == currentLine && 
                    violation.getFilePath().toAbsolutePath().equals(currentPath)) {
                    return true;
                }
            }
            return false;
        }
    }
}