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
import com.flowpowered.math.vector.Vector3d;
import io.github.rednesto.bou.BoxOUtils;
import io.github.rednesto.bou.Config;
import io.github.rednesto.bou.api.customdrops.CustomLoot;
import io.github.rednesto.bou.api.customdrops.ItemLoot;
import io.github.rednesto.bou.api.quantity.FixedIntQuantity;
import io.github.rednesto.bou.listeners.CustomBlockDropsListener;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.event.entity.SpawnEntityEvent;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.difficulty.Difficulties;
import org.spongepowered.mctester.api.junit.MinecraftRunner;
import org.spongepowered.mctester.internal.BaseTest;
import org.spongepowered.mctester.internal.event.StandaloneEventListener;
import org.spongepowered.mctester.junit.TestUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

@RunWith(MinecraftRunner.class)
public class TestRun extends BaseTest {

    public TestRun(TestUtils testUtils) {
        super(testUtils);
    }

    @Test
    public void waw() throws Throwable {
        HashMap<String, CustomLoot> map = new HashMap<>();
        ArrayList<ItemLoot> itemLoots = new ArrayList<>();
        itemLoots.add(new ItemLoot("minecraft:diamond_sword", null, null, 0d, null));
        map.put("minecraft:grass", new CustomLoot(itemLoots, new FixedIntQuantity(0), false, false, Collections.emptyList(), null, null));
        BoxOUtils.getInstance().setBlocksDrops(new Config.BlocksDrops(true, map));
        Sponge.getEventManager().registerListeners(BoxOUtils.getInstance(), new CustomBlockDropsListener());

        Location<World> playerLocation = testUtils.getThePlayer().getLocation();
        Location<World> blockLock = playerLocation.add(Vector3d.FORWARD);

        testUtils.runOnMainThread(() -> {
            blockLock.setBlockType(BlockTypes.GRASS);
            playerLocation.getExtent().getProperties().setDifficulty(Difficulties.PEACEFUL);
        });

        testUtils.listenOneShot(() -> {
                    client.lookAt(blockLock.getPosition());
                    client.leftClick();
                },
                new StandaloneEventListener<>(SpawnEntityEvent.class, event -> {
                    System.out.println(event.getCause().first(BoxOUtils.class).isPresent());
                    Assert.assertTrue(event.getEntities().stream().anyMatch(entity -> {
                        ItemStackSnapshot snapshot = entity.get(Keys.REPRESENTED_ITEM).orElse(null);
                        if (snapshot == null) {
                            return false;
                        }

                        System.out.println(snapshot.getType().getId());
                        System.out.println(snapshot.getType().getName());

                        return snapshot.getType() == ItemTypes.DIAMOND_SWORD;
                    }));
                }));
    }
}
