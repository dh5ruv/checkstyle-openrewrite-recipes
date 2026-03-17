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
// ADD THESE TWO IMPORTS
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public final class CheckstyleViolation {

    private final int line;
    private final int column;
    private final String severity;
    private final CheckstyleCheck source;
    private final String message;
    private final Path filePath;

    // UPDATE THIS CONSTRUCTOR
    @JsonCreator
    public CheckstyleViolation(
            @JsonProperty("line") int line,
            @JsonProperty("column") int column,
            @JsonProperty("severity") String severity,
            @JsonProperty("source") CheckstyleCheck source,
            @JsonProperty("message") String message,
            @JsonProperty("filePath") Path filePath) {
        this.line = line;
        this.column = column;
        this.severity = severity;
        this.source = source;
        this.message = message;
        this.filePath = filePath;
    }

    public CheckstyleViolation(int line, String severity,
                               CheckstyleCheck source, String message, Path filePath) {
        this(line, -1, severity, source, message, filePath);
    }

    // ... rest of your getters remain the same ...
    public Integer getLine() { return line; }
    public Integer getColumn() { return column; }
    public CheckstyleCheck getSource() { return source; }
    public String getMessage() { return message; }
    public Path getFilePath() { return filePath; }
    public String getSeverity() { return severity; }
}