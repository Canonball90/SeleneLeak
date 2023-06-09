package mod.selene.shader.impl;


import mod.selene.api.utils.interfaces.MC;
import mod.selene.api.utils.render.RenderUtil;
import mod.selene.impl.modules.client.Colors;
import mod.selene.shader.ShaderUtil;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.shader.Framebuffer;

import java.awt.*;

import static org.lwjgl.opengl.GL11.*;

public class GradientShader implements MC {
    private final static ShaderUtil shader = new ShaderUtil("textures/shaders/gradient.frag");
    private static Framebuffer framebuffer = new Framebuffer(1, 1, false);

    public static void setupUniforms(final float step, final float speed, final Color color, final Color color2, final float opacity) {
        shader.setUniformi("texture", 0);
        shader.setUniformf("rgb", color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f);
        shader.setUniformf("rgb1", color2.getRed() / 255.0f, color2.getGreen() / 255.0f, color2.getBlue() / 255.0f);
        shader.setUniformf("step", 300 * step);
        shader.setUniformf("offset", (float) ((((double) System.currentTimeMillis() * (double) speed) % (mc.displayWidth * mc.displayHeight)) / 10.0f));
        shader.setUniformf("mix", opacity);
    }

    public static void setup() {
        setup(Colors.INSTANCE.step.getValue(), Colors.INSTANCE.speed.getValue(), Colors.INSTANCE.getGradient()[0], Colors.INSTANCE.getGradient()[1]);
    }

    public static void setup(final float opacity) {
        setup(Colors.INSTANCE.step.getValue(), Colors.INSTANCE.speed.getValue(), Colors.INSTANCE.getGradient()[0], Colors.INSTANCE.getGradient()[1], opacity);
    }

    public static void setup(final float step, final float speed, final Color color, final Color color2) {
        setup(step, speed, color, color2, 1.0f);
    }

    public static void setup(final float step, final float speed, final Color color, final Color color2, final float opacity) {
        GlStateManager.enableBlend();
        GlStateManager.color(1, 1, 1, 1);
        GlStateManager.tryBlendFuncSeparate(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ONE, GL_ZERO);

        framebuffer = RenderUtil.createFrameBuffer(framebuffer);

        mc.getFramebuffer().bindFramebuffer(true);
        shader.init();
        setupUniforms(step, speed, color, color2, opacity);

        glBindTexture(GL_TEXTURE_2D, framebuffer.framebufferTexture);
    }

    public static void finish() {
        shader.unload();
        GlStateManager.color(1, 1, 1, 1);
        GlStateManager.bindTexture(0);
        glEnable(GL_BLEND);
    }
}
