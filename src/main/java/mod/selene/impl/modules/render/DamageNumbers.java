package mod.selene.impl.modules.render;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.ibm.icu.math.BigDecimal;
import mod.selene.api.utils.Timer;
import mod.selene.impl.Module;
import mod.selene.system.Setting;
import mod.selene.world.Render3DEvent;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.List;
import java.util.Map;

public class DamageNumbers
        extends Module {
    private final Map<Integer, Float> hpData = Maps.newHashMap();
    private final List<Particle> particles = Lists.newArrayList();
    private final Timer timer = new Timer();
    public Setting<Integer> deleteAfter = register(new Setting<Integer>("Remove Ticks", 7, 1, 60));

    public DamageNumbers() {
        super("DmgNumbers", "show damage", Module.Category.RENDER, true, false, false);
    }

    @Override
    public void onRender3D(Render3DEvent render3DEvent) {
        if (!particles.isEmpty()) {
            for (Particle particle : particles) {
                if (particle == null || particle.ticks > deleteAfter.getValue()) continue;
                GlStateManager.pushMatrix();
                GlStateManager.disableDepth();
                Tessellator tessellator = Tessellator.getInstance();
                BufferBuilder bufferBuilder = tessellator.getBuffer();
                bufferBuilder.begin(3, DefaultVertexFormats.POSITION_COLOR);
                tessellator.draw();
                GL11.glDisable(2848);
                GlStateManager.depthMask(true);
                GlStateManager.enableDepth();
                GlStateManager.enableTexture2D();
                GlStateManager.disableBlend();
                GlStateManager.popMatrix();
                GlStateManager.pushMatrix();
                GlStateManager.enablePolygonOffset();
                GlStateManager.doPolygonOffset(1.0f, -1500000.0f);
                GlStateManager.translate(particle.posX - mc.getRenderManager().renderPosX, particle.posY - mc.getRenderManager().renderPosY, particle.posZ - mc.getRenderManager().renderPosZ);
                GlStateManager.rotate(-mc.getRenderManager().playerViewY, 0.0f, 1.0f, 0.0f);
                GlStateManager.rotate(mc.getRenderManager().playerViewX, mc.gameSettings.thirdPersonView == 2 ? -1.0f : 1.0f, 0.0f, 0.0f);
                GlStateManager.scale(-0.03, -0.03, 0.03);
                GL11.glDepthMask(false);
                mc.fontRenderer.drawStringWithShadow(particle.str, (float) ((double) (-mc.fontRenderer.getStringWidth(particle.str)) * 0.5), (float) (-mc.fontRenderer.FONT_HEIGHT + 1), particle.color.getRGB());
                GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
                GL11.glDepthMask(true);
                GlStateManager.doPolygonOffset(1.0f, 1500000.0f);
                GlStateManager.disablePolygonOffset();
                GlStateManager.resetColor();
                GlStateManager.popMatrix();
            }
        }
    }

    @Override
    public void onDisable() {
        particles.clear();
    }

    @Override
    public void onUpdate() {
        if (timer.passedMs(12000L)) {
            particles.clear();
            timer.reset();
        }
        if (!particles.isEmpty()) {
            for (Particle particle : particles) {
                if (particle == null) continue;
                ++particle.ticks;
            }
        }
        for (Entity particle : mc.world.loadedEntityList) {
            if (!(particle instanceof EntityLivingBase)) continue;
            EntityLivingBase entityLivingBase = (EntityLivingBase) particle;
            double d = hpData.getOrDefault(entityLivingBase.getEntityId(), Float.valueOf(entityLivingBase.getMaxHealth())).floatValue();
            hpData.remove(particle.getEntityId());
            hpData.put(particle.getEntityId(), Float.valueOf(entityLivingBase.getHealth()));
            if (d == (double) entityLivingBase.getHealth()) continue;
            Color color = Color.YELLOW;
            Vec3d vec3d = new Vec3d(particle.posX + Math.random() * 0.5 * (double) (Math.random() > 0.5 ? -1 : 1), particle.getEntityBoundingBox().minY + (particle.getEntityBoundingBox().maxY - particle.getEntityBoundingBox().minY) * 0.5, particle.posZ + Math.random() * 0.5 * (double) (Math.random() > 0.5 ? -1 : 1));
            double d2 = new BigDecimal(Math.abs(d - (double) entityLivingBase.getHealth())).setScale(1, 4).doubleValue();
            particles.add(new Particle("" + d2, vec3d.x, vec3d.y, vec3d.z, color));
        }
    }

    static class Particle {
        public double posY;
        public Color color;
        public String str;
        public int ticks;
        public double posZ;
        public double posX;

        public Particle(String string, double d, double d2, double d3, Color color2) {
            str = string;
            posX = d;
            posY = d2;
            posZ = d3;
            color = color2;
            ticks = 0;
        }
    }
}