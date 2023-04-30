package mod.selene.api.ui.components.items.buttons;

import mod.selene.api.ui.ClickGUI;
import mod.selene.api.utils.ColorUtil;
import mod.selene.api.utils.TextUtil;
import mod.selene.api.utils.math.MathUtil;
import mod.selene.api.utils.render.RenderUtil;
import mod.selene.impl.modules.client.ClickGui;
import mod.selene.impl.modules.client.HUD;
import mod.selene.loader.SeleneLoader;
import mod.selene.system.Setting;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.init.SoundEvents;

public class UnlimitedSlider extends Button {

    public Setting setting;

    public UnlimitedSlider(Setting setting) {
        super(setting.getName());
        this.setting = setting;
        width = 15;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        RenderUtil.drawRect(x, y, x + width + 7.4F, y + height - 0.5f, !isHovering(mouseX, mouseY) ? SeleneLoader.colorManager.getColorWithAlpha((SeleneLoader.moduleManager.getModuleByClass(ClickGui.class)).hoverAlpha.getValue()) : SeleneLoader.colorManager.getColorWithAlpha((SeleneLoader.moduleManager.getModuleByClass(ClickGui.class)).alpha.getValue()));
        SeleneLoader.textManager.drawStringWithShadow(" - " + setting.getName() + " " + TextUtil.GRAY + setting.getValue() + TextUtil.RESET + " +", x + 2.3F, y - 1.7F - ClickGUI.getClickGui().getTextOffset(), getState() ? 0xFFFFFFFF : 0xFFAAAAAA);
        if (ClickGui.getInstance().rainbowRolling.getValue().booleanValue()) {
            int color = ColorUtil.changeAlpha(HUD.getInstance().colorMap.get(MathUtil.clamp((int) this.y, 0, this.renderer.scaledHeight)), SeleneLoader.moduleManager.getModuleByClass(ClickGui.class).moduleMainC.getValue().getAlpha());
            int color1 = ColorUtil.changeAlpha(HUD.getInstance().colorMap.get(MathUtil.clamp((int) this.y + this.height, 0, this.renderer.scaledHeight)), SeleneLoader.moduleManager.getModuleByClass(ClickGui.class).moduleMainC.getValue().getAlpha());
            RenderUtil.drawGradientRect2((float) ((int) this.x), (float) ((int) this.y), (float) this.width + 7.4f, (float) this.height, color, color1);
        } else {
            RenderUtil.drawRect(this.x, this.y, this.x + (float) this.width + 7.4f, this.y + (float) this.height - 0.5f, !this.isHovering(mouseX, mouseY) ? SeleneLoader.colorManager.getColorWithAlpha(SeleneLoader.moduleManager.getModuleByClass(ClickGui.class).moduleMainC.getValue().getAlpha()) : SeleneLoader.colorManager.getColorWithAlpha(SeleneLoader.moduleManager.getModuleByClass(ClickGui.class).hoverAlpha.getValue()));
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (isHovering(mouseX, mouseY)) {
            mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            if (isRight(mouseX)) {
                if (setting.getValue() instanceof Double) {
                    setting.setValue(((Double) setting.getValue() + 1));
                } else if (setting.getValue() instanceof Float) {
                    setting.setValue(((Float) setting.getValue() + 1));
                } else if (setting.getValue() instanceof Integer) {
                    setting.setValue(((Integer) setting.getValue() + 1));
                }
            } else {
                if (setting.getValue() instanceof Double) {
                    setting.setValue(((Double) setting.getValue() - 1));
                } else if (setting.getValue() instanceof Float) {
                    setting.setValue(((Float) setting.getValue() - 1));
                } else if (setting.getValue() instanceof Integer) {
                    setting.setValue(((Integer) setting.getValue() - 1));
                }
            }
        }
    }

    @Override
    public void update() {
        this.setHidden(!setting.isVisible());
    }

    @Override
    public int getHeight() {
        return 14;
    }

    public void toggle() {
    }

    public boolean getState() {
        return true;
    }

    public boolean isRight(int x) {
        return x > this.x + ((width + 7.4F) / 2);
    }
}
