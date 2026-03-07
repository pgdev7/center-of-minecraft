package com.centerofminecraft;

import com.centerofminecraft.model.RobotModelPlugin;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;

public class CenterofminecraftClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		MobSkinManager.register(EntityType.CREEPER, Identifier.fromNamespaceAndPath("centerofminecraft", "textures/entity/creeper.png"));

		ModelLoadingPlugin.register(new RobotModelPlugin());
	}
}
