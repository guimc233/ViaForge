package de.enzaxd.viaforge.injection.mixins;

import de.enzaxd.viaforge.ViaForge;
import net.minecraft.entity.player.EntityPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityPlayer.class)
public abstract class MixinEntityPlayer extends MixinEntity {
    @Shadow
    public abstract void addExhaustion(float p_71020_1_);

    @Inject(method = "jump()V", at = @At(value = "INVOKE", shift = At.Shift.AFTER, target = "Lnet/minecraft/entity/player/EntityPlayer;triggerAchievement(Lnet/minecraft/stats/StatBase;)V"), cancellable = true)
    public void jump(CallbackInfo ci) {
        if (ViaForge.getInstance().getVersion() > ViaForge.SHARED_VERSION) {
            ci.cancel();

            if (this.isSprinting()) {
                this.addExhaustion(0.2F);
            } else {
                this.addExhaustion(0.05F);
            }
        }
    }
}
