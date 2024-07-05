package com.iafenvoy.thinkbeforedrop.mixin;

import com.iafenvoy.thinkbeforedrop.DropManager;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@OnlyIn(Dist.CLIENT)
@Mixin(ClientPlayerEntity.class)
public class ClientPlayerEntityMixin extends AbstractClientPlayerEntity {
    @Shadow
    @Final
    protected Minecraft minecraft;

    public ClientPlayerEntityMixin(ClientWorld world, GameProfile profile) {
        super(world, profile);
    }

    @Inject(method = "drop", at = @At("HEAD"), cancellable = true, remap = false)
    public void beforeDropItem(boolean dropEntireStack, CallbackInfoReturnable<Boolean> cir) {
        if (!DropManager.shouldThrow(this.inventory.getSelected(), this.inventory.selected)) {
            assert minecraft.player != null;
            minecraft.player.displayClientMessage(DropManager.getWarningText(), true);
            cir.setReturnValue(false);
        }
    }
}
