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
package io.github.rednesto.bou.config;

import com.typesafe.config.*;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import javax.annotation.Nullable;

public class SimpleConfigIncluderFile implements ConfigIncluder, ConfigIncluderFile {

    private final Path basePath;
    private final ConfigIncluder fallback;

    public SimpleConfigIncluderFile(Path basePath) {
        this(basePath, null);
    }

    public SimpleConfigIncluderFile(Path basePath, @Nullable ConfigIncluder fallback) {
        this.basePath = basePath.toAbsolutePath();
        this.fallback = fallback;
    }

    @Override
    public ConfigIncluder withFallback(ConfigIncluder fallback) {
        // Implementation taken from com.typesafe.config.impl.SimpleIncluder
        if (this == fallback) {
            throw new ConfigException.BugOrBroken("trying to create includer cycle");
        } else if (this.fallback == fallback) {
            return this;
        } else if (this.fallback != null) {
            return new SimpleConfigIncluderFile(this.basePath, this.fallback.withFallback(fallback));
        } else {
            return new SimpleConfigIncluderFile(this.basePath, fallback);
        }
    }

    @Override
    public ConfigObject include(ConfigIncludeContext context, String what) {
        Path toInclude = basePath.resolve(what);
        ConfigObject configRoot = doInclude(context, toInclude);
        if (fallback != null) {
            return configRoot.withFallback(fallback.include(context, what));
        }

        return configRoot;
    }

    @Override
    public ConfigObject includeFile(ConfigIncludeContext context, File what) {
        Path toInclude = basePath.resolve(what.toString());
        ConfigObject configRoot = doInclude(context, toInclude);
        if (fallback != null && fallback instanceof ConfigIncluderFile) {
            final ConfigObject fallbackIncluderFile = ((ConfigIncluderFile) fallback).includeFile(context, what);
            return configRoot.withFallback(fallbackIncluderFile);
        }

        return configRoot;
    }

    private ConfigObject doInclude(ConfigIncludeContext context, Path toInclude) {
        if (Files.isDirectory(toInclude)) {
            final ConfigObject[] configRoot = {null};
            try {
                Files.walkFileTree(toInclude, new SimpleFileVisitor<Path>() {
                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                        ConfigObject obj = includeFile(context, file.toFile());
                        if (configRoot[0] == null) {
                            configRoot[0] = obj;
                        } else {
                            configRoot[0] = obj.withFallback(configRoot[0]);
                        }

                        return FileVisitResult.CONTINUE;
                    }
                });
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }

            if (configRoot[0] == null) {
                return ConfigFactory.empty().root();
            }

            return configRoot[0];
        } else {
            Config config = ConfigFactory.parseFileAnySyntax(toInclude.toFile(), context.parseOptions());
            return config.root();
        }
    }
}
