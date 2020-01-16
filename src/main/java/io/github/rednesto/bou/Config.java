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

import io.github.rednesto.bou.api.blockspawners.SpawnedMob;
import io.github.rednesto.bou.api.customdrops.CustomLoot;
import io.github.rednesto.bou.api.fastharvest.FastHarvestCrop;
import io.github.rednesto.bou.api.fastharvest.FastHarvestTools;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.util.ArrayList;
import java.util.HashMap;
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

    public static CropsControl getCropsControl() {
        return BoxOUtils.getInstance().getCropsControl();
    }

    @ConfigSerializable
    public static class BlocksDrops {

        @Setting("enabled")
        public boolean enabled;
        @Setting("blocks")
        public Map<String, List<CustomLoot>> drops;

        public BlocksDrops() {}

        public BlocksDrops(boolean enabled, Map<String, List<CustomLoot>> drops) {
            this.enabled = enabled;
            this.drops = drops;
        }
    }

    @ConfigSerializable
    public static class MobsDrops {

        @Setting("enabled")
        public boolean enabled;
        @Setting("mobs")
        public Map<String, List<CustomLoot>> drops;

        public MobsDrops() {}

        public MobsDrops(boolean enabled, Map<String, List<CustomLoot>> drops) {
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

        @Setting("drop_in_world")
        public boolean dropInWorld;

        @Setting("tools")
        public FastHarvestTools tools;

        public FastHarvest() {}

        public FastHarvest(boolean enabled, boolean dropInWorld, FastHarvestTools tools) {
            this.enabled = enabled;
            this.dropInWorld = dropInWorld;
            this.tools = tools;
        }

        public static FastHarvest createDefault() {
            FastHarvestTools tools = new FastHarvestTools(false, true, true, new ArrayList<>());
            return new FastHarvest(false, true, tools);
        }
    }

    @ConfigSerializable
    public static class CropsControl {

        @Setting("enabled")
        public boolean enabled;

        @Setting("crops")
        public Map<String, FastHarvestCrop> crops;

        public CropsControl() {}

        public CropsControl(boolean enabled, Map<String, FastHarvestCrop> crops) {
            this.enabled = enabled;
            this.crops = crops;
        }

        public static CropsControl createDefault() {
            HashMap<String, FastHarvestCrop> crops = new HashMap<>();
            crops.put("minecraft:beetroot", FastHarvestCrop.createDefault());
            crops.put("minecraft:beetroot_seed", FastHarvestCrop.createDefault(3));
            crops.put("minecraft:carrot", FastHarvestCrop.createDefault(3));
            crops.put("minecraft:potato", FastHarvestCrop.createDefault(3));
            crops.put("minecraft:seed", FastHarvestCrop.createDefault(3));
            crops.put("minecraft:wheat", FastHarvestCrop.createDefault());

            return new CropsControl(false, crops);
        }
    }
}
