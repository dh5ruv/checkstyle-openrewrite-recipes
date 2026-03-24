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

package org.checkstyle.autofix.parser;

import java.nio.file.Path;

import org.checkstyle.autofix.CheckstyleCheck;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class CheckstyleViolation {

    private final int line;
    private final int column;
    private final String severity;
    private final CheckstyleCheck check;
    private final String message;
    private final Path filePath;

    @JsonCreator
    public CheckstyleViolation(
            @JsonProperty("line") final int line,
            @JsonProperty("column") final int column,
            @JsonProperty("severity") final String severity,
            @JsonProperty("check") final CheckstyleCheck check,
            @JsonProperty("message") final String message,
            @JsonProperty("filePath") final Path filePath) {
        this.line = line;
        this.column = column;
        this.severity = severity;
        this.check = check;
        this.message = message;
        this.filePath = filePath;
    }

    public CheckstyleViolation(
            final int line,
            final String severity,
            final CheckstyleCheck check,
            final String message,
            final Path filePath) {
        this(line, 0, severity, check, message, filePath);
    }

    public int getLine() {
        return line;
    }

    public int getColumn() {
        return column;
    }

    public String getSeverity() {
        return severity;
    }

    public CheckstyleCheck getCheck() {
        return check;
    }

    public String getMessage() {
        return message;
    }

    public Path getFilePath() {
        return filePath;
    }

    public Path getSource() {
        return filePath;
    }
}
