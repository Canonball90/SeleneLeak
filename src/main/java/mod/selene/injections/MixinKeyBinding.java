package mod.selene.injections;

import mod.selene.world.KeyDownEvent;
import mod.selene.world.KeyEvent;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(KeyBinding.class)
public class MixinKeyBinding {

    @Shadow
    private boolean pressed;

    @Shadow
    private int keyCode;

    //TODO: No Event?
    @Inject(method = "isKeyDown", at = @At("RETURN"), cancellable = true)
    private void isKeyDown(final CallbackInfoReturnable<Boolean> info) {
        KeyEvent event = new KeyEvent(0, info.getReturnValue(), this.pressed);
        MinecraftForge.EVENT_BUS.post(event);
        info.setReturnValue(event.info);
    }

    @Inject(method = "isKeyDown", at = @At("HEAD"), cancellable = true)
    public void onIsKeyDown(CallbackInfoReturnable<Boolean> info) {
        KeyDownEvent keyDownEvent = new KeyDownEvent(keyCode, pressed);
        MinecraftForge.EVENT_BUS.post(keyDownEvent);

        if (keyDownEvent.isCanceled()) {
            info.setReturnValue(keyDownEvent.isPressed());
        }
    }
}
