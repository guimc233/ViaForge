package de.enzaxd.viaforge.injection.mixins;

import de.enzaxd.viaforge.ViaForge;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.util.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.IOException;

@Mixin(C08PacketPlayerBlockPlacement.class)
public class MixinC08PacketPlayerBlockPlacement {
    @Shadow
    private BlockPos position;

    @Shadow
    private int placedBlockDirection;

    @Shadow
    private ItemStack stack;

    @Shadow
    private float facingX;

    @Shadow
    private float facingY;

    @Shadow
    private float facingZ;

    @Inject(method = "readPacketData(Lnet/minecraft/network/PacketBuffer;)V", at = @At("HEAD"), cancellable = true)
    public void readPacketData(PacketBuffer buf, CallbackInfo ci) throws IOException {
        if (ViaForge.getInstance().getVersion() > ViaForge.SHARED_VERSION) {
            ci.cancel();
            this.position = buf.readBlockPos();
            this.placedBlockDirection = buf.readUnsignedByte();
            this.stack = buf.readItemStackFromBuffer();
            this.facingX = buf.readFloat();
            this.facingY = buf.readFloat();
            this.facingZ = buf.readFloat();
        }
    }

    @Inject(method = "writePacketData(Lnet/minecraft/network/PacketBuffer;)V", at = @At("HEAD"), cancellable = true)
    public void writePacketData(PacketBuffer buf, CallbackInfo ci) throws IOException {
        if (ViaForge.getInstance().getVersion() > ViaForge.SHARED_VERSION) {
            ci.cancel();
            buf.writeBlockPos(this.position);
            buf.writeByte(this.placedBlockDirection);
            buf.writeItemStackToBuffer(this.stack);
            buf.writeFloat(this.facingX);
            buf.writeFloat(this.facingY);
            buf.writeFloat(this.facingZ);
        }
    }
}
