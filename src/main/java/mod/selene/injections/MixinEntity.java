package mod.selene.injections;

import mod.selene.world.PushEvent;
import mod.selene.world.StepEvent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static mod.selene.api.utils.interfaces.MC.mc;

@Mixin(Entity.class)
public abstract class MixinEntity {

    @Shadow
    public float stepHeight;

    public MixinEntity(World worldIn) {
    }

    @Shadow
    public abstract int getMaxInPortalTime();

    @Shadow
    public abstract AxisAlignedBB getEntityBoundingBox();

    @Redirect(method = "onEntityUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;getMaxInPortalTime()I"))
    private int getMaxInPortalTimeHook(Entity entity) {
        int time = this.getMaxInPortalTime();
        return time;
    }

    @Redirect(method = "applyEntityCollision", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;addVelocity(DDD)V"))
    public void addVelocityHook(Entity entity, double x, double y, double z) {
        PushEvent event = new PushEvent(entity, x, y, z, true);
        MinecraftForge.EVENT_BUS.post(event);
        if (!event.isCanceled()) {
            entity.motionX += event.x;
            entity.motionY += event.y;
            entity.motionZ += event.z;
            entity.isAirBorne = event.airbone;
        }
    }

    @Inject(method = "move", at = @At(value = "INVOKE", target = "Lnet/minecraft/profiler/Profiler;endSection()V", shift = At.Shift.BEFORE, ordinal = 0))
    public void onMove(MoverType type, double x, double y, double z, CallbackInfo info) {
        if (this.equals(mc.player)) {
            StepEvent event = new StepEvent(getEntityBoundingBox(), stepHeight, 0);

            if (event.isCanceled()) {
                stepHeight = event.getHeight();
            }
        }
    }

    @Inject(method = "move", at = @At(value = "INVOKE", target = "Lnet/minecraft/profiler/Profiler;endSection()V", shift = At.Shift.AFTER, ordinal = 0))
    public void onMovePost(MoverType type, double x, double y, double z, CallbackInfo info) {
        if (this.equals(mc.player)) {
            StepEvent event = new StepEvent(getEntityBoundingBox(), stepHeight, 1);

            if (event.isCanceled()) {
                stepHeight = event.getHeight();
            }
        }
    }
}