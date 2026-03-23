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

import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

import org.checkstyle.autofix.PositionHelper;
import org.checkstyle.autofix.parser.CheckstyleViolation;
import org.openrewrite.Cursor;
import org.openrewrite.ExecutionContext;
import org.openrewrite.Recipe;
import org.openrewrite.TreeVisitor;
import org.openrewrite.java.JavaIsoVisitor;
import org.openrewrite.java.tree.J;

public class EmptyStatement extends Recipe {
    private final List<CheckstyleViolation> violations;

    @com.fasterxml.jackson.annotation.JsonCreator
    public EmptyStatement(
            @com.fasterxml.jackson.annotation.JsonProperty("violations")
            final List<CheckstyleViolation> violations) {
        if (violations == null) {
            this.violations = Collections.emptyList();
        }
        else {
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
        public J.CompilationUnit visitCompilationUnit(final J.CompilationUnit cu,
                                                       final ExecutionContext ctx) {
            final Path p = cu.getSourcePath();
            if (p.isAbsolute()) {
                this.sourcePath = p;
            }
            else {
                this.sourcePath = p.toAbsolutePath();
            }
            return super.visitCompilationUnit(cu, ctx);
        }

        @Override
        public J.Empty visitEmpty(final J.Empty empty, final ExecutionContext ctx) {
            final J.Empty e = super.visitEmpty(empty, ctx);
            // Single return variable to satisfy ReturnCount
            J.Empty result = null;
            if (!isAtViolationLocation(e)) {
                result = e;
            }
            return result;
        }

        private boolean isAtViolationLocation(final J.Empty empty) {
            final Cursor cursor = getCursor();
            final J.CompilationUnit cu = cursor.firstEnclosing(J.CompilationUnit.class);
            final Path currentPath = cu.getSourcePath().toAbsolutePath();
            final int currentLine = PositionHelper.computeLinePosition(cu, empty, cursor);
            boolean found = false;
            for (final CheckstyleViolation violation : violations) {
                if (violation.getLine() == currentLine
                        && violation.getFilePath().toAbsolutePath().equals(currentPath)) {
                    found = true;
                    break;
                }
            }
            return found;
        }
    }
}
