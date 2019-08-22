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
package io.github.rednesto.bou.api.customdrops;

import com.google.common.base.MoreObjects;
import io.github.rednesto.bou.api.requirement.Requirement;

import java.util.List;

public class CustomLootCommand {

    private String rawCommand;
    private SenderMode senderMode;
    private List<List<Requirement<?>>> requirements;

    public CustomLootCommand(String rawCommand, SenderMode senderMode, List<List<Requirement<?>>> requirements) {
        this.rawCommand = rawCommand;
        this.senderMode = senderMode;
        this.requirements = requirements;
    }

    public String getRawCommand() {
        return rawCommand;
    }

    public SenderMode getSenderMode() {
        return senderMode;
    }

    public List<List<Requirement<?>>> getRequirements() {
        return requirements;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CustomLootCommand)) {
            return false;
        }
        CustomLootCommand that = (CustomLootCommand) o;
        return rawCommand.equals(that.rawCommand) &&
                senderMode == that.senderMode &&
                requirements.equals(that.requirements);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("rawCommand", rawCommand)
                .add("senderMode", senderMode)
                .add("requirements", requirements)
                .toString();
    }

    public enum SenderMode {
        SERVER,
        PLAYER
    }
}
