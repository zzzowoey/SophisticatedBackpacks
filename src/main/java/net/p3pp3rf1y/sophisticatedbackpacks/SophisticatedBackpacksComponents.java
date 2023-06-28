package net.p3pp3rf1y.sophisticatedbackpacks;

import dev.onyxstudios.cca.api.v3.item.ItemComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.item.ItemComponentInitializer;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.BackpackItem;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.wrapper.BackpackWrapper;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.wrapper.IBackpackWrapper;

public final class SophisticatedBackpacksComponents implements ItemComponentInitializer {
	@Override
	public void registerItemComponentFactories(ItemComponentFactoryRegistry registry) {
		registry.register((item) -> item instanceof BackpackItem, IBackpackWrapper.BACKPACK_WRAPPER_COMPONENT, BackpackWrapper::new);
	}

	/*public static final Capability<IBackpackWrapper> BACKPACK_WRAPPER_CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {});

	public static Capability<IBackpackWrapper> getCapabilityInstance() {
		return BACKPACK_WRAPPER_CAPABILITY;
	}

	public static void onRegister(RegisterCapabilitiesEvent event) {
		event.register(IBackpackWrapper.class);
	}*/
}
