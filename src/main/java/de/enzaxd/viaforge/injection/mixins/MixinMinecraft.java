package de.enzaxd.viaforge.injection.mixins;

import de.enzaxd.viaforge.ViaForge;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.main.GameConfiguration;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public abstract class MixinMinecraft {
    @Final
    @Shadow
    private static Logger logger;

    @Shadow
    public PlayerControllerMP playerController;
    @Shadow
    private int leftClickCounter;
    @Shadow
    public MovingObjectPosition objectMouseOver;
    @Shadow
    public EntityPlayerSP thePlayer;
    @Shadow
    public WorldClient theWorld;

    @Inject(method = "<init>", at = @At("RETURN"))
    public void injectConstructor(GameConfiguration p_i45547_1_, CallbackInfo ci) {
        try {
            ViaForge.getInstance().start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Inject(method = "clickMouse()V", at = @At("HEAD"), cancellable = true)
    public void injectClickMouse(CallbackInfo ci) {
        if (ViaForge.getInstance().getVersion() > ViaForge.SHARED_VERSION) {
            ci.cancel();
            if (this.leftClickCounter <= 0) {
                if (this.objectMouseOver == null) {
                    logger.error("Null returned as \'hitResult\', this shouldn\'t happen!");

                    if (this.playerController.isNotCreative()) {
                        this.leftClickCounter = 10;
                    }
                } else {
                    switch (this.objectMouseOver.typeOfHit) {
                        case ENTITY:
                            this.playerController.attackEntity(this.thePlayer, this.objectMouseOver.entityHit);
                            break;
                        case BLOCK:
                            BlockPos blockpos = this.objectMouseOver.getBlockPos();
                            if (this.theWorld.getBlockState(blockpos).getBlock().getMaterial() != Material.air) {
                                this.playerController.clickBlock(blockpos, this.objectMouseOver.sideHit);
                                break;
                            }
                        case MISS:
                        default:
                            if (this.playerController.isNotCreative()) {
                                this.leftClickCounter = 10;
                            }
                    }
                }
                this.thePlayer.swingItem();
            }
        }
    }
}
