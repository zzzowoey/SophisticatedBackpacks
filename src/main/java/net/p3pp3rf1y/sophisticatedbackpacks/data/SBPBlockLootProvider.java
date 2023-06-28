package net.p3pp3rf1y.sophisticatedbackpacks.data;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootTables;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.BackpackItem;
import net.p3pp3rf1y.sophisticatedbackpacks.init.ModBlocks;
import net.p3pp3rf1y.sophisticatedbackpacks.init.ModItems;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class SBPBlockLootProvider extends FabricBlockLootTableProvider {
	public SBPBlockLootProvider(FabricDataOutput dataOutput) {
		super(dataOutput);
	}

	@Override
	public void generate() {
		Map<Block, LootTable.Builder> tables = new HashMap<>();

		tables.put(ModBlocks.BACKPACK.get(), getBackpack(ModItems.BACKPACK.get()));
		tables.put(ModBlocks.IRON_BACKPACK.get(), getBackpack(ModItems.IRON_BACKPACK.get()));
		tables.put(ModBlocks.GOLD_BACKPACK.get(), getBackpack(ModItems.GOLD_BACKPACK.get()));
		tables.put(ModBlocks.DIAMOND_BACKPACK.get(), getBackpack(ModItems.DIAMOND_BACKPACK.get()));
		tables.put(ModBlocks.NETHERITE_BACKPACK.get(), getBackpack(ModItems.NETHERITE_BACKPACK.get()));

		for (Map.Entry<Block, LootTable.Builder> e : tables.entrySet()) {
			add(e.getKey(), e.getValue());
		}
	}

	@Override
	public String getName() {
		return "SophisticatedBackpacks block loot tables";
	}

	private static LootTable.Builder getBackpack(BackpackItem item) {
		LootPoolEntryContainer.Builder<?> entry = LootItem.lootTableItem(item);
		LootPool.Builder pool = LootPool.lootPool().name("main").setRolls(ConstantValue.exactly(1)).add(entry).apply(CopyBackpackDataFunction.builder());
		return LootTable.lootTable().withPool(pool).setParamSet(LootContextParamSets.BLOCK);
	}
}
