package mod.selene.api.ui.particle;

import mod.selene.api.utils.ColorUtil;
import mod.selene.api.utils.render.RenderUtil;
import mod.selene.impl.modules.client.Particles;
import mod.selene.loader.SeleneLoader;
import net.minecraft.client.gui.ScaledResolution;

import javax.vecmath.Vector2f;

public final class ParticleSystem {
    private final int PARTS = 200;
    private final Particle[] particles = new Particle[200];
    private ScaledResolution scaledResolution;

    public ParticleSystem(ScaledResolution scaledResolution) {
        this.scaledResolution = scaledResolution;
        for (int i = 0; i < 200; ++i) {
            this.particles[i] = new Particle(new Vector2f((float) (Math.random() * (double) scaledResolution.getScaledWidth()), (float) (Math.random() * (double) scaledResolution.getScaledHeight())));
        }
    }

    public static double map(double value, double a, double b, double c, double d) {
        value = (value - a) / (b - a);
        return c + value * (d - c);
    }

    public void update() {
        for (int i = 0; i < 200; ++i) {
            Particle particle = this.particles[i];
            if (this.scaledResolution != null) {
                boolean isOffScreenY;
                boolean isOffScreenX = particle.getPos().x > (float) this.scaledResolution.getScaledWidth() || particle.getPos().x < 0.0f;
                boolean bl = isOffScreenY = particle.getPos().y > (float) this.scaledResolution.getScaledHeight() || particle.getPos().y < 0.0f;
                if (isOffScreenX || isOffScreenY) {
                    particle.respawn(this.scaledResolution);
                }
            }
            particle.update();
        }
    }

    public void render(int mouseX, int mouseY) {
        if (!SeleneLoader.moduleManager.isModuleEnabled(Particles.class)) {
            return;
        }
        for (int i = 0; i < 200; ++i) {
            Particle particle = this.particles[i];
            for (int j = 1; j < 200; ++j) {
                int lineAlpha;
                if (i == j) continue;
                Particle otherParticle = this.particles[j];
                Vector2f diffPos = new Vector2f(particle.getPos());
                diffPos.sub(otherParticle.getPos());
                float diff = diffPos.length();
                int distance = Particles.getInstance().particleLength.getValue() / (this.scaledResolution.getScaleFactor() <= 1 ? 3 : this.scaledResolution.getScaleFactor());
                if (!(diff < (float) distance) || (lineAlpha = (int) ParticleSystem.map(diff, distance, 0.0, 0.0, 127.0)) <= 8)
                    continue;
                int rainbow = Particles.getInstance().getRainbow();
                RenderUtil.drawLine(particle.getPos().x + particle.getSize() / 2.0f, particle.getPos().y + particle.getSize() / 2.0f, otherParticle.getPos().x + otherParticle.getSize() / 2.0f, otherParticle.getPos().y + otherParticle.getSize() / 2.0f, 1.0f, Particles.getInstance().particleRainbow.getValue() ? Particle.changeAlpha(ColorUtil.toRGBA(rainbow, rainbow, rainbow), lineAlpha) : Particle.changeAlpha(ColorUtil.toRGBA(Particles.getInstance().particleC.getValue().getRed(), Particles.getInstance().particleC.getValue().getGreen(), Particles.getInstance().particleC.getValue().getBlue()), lineAlpha));
            }
            particle.render(mouseX, mouseY);
        }
    }

    public ScaledResolution getScaledResolution() {
        return this.scaledResolution;
    }

    public void setScaledResolution(ScaledResolution scaledResolution) {
        this.scaledResolution = scaledResolution;
    }
}
