package com.iafenvoy.thinkbeforedrop.mixin;

import com.iafenvoy.thinkbeforedrop.DropManager;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@OnlyIn(Dist.CLIENT)
@Mixin(LocalPlayer.class)
public class ClientPlayerEntityMixin extends AbstractClientPlayer {
    @Shadow
    @Final
    protected Minecraft minecraft;

    public ClientPlayerEntityMixin(ClientLevel world, GameProfile profile) {
        super(world, profile);
    }

    @Inject(method = "drop", at = @At("HEAD"), cancellable = true)
    public void beforeDropItem(boolean dropEntireStack, CallbackInfoReturnable<Boolean> cir) {
        if (!DropManager.shouldThrow(this.getInventory().getSelected(), this.getInventory().selected)) {
            assert minecraft.player != null;
            minecraft.player.displayClientMessage(DropManager.getWarningText(), true);
            cir.setReturnValue(false);
        }
    }
}
