package net.p3pp3rf1y.sophisticatedbackpacks.data;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.SimpleFabricLootTableProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.EmptyLootItem;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.p3pp3rf1y.sophisticatedbackpacks.init.ModItems;

import java.util.function.BiConsumer;

public class SBPLootInjectProvider extends SimpleFabricLootTableProvider {
	public SBPLootInjectProvider(FabricDataOutput output) {
		super(output, LootContextParamSets.CHEST);
	}

	@Override
	public void generate(BiConsumer<ResourceLocation, LootTable.Builder> biConsumer) {

		biConsumer.accept(BuiltInLootTables.SIMPLE_DUNGEON, getLootTable(92,
				getItemLootEntry(ModItems.BACKPACK.get(), 4),
				getItemLootEntry(ModItems.IRON_BACKPACK.get(), 2),
				getItemLootEntry(ModItems.PICKUP_UPGRADE.get(), 2)));
		biConsumer.accept(BuiltInLootTables.ABANDONED_MINESHAFT, getLootTable(89,
				getItemLootEntry(ModItems.BACKPACK.get(), 5),
				getItemLootEntry(ModItems.IRON_BACKPACK.get(), 3),
				getItemLootEntry(ModItems.GOLD_BACKPACK.get(), 1),
				getItemLootEntry(ModItems.MAGNET_UPGRADE.get(), 2)));
		biConsumer.accept(BuiltInLootTables.DESERT_PYRAMID, getLootTable(89,
				getItemLootEntry(ModItems.BACKPACK.get(), 5),
				getItemLootEntry(ModItems.IRON_BACKPACK.get(), 3),
				getItemLootEntry(ModItems.GOLD_BACKPACK.get(), 1),
				getItemLootEntry(ModItems.MAGNET_UPGRADE.get(), 2)));
		biConsumer.accept(BuiltInLootTables.SHIPWRECK_TREASURE, getLootTable(92,
				getItemLootEntry(ModItems.IRON_BACKPACK.get(), 4),
				getItemLootEntry(ModItems.GOLD_BACKPACK.get(), 2),
				getItemLootEntry(ModItems.ADVANCED_MAGNET_UPGRADE.get(), 2)));
		biConsumer.accept(BuiltInLootTables.WOODLAND_MANSION, getLootTable(92,
				getItemLootEntry(ModItems.IRON_BACKPACK.get(), 4),
				getItemLootEntry(ModItems.GOLD_BACKPACK.get(), 2),
				getItemLootEntry(ModItems.ADVANCED_MAGNET_UPGRADE.get(), 2)));
		biConsumer.accept(BuiltInLootTables.NETHER_BRIDGE, getLootTable(90,
				getItemLootEntry(ModItems.IRON_BACKPACK.get(), 5),
				getItemLootEntry(ModItems.GOLD_BACKPACK.get(), 3),
				getItemLootEntry(ModItems.FEEDING_UPGRADE.get(), 2)));
		biConsumer.accept(BuiltInLootTables.BASTION_TREASURE, getLootTable(90,
				getItemLootEntry(ModItems.IRON_BACKPACK.get(), 3),
				getItemLootEntry(ModItems.GOLD_BACKPACK.get(), 5),
				getItemLootEntry(ModItems.FEEDING_UPGRADE.get(), 2)));
		biConsumer.accept(BuiltInLootTables.END_CITY_TREASURE, getLootTable(90,
				getItemLootEntry(ModItems.DIAMOND_BACKPACK.get(), 3),
				getItemLootEntry(ModItems.GOLD_BACKPACK.get(), 5),
				getItemLootEntry(ModItems.ADVANCED_MAGNET_UPGRADE.get(), 2)));
	}

	@Override
	public String getName() {
		return "SophisticatedBackpacks chest loot additions";
	}


	private LootPoolEntryContainer.Builder<?> getItemLootEntry(Item item, int weight) {
		return LootItem.lootTableItem(item).setWeight(weight);
	}

	private static LootTable.Builder getLootTable(int emptyWeight, LootPoolEntryContainer.Builder<?>... entries) {
		LootPool.Builder pool = LootPool.lootPool().setRolls(ConstantValue.exactly(1));
		for (LootPoolEntryContainer.Builder<?> entry : entries) {
			pool.add(entry);
		}
		pool.add(EmptyLootItem.emptyItem().setWeight(emptyWeight));
		return LootTable.lootTable().withPool(pool);
	}
}
