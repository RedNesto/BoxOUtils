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
package io.github.rednesto.bou.config.serializers;

import com.google.common.reflect.TypeToken;
import io.github.rednesto.bou.config.linting.LinterContext;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

public class SerializerUtils {

    // There are more to add but for now this will be enough
    private static final char[] charactersToEscape = new char[]{'.', ':', '=', '{', '}', '[', ']'};

    public static String getFullKey(ConfigurationNode node) {
        ConfigurationNode nextNode = node;
        StringBuilder fullKey = new StringBuilder(escapeKey((String.valueOf(nextNode.getKey()))));
        while ((nextNode = nextNode.getParent()) != null) {
            Object keyObj = nextNode.getKey();
            if (keyObj == null) {
                continue;
            }

            String key = escapeKey(keyObj.toString());
            fullKey.insert(0, key + ".");
        }

        return fullKey.toString();
    }

    public static String escapeKey(@Nullable String key) {
        if (key == null) {
            return "";
        }

        if (StringUtils.containsAny(key, charactersToEscape)) {
            return '"' + key + '"';
        }

        return key;
    }

    /**
     * Equivalent to {@link ConfigurationNode#getList(TypeToken)} but without throwing
     * {@link ObjectMappingException} and always returning a list of non-null elements.
     *
     * @param node the node to deserialize
     * @param <E> the type of elements the list is expected to contain
     *
     * @return the list containing non-null elements of the given type
     */
    public static <E> List<@NonNull E> getListSafe(ConfigurationNode node, TypeToken<E> type) {
        ArrayList<@NonNull E> values = new ArrayList<>();
        if (node.hasListChildren()) {
            for (ConfigurationNode childNode : node.getChildrenList()) {
                try {
                    E value = childNode.getValue(type);
                    if (value != null) {
                        values.add(value);
                    }
                } catch (ObjectMappingException e) {
                    LinterContext.registerError("List element deserialize error", childNode, e);
                }
            }
        } else {
            try {
                E value = node.getValue(type);
                if (value != null) {
                    values.add(value);
                }
            } catch (ObjectMappingException e) {
                LinterContext.registerError("List element deserialize error", node, e);
            }
        }
        return values;
    }
}
