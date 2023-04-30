package mod.selene.injections;

import mod.selene.loader.SeleneLoader;
import net.minecraft.util.Timer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(Timer.class)
public class MixinTimer {
    @Shadow
    public float elapsedPartialTicks;

    @Inject(method = "updateTimer", at = @At(value = "FIELD", target = "net/minecraft/util/Timer.elapsedPartialTicks:F", ordinal = 1))
    public void updateTimer(CallbackInfo info) {
        elapsedPartialTicks *= SeleneLoader.TICK_TIMER;
    }
}