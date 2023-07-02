package net.p3pp3rf1y.sophisticatedbackpacks.data;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.p3pp3rf1y.sophisticatedbackpacks.SophisticatedBackpacks;

public class SophisticatedBackpacksData implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator generator) {
		SophisticatedBackpacks.gatherData(generator);
	}
}
