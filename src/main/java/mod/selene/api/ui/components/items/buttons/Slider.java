package mod.selene.api.ui.components.items.buttons;

import mod.selene.api.ui.ClickGUI;
import mod.selene.api.ui.components.Component;
import mod.selene.api.utils.ColorUtil;
import mod.selene.api.utils.TextUtil;
import mod.selene.api.utils.math.MathUtil;
import mod.selene.api.utils.render.RenderUtil;
import mod.selene.impl.modules.client.ClickGui;
import mod.selene.impl.modules.client.HUD;
import mod.selene.loader.SeleneLoader;
import mod.selene.system.Setting;
import org.lwjgl.input.Mouse;

public class Slider extends Button {

    private final Number min;
    private final Number max;
    private final int difference;
    public Setting setting;

    public Slider(Setting setting) {
        super(setting.getName());
        this.setting = setting;
        this.min = (Number) setting.getMin();
        this.max = (Number) setting.getMax();
        this.difference = max.intValue() - min.intValue();
        width = 15;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        dragSetting(mouseX, mouseY);
        RenderUtil.drawRect(x, y, x + width + 7.4F, y + height - 0.5f, !isHovering(mouseX, mouseY) ? 0x11555555 : 0x88555555);
        RenderUtil.drawRect(x, y, ((Number) setting.getValue()).floatValue() <= min.floatValue() ? x : x + (width + 7.4F) * partialMultiplier(), y + height - 0.5f, !isHovering(mouseX, mouseY) ? SeleneLoader.colorManager.getColorWithAlpha((SeleneLoader.moduleManager.getModuleByClass(ClickGui.class)).hoverAlpha.getValue()) : SeleneLoader.colorManager.getColorWithAlpha(((SeleneLoader.moduleManager.getModuleByClass(ClickGui.class)).alpha.getValue())));
        SeleneLoader.textManager.drawStringWithShadow(getName() + " " + TextUtil.GRAY + (setting.getValue() instanceof Float ? ((Number) setting.getValue()) : ((Number) setting.getValue()).doubleValue()), x + 2.3F, y - 1.7F - ClickGUI.getClickGui().getTextOffset(), 0xFFFFFFFF);
        if (ClickGui.getInstance().rainbowRolling.getValue().booleanValue()) {
            int color = ColorUtil.changeAlpha(HUD.getInstance().colorMap.get(MathUtil.clamp((int) this.y, 0, this.renderer.scaledHeight)), SeleneLoader.moduleManager.getModuleByClass(ClickGui.class).moduleMainC.getValue().getAlpha());
            int color1 = ColorUtil.changeAlpha(HUD.getInstance().colorMap.get(MathUtil.clamp((int) this.y + this.height, 0, this.renderer.scaledHeight)), SeleneLoader.moduleManager.getModuleByClass(ClickGui.class).moduleMainC.getValue().getAlpha());
            RenderUtil.drawGradientRect2(this.x, this.y, ((Number) this.setting.getValue()).floatValue() <= this.min.floatValue() ? 0.0f : ((float) this.width + 7.4f) * this.partialMultiplier(), (float) this.height - 0.5f, !this.isHovering(mouseX, mouseY) ? HUD.getInstance().colorMap.get(MathUtil.clamp((int) this.y, 0, this.renderer.scaledHeight)) : color, !this.isHovering(mouseX, mouseY) ? HUD.getInstance().colorMap.get(MathUtil.clamp((int) this.y, 0, this.renderer.scaledHeight)) : color1);
        } /*else {
            if (ClickGui.getInstance().sliderType.getValue() == ClickGui.SliderType.Line) {
                int sliderColor = ColorUtil.toRGBA(ClickGui.getInstance().sliderC.getValue().getRed(), ClickGui.getInstance().sliderC.getValue().getGreen(), ClickGui.getInstance().sliderC.getValue().getBlue(), ClickGui.getInstance().sliderC.getValue().getAlpha());
                //todo
                // int sliderColorHovering = ColorUtil.toRGBA(ClickGui.getInstance().sliderRed.getValue(), ClickGui.getInstance().sliderGreen.getValue(), ClickGui.getInstance().sliderBlue.getValue(), ClickGui.getInstance().moduleMainC.getValue().getAlpha());
                RenderUtil.drawRect(this.x, this.y + (float) this.height - 2.0f, ((Number) this.setting.getValue()).floatValue() <= this.min.floatValue() ? this.x : this.x + ((float) this.width + 7.4f) * this.partialMultiplier(), this.y + (float) this.height - 0.5f, sliderColor);
            } else if (ClickGui.getInstance().sliderType.getValue() == ClickGui.SliderType.Fill) {
                int sliderColor = ColorUtil.toRGBA(ClickGui.getInstance().sliderC.getValue().getRed(), ClickGui.getInstance().sliderC.getValue().getGreen(), ClickGui.getInstance().sliderC.getValue().getBlue(), ClickGui.getInstance().sliderC.getValue().getAlpha());
                //todo
                //  int sliderColorHovering = ColorUtil.toRGBA(ClickGui.getInstance().sliderRed.getValue(), ClickGui.getInstance().sliderGreen.getValue(), ClickGui.getInstance().sliderBlue.getValue(), ClickGui.getInstance().moduleMainC.getValue().getAlpha());
                RenderUtil.drawRect(this.x, this.y, ((Number) this.setting.getValue()).floatValue() <= this.min.floatValue() ? this.x : this.x + ((float) this.width + 7.4f) * this.partialMultiplier(), this.y + (float) this.height - 0.5f, sliderColor);
            }
        }
        SeleneLoader.textManager.drawStringWithShadow(this.getName() + " \u00a77" + (this.setting.getValue() instanceof Float ? (Number) this.setting.getValue() : (Number) ((Number) this.setting.getValue()).doubleValue()), this.x + 2.3f, this.y - 1.7f - (float) Nhack4Gui.getClickGui().getTextOffset(), -1);*/
    }


    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (isHovering(mouseX, mouseY)) {
            setSettingFromX(mouseX);
        }
    }

    @Override
    public boolean isHovering(int mouseX, int mouseY) {
        for (Component component : ClickGUI.getClickGui().getComponents()) {
            if (component.drag) {
                return false;
            }
        }
        return mouseX >= getX() && mouseX <= getX() + getWidth() + 8 && mouseY >= getY() && mouseY <= getY() + height;
    }

    @Override
    public void update() {
        this.setHidden(!setting.isVisible());
    }

    private void dragSetting(int mouseX, int mouseY) {
        if (isHovering(mouseX, mouseY) && Mouse.isButtonDown(0)) {
            setSettingFromX(mouseX);
        }
    }

    @Override
    public int getHeight() {
        return 14;
    }

    private void setSettingFromX(int mouseX) {
        float percent = (mouseX - x) / (width + 7.4F);
        if (setting.getValue() instanceof Double) {
            double result = (Double) setting.getMin() + (difference * percent);
            setting.setValue(Math.round(10.0 * result) / 10.0);
        } else if (setting.getValue() instanceof Float) {
            float result = (Float) setting.getMin() + (difference * percent);
            setting.setValue(Math.round(10.0f * result) / 10.0f);
        } else if (setting.getValue() instanceof Integer) {
            setting.setValue(((Integer) setting.getMin() + (int) (difference * percent)));
        }
    }

    private float middle() {
        return max.floatValue() - min.floatValue();
    }

    private float part() {
        return ((Number) setting.getValue()).floatValue() - min.floatValue();
    }

    private float partialMultiplier() {
        return part() / middle();
    }
}
