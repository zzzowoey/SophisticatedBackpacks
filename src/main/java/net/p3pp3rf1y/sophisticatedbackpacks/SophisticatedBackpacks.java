package net.p3pp3rf1y.sophisticatedbackpacks;

import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.p3pp3rf1y.sophisticatedbackpacks.command.SBPCommand;
import net.p3pp3rf1y.sophisticatedbackpacks.common.CommonEventHandler;
import net.p3pp3rf1y.sophisticatedbackpacks.data.SBPBlockLootProvider;
import net.p3pp3rf1y.sophisticatedbackpacks.data.SBPLootInjectProvider;
import net.p3pp3rf1y.sophisticatedbackpacks.data.SBPRecipeProvider;
import net.p3pp3rf1y.sophisticatedbackpacks.init.ModCompat;
import net.p3pp3rf1y.sophisticatedbackpacks.init.ModItems;
import net.p3pp3rf1y.sophisticatedbackpacks.init.ModLoot;
import net.p3pp3rf1y.sophisticatedbackpacks.network.SBPPacketHandler;
import net.p3pp3rf1y.sophisticatedbackpacks.registry.RegistryLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SophisticatedBackpacks {
	public static final String MOD_ID = "sophisticatedbackpacks";
	public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

	public static final CreativeModeTab ITEM_GROUP = FabricItemGroup.builder(getRL("item_group"))
			.icon(() -> new ItemStack(ModItems.BACKPACK.get()))
			.build();

	private final RegistryLoader registryLoader = new RegistryLoader();
	public final CommonEventHandler commonEventHandler = new CommonEventHandler();

	@SuppressWarnings("java:S1118") //needs to be public for mod to work
	public SophisticatedBackpacks() {
		Config.register();

		commonEventHandler.registerHandlers();
		setup();

		ModLoot.init();
		SBPCommand.init();

		ResourceManagerHelper.get(PackType.SERVER_DATA).registerReloadListener(registryLoader);
	}

	private static void setup() {
		SBPPacketHandler.init();

		ModCompat.initCompats();
		ModItems.registerDispenseBehavior();
		ModItems.registerCauldronInteractions();
		ModItems.registerItemGroup();
	}

	public static ResourceLocation getRL(String regName) {
		return new ResourceLocation(getRegistryName(regName));
	}

	public static String getRegistryName(String regName) {
		return MOD_ID + ":" + regName;
	}

	public static void gatherData(FabricDataGenerator gen) {
		FabricDataGenerator.Pack pack = gen.createPack();
		pack.addProvider(SBPBlockLootProvider::new);
		pack.addProvider(SBPRecipeProvider::new);
		pack.addProvider(SBPLootInjectProvider::new);
	}
}
