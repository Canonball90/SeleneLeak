package mod.selene.api.ui.components.items.buttons;

import mod.selene.api.ui.ClickGUI;
import mod.selene.api.ui.components.Component;
import mod.selene.api.ui.components.items.Item;
import mod.selene.api.utils.ColorUtil;
import mod.selene.api.utils.math.MathUtil;
import mod.selene.api.utils.render.RenderUtil;
import mod.selene.impl.modules.client.ClickGui;
import mod.selene.impl.modules.client.FontMod;
import mod.selene.impl.modules.client.HUD;
import mod.selene.loader.SeleneLoader;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.init.SoundEvents;
import org.lwjgl.opengl.GL11;

public class Button extends Item {

    private boolean state;

    public Button(String name) {
        super(name);
        this.height = 15;
    }

    public static void glColor(int hex) {
        float alpha = (float) (hex >> 24 & 0xFF) / 255.0f;
        float red = (float) (hex >> 16 & 0xFF) / 255.0f;
        float green = (float) (hex >> 8 & 0xFF) / 255.0f;
        float blue = (float) (hex & 0xFF) / 255.0f;
        GL11.glColor4f(red, green, blue, alpha);
    }

    public static void enableGL2D() {
        GL11.glDisable(2929);
        GL11.glEnable(3042);
        GL11.glDisable(3553);
        GL11.glBlendFunc(770, 771);
        GL11.glDepthMask(true);
        GL11.glEnable(2848);
        GL11.glHint(3154, 4354);
        GL11.glHint(3155, 4354);
    }

    public static void disableGL2D() {
        GL11.glEnable(3553);
        GL11.glDisable(3042);
        GL11.glEnable(2929);
        GL11.glDisable(2848);
        GL11.glHint(3154, 4352);
        GL11.glHint(3155, 4352);
    }

    public static void drawGradientRect(float x, float y, float x1, float y1, int topColor, int bottomColor) {
        enableGL2D();
        GL11.glShadeModel(7425);
        GL11.glBegin(7);
        glColor(topColor);
        GL11.glVertex2f(x, y1);
        GL11.glVertex2f(x1, y1);
        glColor(bottomColor);
        GL11.glVertex2f(x1, y);
        GL11.glVertex2f(x, y);
        GL11.glEnd();
        GL11.glShadeModel(7424);
        disableGL2D();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
//        RenderUtil.drawRect(x, y, x + width, y + height - 0.5f, getState() ? (!isHovering(mouseX, mouseY) ? SeleneLoader.colorManager.getColorWithAlpha((SeleneLoader.moduleManager.getModuleByClass(ClickGui.class)).hoverAlpha.getValue()) : SeleneLoader.colorManager.getColorWithAlpha((SeleneLoader.moduleManager.getModuleByClass(ClickGui.class)).alpha.getValue())) : !isHovering(mouseX, mouseY) ? 0x11555555 : 0x88555555);
        drawGradientRect(this.x, this.y, this.x + (float) this.width, this.y + (float) this.height, this.getState() ? (!this.isHovering(mouseX, mouseY) ? SeleneLoader.colorManager.getColorWithAlpha((SeleneLoader.moduleManager.getModuleByClass(ClickGui.class)).hoverAlpha.getValue()) : SeleneLoader.colorManager.getColorWithAlpha((SeleneLoader.moduleManager.getModuleByClass(ClickGui.class)).alpha.getValue())) : (!this.isHovering(mouseX, mouseY) ? 0x33555555 : 0x77AAAAAB), this.getState() ? (!this.isHovering(mouseX, mouseY) ? SeleneLoader.colorManager.getColorWithAlpha((SeleneLoader.moduleManager.getModuleByClass(ClickGui.class)).hoverAlpha.getValue()) : SeleneLoader.colorManager.getColorWithAlpha((SeleneLoader.moduleManager.getModuleByClass(ClickGui.class)).alpha.getValue())) : (!this.isHovering(mouseX, mouseY) ? 0x55555555 : 0x66AAAAAB));
        SeleneLoader.textManager.drawStringWithShadow(getName(), x + 2.3F, y - 2F - ClickGUI.getClickGui().getTextOffset(), getState() ? 0xFFFFFFFF : 0xFFAAAAAA);
        if (ClickGui.getInstance().moduleSeperate.getValue().booleanValue()) {
            RenderUtil.drawRect(this.x + 2.0f, this.y - 0.5f, this.x + (float) this.width - 2.0f, this.y - 0.2f, ColorUtil.toRGBA(ClickGui.getInstance().moduleSeparateColor.getValue().getRed(), ClickGui.getInstance().moduleSeparateColor.getValue().getGreen(), ClickGui.getInstance().moduleSeparateColor.getValue().getBlue(), ClickGui.getInstance().moduleSeparateColor.getValue().getAlpha()));
        }
        if (ClickGui.getInstance().rainbowRolling.getValue().booleanValue()) {
            int color = ColorUtil.changeAlpha(HUD.getInstance().colorMap.get(MathUtil.clamp((int) this.y, 0, this.renderer.scaledHeight)), SeleneLoader.moduleManager.getModuleByClass(ClickGui.class).moduleMainC.getValue().getAlpha());
            int color1 = ColorUtil.changeAlpha(HUD.getInstance().colorMap.get(MathUtil.clamp((int) this.y + this.height, 0, this.renderer.scaledHeight)), SeleneLoader.moduleManager.getModuleByClass(ClickGui.class).moduleMainC.getValue().getAlpha());
            RenderUtil.drawGradientRect2(this.x, this.y, (float) this.width, (float) this.height - 0.5f, this.getState() ? (!this.isHovering(mouseX, mouseY) ? HUD.getInstance().colorMap.get(MathUtil.clamp((int) this.y, 0, this.renderer.scaledHeight)) : color) : (!this.isHovering(mouseX, mouseY) ? 0x11555555 : -2007673515), this.getState() ? (!this.isHovering(mouseX, mouseY) ? HUD.getInstance().colorMap.get(MathUtil.clamp((int) this.y + this.height, 0, this.renderer.scaledHeight)) : color1) : (!this.isHovering(mouseX, mouseY) ? 0x11555555 : -2007673515));
        } else {
            RenderUtil.drawRect(this.x, this.y, this.x + (float) this.width, this.y + (float) this.height - 0.5f, this.getState() ? (!this.isHovering(mouseX, mouseY) ? SeleneLoader.colorManager.getColorWithAlpha(SeleneLoader.moduleManager.getModuleByClass(ClickGui.class).moduleMainC.getValue().getAlpha()) : SeleneLoader.colorManager.getColorWithAlpha(SeleneLoader.moduleManager.getModuleByClass(ClickGui.class).hoverAlpha.getValue())) : (!this.isHovering(mouseX, mouseY) ? 0x11555555 : -2007673515));
            if (ClickGui.getInstance().buttonTextCenter.getValue().booleanValue()) {
                SeleneLoader.textManager.drawStringWithShadow(this.getName(), this.x + (this.width / 2.0f) - (this.renderer.getStringWidth(this.getName()) / 2.0f), this.y - 2.0f - (float) ClickGUI.getClickGui().getTextOffset(), this.getState() ? ColorUtil.toRGBA(FontMod.getInstance().tEnableColor.getValue().getRed(), FontMod.getInstance().tEnableColor.getValue().getGreen(), FontMod.getInstance().tEnableColor.getValue().getBlue(), 255) : ColorUtil.toRGBA(FontMod.getInstance().tDisabledColor.getValue().getRed(), FontMod.getInstance().tDisabledColor.getValue().getGreen(), FontMod.getInstance().tDisabledColor.getValue().getBlue(), 255));
            } else {
                SeleneLoader.textManager.drawStringWithShadow(this.getName(), this.x + 2.3f, this.y - 2.0f - (float) ClickGUI.getClickGui().getTextOffset(), this.getState() ? ColorUtil.toRGBA(FontMod.getInstance().tEnableColor.getValue().getRed(), FontMod.getInstance().tEnableColor.getValue().getGreen(), FontMod.getInstance().tEnableColor.getValue().getBlue(), 255) : ColorUtil.toRGBA(FontMod.getInstance().tDisabledColor.getValue().getRed(), FontMod.getInstance().tDisabledColor.getValue().getGreen(), FontMod.getInstance().tDisabledColor.getValue().getBlue(), 255));
            }
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (mouseButton == 0 && isHovering(mouseX, mouseY)) {
            onMouseClick();
        }
    }

    public void onMouseClick() {
        state = !state;
        toggle();
        mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
    }

    public void toggle() {
    }

    public boolean getState() {
        return state;
    }

    @Override
    public int getHeight() {
        return 14;
    }

    public boolean isHovering(int mouseX, int mouseY) {
        for (Component component : ClickGUI.getClickGui().getComponents()) {
            if (component.drag) {
                return false;
            }
        }
        return mouseX >= getX() && mouseX <= getX() + getWidth() && mouseY >= getY() && mouseY <= getY() + height;
    }
}
