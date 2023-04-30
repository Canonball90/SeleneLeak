package mod.selene.injections;

import com.google.common.base.Predicate;
import mod.selene.impl.modules.client.Notifications;
import mod.selene.impl.modules.render.Ambience;
import mod.selene.impl.modules.render.NoRender;
import mod.selene.loader.SeleneLoader;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import javax.vecmath.Vector3f;
import java.awt.*;
import java.util.List;

@Mixin(EntityRenderer.class)
public abstract class MixinEntityRenderer {

    @Shadow
    public ItemStack itemActivationItem;
    public Minecraft mc;
    private boolean injection = true;
    @Shadow
    @Final
    private int[] lightmapColors;

    @Shadow
    public abstract void getMouseOver(float partialTicks);

    @Inject(method = "renderItemActivation", at = @At("HEAD"), cancellable = true)
    public void renderItemActivationHook(CallbackInfo info) {
        if (this.itemActivationItem != null && (NoRender.getInstance().isOn() && NoRender.getInstance().totemPops.getValue() && this.itemActivationItem.getItem() == Items.TOTEM_OF_UNDYING)) {
            info.cancel();
        }
    }

    //TODO: WTF
    @Inject(method = "getMouseOver(F)V", at = @At(value = "HEAD"), cancellable = true)
    public void getMouseOverHook(float partialTicks, CallbackInfo info) {
        if (injection) {
            info.cancel();
            injection = false;
            try {
                this.getMouseOver(partialTicks);
            } catch (Exception e) {
                e.printStackTrace();
                if (Notifications.getInstance().isOn() && Notifications.getInstance().crash.getValue()) {
                    Notifications.displayCrash(e);
                }
            }
            injection = true;
        }
    }

    @Inject(method = {"updateLightmap"}, at = {@At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/texture/DynamicTexture;updateDynamicTexture()V", shift = At.Shift.BEFORE)})
    private void updateTextureHook(float partialTicks, CallbackInfo ci) {
        try {
            Ambience ambience = SeleneLoader.moduleManager.getModuleByClass(Ambience.class);
            if (ambience.isEnabled()) {
                for (int i = 0; i < this.lightmapColors.length; ++i) {
                    Color ambientColor = ambience.color.getValue();
                    int alpha = ambientColor.getAlpha();
                    float modifier = (float) alpha / 255.0f;
                    int color = this.lightmapColors[i];
                    int[] bgr = this.toRGBAArray(color);
                    Vector3f values = new Vector3f((float) bgr[2] / 255.0f, (float) bgr[1] / 255.0f, (float) bgr[0] / 255.0f);
                    Vector3f newValues = new Vector3f((float) ambientColor.getRed() / 255.0f, (float) ambientColor.getGreen() / 255.0f, (float) ambientColor.getBlue() / 255.0f);
                    Vector3f finalValues = this.mix(values, newValues, modifier);
                    int red = (int) (finalValues.x * 255.0f);
                    int green = (int) (finalValues.y * 255.0f);
                    int blue = (int) (finalValues.z * 255.0f);
                    this.lightmapColors[i] = 0xFF000000 | red << 16 | green << 8 | blue;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int[] toRGBAArray(int colorBuffer) {
        return new int[]{colorBuffer >> 16 & 0xFF, colorBuffer >> 8 & 0xFF, colorBuffer & 0xFF};
    }

    private Vector3f mix(Vector3f first, Vector3f second, float factor) {
        return new Vector3f(first.x * (1.0f - factor) + second.x * factor, first.y * (1.0f - factor) + second.y * factor, first.z * (1.0f - factor) + first.z * factor);
    }

    @Redirect(method = "setupCameraTransform", at = @At(value = "FIELD", target = "Lnet/minecraft/client/entity/EntityPlayerSP;prevTimeInPortal:F"))
    public float prevTimeInPortalHook(EntityPlayerSP entityPlayerSP) {
        if (NoRender.getInstance().isOn() && NoRender.getInstance().nausea.getValue()) {
            return -3.4028235E38f;
        }
        return entityPlayerSP.prevTimeInPortal;
    }

    @Inject(method = "setupFog", at = @At(value = "HEAD"), cancellable = true)
    public void setupFogHook(int startCoords, float partialTicks, CallbackInfo info) {
        if (NoRender.getInstance().isOn() && NoRender.getInstance().fog.getValue() == NoRender.Fog.NOFOG) {
            info.cancel();
        }
    }

    @Redirect(method = "setupFog", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/ActiveRenderInfo;getBlockStateAtEntityViewpoint(Lnet/minecraft/world/World;Lnet/minecraft/entity/Entity;F)Lnet/minecraft/block/state/IBlockState;"))
    public IBlockState getBlockStateAtEntityViewpointHook(World worldIn, Entity entityIn, float p_186703_2_) {
        if (NoRender.getInstance().isOn() && NoRender.getInstance().fog.getValue() == NoRender.Fog.AIR) {
            return Blocks.AIR.defaultBlockState;
        }
        return ActiveRenderInfo.getBlockStateAtEntityViewpoint(worldIn, entityIn, p_186703_2_);
    }

    @Inject(method = "hurtCameraEffect", at = @At("HEAD"), cancellable = true)
    public void hurtCameraEffectHook(float ticks, CallbackInfo info) {
        if (NoRender.getInstance().isOn() && NoRender.getInstance().hurtcam.getValue()) {
            info.cancel();
        }
    }

    @Redirect(method = "getMouseOver", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/WorldClient;getEntitiesInAABBexcluding(Lnet/minecraft/entity/Entity;Lnet/minecraft/util/math/AxisAlignedBB;Lcom/google/common/base/Predicate;)Ljava/util/List;"))
    public List<Entity> getEntitiesInAABBexcludingHook(WorldClient worldClient, @Nullable Entity entityIn, AxisAlignedBB boundingBox, @Nullable Predicate<? super Entity> predicate) {
        return worldClient.getEntitiesInAABBexcluding(entityIn, boundingBox, predicate);
    }
}
