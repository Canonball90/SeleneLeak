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
import net.minecraft.util.ChatAllowedCharacters;

public class StringButton extends Button {

    private final Setting setting;
    public boolean isListening;
    private CurrentString currentString = new CurrentString("");

    public StringButton(Setting setting) {
        super(setting.getName());
        this.setting = setting;
        width = 15;
    }

    public static String removeLastChar(String str) {
        String output = "";
        if (str != null && str.length() > 0) {
            output = str.substring(0, str.length() - 1);
        }
        return output;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        RenderUtil.drawRect(x, y, x + width + 7.4F, y + height - 0.5f, getState() ? (!isHovering(mouseX, mouseY) ? SeleneLoader.colorManager.getColorWithAlpha(((SeleneLoader.moduleManager.getModuleByClass(ClickGui.class)).hoverAlpha.getValue())) : SeleneLoader.colorManager.getColorWithAlpha(((SeleneLoader.moduleManager.getModuleByClass(ClickGui.class)).alpha.getValue()))) : !isHovering(mouseX, mouseY) ? 0x11555555 : 0x88555555);
        if (isListening) {
            SeleneLoader.textManager.drawStringWithShadow(currentString.getString() + SeleneLoader.textManager.getIdleSign(), x + 2.3F, y - 1.7F - ClickGUI.getClickGui().getTextOffset(), getState() ? 0xFFFFFFFF : 0xFFAAAAAA);
        } else {
            SeleneLoader.textManager.drawStringWithShadow((setting.getName().equals("Buttons") ? "Buttons " : (setting.getName().equals("Prefix") ? "Prefix  " + TextUtil.GRAY : "")) + setting.getValue(), x + 2.3F, y - 1.7F - ClickGUI.getClickGui().getTextOffset(), getState() ? 0xFFFFFFFF : 0xFFAAAAAA);
            if (ClickGui.getInstance().rainbowRolling.getValue().booleanValue()) {
                int color = ColorUtil.changeAlpha(HUD.getInstance().colorMap.get(MathUtil.clamp((int) this.y, 0, this.renderer.scaledHeight)), SeleneLoader.moduleManager.getModuleByClass(ClickGui.class).moduleMainC.getValue().getAlpha());
                int color1 = ColorUtil.changeAlpha(HUD.getInstance().colorMap.get(MathUtil.clamp((int) this.y + this.height, 0, this.renderer.scaledHeight)), SeleneLoader.moduleManager.getModuleByClass(ClickGui.class).moduleMainC.getValue().getAlpha());
                RenderUtil.drawGradientRect2(this.x, this.y, (float) this.width + 7.4f, (float) this.height - 0.5f, this.getState() ? (!this.isHovering(mouseX, mouseY) ? HUD.getInstance().colorMap.get(MathUtil.clamp((int) this.y, 0, this.renderer.scaledHeight)) : color) : (!this.isHovering(mouseX, mouseY) ? 0x11555555 : -2007673515), this.getState() ? (!this.isHovering(mouseX, mouseY) ? HUD.getInstance().colorMap.get(MathUtil.clamp((int) this.y + this.height, 0, this.renderer.scaledHeight)) : color1) : (!this.isHovering(mouseX, mouseY) ? 0x11555555 : -2007673515));
            } else {
                RenderUtil.drawRect(this.x, this.y, this.x + (float) this.width + 7.4f, this.y + (float) this.height - 0.5f, this.getState() ? (!this.isHovering(mouseX, mouseY) ? SeleneLoader.colorManager.getColorWithAlpha(SeleneLoader.moduleManager.getModuleByClass(ClickGui.class).moduleMainC.getValue().getAlpha()) : SeleneLoader.colorManager.getColorWithAlpha(SeleneLoader.moduleManager.getModuleByClass(ClickGui.class).hoverAlpha.getValue())) : (!this.isHovering(mouseX, mouseY) ? 0x11555555 : -2007673515));
            }
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (isHovering(mouseX, mouseY)) {
            mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
        }
    }

    //TODO: CHINESE
    @Override
    public void onKeyTyped(char typedChar, int keyCode) {
        if (isListening) {
            switch (keyCode) {
                case 1:
                    break;
                case 28:
                    enterString();
                    break;
                case 14:
                    setString(removeLastChar(currentString.getString()));
                    break;
                default:
                    if (ChatAllowedCharacters.isAllowedCharacter(typedChar)) {
                        setString(currentString.getString() + typedChar);
                    }
            }
        }
    }

    @Override
    public void update() {
        this.setHidden(!setting.isVisible());
    }

    private void enterString() {
        if (currentString.getString().isEmpty()) {
            setting.setValue(setting.getDefaultValue());
        } else {
            setting.setValue(currentString.getString());
        }
        setString("");
        super.onMouseClick();
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

    public void setString(String newString) {
        this.currentString = new CurrentString(newString);
    }

    //TODO: WTF IS THIS
    public static class CurrentString {
        private final String string;

        public CurrentString(String string) {
            this.string = string;
        }

        public String getString() {
            return this.string;
        }
    }
}
