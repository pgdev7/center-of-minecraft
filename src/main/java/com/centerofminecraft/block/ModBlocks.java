package com.centerofminecraft.block;

import com.centerofminecraft.Centerofminecraft;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class ModBlocks {
    private static ResourceKey<Block> blockKey(String name) {
        return ResourceKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(Centerofminecraft.MOD_ID, name));
    }

    public static final Block SOS_ROBOT = registerBlock("sos_robot",
            new Block(BlockBehaviour.Properties.of()
                    .setId(blockKey("sos_robot"))
                    .strength(3.0f, 6.0f)
                    .sound(SoundType.METAL)
                    .noOcclusion()));

    public static final Block RB26_ROBOT = registerBlock("rb26_robot",
            new Block(BlockBehaviour.Properties.of()
                    .setId(blockKey("rb26_robot"))
                    .strength(3.0f, 6.0f)
                    .sound(SoundType.METAL)
                    .noOcclusion()));

    private static Block registerBlock(String name, Block block) {
        Identifier id = Identifier.fromNamespaceAndPath(Centerofminecraft.MOD_ID, name);
        Registry.register(BuiltInRegistries.ITEM, id, new BlockItem(block, new Item.Properties()
                .setId(ResourceKey.create(Registries.ITEM, id))
                .useBlockDescriptionPrefix()));
        return Registry.register(BuiltInRegistries.BLOCK, id, block);
    }

    public static void initialize() {
        Centerofminecraft.LOGGER.info("Registering robot blocks");

        ResourceKey<CreativeModeTab> functionalBlocks = ResourceKey.create(
                Registries.CREATIVE_MODE_TAB,
                Identifier.fromNamespaceAndPath("minecraft", "functional_blocks")
        );
        ItemGroupEvents.modifyEntriesEvent(functionalBlocks).register(entries -> {
            entries.accept(SOS_ROBOT.asItem());
            entries.accept(RB26_ROBOT.asItem());
        });
    }
}
