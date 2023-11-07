package net.p3pp3rf1y.sophisticatedbackpacks.init;

import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.fabricmc.fabric.api.loot.v2.LootTableSource;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootTables;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.entries.LootTableReference;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.p3pp3rf1y.sophisticatedbackpacks.Config;
import net.p3pp3rf1y.sophisticatedbackpacks.SophisticatedBackpacks;
import net.p3pp3rf1y.sophisticatedbackpacks.data.CopyBackpackDataFunction;

import java.util.List;

public class ModLoot {
	private ModLoot() {}

	public static final LootItemFunctionType COPY_BACKPACK_DATA = new LootItemFunctionType(new CopyBackpackDataFunction.Serializer());
	private static final List<String> CHEST_TABLES = List.of("abandoned_mineshaft", "bastion_treasure", "desert_pyramid", "end_city_treasure", "nether_bridge", "shipwreck_treasure", "simple_dungeon", "woodland_mansion");

	public static void init() {
		ModLoot.registerLootFunction();

		LootTableEvents.MODIFY.register(ModLoot::lootLoad);
	}

	private static void registerLootFunction() {
		Registry.register(BuiltInRegistries.LOOT_FUNCTION_TYPE, new ResourceLocation(SophisticatedBackpacks.ID, "copy_backpack_data"), COPY_BACKPACK_DATA);
	}

	public static void lootLoad(ResourceManager resourceManager, LootTables lootManager, ResourceLocation id, LootTable.Builder tableBuilder, LootTableSource source) {
		if (Boolean.FALSE.equals(Config.COMMON.chestLootEnabled.get())) {
			return;
		}

		String chestsPrefix = "minecraft:chests/";
		String name = id.getPath();

		if (name.startsWith(chestsPrefix) && CHEST_TABLES.contains(name.substring(chestsPrefix.length()))) {
			String file = name.substring("minecraft:".length());
			tableBuilder.pool(getInjectPool(file));
		}
	}

	private static LootPool getInjectPool(String entryName) {
		return LootPool.lootPool().add(getInjectEntry(entryName)).setBonusRolls(UniformGenerator.between(0, 1)).build();
	}

	private static LootPoolEntryContainer.Builder<?> getInjectEntry(String name) {
		return LootTableReference.lootTableReference(new ResourceLocation(SophisticatedBackpacks.ID, "inject/" + name)).setWeight(1);
	}
}
