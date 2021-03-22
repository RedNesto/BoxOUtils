/*
 * MIT License
 *
 * Copyright (c) 2019 RedNesto
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package io.github.rednesto.bou.config.linting;

import io.github.rednesto.bou.BouUtils;
import io.github.rednesto.bou.BoxOUtils;
import io.github.rednesto.bou.config.serializers.SerializerUtils;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.jetbrains.annotations.Contract;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

public final class LinterContext {

    @Nullable
    private static LinterContext currentInstance;

    private final List<ReportItem> reportItems = new ArrayList<>();

    public void register(ReportItem item) {
        reportItems.add(item);
    }

    public void register(Severity severity, String message, @Nullable String key, @Nullable Throwable cause) {
        register(new ReportItem(severity, message, key, cause));
    }

    public void warning(String message, @Nullable String key, @Nullable Throwable cause) {
        register(Severity.WARNING, message, key, cause);
    }

    public void error(String message, @Nullable String key, @Nullable Throwable cause) {
        register(Severity.ERROR, message, key, cause);
    }

    public List<ReportItem> getReportItems() {
        return reportItems;
    }

    public enum Severity {
        WARNING,
        ERROR
    }

    public static final class ReportItem {

        private final Severity severity;
        private final String message;
        @Nullable
        private final String key;
        @Nullable
        private final Throwable cause;

        public ReportItem(Severity severity, String message, @Nullable String key, @Nullable Throwable cause) {
            this.severity = severity;
            this.message = message;
            this.key = key;
            this.cause = cause;
        }

        public Severity getSeverity() {
            return severity;
        }

        public String getMessage() {
            return message;
        }

        @Nullable
        public String getKey() {
            return key;
        }

        @Nullable
        public Throwable getCause() {
            return cause;
        }
    }

    @Nullable
    public static LinterContext current() {
        return currentInstance;
    }

    public static void setCurrent(@Nullable LinterContext context) {
        LinterContext.currentInstance = context;
    }

    public static boolean isLinting() {
        return currentInstance != null;
    }

    public static void registerWarning(String message) {
        registerWarning(message, null, null);
    }

    public static void registerWarning(String message, ConfigurationNode node) {
        registerWarning(message, SerializerUtils.getFullKey(node), null);
    }

    public static void registerWarning(String message, @Nullable String key, @Nullable Throwable cause) {
        if (cause instanceof LintingException) {
            return;
        }

        LinterContext current = current();
        if (current != null) {
            current.warning(message, key, cause);
        }

        if (!BouUtils.isOnlyLinting() && !BouUtils.isTesting()) {
            if (cause == null) {
                BoxOUtils.getInstance().getLogger().warn("At '{}': {}", key, message);
            } else {
                BoxOUtils.getInstance().getLogger().warn("At '{}': {}", key, message, cause);
            }
        }
    }

    public static void registerError(String message) {
        registerError(message, (String) null, null);
    }

    public static void registerError(String message, @Nullable Throwable throwable) {
        registerError(message, (String) null, throwable);
    }

    public static void registerError(String message, ConfigurationNode node) {
        registerError(message, SerializerUtils.getFullKey(node), null);
    }

    public static void registerError(String message, ConfigurationNode node, @Nullable Throwable cause) {
        registerError(message, SerializerUtils.getFullKey(node), cause);
    }

    public static void registerError(String message, @Nullable String key, @Nullable Throwable cause) {
        if (cause instanceof LintingException) {
            return;
        }

        LinterContext current = current();
        if (current != null) {
            current.error(message, key, cause);
        }

        if (!BouUtils.isOnlyLinting() && !BouUtils.isTesting()) {
            if (cause == null) {
                BoxOUtils.getInstance().getLogger().error("At '{}': {}", key, message);
            } else {
                BoxOUtils.getInstance().getLogger().error("At '{}': {}", key, message, cause);
            }
        }
    }

    @Contract("_, _ -> fail")
    public static void fail(String message, ConfigurationNode node) throws ObjectMappingException {
        fail(message, node, null);
    }

    @Contract("_, _, _ -> fail")
    public static void fail(String message, ConfigurationNode node, @Nullable Throwable throwable) throws ObjectMappingException {
        if (throwable instanceof LintingException) {
            return;
        }

        registerError(message, node, throwable);
        throw new LintingException(SerializerUtils.getFullKey(node), message);
    }
}
