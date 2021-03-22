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

import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class LinterReportExporter {

    public static List<Text> export(LinterContext context) {
        List<LinterContext.ReportItem> items = context.getReportItems();
        if (items.isEmpty()) {
            return Collections.emptyList();
        }

        return items.stream()
                .map(item -> {
                    String key = item.getKey() != null ? item.getKey() : "<unknown>";
                    return Text.of("@", TextColors.GRAY, key, TextColors.WHITE, ": ", severityToColor(item), item.getMessage());
                })
                .collect(Collectors.toList());
    }

    private static TextColor severityToColor(LinterContext.ReportItem item) {
        switch (item.getSeverity()) {
            case ERROR:
                return TextColors.RED;
            case WARNING:
                return TextColors.YELLOW;
            default:
                return TextColors.WHITE;
        }
    }
}
