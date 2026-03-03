package com.centerofminecraft;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;

public class CenterofminecraftClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		MobSkinManager.register(EntityType.CREEPER, ResourceLocation.fromNamespaceAndPath("centerofminecraft", "textures/entity/creeper.png"));
	}
}