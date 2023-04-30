package mod.selene.api.ui.components;

import mod.selene.api.ui.ClickGUI;
import mod.selene.api.ui.components.items.Item;
import mod.selene.api.ui.components.items.buttons.Button;
import mod.selene.api.utils.ColorUtil;
import mod.selene.api.utils.render.RenderUtil;
import mod.selene.impl.modules.client.ClickGui;
import mod.selene.loader.Feature;
import mod.selene.loader.SeleneLoader;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;

import static mod.selene.api.ui.components.items.buttons.ModuleButton.drawModalRect;

public class Component extends Feature {

    private final ArrayList<Item> items = new ArrayList<>();
    public boolean drag;
    private int angle;
    private int x;
    private int y;
    private int x2;
    private int y2;
    private int width;
    private int height;
    private boolean open;
    private boolean hidden = false;

    public Component(String name, int x, int y, boolean open) {
        super(name);
        this.x = x;
        this.y = y;
        this.angle = 180;
        this.width = 88;
        this.height = 18;
        this.open = open;
        setupItems();
    }

    public static float calculateRotation(float var0) {
        if ((var0 %= 360.0F) >= 180.0F) {
            var0 -= 360.0F;
        }

        if (var0 < -180.0F) {
            var0 += 360.0F;
        }

        return var0;
    }

    public static void glColor(Color color) {
        GL11.glColor4f((float) color.getRed() / 255.0f, (float) color.getGreen() / 255.0f, (float) color.getBlue() / 255.0f, (float) color.getAlpha() / 255.0f);
    }

    public void setupItems() {
        //For the child class
    }

    private void drag(int mouseX, int mouseY) {
        if (!drag) {
            return;
        }
        x = x2 + mouseX;
        y = y2 + mouseY;
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drag(mouseX, mouseY);
        float totalItemHeight = open ? getTotalItemHeight() - 2F : 0F;
        int color = 0xFF888888;
        if (ClickGui.getInstance().devSettings.getValue()) {
            color = ColorUtil.toARGB(ClickGui.getInstance().topColor.getValue().getRed(), ClickGui.getInstance().topColor.getValue().getGreen(), ClickGui.getInstance().topColor.getValue().getBlue(), ClickGui.getInstance().topColor.getValue().getAlpha());
        }
        RenderUtil.drawRect(x, y - 1.5F, (x + width), y + height - 6, color);
        if (open) {
            RenderUtil.drawRect(x, y + 12.5F, (x + width), y + height + totalItemHeight, 0x77000000);
        }

        SeleneLoader.textManager.drawStringWithShadow(this.getName(), x + 3F, y - 4F - ClickGUI.getClickGui().getTextOffset(), 0xFFFFFFFF);
        if (this.open) {
            RenderUtil.drawRect(this.x, (float) this.y + 12.5f, this.x + this.width, (float) (this.y + this.height) + totalItemHeight, ColorUtil.toRGBA(ClickGui.getInstance().moduleColor.getValue().getRed(), ClickGui.getInstance().moduleColor.getValue().getGreen(), ClickGui.getInstance().moduleColor.getValue().getBlue(), ClickGui.getInstance().moduleColor.getValue().getAlpha()));
        }
        if (!this.open) {
            if (this.angle > 0) {
                this.angle -= 6;
            }
        } else if (this.angle < 180) {
            this.angle += 6;
        }

        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        glColor(new Color(255, 255, 255, 255));
        mc.getTextureManager().bindTexture(new ResourceLocation("textures/arrow.png"));
        GlStateManager.translate(getX() + getWidth() - 7, (getY() + 6) - 0.3F, 0.0F);
        GlStateManager.rotate(calculateRotation(angle), 0.0F, 0.0F, 1.0F);
        drawModalRect(-5, -5, 0.0F, 0.0F, 10, 10, 10, 10, 10.0F, 10.0F);
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
        if (open) {
            float y = getY() + getHeight() - 3F;
            for (Item item : getItems()) {
                if (!item.isHidden()) {
                    item.setLocation(x + 2F, y);
                    item.setWidth(getWidth() - 4);
                    item.drawScreen(mouseX, mouseY, partialTicks);
                    y += item.getHeight() + 1.5F;
                }
            }
        }
    }

    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (mouseButton == 0 && isHovering(mouseX, mouseY)) {
            x2 = x - mouseX;
            y2 = y - mouseY;
            ClickGUI.getClickGui().getComponents().forEach(component -> {
                if (component.drag) {
                    component.drag = false;
                }
            });
            drag = true;
            return;
        }
        if (mouseButton == 1 && isHovering(mouseX, mouseY)) {
            open = !open;
            mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            return;
        }
        if (!open) {
            return;
        }
        getItems().forEach(item -> item.mouseClicked(mouseX, mouseY, mouseButton));
    }

    public void mouseReleased(final int mouseX, int mouseY, int releaseButton) {
        if (releaseButton == 0)
            drag = false;
        if (!open) {
            return;
        }
        getItems().forEach(item -> item.mouseReleased(mouseX, mouseY, releaseButton));
    }

    public void onKeyTyped(char typedChar, int keyCode) {
        if (!open) {
            return;
        }
        getItems().forEach(item -> item.onKeyTyped(typedChar, keyCode));
    }

    public void addButton(Button button) {
        items.add(button);
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public boolean isHidden() {
        return this.hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    public boolean isOpen() {
        return open;
    }

    public final ArrayList<Item> getItems() {
        return items;
    }

    private boolean isHovering(int mouseX, int mouseY) {
        return mouseX >= getX() && mouseX <= getX() + getWidth() && mouseY >= getY() && mouseY <= getY() + getHeight() - (open ? 2 : 0);
    }

    private float getTotalItemHeight() {
        float height = 0;
        for (Item item : getItems()) {
            height += item.getHeight() + 1.5F;
        }
        return height;
    }
}
