package com.centerofminecraft;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class MobSkinManager {
	private static final Map<EntityType<?>, ResourceLocation> SKINS = new HashMap<>();

	public static void register(EntityType<?> entityType, ResourceLocation texture) {
		SKINS.put(entityType, texture);
	}

	public static Optional<ResourceLocation> getTexture(EntityType<?> entityType) {
		return Optional.ofNullable(SKINS.get(entityType));
	}
}
