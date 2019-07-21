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
package io.github.rednesto.bou;

import io.github.rednesto.bou.models.CustomLoot;
import io.github.rednesto.bou.models.FastHarvestCrop;
import io.github.rednesto.bou.models.FastHarvestTools;
import io.github.rednesto.bou.models.SpawnedMob;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class Config {

    // TODO add a way to enable/disable the removal of a seed due to seeding

    private Config() {}

    public static boolean canHarvest(String item) {
        FastHarvestTools tools = getFastHarvest().tools;
        return !tools.isEnabled()
                || tools.isWhitelist() && tools.getToolsIds().contains(item)
                || !tools.isWhitelist() && !tools.getToolsIds().contains(item);
    }

    public static BlocksDrops getBlocksDrops() {
        return BoxOUtils.getInstance().getBlocksDrops();
    }

    public static MobsDrops getMobsDrops() {
        return BoxOUtils.getInstance().getMobsDrops();
    }

    public static BlockSpawners getBlockSpawners() {
        return BoxOUtils.getInstance().getBlockSpawners();
    }

    public static FastHarvest getFastHarvest() {
        return BoxOUtils.getInstance().getFastHarvest();
    }

    @ConfigSerializable
    public static class BlocksDrops {

        @Setting("enabled")
        public boolean enabled;
        @Setting("blocks")
        public Map<String, CustomLoot> drops;

        public BlocksDrops() {}

        public BlocksDrops(boolean enabled, Map<String, CustomLoot> drops) {
            this.enabled = enabled;
            this.drops = drops;
        }
    }

    @ConfigSerializable
    public static class MobsDrops {

        @Setting("enabled")
        public boolean enabled;
        @Setting("mobs")
        public Map<String, CustomLoot> drops;

        public MobsDrops() {}

        public MobsDrops(boolean enabled, Map<String, CustomLoot> drops) {
            this.enabled = enabled;
            this.drops = drops;
        }
    }

    @ConfigSerializable
    public static class BlockSpawners {

        @Setting("enabled")
        public boolean enabled;
        @Setting("blocks")
        public Map<String, List<SpawnedMob>> spawners;

        public BlockSpawners() {}

        public BlockSpawners(boolean enabled, Map<String, List<SpawnedMob>> spawners) {
            this.enabled = enabled;
            this.spawners = spawners;
        }
    }

    @ConfigSerializable
    public static class FastHarvest {

        @Setting("enabled")
        public boolean enabled;

        @Setting("beetroot")
        public FastHarvestCrop beetroot;
        @Setting("beetroot_seed")
        public FastHarvestCrop beetrootSeed;
        @Setting("carrot")
        public FastHarvestCrop carrot;
        @Setting("potato")
        public FastHarvestCrop potato;
        @Setting("seed")
        public FastHarvestCrop seed;
        @Setting("wheat")
        public FastHarvestCrop wheat;

        @Setting("list")
        public FastHarvestTools tools;

        public FastHarvest() {}

        public FastHarvest(boolean enabled,
                           FastHarvestCrop beetroot,
                           FastHarvestCrop beetrootSeed,
                           FastHarvestCrop carrot,
                           FastHarvestCrop potato,
                           FastHarvestCrop seed,
                           FastHarvestCrop wheat,
                           FastHarvestTools tools) {
            this.enabled = enabled;
            this.beetroot = beetroot;
            this.beetrootSeed = beetrootSeed;
            this.carrot = carrot;
            this.potato = potato;
            this.seed = seed;
            this.wheat = wheat;
            this.tools = tools;
        }

        public static FastHarvest createDefault() {
            return new FastHarvest(false,
                    FastHarvestCrop.createDefault(),
                    FastHarvestCrop.createDefault(),
                    FastHarvestCrop.createDefault(),
                    FastHarvestCrop.createDefault(),
                    FastHarvestCrop.createDefault(),
                    FastHarvestCrop.createDefault(),
                    new FastHarvestTools(false, true, new ArrayList<>()));
        }
    }
}
