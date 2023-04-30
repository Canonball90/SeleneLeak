package mod.selene.injections.inj;

import net.minecraft.network.play.server.SPacketPlayerPosLook;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = {SPacketPlayerPosLook.class})
public interface ISPacketPlayerPosLook {
    @Accessor(value = "yaw")
    void setYaw(float var1);

    @Accessor(value = "pitch")
    void setPitch(float var1);

    @Accessor(value = "x")
    void setX(double var1);

    @Accessor(value = "y")
    void setY(double var1);

    @Accessor(value = "z")
    void setZ(double var1);
}