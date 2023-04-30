package mod.selene.injections;

import mod.selene.loader.SeleneLoader;
import mod.selene.world.MoveEvent;
import mod.selene.world.UpdateWalkingPlayerEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.MoverType;
import net.minecraft.stats.RecipeBook;
import net.minecraft.stats.StatisticsManager;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = EntityPlayerSP.class, priority = 9998)
public abstract class MixinEntityPlayerSP extends AbstractClientPlayer {

    private boolean updateLock;

    public MixinEntityPlayerSP(Minecraft p_i47378_1_, World p_i47378_2_, NetHandlerPlayClient p_i47378_3_, StatisticsManager p_i47378_4_, RecipeBook p_i47378_5_) {
        super(p_i47378_2_, p_i47378_3_.getGameProfile());
    }

    @Shadow
    protected abstract void onUpdateWalkingPlayer();

    @Redirect(method = "onLivingUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/entity/EntityPlayerSP;closeScreen()V"))
    public void closeScreenHook(EntityPlayerSP entityPlayerSP) {
    }

    @Redirect(method = "onLivingUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;displayGuiScreen(Lnet/minecraft/client/gui/GuiScreen;)V"))
    public void displayGuiScreenHook(Minecraft mc, GuiScreen screen) {

    }

    @Inject(method = "onUpdateWalkingPlayer", at = @At("HEAD"))
    private void preMotion(CallbackInfo info) {
        UpdateWalkingPlayerEvent event = new UpdateWalkingPlayerEvent(0);
        MinecraftForge.EVENT_BUS.post(event);
    }

    @Inject(method = "onUpdateWalkingPlayer", at = @At("RETURN"))
    private void postMotion(CallbackInfo info) {
        if (updateLock) {
            return;
        }
        UpdateWalkingPlayerEvent event = new UpdateWalkingPlayerEvent(1);
        MinecraftForge.EVENT_BUS.post(event);
//        SeleneLoader.rotationManager2.restoreRotations();
        if (event.isCanceled()) {
            info.cancel();

            // idk
            if (event.getIterations() > 0) {

                // run
                for (int i = 0; i < event.getIterations(); i++) {

                    // lock
                    updateLock = true;

                    // run onUpdate, this updates motion
                    onUpdate();

                    // unlock
                    updateLock = false;
                    onUpdateWalkingPlayer();
                }
            }
        }
    }

    @Inject(method = "Lnet/minecraft/client/entity/EntityPlayerSP;setServerBrand(Ljava/lang/String;)V", at = @At("HEAD"))
    public void getBrand(String brand, CallbackInfo callbackInfo) {
        if (SeleneLoader.serverManager != null) {
            SeleneLoader.serverManager.setServerBrand(brand);
        }
    }

    @Redirect(method = "move", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/entity/AbstractClientPlayer;move(Lnet/minecraft/entity/MoverType;DDD)V"))
    public void move(AbstractClientPlayer player, MoverType moverType, double x, double y, double z) {
        final MoveEvent event = new MoveEvent(0, moverType, x, y, z);
        MinecraftForge.EVENT_BUS.post(event);
        if (!event.isCanceled()) {
            super.move(event.getType(), event.getX(), event.getY(), event.getZ());
        }
    }
}
