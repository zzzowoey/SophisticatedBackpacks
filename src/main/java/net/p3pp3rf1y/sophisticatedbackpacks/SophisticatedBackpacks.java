package net.p3pp3rf1y.sophisticatedbackpacks;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.p3pp3rf1y.sophisticatedbackpacks.command.SBPCommand;
import net.p3pp3rf1y.sophisticatedbackpacks.common.CommonEventHandler;
import net.p3pp3rf1y.sophisticatedbackpacks.init.ModCompat;
import net.p3pp3rf1y.sophisticatedbackpacks.network.SBPPacketHandler;
import net.p3pp3rf1y.sophisticatedbackpacks.registry.RegistryLoader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SophisticatedBackpacks implements ModInitializer {
	public static final String ID = "sophisticatedbackpacks";
	public static final Logger LOGGER = LoggerFactory.getLogger(ID);

	private final RegistryLoader registryLoader = new RegistryLoader();
	public final CommonEventHandler commonEventHandler = new CommonEventHandler();

	@SuppressWarnings("java:S1118") //needs to be public for mod to work
	public SophisticatedBackpacks() {
	}


	@Override
	public void onInitialize() {
		Config.register();
		commonEventHandler.registerHandlers();
		// TODO: Check
		/*IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
		if (FMLEnvironment.dist == Dist.CLIENT) {
			ClientEventHandler.registerHandlers();
			modBus.addListener(KeybindHandler::registerKeyMappings);
			modBus.addListener(SophisticatedBackpacks::registerTooltipComponent);
		}

		modBus.addListener(SophisticatedBackpacks::setup);
		modBus.addListener(DataGenerators::gatherData);
		modBus.addListener(Config.SERVER::onConfigReload);
		modBus.addListener(CapabilityBackpackWrapper::onRegister);
		modBus.addListener(SophisticatedBackpacks::clientSetup);
		SBPCommand.init(modBus);*/

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

}
