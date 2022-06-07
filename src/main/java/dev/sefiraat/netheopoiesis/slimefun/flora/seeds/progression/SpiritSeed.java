package dev.sefiraat.netheopoiesis.slimefun.flora.seeds.progression;

import dev.sefiraat.netheopoiesis.Netheopoiesis;
import dev.sefiraat.netheopoiesis.Purification;
import dev.sefiraat.netheopoiesis.core.plant.GrowthDescription;
import dev.sefiraat.netheopoiesis.slimefun.NpsItems;
import dev.sefiraat.netheopoiesis.slimefun.NpsRecipeTypes;
import dev.sefiraat.netheopoiesis.slimefun.flora.blocks.NetherSeedCrux;
import dev.sefiraat.netheopoiesis.slimefun.flora.seeds.NetherSeed;
import dev.sefiraat.netheopoiesis.utils.Protection;
import dev.sefiraat.netheopoiesis.utils.Theme;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.libraries.dough.protection.Interaction;
import me.mrCookieSlime.CSCoreLibPlugin.Configuration.Config;
import me.mrCookieSlime.Slimefun.api.BlockStorage;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class SpiritSeed extends NetherSeed {

    public SpiritSeed(@Nonnull ItemGroup itemGroup,
                      @Nonnull SlimefunItemStack item,
                      @Nonnull GrowthDescription growthDescription,
                      @Nonnull Set<String> placement
    ) {
        super(itemGroup, item, NpsRecipeTypes.PLANT_BREEDING, new ItemStack[0], growthDescription, placement);
    }

    @Override
    @ParametersAreNonnullByDefault
    public void onTickFullyGrown(Location location, NetherSeed seed, Config data) {
        double randomChance = ThreadLocalRandom.current().nextDouble();

        if (randomChance > 0.15) {
            // Fails chance roll
            return;
        }

        final double randomX = ThreadLocalRandom.current().nextInt(-3, 4);
        final double randomZ = ThreadLocalRandom.current().nextInt(-3, 4);
        // For loop to make sure the purification can creep up and down.
        for (int i = -1; i < 2; i++) {
            final Block block = location.clone().add(randomX, i, randomZ).getBlock();
            final SlimefunItem possibleCrux = BlockStorage.check(block);
            if (possibleCrux instanceof NetherSeedCrux crux
                && getPlacements().contains(crux.getId())
                && Protection.hasPermission(getOwner(location), block, Interaction.BREAK_BLOCK)
            ) {
                BlockStorage.clearBlockInfo(block);
                Purification.removeValue(block);
                // Schedule a task to ensure the new block storage happens only AFTER deletion
                UpdateCruxTask task = new UpdateCruxTask(block, NpsItems.VORACIOUS_DIRT);
                task.runTaskTimer(Netheopoiesis.getInstance(), 1, 20);
                // Return so we only effect the one block per valid tick
                return;
            }
        }
    }

    @Nonnull
    @Override
    public Theme getTheme() {
        return Theme.SEED_BLUE;
    }

    @Override
    public double getGrowthRate() {
        return 0.15;
    }

    @Override
    public int getPurificationValue() {
        return 4;
    }
}
