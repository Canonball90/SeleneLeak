package mod.selene.impl.modules.render;

import mod.selene.api.utils.math.MathUtil;
import mod.selene.api.utils.render.RenderUtil;
import mod.selene.impl.Module;
import mod.selene.loader.SeleneLoader;
import mod.selene.system.Setting;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.lwjgl.opengl.GL11.*;

public class NameTags extends Module {
    public static NameTags INSTANCE;
    public List<EntityPlayer> entityPlayers = new ArrayList<>();
    //    Setting<Boolean> cFont = register(new Setting("CFont", false));
//    Setting<Float> cfontOffsetY = register(new Setting("Y Offset", 1, -2.0F, 2.0F, v -> cFont.getValue()));
//    Setting<Float> cfontOffsetX = register(new Setting("X Offset", 0, -2.0F, 2.0F, v -> cFont.getValue()));
    Setting<Boolean> armor = register(new Setting("Armor", true));
    Setting<Boolean> items = register(new Setting("Items", true));
    Setting<Boolean> heart = register(new Setting("Heart", false));
    Setting<Boolean> sneak = register(new Setting("Sneak", false));
    Setting<Boolean> enchant = register(new Setting("Enchantments", false));
    Setting<Boolean> healthBar = register(new Setting("Health Bar", false));
    Setting<Boolean> background = register(new Setting("Background", true));
    Setting<Boolean> roundedBackground = register(new Setting("RoundedBackground", true, v -> !background.getValue()));
    Setting<Integer> radius = register(new Setting("Radius", 1, 1, 5, v -> !background.getValue()));
    Setting<Boolean> gradientBackground = register(new Setting("Gradient Background", true, v -> background.getValue()));
    Setting<Float> outlineWidth = register(new Setting("Outline Width", 1.0f, 0f, 3.0f));
    Setting<Color> color = register(new Setting("Color", new Color(255, 255, 255)));
    //    Setting<Float> yOffset = register(new Setting("Y Offset", 0, -10.0F, 10.0F));
    Setting<Float> sizel = register(new Setting("Size", 1.3F, -10.0F, 10.0F));
    Setting<Integer> width = register(new Setting("Width", 0, -5, 5));
    Setting<Integer> height = register(new Setting("Height", 0, -5, 5));

    public NameTags() {
        super("NameTags", "NameTags", Category.RENDER, true, false, false);
        INSTANCE = this;
    }

    public static NameTags getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new NameTags();
        }
        return INSTANCE;
    }

    public static void drawFilledCircle(int x, int y, double radius, int color, int start, int stop) {
        glColor4f(((color >> 16) & 0xff) / 255F, ((color >> 8) & 0xff) / 255F, (color & 0xff) / 255F, ((color >> 24) & 0xff) / 255F);
        for (int i = start; i <= stop; i++)
            glVertex2d(x + Math.sin(((i * Math.PI) / 180)) * radius, y + Math.cos(((i * Math.PI) / 180)) * radius);
    }

    @SubscribeEvent
    public void onRenderWorld(RenderWorldLastEvent event) {
        if (fullNullCheck()) return;
        for (Entity e : NameTags.mc.world.loadedEntityList) {
            if (!(e instanceof EntityPlayer) || e == NameTags.mc.player) continue;
            double x = e.lastTickPosX + (e.posX - e.lastTickPosX) * (double) event.getPartialTicks() - NameTags.mc.getRenderManager().viewerPosX;
            double y = e.lastTickPosY + (e.posY - e.lastTickPosY) * (double) event.getPartialTicks() - NameTags.mc.getRenderManager().viewerPosY;
            double z = e.lastTickPosZ + (e.posZ - e.lastTickPosZ) * (double) event.getPartialTicks() - NameTags.mc.getRenderManager().viewerPosZ;
            GL11.glPushMatrix();
            GL11.glDisable(2929);
            GL11.glDisable(3553);
            GL11.glNormal3f(0.0f, 1.0f, 0.0f);
            GlStateManager.enablePolygonOffset();
            GlStateManager.doPolygonOffset(1.0f, -1500000.0f);
            GlStateManager.disableLighting();
            GlStateManager.enableBlend();
            float size = Math.min(Math.max(1.2f * (NameTags.mc.player.getDistance(e) * 0.15f), 1.25f), 6.0f) * 0.015f * sizel.getValue();
            GL11.glTranslatef((float) x, (float) y + e.height + 0.6f, (float) z);
            GlStateManager.glNormal3f(0.0f, 1.0f, 0.0f);
            GlStateManager.rotate(-NameTags.mc.getRenderManager().playerViewY, 0.0f, 1.0f, 0.0f);
            GlStateManager.rotate(NameTags.mc.getRenderManager().playerViewX, 1.0f, 0.0f, 0.0f);
            GL11.glScalef(-size, -size, -size);
            GlStateManager.disableDepth();
            GlStateManager.enableBlend();
            GlStateManager.disableAlpha();
            int health = (int) (((EntityPlayer) e).getHealth() / ((EntityPlayer) e).getMaxHealth() * 100.0f);
            if (background.getValue() && gradientBackground.getValue()) {
                RenderUtil.drawGradientBorderedRect(-NameTags.mc.fontRenderer.getStringWidth(displayColoredUsername(e.getName(), (EntityPlayer) e) + " " + health + "%") / 2 - 2 - width.getValue(), -2 - height.getValue(), NameTags.mc.fontRenderer.getStringWidth(displayColoredUsername(e.getName(), (EntityPlayer) e)) / 2 + 16 + width.getValue(), 10 + height.getValue(), new Color(25, 25, 25, 150).getRGB());
            } else {
                RenderUtil.drawBorderedRect(-NameTags.mc.fontRenderer.getStringWidth(displayColoredUsername(e.getName(), (EntityPlayer) e) + " " + health + "%") / 2 - 2 - width.getValue(), -2 - height.getValue(), NameTags.mc.fontRenderer.getStringWidth(displayColoredUsername(e.getName(), (EntityPlayer) e)) / 2 + 16 + width.getValue(), 10 + height.getValue(), outlineWidth.getValue(), (background.getValue() ? new Color(25, 25, 25, 150).getRGB() : new Color(25, 25, 25, 0).getRGB()), color.getValue().getRGB());
            }
            if (roundedBackground.getValue()) {
                drawRoundedRect(-NameTags.mc.fontRenderer.getStringWidth(displayColoredUsername(e.getName(), (EntityPlayer) e) + " " + health + "%") / 2 - 2 - width.getValue(), -2 - height.getValue(), NameTags.mc.fontRenderer.getStringWidth(displayColoredUsername(e.getName(), (EntityPlayer) e)) / 2 + 16 + width.getValue(), 10 + height.getValue(), new Color(25, 25, 25, 150).getRGB(), radius.getValue().intValue());
                drawOutlineRoundedRect(-NameTags.mc.fontRenderer.getStringWidth(displayColoredUsername(e.getName(), (EntityPlayer) e) + " " + health + "%") / 2 - 2 - width.getValue(), -2 - height.getValue(), NameTags.mc.fontRenderer.getStringWidth(displayColoredUsername(e.getName(), (EntityPlayer) e)) / 2 + 16 + width.getValue(), 10 + height.getValue(), color.getValue().getRGB(), radius.getValue().intValue(), mc.player.getDistance(e), size);
            } else {
                RenderUtil.drawBorderedRect(-NameTags.mc.fontRenderer.getStringWidth(displayColoredUsername(e.getName(), (EntityPlayer) e) + " " + health + "%") / 2 - 2 - width.getValue(), -2 - height.getValue(), NameTags.mc.fontRenderer.getStringWidth(displayColoredUsername(e.getName(), (EntityPlayer) e)) / 2 + 16 + width.getValue(), 10 + height.getValue(), outlineWidth.getValue(), new Color(25, 25, 25, 0).getRGB(), color.getValue().getRGB());
            }
            GlStateManager.enableAlpha();
//            if (healthBar.getValue()) {
//                int length = (int) (((NameTags.mc.fontRenderer.getStringWidth(displayColoredUsername(e.getName(), (EntityPlayer) e) + " " + health + "%") * 2 - 1) * health));
//                length = Math.max(2, length);
//                Color color = new Color(0xBB0A0A);
//                GlStateManager.disableDepth();
//                drawRoundedRect((int) (-NameTags.mc.fontRenderer.getStringWidth(displayColoredUsername(e.getName(), (EntityPlayer) e) + " " + health + "%") / 2 - 2) - width.getValue(), 4, (int) (NameTags.mc.fontRenderer.getStringWidth(displayColoredUsername(e.getName(), (EntityPlayer) e)) / 2 + 16)  + width.getValue() - length, 6, color.getRGB(), 1);
//            }
//            if (cFont.getValue()) {
//                textManager.drawStringWithShadow(displayColoredUsername(e.getName(), (EntityPlayer) e) + " " + TextFormatting.GREEN + health + (heart.getValue() ? "\u2764" : "%"), -this.getcenter(displayColoredUsername(e.getName(), (EntityPlayer) e) + " " + TextFormatting.GREEN + health + "%") + cfontOffsetX.getValue(), 2, -1);
//            } else {
            mc.fontRenderer.drawStringWithShadow(displayColoredUsername(e.getName(), (EntityPlayer) e) + " " + TextFormatting.GREEN + health + (heart.getValue() ? "\u2764" : "%"), -this.getcenter(displayColoredUsername(e.getName(), (EntityPlayer) e) + " " + TextFormatting.GREEN + health + "%"), 1, -1);
//            }
            int posX = -NameTags.mc.fontRenderer.getStringWidth(displayColoredUsername(e.getName(), (EntityPlayer) e)) / 2 - 8;
            if (healthBar.getValue()) {
                RenderUtil.drawLine((-NameTags.mc.fontRenderer.getStringWidth(displayColoredUsername(e.getName(), (EntityPlayer) e) + " " + health + "%") / 2 - 2) - width.getValue(), 11 + height.getValue(), NameTags.mc.fontRenderer.getStringWidth(displayColoredUsername(e.getName(), (EntityPlayer) e)) / 2 + 16 + width.getValue() * ((NameTags.mc.fontRenderer.getStringWidth(displayColoredUsername(e.getName(), (EntityPlayer) e)) / 2 + 16) + width.getValue() - health), 11 + height.getValue(), 3, 0xff00);
            }
            if (items.getValue()) {
                if (Item.getIdFromItem(((EntityPlayer) e).inventory.getCurrentItem().getItem()) != 0) {
                    NameTags.mc.getRenderItem().zLevel = -100.0f;
                    mc.getRenderItem().renderItemIntoGUI(new ItemStack(((EntityPlayer) e).inventory.getCurrentItem().getItem()), posX - 2, -20);
                    NameTags.mc.getRenderItem().zLevel = 0.0f;
                    int posY = -30;
                    Map enchantments = EnchantmentHelper.getEnchantments(((EntityPlayer) e).inventory.getCurrentItem());
                    for (Object enchantment : enchantments.keySet()) {
                        if (enchant.getValue()) {
                            int level = EnchantmentHelper.getEnchantmentLevel((Enchantment) enchantment, ((EntityPlayer) e).inventory.getCurrentItem());
                            mc.fontRenderer.drawStringWithShadow(String.valueOf(((Enchantment) enchantment).getName().substring(12).charAt(0)).toUpperCase() + level, (float) (posX + 6 - this.getcenter(String.valueOf(((Enchantment) enchantment).getName().substring(12).charAt(0)).toUpperCase() + level)), (float) posY, -1);
                            posY -= 12;
                        }
                    }
                    posX += 15;
                }
            }
            for (ItemStack item : e.getArmorInventoryList()) {
                if (armor.getValue()) {
                    NameTags.mc.getRenderItem().zLevel = -100.0f;
                    renderItem(item, posX, -20);
                    NameTags.mc.getRenderItem().zLevel = 0.0f;
                    int posY = -30;
                    Map enchantments = EnchantmentHelper.getEnchantments(item);
                    for (Object enchantment : enchantments.keySet()) {
                        if (enchant.getValue()) {

                            //mc.fontRenderer.drawStringWithShadow(String.valueOf(((Enchantment) enchantment).getName().substring(12).charAt(0)).toUpperCase() + level, (float) (posX + 9 - this.getcenter(((Enchantment) enchantment).getName().substring(12).charAt(0) + level)), (float) posY, -1);
                            posY -= 12;
                        }
                    }
                    posX += 17;
                }

            }
            int gapples = 0;
            if (Item.getIdFromItem(((EntityPlayer) e).inventory.getCurrentItem().getItem()) == 322) {
                gapples = ((EntityPlayer) e).inventory.getCurrentItem().getCount();
            } else if (Item.getIdFromItem(((EntityPlayer) e).getHeldItemOffhand().getItem()) == 322) {
                gapples = ((EntityPlayer) e).getHeldItemOffhand().getCount();
            }
            if (gapples > 0) {
                NameTags.mc.getRenderItem().zLevel = -100.0f;
                mc.getRenderItem().renderItemIntoGUI(new ItemStack(Items.GOLDEN_APPLE), posX, -20);
                NameTags.mc.getRenderItem().zLevel = 0.0f;
                mc.fontRenderer.drawStringWithShadow(String.valueOf(gapples), (float) (posX + 9 - this.getcenter(String.valueOf(gapples))), -30.0f, -1);
            }

            GlStateManager.enableDepth();
            GlStateManager.enableLighting();
            GlStateManager.disableBlend();
            GlStateManager.enableLighting();
            GlStateManager.disablePolygonOffset();
            GlStateManager.doPolygonOffset(1.0f, 1500000.0f);
            GL11.glPopMatrix();
        }
    }

    public int getcenter(String text) {
        return NameTags.mc.fontRenderer.getStringWidth(text) / 2;
    }

    public int getcenter(int text) {
        return NameTags.mc.fontRenderer.getStringWidth(String.valueOf(text)) / 2;
    }

    public void drawRoundedRect(int x, int y, int right, int bottom, int color, int radius) {
        GlStateManager.pushMatrix();
        GlStateManager.tryBlendFuncSeparate(770, 771, 0, 1);
        glEnable(GL_LINE_SMOOTH);
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.depthMask(false);
        GL11.glEnable(GL_LINE_SMOOTH);
        GL11.glHint(GL_LINE_SMOOTH_HINT, GL_NICEST);
        glBegin(GL_TRIANGLE_FAN);
        drawFilledCircle(right - radius, bottom - radius, radius, color, 0, 90);
        drawFilledCircle(right - radius, y + radius, radius, color, 90, 180);
        drawFilledCircle(x + radius, y + radius, radius, color, 180, 270);
        drawFilledCircle(x + radius, bottom - radius, radius, color, 270, 360);
        glEnd();
        glDisable(GL_LINE_SMOOTH);
        GL11.glDisable(GL_LINE_SMOOTH);
        GlStateManager.depthMask(true);
        GlStateManager.enableDepth();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }
    /*
    RenderUtil.drawRect(-allWidth+2, 2, (allWidth - (allWidth * (1 - healthP)) * 2)-1, 1, Colors.RED.getRGB());
    */

    public void drawOutlineRoundedRect(int x, int y, int right, int bottom, int color, int radius, double distance, double distanceScale) {


        double divisor = MathUtil.lerp(MathHelper.clamp(distance * (sizel.getMax() + 1 - sizel.getValue()), 1, 90), MathHelper.clamp((sizel.getMax() + 1 - sizel.getValue()) * 2, 1, distance), sizel.getValue());


        RenderUtil.drawPolygonOutline(0, 90, (int) (360 / divisor), x, y, radius, 1f, color);
        RenderUtil.drawPolygonOutline(90, 180, (int) (360 / divisor), right - radius * 2, y, radius, 1f, color);
        RenderUtil.drawPolygonOutline(180, 270, (int) (360 / divisor), right - radius * 2, bottom - radius * 2, radius, 1f, color);
        RenderUtil.drawPolygonOutline(270, 360, (int) (360 / divisor), x, bottom - radius * 2, radius, 1f, color);
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.disableDepth();
        GlStateManager.tryBlendFuncSeparate(770, 771, 0, 1);
        GlStateManager.disableTexture2D();
        GlStateManager.depthMask(false);
        glEnable(GL_LINE_SMOOTH);
        glHint(GL_LINE_SMOOTH_HINT, GL_NICEST);
        glLineWidth((float) 1);

        float a = (float) (color >> 24 & 255) / 255.0F;
        float r = (float) (color >> 16 & 255) / 255.0F;
        float g = (float) (color >> 8 & 255) / 255.0F;
        float b = (float) (color & 255) / 255.0F;

        final Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(GL_LINE_STRIP, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos(x + radius, y, 0.0D).color(r, g, b, a).endVertex();
        bufferbuilder.pos(right - radius, y, 0.0D).color(r, g, b, a).endVertex();
        tessellator.draw();
        bufferbuilder.begin(GL_LINE_STRIP, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos(right, y + radius, 0.0D).color(r, g, b, a).endVertex();
        bufferbuilder.pos(right, bottom - radius, 0.0D).color(r, g, b, a).endVertex();
        tessellator.draw();
        bufferbuilder.begin(GL_LINE_STRIP, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos(x + radius, bottom, 0.0D).color(r, g, b, a).endVertex();
        bufferbuilder.pos(right - radius, bottom, 0.0D).color(r, g, b, a).endVertex();
        tessellator.draw();
        bufferbuilder.begin(GL_LINE_STRIP, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos(x, y + radius, 0.0D).color(r, g, b, a).endVertex();
        bufferbuilder.pos(x, bottom - radius, 0.0D).color(r, g, b, a).endVertex();
        tessellator.draw();
        glDisable(GL_LINE_SMOOTH);
        GlStateManager.depthMask(true);
        GlStateManager.enableDepth();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    private void renderItem(final ItemStack stack, final int x, final int y) {
        GL11.glPushMatrix();
        GL11.glDepthMask(true);
        GlStateManager.clear(256);
        GlStateManager.disableDepth();
        GlStateManager.enableDepth();
        RenderHelper.enableStandardItemLighting();
        mc.getRenderItem().zLevel = -100.0f;
        GlStateManager.scale(1.0f, 1.0f, 0.01f);
        mc.getRenderItem().renderItemAndEffectIntoGUI(stack, x, y / 2 - 12);
        mc.getRenderItem().renderItemOverlays(mc.fontRenderer, stack, x, y / 2 - 12);
        mc.getRenderItem().zLevel = 0.0f;
        GlStateManager.scale(1.0f, 1.0f, 1.0f);
        RenderHelper.disableStandardItemLighting();
        GlStateManager.enableAlpha();
        GlStateManager.disableBlend();
        GlStateManager.disableLighting();
        GlStateManager.scale(0.5, 0.5, 0.5);
        GlStateManager.disableDepth();
        GlStateManager.enableDepth();
        GlStateManager.scale(2.0f, 2.0f, 2.0f);
        GL11.glPopMatrix();
    }

    private String displayColoredUsername(String string, EntityPlayer player) {
        if (SeleneLoader.friendManager.isFriend(player)) {
            return TextFormatting.BLUE + string;
        } else if (player.isInvisible()) {
            return TextFormatting.RED + string;
        } else if (player.isSneaking() && sneak.getValue()) {
            return TextFormatting.DARK_PURPLE + string;
        }
        return string;
    }
}