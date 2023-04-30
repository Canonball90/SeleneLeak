package mod.selene.injections.inj;

import net.minecraft.client.gui.FontRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(value = {FontRenderer.class})
public interface IFontRenderer {
    @Invoker(value = "renderSplitString")
    void invokeRenderSplitString(String var1, int var2, int var3, int var4, boolean var5);
}

