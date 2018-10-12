/*
 * MIT License
 *
 * Copyright (c) 2018 RedNesto
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
package io.github.rednesto.bou.common;

import java.util.List;

public class CustomLoot {

    private List<ItemLoot> itemLoots;
    private int experience;
    private boolean overwrite;
    private boolean expOverwrite;

    public CustomLoot(List<ItemLoot> itemLoots, int experience, boolean overwrite, boolean expOverwrite) {
        this.itemLoots = itemLoots;
        this.experience = experience;
        this.overwrite = overwrite;
        this.expOverwrite = expOverwrite;
    }

    public List<ItemLoot> getItemLoots() {
        return itemLoots;
    }

    public int getExperience() {
        return experience;
    }

    public boolean isOverwrite() {
        return overwrite;
    }

    public boolean isExpOverwrite() {
        return expOverwrite;
    }
}
