package net.p3pp3rf1y.sophisticatedbackpacks;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.fabricmc.api.ModInitializer;
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
import net.p3pp3rf1y.sophisticatedbackpacks.network.SBPPacketHandler;
import net.p3pp3rf1y.sophisticatedbackpacks.registry.RegistryLoader;

public class SophisticatedBackpacks implements ModInitializer {
	public static final String ID = "sophisticatedbackpacks";
	public static final Logger LOGGER = LoggerFactory.getLogger(ID);

	public static final CreativeModeTab ITEM_GROUP = FabricItemGroup.builder(getRL("item_group"))
			.icon(() -> new ItemStack(ModItems.BACKPACK))
			.build();

	private final RegistryLoader registryLoader = new RegistryLoader();
	public final CommonEventHandler commonEventHandler = new CommonEventHandler();

	@SuppressWarnings("java:S1118") //needs to be public for mod to work
	public SophisticatedBackpacks() {
	}


	@Override
	public void onInitialize() {
		Config.register();
		commonEventHandler.registerHandlers();

		ModCompat.initCompats();

		SBPCommand.init();
		SBPPacketHandler.init();

		ResourceManagerHelper.get(PackType.SERVER_DATA).registerReloadListener(registryLoader);

		SBPPacketHandler.getChannel().initServerListener();
	}

	public static ResourceLocation getRL(String regName) {
		return new ResourceLocation(getRegistryName(regName));
	}

	public static String getRegistryName(String regName) {
		return ID + ":" + regName;
	}

	public static void gatherData(FabricDataGenerator gen) {
		FabricDataGenerator.Pack pack = gen.createPack();
		pack.addProvider(SBPBlockLootProvider::new);
		pack.addProvider(SBPRecipeProvider::new);
		pack.addProvider(SBPLootInjectProvider::new);
	}
}
