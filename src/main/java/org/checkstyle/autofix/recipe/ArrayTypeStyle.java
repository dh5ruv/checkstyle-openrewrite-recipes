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
import org.openrewrite.ExecutionContext;
import org.openrewrite.Recipe;
import org.openrewrite.Tree;
import org.openrewrite.TreeVisitor;
import org.openrewrite.java.JavaIsoVisitor;
import org.openrewrite.java.tree.J;
import org.openrewrite.java.tree.JLeftPadded;
import org.openrewrite.java.tree.JavaType;
import org.openrewrite.java.tree.Space;
import org.openrewrite.java.tree.TypeTree;
import org.openrewrite.marker.Markers;

/**
 * Fixes Checkstyle ArrayTypeStyle violations by converting C-style
 * array declarations to Java-style.
 * Example: {@code String args[]} becomes {@code String[] args}
 */
public class ArrayTypeStyle extends Recipe {

    private final List<CheckstyleViolation> violations;

    public ArrayTypeStyle(List<CheckstyleViolation> violations) {
        this.violations = violations;
    }

    @Override
    public String getDisplayName() {
        return "ArrayTypeStyle recipe";
    }

    @Override
    public String getDescription() {
        return "Converts C-style array type declarations to Java-style. "
                + "For example, converts 'String args[]' to 'String[] args'.";
    }

    @Override
    public TreeVisitor<?, ExecutionContext> getVisitor() {
        return new ArrayTypeStyleVisitor();
    }

    private final class ArrayTypeStyleVisitor extends JavaIsoVisitor<ExecutionContext> {

        private Path sourcePath;
        private J.CompilationUnit compilationUnit;

        @Override
        public J.CompilationUnit visitCompilationUnit(
                J.CompilationUnit cu, ExecutionContext executionContext) {
            this.sourcePath = cu.getSourcePath().toAbsolutePath();
            this.compilationUnit = cu;
            return super.visitCompilationUnit(cu, executionContext);
        }

        @Override
        public J.VariableDeclarations visitVariableDeclarations(
                J.VariableDeclarations multiVariable, ExecutionContext executionContext) {

            final J.VariableDeclarations visited = super.visitVariableDeclarations(
                    multiVariable, executionContext);

            J.VariableDeclarations result = visited;

            if (isAtViolationLocation(visited)) {
                final List<J.VariableDeclarations.NamedVariable> variables =
                        visited.getVariables();
                final int dimCount = variables.get(0).getDimensionsAfterName().size();

                TypeTree newType = visited.getTypeExpression();
                for (int idx = 0; idx < dimCount; idx++) {
                    newType = new J.ArrayType(
                            Tree.randomId(),
                            Space.EMPTY,
                            Markers.EMPTY,
                            newType,
                            null,
                            JLeftPadded.build(Space.EMPTY),
                            new JavaType.Array(null, newType.getType(), null)
                    );
                }

                final TypeTree finalNewType = newType;
                final List<J.VariableDeclarations.NamedVariable> newVars = variables.stream()
                        .map(variable -> {
                            return variable.withDimensionsAfterName(Collections.emptyList());
                        })
                        .toList();

                result = visited.withTypeExpression(finalNewType).withVariables(newVars);
            }

            return result;
        }

        private boolean isAtViolationLocation(
                J.VariableDeclarations variableDeclarations) {
            final List<J.VariableDeclarations.NamedVariable> variables =
                    variableDeclarations.getVariables();
            final boolean result;

            if (variables.isEmpty()
                    || variables.get(0).getDimensionsAfterName().isEmpty()) {
                result = false;
            }
            else {
                final J.VariableDeclarations.NamedVariable firstVar = variables.get(0);
                final int line = PositionHelper
                        .computeLinePosition(compilationUnit, firstVar, getCursor());
                final int column = PositionHelper
                        .computeColumnPosition(compilationUnit, firstVar, getCursor())
                        + firstVar.getSimpleName().length();

                result = violations.stream().anyMatch(violation -> {
                    final Path absolutePath = violation.getFilePath().toAbsolutePath();
                    return violation.getLine() == line
                            && violation.getColumn() == column
                            && absolutePath.endsWith(sourcePath);
                });
            }

            return result;
        }
    }
}
