package mod.selene.injections;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiPlayerTabOverlay;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(GuiPlayerTabOverlay.class)
public class MixinGuiPlayerTabOverlay extends Gui {
}
