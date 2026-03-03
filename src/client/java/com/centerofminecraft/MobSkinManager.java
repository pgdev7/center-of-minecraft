package com.centerofminecraft;

import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class MobSkinManager {
	private static final Map<EntityType<?>, Identifier> SKINS = new HashMap<>();

	public static void register(EntityType<?> entityType, Identifier texture) {
		SKINS.put(entityType, texture);
	}

	public static Optional<Identifier> getTexture(EntityType<?> entityType) {
		return Optional.ofNullable(SKINS.get(entityType));
	}
}
