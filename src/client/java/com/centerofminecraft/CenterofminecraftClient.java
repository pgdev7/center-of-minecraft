package com.centerofminecraft;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;

public class CenterofminecraftClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		MobSkinManager.register(EntityType.CREEPER, Identifier.fromNamespaceAndPath("centerofminecraft", "textures/entity/creeper.png"));
	}
}