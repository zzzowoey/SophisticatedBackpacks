package net.p3pp3rf1y.sophisticatedbackpacks;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class SophisticatedBackpacksData implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator generator) {
		SophisticatedBackpacks.gatherData(generator);
	}
}
