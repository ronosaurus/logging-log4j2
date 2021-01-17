/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache license, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the license for the specific language governing permissions and
 * limitations under the license.
 */
package org.apache.logging.log4j.core.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.apache.logging.log4j.core.pattern.JAnsiTextRenderer;
import org.apache.logging.log4j.core.pattern.PlainTextRenderer;
import org.apache.logging.log4j.core.pattern.TextRenderer;
import org.apache.logging.log4j.core.util.Loader;
import org.apache.logging.log4j.core.util.Patterns;
import org.apache.logging.log4j.status.StatusLogger;
import org.apache.logging.log4j.util.Strings;

/**
 * Contains options which control how a {@link Throwable} pattern is formatted.
 */
public final class ThrowableFormatOptions {

    private static final int DEFAULT_LINES = Integer.MAX_VALUE;

    /**
     * Default instance of {@code ThrowableFormatOptions}.
     */
    protected static final ThrowableFormatOptions DEFAULT = new ThrowableFormatOptions();

    /**
     * Format the whole stack trace.
     */
    private static final String FULL = "full";

    /**
     * Do not format the exception.
     */
    private static final String NONE = "none";

    /**
     * Format only the first line of the throwable.
     */
    private static final String SHORT = "short";

    /**
     * ANSI renderer
     */
    private final TextRenderer textRenderer;

    /**
     * The number of lines to write.
     */
    private final int lines;

    /**
     * The stack trace separator.
     */
    private final String separator;

    private final String suffix;

    /**
     * The list of packages to filter.
     */
    private final List<String> filterPackages;

    private final List<String> filterStartFrames;

    public static final String CLASS_NAME = "short.className";
    public static final String METHOD_NAME = "short.methodName";
    public static final String LINE_NUMBER = "short.lineNumber";
    public static final String FILE_NAME = "short.fileName";
    public static final String MESSAGE = "short.message";
    public static final String LOCALIZED_MESSAGE = "short.localizedMessage";

    /**
     * Constructs the options for printing stack trace.
     *
     * @param lines
     *            The number of lines.
     * @param separator
     *            The stack trace separator.
     * @param filterPackages
     *            The packages to filter.
     * @param filterStartFrames
     *            The frame (package+method) to filter.
     * @param textRenderer
     *            The ANSI renderer
     * @param suffix
     */
    protected ThrowableFormatOptions(final int lines, final String separator, final List<String> filterPackages,
            final List<String> filterStartFrames, final TextRenderer textRenderer, final String suffix) {
        this.lines = lines;
        this.separator = separator == null ? Strings.LINE_SEPARATOR : separator;
        this.filterPackages = filterPackages;
        this.filterStartFrames = filterStartFrames;
        this.textRenderer = textRenderer == null ? PlainTextRenderer.getInstance() : textRenderer;
        this.suffix = suffix;
    }

    /**
     * Constructs the options for printing stack trace.
     *
     * @param filterPackages
     *            The packages to filter.
     */
    protected ThrowableFormatOptions(final List<String> filterPackages) {
        this(DEFAULT_LINES, null, filterPackages, null, null, null);
    }

    /**
     * Constructs the options for printing stack trace.
     */
    protected ThrowableFormatOptions() {
        this(DEFAULT_LINES, null, null, null, null, null);
    }

    /**
     * Returns the number of lines to write.
     *
     * @return The number of lines to write.
     */
    public int getLines() {
        return this.lines;
    }

    /**
     * Returns the stack trace separator.
     *
     * @return The stack trace separator.
     */
    public String getSeparator() {
        return this.separator;
    }

    /**
     * Returns the message rendered.
     *
     * @return the message rendered.
     */
    public TextRenderer getTextRenderer() {
        return textRenderer;
    }

    /**
     * Returns the list of packages to ignore (filter out).
     *
     * @return The list of packages to ignore (filter out).
     */
    public List<String> getFilterPackages() {
        return this.filterPackages;
    }

    /**
     * Returns the list of frames (package + method name) that identify a stack trace starting point.
     *
     * @return the list of frames (package + method name) that identify a stack trace starting point.
     */
    public List<String> getFilterStartFrames() {
        return this.filterStartFrames;
    }

    /**
     * Determines if all lines should be printed.
     *
     * @return true for all lines, false otherwise.
     */
    public boolean allLines() {
        return this.lines == DEFAULT_LINES;
    }

    /**
     * Determines if any lines should be printed.
     *
     * @return true for any lines, false otherwise.
     */
    public boolean anyLines() {
        return this.lines > 0;
    }

    /**
     * Returns the minimum between the lines and the max lines.
     *
     * @param maxLines
     *            The maximum number of lines.
     * @return The number of lines to print.
     */
    public int minLines(final int maxLines) {
        return this.lines > maxLines ? maxLines : this.lines;
    }

    /**
     * Determines if there are any packages to filter.
     *
     * @return true if there are packages, false otherwise.
     */
    public boolean hasFilterPackages() {
        return this.filterPackages != null && !this.filterPackages.isEmpty();
    }

    /**
     * Determines if there are any packages to filter.
     *
     * @return true if there are packages, false otherwise.
     */
    public boolean hasfilterStartFrames() {
        return this.filterStartFrames != null && !this.filterStartFrames.isEmpty();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        final StringBuilder s = new StringBuilder();
        s.append('{')
                .append(allLines() ? FULL : this.lines == 2 ? SHORT : anyLines() ? String.valueOf(this.lines) : NONE)
                .append('}');
        s.append("{separator(").append(this.separator).append(")}");
        if (hasFilterPackages()) { // duplicate code
            s.append("{filters(");
            for (final String p : this.filterPackages) {
                s.append(p).append(',');
            }
            s.deleteCharAt(s.length() - 1);
            s.append(")}");
        }
        if (hasfilterStartFrames()) { // duplicate code
            s.append("{filters.startFrames(");
            for (final String p : this.filterStartFrames) {
                s.append(p).append(',');
            }
            s.deleteCharAt(s.length() - 1);
            s.append(")}");
        }
        return s.toString();
    }

    /**
     * Creates a new instance based on the array of options.
     *
     * @param options
     *            The array of options.
     * @return A new initialized instance.
     */
    public static ThrowableFormatOptions newInstance(String[] options) {
        if (options == null || options.length == 0) {
            return DEFAULT;
        }
        // NOTE: The following code is present for backward compatibility
        // and was copied from Extended/RootThrowablePatternConverter.
        // This supports a single option with the format:
        // %xEx{["none"|"short"|"full"|depth],[filters(packages)}
        // However, the convention for multiple options should be:
        // %xEx{["none"|"short"|"full"|depth]}[{filters(packages)}]
        if (options.length == 1 && Strings.isNotEmpty(options[0])) {
            final String[] opts = options[0].split(Patterns.COMMA_SEPARATOR, 2);
            final String first = opts[0].trim();
            try (final Scanner scanner = new Scanner(first)) {
                if (opts.length > 1 && (first.equalsIgnoreCase(FULL) || first.equalsIgnoreCase(SHORT)
                        || first.equalsIgnoreCase(NONE) || scanner.hasNextInt())) {
                    options = new String[] { first, opts[1].trim() };
                }
            }
        }

        int lines = DEFAULT.lines;
        String separator = DEFAULT.separator;
        List<String> filterPackages = DEFAULT.filterPackages;
        List<String> filterStartFrames = DEFAULT.filterStartFrames;
        TextRenderer ansiRenderer = DEFAULT.textRenderer;
        String suffix = DEFAULT.getSuffix();
        for (final String rawOption : options) {
            if (rawOption != null) {
                final String option = rawOption.trim();
                if (option.isEmpty()) {
                    // continue;
                } else if (option.startsWith("separator(") && option.endsWith(")")) {
                    separator = option.substring("separator(".length(), option.length() - 1);
                } else if (option.startsWith("filters(") && option.endsWith(")")) {
                    filterPackages = extractFilters(filterPackages, option, "filters(");
                } else if (option.startsWith("filters.startFrames(") && option.endsWith(")")) {
                    filterStartFrames = extractFilters(filterStartFrames, option, "filters.startFrames(");
                } else if (option.equalsIgnoreCase(NONE)) {
                    lines = 0;
                } else if (option.equalsIgnoreCase(SHORT) || option.equalsIgnoreCase(CLASS_NAME)
                        || option.equalsIgnoreCase(METHOD_NAME) || option.equalsIgnoreCase(LINE_NUMBER)
                        || option.equalsIgnoreCase(FILE_NAME) || option.equalsIgnoreCase(MESSAGE)
                        || option.equalsIgnoreCase(LOCALIZED_MESSAGE)) {
                    lines = 2;
                } else if (option.startsWith("ansi(") && option.endsWith(")") || option.equals("ansi")) {
                    if (Loader.isJansiAvailable()) {
                        final String styleMapStr = option.equals("ansi") ? Strings.EMPTY
                                : option.substring("ansi(".length(), option.length() - 1);
                        ansiRenderer = new JAnsiTextRenderer(new String[] { null, styleMapStr },
                                JAnsiTextRenderer.DefaultExceptionStyleMap);
                    } else {
                        StatusLogger.getLogger().warn(
                                "You requested ANSI exception rendering but JANSI is not on the classpath. Please see https://logging.apache.org/log4j/2.x/runtime-dependencies.html");
                    }
                } else if (option.startsWith("S(") && option.endsWith(")")){
                    suffix = option.substring("S(".length(), option.length() - 1);
                } else if (option.startsWith("suffix(") && option.endsWith(")")){
                    suffix = option.substring("suffix(".length(), option.length() - 1);
                } else if (!option.equalsIgnoreCase(FULL)) {
                    lines = Integer.parseInt(option);
                }
            }
        }
        return new ThrowableFormatOptions(lines, separator, filterPackages, filterStartFrames, ansiRenderer, suffix);
    }

    /**
     * Common code for extracting packages from "filters(com.example)" and "filters.startFrames(com.example)"
     *
     */
    private static List<String> extractFilters(List<String> packages, String option, String conversionWord) {
        final String filterStr = option.substring(conversionWord.length(), option.length() - 1);
        if (filterStr.length() > 0) {
            final String[] array = filterStr.split(Patterns.COMMA_SEPARATOR);
            if (array.length > 0) {
                packages = new ArrayList<>(array.length);
                for (String token : array) {
                    token = token.trim();
                    if (token.length() > 0) {
                        packages.add(token);
                    }
                }
            }
        }
        return packages;
    }

    public String getSuffix() {
        return suffix;
    }

}
