package mod.selene.api.utils.render;

import mod.selene.api.utils.interfaces.Util;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class RenderUtil2
        implements Util {
    private static final ScaledResolution resolution;
    public static Tessellator tessellator;
    public static BufferBuilder bufferBuilder;

    static {
        tessellator = Tessellator.getInstance();
        bufferBuilder = tessellator.getBuffer();
        resolution = new ScaledResolution(mc);
    }

    public static void addChainedGlowBoxVertices(double d, double d2, double d3, double d4, double d5, double d6, Color color, Color color2) {
        bufferBuilder.pos(d, d2, d3).color((float) color.getRed() / 255.0f, (float) color.getGreen() / 255.0f, (float) color.getBlue() / 255.0f, (float) color.getAlpha() / 255.0f).endVertex();
        bufferBuilder.pos(d4, d2, d3).color((float) color.getRed() / 255.0f, (float) color.getGreen() / 255.0f, (float) color.getBlue() / 255.0f, (float) color.getAlpha() / 255.0f).endVertex();
        bufferBuilder.pos(d4, d2, d6).color((float) color.getRed() / 255.0f, (float) color.getGreen() / 255.0f, (float) color.getBlue() / 255.0f, (float) color.getAlpha() / 255.0f).endVertex();
        bufferBuilder.pos(d, d2, d6).color((float) color.getRed() / 255.0f, (float) color.getGreen() / 255.0f, (float) color.getBlue() / 255.0f, (float) color.getAlpha() / 255.0f).endVertex();
        bufferBuilder.pos(d, d5, d3).color((float) color2.getRed() / 255.0f, (float) color2.getGreen() / 255.0f, (float) color2.getBlue() / 255.0f, (float) color2.getAlpha() / 255.0f).endVertex();
        bufferBuilder.pos(d, d5, d6).color((float) color2.getRed() / 255.0f, (float) color2.getGreen() / 255.0f, (float) color2.getBlue() / 255.0f, (float) color2.getAlpha() / 255.0f).endVertex();
        bufferBuilder.pos(d4, d5, d6).color((float) color2.getRed() / 255.0f, (float) color2.getGreen() / 255.0f, (float) color2.getBlue() / 255.0f, (float) color2.getAlpha() / 255.0f).endVertex();
        bufferBuilder.pos(d4, d5, d3).color((float) color2.getRed() / 255.0f, (float) color2.getGreen() / 255.0f, (float) color2.getBlue() / 255.0f, (float) color2.getAlpha() / 255.0f).endVertex();
        bufferBuilder.pos(d, d2, d3).color((float) color.getRed() / 255.0f, (float) color.getGreen() / 255.0f, (float) color.getBlue() / 255.0f, (float) color.getAlpha() / 255.0f).endVertex();
        bufferBuilder.pos(d, d5, d3).color((float) color2.getRed() / 255.0f, (float) color2.getGreen() / 255.0f, (float) color2.getBlue() / 255.0f, (float) color2.getAlpha() / 255.0f).endVertex();
        bufferBuilder.pos(d4, d5, d3).color((float) color2.getRed() / 255.0f, (float) color2.getGreen() / 255.0f, (float) color2.getBlue() / 255.0f, (float) color2.getAlpha() / 255.0f).endVertex();
        bufferBuilder.pos(d4, d2, d3).color((float) color.getRed() / 255.0f, (float) color.getGreen() / 255.0f, (float) color.getBlue() / 255.0f, (float) color.getAlpha() / 255.0f).endVertex();
        bufferBuilder.pos(d4, d2, d3).color((float) color.getRed() / 255.0f, (float) color.getGreen() / 255.0f, (float) color.getBlue() / 255.0f, (float) color.getAlpha() / 255.0f).endVertex();
        bufferBuilder.pos(d4, d5, d3).color((float) color2.getRed() / 255.0f, (float) color2.getGreen() / 255.0f, (float) color2.getBlue() / 255.0f, (float) color2.getAlpha() / 255.0f).endVertex();
        bufferBuilder.pos(d4, d5, d6).color((float) color2.getRed() / 255.0f, (float) color2.getGreen() / 255.0f, (float) color2.getBlue() / 255.0f, (float) color2.getAlpha() / 255.0f).endVertex();
        bufferBuilder.pos(d4, d2, d6).color((float) color.getRed() / 255.0f, (float) color.getGreen() / 255.0f, (float) color.getBlue() / 255.0f, (float) color.getAlpha() / 255.0f).endVertex();
        bufferBuilder.pos(d, d2, d6).color((float) color.getRed() / 255.0f, (float) color.getGreen() / 255.0f, (float) color.getBlue() / 255.0f, (float) color.getAlpha() / 255.0f).endVertex();
        bufferBuilder.pos(d4, d2, d6).color((float) color.getRed() / 255.0f, (float) color.getGreen() / 255.0f, (float) color.getBlue() / 255.0f, (float) color.getAlpha() / 255.0f).endVertex();
        bufferBuilder.pos(d4, d5, d6).color((float) color2.getRed() / 255.0f, (float) color2.getGreen() / 255.0f, (float) color2.getBlue() / 255.0f, (float) color2.getAlpha() / 255.0f).endVertex();
        bufferBuilder.pos(d, d5, d6).color((float) color2.getRed() / 255.0f, (float) color2.getGreen() / 255.0f, (float) color2.getBlue() / 255.0f, (float) color2.getAlpha() / 255.0f).endVertex();
        bufferBuilder.pos(d, d2, d3).color((float) color.getRed() / 255.0f, (float) color.getGreen() / 255.0f, (float) color.getBlue() / 255.0f, (float) color.getAlpha() / 255.0f).endVertex();
        bufferBuilder.pos(d, d2, d6).color((float) color.getRed() / 255.0f, (float) color.getGreen() / 255.0f, (float) color.getBlue() / 255.0f, (float) color.getAlpha() / 255.0f).endVertex();
        bufferBuilder.pos(d, d5, d6).color((float) color2.getRed() / 255.0f, (float) color2.getGreen() / 255.0f, (float) color2.getBlue() / 255.0f, (float) color2.getAlpha() / 255.0f).endVertex();
        bufferBuilder.pos(d, d5, d3).color((float) color2.getRed() / 255.0f, (float) color2.getGreen() / 255.0f, (float) color2.getBlue() / 255.0f, (float) color2.getAlpha() / 255.0f).endVertex();
    }

    public static void glBillboardDistanceScaled(float f, float f2, float f3, EntityPlayer entityPlayer, float f4) {
        glBillboard(f, f2, f3);
        int n = (int) entityPlayer.getDistance(f, f2, f3);
        float f5 = (float) n / 2.0f / (2.0f + (2.0f - f4));
        if (f5 < 1.0f) {
            f5 = 1.0f;
        }
        GlStateManager.scale(f5, f5, f5);
    }

    public static void addChainedFilledBoxVertices(double d, double d2, double d3, double d4, double d5, double d6, Color color) {
        bufferBuilder.pos(d, d2, d3).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        bufferBuilder.pos(d, d2, d3).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        bufferBuilder.pos(d, d2, d3).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        bufferBuilder.pos(d, d2, d6).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        bufferBuilder.pos(d, d5, d3).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        bufferBuilder.pos(d, d5, d6).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        bufferBuilder.pos(d, d5, d6).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        bufferBuilder.pos(d, d2, d6).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        bufferBuilder.pos(d4, d5, d6).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        bufferBuilder.pos(d4, d2, d6).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        bufferBuilder.pos(d4, d2, d6).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        bufferBuilder.pos(d4, d2, d3).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        bufferBuilder.pos(d4, d5, d6).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        bufferBuilder.pos(d4, d5, d3).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        bufferBuilder.pos(d4, d5, d3).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        bufferBuilder.pos(d4, d2, d3).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        bufferBuilder.pos(d, d5, d3).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        bufferBuilder.pos(d, d2, d3).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        bufferBuilder.pos(d, d2, d3).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        bufferBuilder.pos(d4, d2, d3).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        bufferBuilder.pos(d, d2, d6).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        bufferBuilder.pos(d4, d2, d6).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        bufferBuilder.pos(d4, d2, d6).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        bufferBuilder.pos(d, d5, d3).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        bufferBuilder.pos(d, d5, d3).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        bufferBuilder.pos(d, d5, d6).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        bufferBuilder.pos(d4, d5, d3).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        bufferBuilder.pos(d4, d5, d6).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        bufferBuilder.pos(d4, d5, d6).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        bufferBuilder.pos(d4, d5, d6).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
    }

    public static void drawLine3D(Vec3d vec3d, Vec3d vec3d2, Color color, double d) {
        GL11.glDepthMask(false);
        GL11.glDisable(2929);
        GL11.glDisable(3008);
        GL11.glEnable(3042);
        GL11.glDisable(3553);
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(2848);
        GL11.glHint(3154, 4354);
        GL11.glLineWidth(0.1f);
        GL11.glColor4f((float) color.getRed() / 255.0f, (float) color.getGreen() / 255.0f, (float) color.getBlue() / 255.0f, (float) color.getAlpha() / 255.0f);
        GL11.glLineWidth((float) d);
        GL11.glBegin(1);
        GL11.glVertex3d(vec3d.x, vec3d.y, vec3d.z);
        GL11.glVertex3d(vec3d2.x, vec3d2.y, vec3d2.z);
        GL11.glEnd();
        GL11.glDepthMask(true);
        GL11.glEnable(2929);
        GL11.glEnable(3553);
        GL11.glDisable(3042);
        GL11.glEnable(3008);
        GL11.glDisable(2848);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
    }

    public static void drawSelectionGlowFilledBox(AxisAlignedBB axisAlignedBB, double d, double d2, double d3, Color color, Color color2) {
        bufferBuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
        addChainedGlowBoxVertices(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ, axisAlignedBB.maxX + d2, axisAlignedBB.maxY + d, axisAlignedBB.maxZ + d3, color, color2);
        tessellator.draw();
    }

    public static void drawBorder(float f, float f2, float f3, float f4, Color color) {
        drawRect(f - 0.5f, f2 - 0.5f, 0.5f, f4 + 1.0f, color);
        drawRect(f + f3, f2 - 0.5f, 0.5f, f4 + 1.0f, color);
        drawRect(f, f2 - 0.5f, f3, 0.5f, color);
        drawRect(f, f2 + f4, f3, 0.5f, color);
    }

    public static void drawCircle(RenderBuilder renderBuilder, Vec3d vec3d, double d, double d2, Color color) {
        renderCircle(bufferBuilder, vec3d, d, d2, color);
        renderBuilder.build();
    }

    public static void drawSelectionBoundingBox(AxisAlignedBB axisAlignedBB, double d, double d2, double d3, Color color) {
        bufferBuilder.begin(3, DefaultVertexFormats.POSITION_COLOR);
        addChainedBoundingBoxVertices(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ, axisAlignedBB.maxX + d2, axisAlignedBB.maxY + d, axisAlignedBB.maxZ + d3, color);
        tessellator.draw();
    }

    public static void drawBox(RenderBuilder renderBuilder) {
        if (mc.getRenderViewEntity() != null) {
            AxisAlignedBB axisAlignedBB = renderBuilder.getAxisAlignedBB().offset(-mc.getRenderManager().viewerPosX, -mc.getRenderManager().viewerPosY, -mc.getRenderManager().viewerPosZ);
            switch (renderBuilder.getBox()) {
                case FILL: {
                    drawSelectionBox(axisAlignedBB, renderBuilder.getHeight(), renderBuilder.getLength(), renderBuilder.getWidth(), renderBuilder.getColor());
                    break;
                }
                case OUTLINE: {
                    drawSelectionBoundingBox(axisAlignedBB, renderBuilder.getHeight(), renderBuilder.getLength(), renderBuilder.getWidth(), new Color(renderBuilder.getColor().getRed(), renderBuilder.getColor().getGreen(), renderBuilder.getColor().getBlue(), 144));
                    break;
                }
                case BOTH: {
                    drawSelectionBox(axisAlignedBB, renderBuilder.getHeight(), renderBuilder.getLength(), renderBuilder.getWidth(), renderBuilder.getColor());
                    drawSelectionBoundingBox(axisAlignedBB, renderBuilder.getHeight(), renderBuilder.getLength(), renderBuilder.getWidth(), new Color(renderBuilder.getColor().getRed(), renderBuilder.getColor().getGreen(), renderBuilder.getColor().getBlue(), 144));
                    break;
                }
                case GLOW: {
                    drawSelectionGlowFilledBox(axisAlignedBB, renderBuilder.getHeight(), renderBuilder.getLength(), renderBuilder.getWidth(), renderBuilder.getColor(), new Color(renderBuilder.getColor().getRed(), renderBuilder.getColor().getGreen(), renderBuilder.getColor().getBlue(), 0));
                    break;
                }
                case REVERSE: {
                    drawSelectionGlowFilledBox(axisAlignedBB, renderBuilder.getHeight(), renderBuilder.getLength(), renderBuilder.getWidth(), new Color(renderBuilder.getColor().getRed(), renderBuilder.getColor().getGreen(), renderBuilder.getColor().getBlue(), 0), renderBuilder.getColor());
                    break;
                }
                case CLAW: {
                    drawClawBox(axisAlignedBB, renderBuilder.getHeight(), renderBuilder.getLength(), renderBuilder.getWidth(), new Color(renderBuilder.getColor().getRed(), renderBuilder.getColor().getGreen(), renderBuilder.getColor().getBlue(), 255));
                }
            }
            renderBuilder.build();
        }
    }

    public static void addChainedBoundingBoxVertices(double d, double d2, double d3, double d4, double d5, double d6, Color color) {
        bufferBuilder.pos(d, d2, d3).color(color.getRed(), color.getGreen(), color.getBlue(), 0).endVertex();
        bufferBuilder.pos(d, d2, d3).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        bufferBuilder.pos(d4, d2, d3).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        bufferBuilder.pos(d4, d2, d6).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        bufferBuilder.pos(d, d2, d6).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        bufferBuilder.pos(d, d2, d3).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        bufferBuilder.pos(d, d5, d3).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        bufferBuilder.pos(d4, d5, d3).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        bufferBuilder.pos(d4, d5, d6).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        bufferBuilder.pos(d, d5, d6).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        bufferBuilder.pos(d, d5, d3).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        bufferBuilder.pos(d, d5, d6).color(color.getRed(), color.getGreen(), color.getBlue(), 0).endVertex();
        bufferBuilder.pos(d, d2, d6).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        bufferBuilder.pos(d4, d5, d6).color(color.getRed(), color.getGreen(), color.getBlue(), 0).endVertex();
        bufferBuilder.pos(d4, d2, d6).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        bufferBuilder.pos(d4, d5, d3).color(color.getRed(), color.getGreen(), color.getBlue(), 0).endVertex();
        bufferBuilder.pos(d4, d2, d3).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        bufferBuilder.pos(d4, d2, d3).color(color.getRed(), color.getGreen(), color.getBlue(), 0).endVertex();
    }

    public static void addChainedClawBoxVertices(double d, double d2, double d3, double d4, double d5, double d6, Color color) {
        bufferBuilder.pos(d, d2, d3).color(color.getRed(), color.getGreen(), color.getBlue(), 0).endVertex();
        bufferBuilder.pos(d, d2, d6 - 0.8).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        bufferBuilder.pos(d, d2, d6).color(color.getRed(), color.getGreen(), color.getBlue(), 0).endVertex();
        bufferBuilder.pos(d, d2, d3 + 0.8).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        bufferBuilder.pos(d4, d2, d3).color(color.getRed(), color.getGreen(), color.getBlue(), 0).endVertex();
        bufferBuilder.pos(d4, d2, d6 - 0.8).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        bufferBuilder.pos(d4, d2, d6).color(color.getRed(), color.getGreen(), color.getBlue(), 0).endVertex();
        bufferBuilder.pos(d4, d2, d3 + 0.8).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        bufferBuilder.pos(d, d2, d3).color(color.getRed(), color.getGreen(), color.getBlue(), 0).endVertex();
        bufferBuilder.pos(d4 - 0.8, d2, d3).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        bufferBuilder.pos(d, d2, d6).color(color.getRed(), color.getGreen(), color.getBlue(), 0).endVertex();
        bufferBuilder.pos(d4 - 0.8, d2, d6).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        bufferBuilder.pos(d4, d2, d3).color(color.getRed(), color.getGreen(), color.getBlue(), 0).endVertex();
        bufferBuilder.pos(d + 0.8, d2, d3).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        bufferBuilder.pos(d4, d2, d6).color(color.getRed(), color.getGreen(), color.getBlue(), 0).endVertex();
        bufferBuilder.pos(d + 0.8, d2, d6).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        bufferBuilder.pos(d, d2, d3).color(color.getRed(), color.getGreen(), color.getBlue(), 0).endVertex();
        bufferBuilder.pos(d, d2 + 0.2, d3).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        bufferBuilder.pos(d, d2, d6).color(color.getRed(), color.getGreen(), color.getBlue(), 0).endVertex();
        bufferBuilder.pos(d, d2 + 0.2, d6).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        bufferBuilder.pos(d4, d2, d3).color(color.getRed(), color.getGreen(), color.getBlue(), 0).endVertex();
        bufferBuilder.pos(d4, d2 + 0.2, d3).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        bufferBuilder.pos(d4, d2, d6).color(color.getRed(), color.getGreen(), color.getBlue(), 0).endVertex();
        bufferBuilder.pos(d4, d2 + 0.2, d6).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        bufferBuilder.pos(d, d5, d3).color(color.getRed(), color.getGreen(), color.getBlue(), 0).endVertex();
        bufferBuilder.pos(d, d5, d6 - 0.8).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        bufferBuilder.pos(d, d5, d6).color(color.getRed(), color.getGreen(), color.getBlue(), 0).endVertex();
        bufferBuilder.pos(d, d5, d3 + 0.8).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        bufferBuilder.pos(d4, d5, d3).color(color.getRed(), color.getGreen(), color.getBlue(), 0).endVertex();
        bufferBuilder.pos(d4, d5, d6 - 0.8).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        bufferBuilder.pos(d4, d5, d6).color(color.getRed(), color.getGreen(), color.getBlue(), 0).endVertex();
        bufferBuilder.pos(d4, d5, d3 + 0.8).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        bufferBuilder.pos(d, d5, d3).color(color.getRed(), color.getGreen(), color.getBlue(), 0).endVertex();
        bufferBuilder.pos(d4 - 0.8, d5, d3).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        bufferBuilder.pos(d, d5, d6).color(color.getRed(), color.getGreen(), color.getBlue(), 0).endVertex();
        bufferBuilder.pos(d4 - 0.8, d5, d6).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        bufferBuilder.pos(d4, d5, d3).color(color.getRed(), color.getGreen(), color.getBlue(), 0).endVertex();
        bufferBuilder.pos(d + 0.8, d5, d3).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        bufferBuilder.pos(d4, d5, d6).color(color.getRed(), color.getGreen(), color.getBlue(), 0).endVertex();
        bufferBuilder.pos(d + 0.8, d5, d6).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        bufferBuilder.pos(d, d5, d3).color(color.getRed(), color.getGreen(), color.getBlue(), 0).endVertex();
        bufferBuilder.pos(d, d5 - 0.2, d3).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        bufferBuilder.pos(d, d5, d6).color(color.getRed(), color.getGreen(), color.getBlue(), 0).endVertex();
        bufferBuilder.pos(d, d5 - 0.2, d6).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        bufferBuilder.pos(d4, d5, d3).color(color.getRed(), color.getGreen(), color.getBlue(), 0).endVertex();
        bufferBuilder.pos(d4, d5 - 0.2, d3).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        bufferBuilder.pos(d4, d5, d6).color(color.getRed(), color.getGreen(), color.getBlue(), 0).endVertex();
        bufferBuilder.pos(d4, d5 - 0.2, d6).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
    }

    public static void drawRoundedRect(double d, double d2, double d3, double d4, double d5, Color color) {
        int n;
        GL11.glPushAttrib(0);
        GL11.glScaled(0.5, 0.5, 0.5);
        d3 *= 2.0;
        d4 *= 2.0;
        d3 += (d *= 2.0);
        d4 += (d2 *= 2.0);
        GL11.glEnable(3042);
        GL11.glDisable(3553);
        GL11.glColor4f((float) color.getRed() / 255.0f, (float) color.getGreen() / 255.0f, (float) color.getBlue() / 255.0f, (float) color.getAlpha() / 255.0f);
        GL11.glEnable(2848);
        GL11.glBegin(9);
        for (n = 0; n <= 90; ++n) {
            GL11.glVertex2d(d + d5 + Math.sin((double) n * Math.PI / 180.0) * d5 * -1.0, d2 + d5 + Math.cos((double) n * Math.PI / 180.0) * d5 * -1.0);
        }
        for (n = 90; n <= 180; ++n) {
            GL11.glVertex2d(d + d5 + Math.sin((double) n * Math.PI / 180.0) * d5 * -1.0, d4 - d5 + Math.cos((double) n * Math.PI / 180.0) * d5 * -1.0);
        }
        for (n = 0; n <= 90; ++n) {
            GL11.glVertex2d(d3 - d5 + Math.sin((double) n * Math.PI / 180.0) * d5, d4 - d5 + Math.cos((double) n * Math.PI / 180.0) * d5);
        }
        for (n = 90; n <= 180; ++n) {
            GL11.glVertex2d(d3 - d5 + Math.sin((double) n * Math.PI / 180.0) * d5, d2 + d5 + Math.cos((double) n * Math.PI / 180.0) * d5);
        }
        GL11.glEnd();
        GL11.glEnable(3553);
        GL11.glDisable(3042);
        GL11.glDisable(2848);
        GL11.glDisable(3042);
        GL11.glEnable(3553);
        GL11.glScaled(2.0, 2.0, 2.0);
        GL11.glPopAttrib();
    }

    public static void drawClawBox(AxisAlignedBB axisAlignedBB, double d, double d2, double d3, Color color) {
        bufferBuilder.begin(3, DefaultVertexFormats.POSITION_COLOR);
        addChainedClawBoxVertices(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ, axisAlignedBB.maxX + d2, axisAlignedBB.maxY + d, axisAlignedBB.maxZ + d3, color);
        tessellator.draw();
    }

    public static void drawRect(float f, float f2, float f3, float f4, int n) {
        Color color = new Color(n, true);
        GL11.glPushMatrix();
        GL11.glDisable(3553);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glShadeModel(7425);
        GL11.glBegin(7);
        GL11.glColor4f((float) color.getRed() / 255.0f, (float) color.getGreen() / 255.0f, (float) color.getBlue() / 255.0f, (float) color.getAlpha() / 255.0f);
        GL11.glVertex2f(f, f2);
        GL11.glVertex2f(f, f2 + f4);
        GL11.glVertex2f(f + f3, f2 + f4);
        GL11.glVertex2f(f + f3, f2);
        GL11.glColor4f(0.0f, 0.0f, 0.0f, 1.0f);
        GL11.glEnd();
        GL11.glEnable(3553);
        GL11.glDisable(3042);
        GL11.glPopMatrix();
    }

    public static void drawRect(float f, float f2, float f3, float f4, Color color) {
        GL11.glPushMatrix();
        GL11.glDisable(3553);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glShadeModel(7425);
        GL11.glBegin(7);
        GL11.glColor4f((float) color.getRed() / 255.0f, (float) color.getGreen() / 255.0f, (float) color.getBlue() / 255.0f, (float) color.getAlpha() / 255.0f);
        GL11.glVertex2f(f, f2);
        GL11.glVertex2f(f, f2 + f4);
        GL11.glVertex2f(f + f3, f2 + f4);
        GL11.glVertex2f(f + f3, f2);
        GL11.glColor4f(0.0f, 0.0f, 0.0f, 1.0f);
        GL11.glEnd();
        GL11.glEnable(3553);
        GL11.glDisable(3042);
        GL11.glPopMatrix();
    }

    public static double getDisplayWidth() {
        return resolution.getScaledWidth_double();
    }

    public static double getDisplayHeight() {
        return resolution.getScaledHeight_double();
    }

    public static void drawSelectionBox(AxisAlignedBB axisAlignedBB, double d, double d2, double d3, Color color) {
        bufferBuilder.begin(5, DefaultVertexFormats.POSITION_COLOR);
        addChainedFilledBoxVertices(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ, axisAlignedBB.maxX + d2, axisAlignedBB.maxY + d, axisAlignedBB.maxZ + d3, color);
        tessellator.draw();
    }

    public static void glBillboard(float f, float f2, float f3) {
        float f4 = 0.02666667f;
        GlStateManager.translate((double) f - mc.getRenderManager().viewerPosX, (double) f2 - mc.getRenderManager().viewerPosY, (double) f3 - mc.getRenderManager().viewerPosZ);
        GlStateManager.glNormal3f(0.0f, 1.0f, 0.0f);
        GlStateManager.rotate(-mc.player.rotationYaw, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotate(mc.player.rotationPitch, mc.gameSettings.thirdPersonView == 2 ? -1.0f : 1.0f, 0.0f, 0.0f);
        GlStateManager.scale(-f4, -f4, f4);
    }

    public static void drawPolygon(double d, double d2, float f, int n, Color color) {
        GL11.glEnable(3042);
        GL11.glDisable(3553);
        GL11.glBlendFunc(770, 771);
        GL11.glColor4f((float) color.getRed() / 255.0f, (float) color.getGreen() / 255.0f, (float) color.getBlue() / 255.0f, (float) color.getAlpha() / 255.0f);
        bufferBuilder.begin(6, DefaultVertexFormats.POSITION);
        bufferBuilder.pos(d, d2, 0.0).endVertex();
        double d3 = Math.PI * 2;
        for (int i = 0; i <= n; ++i) {
            double d4 = d3 * (double) i / (double) n + Math.toRadians(180.0);
            bufferBuilder.pos(d + Math.sin(d4) * (double) f, d2 + Math.cos(d4) * (double) f, 0.0).endVertex();
        }
        tessellator.draw();
        GL11.glEnable(3553);
        GL11.glDisable(3042);
    }

    public static void renderCircle(BufferBuilder bufferBuilder, Vec3d vec3d, double d, double d2, Color color) {
        GlStateManager.disableCull();
        GlStateManager.disableAlpha();
        GlStateManager.shadeModel(7425);
        bufferBuilder.begin(3, DefaultVertexFormats.POSITION_COLOR);
        for (int i = 0; i < 361; ++i) {
            bufferBuilder.pos(vec3d.x + Math.sin(Math.toRadians(i)) * d - mc.getRenderManager().viewerPosX, vec3d.y + d2 - mc.getRenderManager().viewerPosY, vec3d.z + Math.cos(Math.toRadians(i)) * d - mc.getRenderManager().viewerPosZ).color((float) color.getRed() / 255.0f, (float) color.getGreen() / 255.0f, (float) color.getBlue() / 255.0f, 1.0f).endVertex();
        }
        tessellator.draw();
        GlStateManager.enableCull();
        GlStateManager.enableAlpha();
        GlStateManager.shadeModel(7424);
    }
}