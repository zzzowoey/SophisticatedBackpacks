package net.p3pp3rf1y.sophisticatedbackpacks.data;

import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootPool;

public class SBLootModifierProvider {

	//	SBLootModifierProvider(FabricDataOutput packOutput) {
	//		super(packOutput, SophisticatedBackpacks.ID);
	//	}

	//	@Override
	//	protected void start() {
	public static void start() {
		addInjectLootTableModifier(SBInjectLootSubProvider.SIMPLE_DUNGEON, BuiltInLootTables.SIMPLE_DUNGEON);
		addInjectLootTableModifier(SBInjectLootSubProvider.ABANDONED_MINESHAFT, BuiltInLootTables.ABANDONED_MINESHAFT);
		addInjectLootTableModifier(SBInjectLootSubProvider.DESERT_PYRAMID, BuiltInLootTables.DESERT_PYRAMID);
		addInjectLootTableModifier(SBInjectLootSubProvider.WOODLAND_MANSION, BuiltInLootTables.WOODLAND_MANSION);
		addInjectLootTableModifier(SBInjectLootSubProvider.SHIPWRECK_TREASURE, BuiltInLootTables.SHIPWRECK_TREASURE);
		addInjectLootTableModifier(SBInjectLootSubProvider.BASTION_TREASURE, BuiltInLootTables.BASTION_TREASURE);
		addInjectLootTableModifier(SBInjectLootSubProvider.END_CITY_TREASURE, BuiltInLootTables.END_CITY_TREASURE);
		addInjectLootTableModifier(SBInjectLootSubProvider.NETHER_BRIDGE, BuiltInLootTables.NETHER_BRIDGE);
	}

	private static void addInjectLootTableModifier(ResourceLocation lootTable, ResourceLocation lootTableToInjectInto) {
		//		add(lootTableToInjectInto.getPath(), new InjectLootModifier(lootTable, lootTableToInjectInto));
		LootTableEvents.MODIFY.register((resourceManager, lootManager, id, tableBuilder, source) -> {
			if (lootTableToInjectInto.equals(id) && source.isBuiltin()) {
				for (LootPool pool : lootManager.getLootTable(lootTable).pools) {
					tableBuilder.pool(pool);
				}
			}
		});
	}

//	public static class InjectLootModifier extends LootModifier {
//		public static final Codec<InjectLootModifier> CODEC = RecordCodecBuilder.create(inst -> LootModifier.codecStart(inst).and(
//				inst.group(
//						ResourceLocation.CODEC.fieldOf("loot_table").forGetter(m -> m.lootTable),
//						ResourceLocation.CODEC.fieldOf("loot_table_to_inject_into").forGetter(m -> m.lootTableToInjectInto)
//				)
//		).apply(inst, InjectLootModifier::new));
//		private final ResourceLocation lootTable;
//		private final ResourceLocation lootTableToInjectInto;
//
//		protected InjectLootModifier(LootItemCondition[] conditions, ResourceLocation lootTable, ResourceLocation lootTableToInjectInto) {
//			super(conditions);
//			this.lootTable = lootTable;
//			this.lootTableToInjectInto = lootTableToInjectInto;
//		}
//
//		protected InjectLootModifier(ResourceLocation lootTable, ResourceLocation lootTableToInjectInto) {
//			this(new LootItemCondition[] {SBLootEnabledCondition.builder().build(),
//					LootTableIdCondition.builder(lootTableToInjectInto).build()}, lootTable, lootTableToInjectInto);
//		}
//
//		@Override
//		protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
//			LootTable table = context.getResolver().getLootTable(lootTable);
//			table.getRandomItemsRaw(context, generatedLoot::add);
//			return generatedLoot;
//		}
//
//		@Override
//		public Codec<? extends IGlobalLootModifier> codec() {
//			return ModItems.INJECT_LOOT;
//		}
//	}
}
