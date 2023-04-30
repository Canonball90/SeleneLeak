/*
 * Decompiled with CFR 0.150.
 *
 * Could not load the following classes:
 *  net.minecraft.client.entity.EntityPlayerSP
 */
package mod.selene.injections.inj;

import net.minecraft.client.entity.EntityPlayerSP;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(value = {EntityPlayerSP.class})
public interface IEntityPlayerSP {
    @Accessor(value = "serverSneakState")
    boolean getServerSneakState();

    @Accessor(value = "serverSneakState")
    void setServerSneakState(boolean var1);

    @Accessor(value = "serverSprintState")
    boolean getServerSprintState();

    @Accessor(value = "serverSprintState")
    void setServerSprintState(boolean var1);

    @Accessor(value = "prevOnGround")
    boolean getPrevOnGround();

    @Accessor(value = "prevOnGround")
    void setPrevOnGround(boolean var1);

    @Accessor(value = "autoJumpEnabled")
    boolean getAutoJumpEnabled();

    @Accessor(value = "autoJumpEnabled")
    void setAutoJumpEnabled(boolean var1);

    @Accessor(value = "lastReportedPosX")
    double getLastReportedPosX();

    @Accessor(value = "lastReportedPosX")
    void setLastReportedPosX(double var1);

    @Accessor(value = "lastReportedPosY")
    double getLastReportedPosY();

    @Accessor(value = "lastReportedPosY")
    void setLastReportedPosY(double var1);

    @Accessor(value = "lastReportedPosZ")
    double getLastReportedPosZ();

    @Accessor(value = "lastReportedPosZ")
    void setLastReportedPosZ(double var1);

    @Accessor(value = "lastReportedYaw")
    float getLastReportedYaw();

    @Accessor(value = "lastReportedYaw")
    void setLastReportedYaw(float var1);

    @Accessor(value = "lastReportedPitch")
    float getLastReportedPitch();

    @Accessor(value = "lastReportedPitch")
    void setLastReportedPitch(float var1);

    @Accessor(value = "positionUpdateTicks")
    int getPositionUpdateTicks();

    @Accessor(value = "positionUpdateTicks")
    void setPositionUpdateTicks(int var1);

    @Invoker(value = "onUpdateWalkingPlayer")
    void invokeOnUpdateWalkingPlayer();
}

