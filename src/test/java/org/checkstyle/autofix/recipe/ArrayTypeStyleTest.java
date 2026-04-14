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

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.checkstyle.autofix.parser.ReportParser;
import org.junit.jupiter.api.Test;

public class ArrayTypeStyleTest extends AbstractRecipeTestSupport {

    @Override
    protected String getSubpackage() {
        return "arraytypestyle";
    }

    @RecipeTest
    void javaStyle(ReportParser parser) throws Exception {
        verify(parser, "JavaStyle");
    }

    @RecipeTest
    void noViolation(ReportParser parser) throws Exception {
        verify(parser, "NoViolation");
    }

    @Test
    void testGetDisplayName() {
        final ArrayTypeStyle recipe = new ArrayTypeStyle(List.of());
        assertEquals("ArrayTypeStyle recipe", recipe.getDisplayName());
        assertEquals(
                "Converts C-style array type declarations to Java-style. "
                        + "For example, converts 'String args[]' to 'String[] args'.",
                recipe.getDescription());
    }
}
