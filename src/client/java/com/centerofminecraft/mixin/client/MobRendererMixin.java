package com.centerofminecraft.mixin.client;

import com.centerofminecraft.MobSkinManager;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LivingEntityRenderer.class)
public class MobRendererMixin {
	@SuppressWarnings("unchecked")
	@Redirect(
		method = "getRenderType",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/LivingEntityRenderer;getTextureLocation(Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;)Lnet/minecraft/resources/Identifier;")
	)
	private Identifier centerofminecraft$redirectTexture(@SuppressWarnings("rawtypes") LivingEntityRenderer renderer, LivingEntityRenderState state) {
		if (state.entityType != null) {
			Identifier custom = MobSkinManager.getTexture(state.entityType).orElse(null);
			if (custom != null) return custom;
		}
		return renderer.getTextureLocation(state);
	}
}
