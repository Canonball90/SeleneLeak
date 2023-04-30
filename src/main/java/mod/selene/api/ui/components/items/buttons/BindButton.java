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
import mod.selene.system.impl.Bind;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.init.SoundEvents;

public class BindButton extends Button {

    private final Setting setting;
    public boolean isListening;

    public BindButton(Setting setting) {
        super(setting.getName());
        this.setting = setting;
        width = 15;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        RenderUtil.drawRect(x, y, x + width + 7.4F, y + height - 0.5f, getState() ? (!isHovering(mouseX, mouseY) ? SeleneLoader.colorManager.getColorWithAlpha(((ClickGui) SeleneLoader.moduleManager.getModuleByName("ClickGui")).hoverAlpha.getValue()) : SeleneLoader.colorManager.getColorWithAlpha(((ClickGui) SeleneLoader.moduleManager.getModuleByName("ClickGui")).alpha.getValue())) : !isHovering(mouseX, mouseY) ? 0x11555555 : 0x88555555);
        if (isListening) {
            SeleneLoader.textManager.drawStringWithShadow("Listening...", x + 2.3F, y - 1.7F - ClickGUI.getClickGui().getTextOffset(), getState() ? 0xFFFFFFFF : 0xFFAAAAAA);
        } else {
            SeleneLoader.textManager.drawStringWithShadow(setting.getName() + " " + TextUtil.GRAY + setting.getValue().toString(), x + 2.3F, y - 1.7F - ClickGUI.getClickGui().getTextOffset(), getState() ? 0xFFFFFFFF : 0xFFAAAAAA);
            if (ClickGui.getInstance().rainbowRolling.getValue().booleanValue()) {
                int color = ColorUtil.changeAlpha(HUD.getInstance().colorMap.get(MathUtil.clamp((int) this.y, 0, this.renderer.scaledHeight)), SeleneLoader.moduleManager.getModuleByClass(ClickGui.class).moduleMainC.getValue().getAlpha());
                int color1 = ColorUtil.changeAlpha(HUD.getInstance().colorMap.get(MathUtil.clamp((int) this.y + this.height, 0, this.renderer.scaledHeight)), SeleneLoader.moduleManager.getModuleByClass(ClickGui.class).moduleMainC.getValue().getAlpha());
                RenderUtil.drawGradientRect2(this.x, this.y, (float) this.width + 7.4f, (float) this.height - 0.5f, this.getState() ? (!this.isHovering(mouseX, mouseY) ? HUD.getInstance().colorMap.get(MathUtil.clamp((int) this.y, 0, this.renderer.scaledHeight)) : color) : (!this.isHovering(mouseX, mouseY) ? 0x11555555 : -2007673515), this.getState() ? (!this.isHovering(mouseX, mouseY) ? HUD.getInstance().colorMap.get(MathUtil.clamp((int) this.y + this.height, 0, this.renderer.scaledHeight)) : color1) : (!this.isHovering(mouseX, mouseY) ? 0x11555555 : -2007673515));
            } else {
                RenderUtil.drawRect(this.x, this.y, this.x + (float) this.width + 7.4f, this.y + (float) this.height - 0.5f, this.getState() ? (!this.isHovering(mouseX, mouseY) ? SeleneLoader.colorManager.getColorWithAlpha(((ClickGui) SeleneLoader.moduleManager.getModuleByName("ClickGui")).moduleMainC.getValue().getAlpha()) : SeleneLoader.colorManager.getColorWithAlpha(((ClickGui) SeleneLoader.moduleManager.getModuleByName("ClickGui")).hoverAlpha.getValue())) : (!this.isHovering(mouseX, mouseY) ? 0x11555555 : -2007673515));
            }
        }
    }

    @Override
    public void update() {
        this.setHidden(!setting.isVisible());
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (isHovering(mouseX, mouseY)) {
            mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
        }
    }

    @Override
    public void onKeyTyped(char typedChar, int keyCode) {
        if (isListening) {
            Bind bind = new Bind(keyCode);
            if (bind.toString().equalsIgnoreCase("Escape")) {
                return;
            } else if (bind.toString().equalsIgnoreCase("Delete")) {
                bind = new Bind(-1);
            }
            setting.setValue(bind);
            super.onMouseClick();
        }
    }

    @Override
    public int getHeight() {
        return 14;
    }

    public void toggle() {
        isListening = !isListening;
    }

    public boolean getState() {
        return !isListening;
    }
}
