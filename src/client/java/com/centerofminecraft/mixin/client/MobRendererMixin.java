package com.centerofminecraft.mixin.client;

import com.centerofminecraft.MobSkinManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Mob;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MobRenderer.class)
public class MobRendererMixin {
	@Inject(method = "getTextureLocation", at = @At("HEAD"), cancellable = true)
	private void centerofminecraft$redirectTexture(Mob entity, CallbackInfoReturnable<Identifier> cir) {
		MobSkinManager.getTexture(entity.getType()).ifPresent(cir::setReturnValue);
	}
}
