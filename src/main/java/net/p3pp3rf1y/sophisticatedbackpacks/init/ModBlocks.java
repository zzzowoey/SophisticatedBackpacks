package net.p3pp3rf1y.sophisticatedbackpacks.init;

import io.github.fabricators_of_create.porting_lib.util.LazyRegistrar;
import io.github.fabricators_of_create.porting_lib.util.RegistryObject;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.p3pp3rf1y.sophisticatedbackpacks.SophisticatedBackpacks;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.BackpackBlock;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.BackpackBlockEntity;

public class ModBlocks {
	private static final LazyRegistrar<Block> BLOCKS = LazyRegistrar.create(BuiltInRegistries.BLOCK, SophisticatedBackpacks.ID);
	private static final LazyRegistrar<BlockEntityType<?>> BLOCK_ENTITY_TYPES = LazyRegistrar.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, SophisticatedBackpacks.ID);

	private ModBlocks() {}

	public static final RegistryObject<BackpackBlock> BACKPACK = BLOCKS.register("backpack", BackpackBlock::new);
	public static final RegistryObject<BackpackBlock> IRON_BACKPACK = BLOCKS.register("iron_backpack", BackpackBlock::new);
	public static final RegistryObject<BackpackBlock> GOLD_BACKPACK = BLOCKS.register("gold_backpack", BackpackBlock::new);
	public static final RegistryObject<BackpackBlock> DIAMOND_BACKPACK = BLOCKS.register("diamond_backpack", BackpackBlock::new);
	public static final RegistryObject<BackpackBlock> NETHERITE_BACKPACK = BLOCKS.register("netherite_backpack", () -> new BackpackBlock(1200));

	@SuppressWarnings("ConstantConditions") //no datafixer type needed
	public static final RegistryObject<BlockEntityType<BackpackBlockEntity>> BACKPACK_TILE_TYPE = BLOCK_ENTITY_TYPES.register("backpack", () ->
			BlockEntityType.Builder.of(BackpackBlockEntity::new, BACKPACK.get(), IRON_BACKPACK.get(), GOLD_BACKPACK.get(), DIAMOND_BACKPACK.get(), NETHERITE_BACKPACK.get())
					.build(null));

	public static void register() {
		BLOCKS.register();
		BLOCK_ENTITY_TYPES.register();

		UseBlockCallback.EVENT.register(BackpackBlock::playerInteract);
	}
}
