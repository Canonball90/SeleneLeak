package mod.selene.injections;

import mod.selene.impl.modules.client.HUD;
import mod.selene.loader.SeleneLoader;
import net.minecraft.client.gui.FontRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(FontRenderer.class) //Could be done easier, we want compatibility with future tho.
public abstract class MixinFontRenderer {

    @Shadow
    public abstract int renderString(String text, float x, float y, int color, boolean dropShadow);

    @Redirect(method = "drawString(Ljava/lang/String;FFIZ)I", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/FontRenderer;renderString(Ljava/lang/String;FFIZ)I"))
    public int renderStringHook(FontRenderer fontrenderer, String text, float x, float y, int color, boolean dropShadow) {
        if (SeleneLoader.moduleManager != null && HUD.getInstance().shadow.getValue() && dropShadow) {
            return this.renderString(text, x - 0.5f, y - 0.5f, color, true);
        }
        return this.renderString(text, x, y, color, dropShadow);
    }

}
