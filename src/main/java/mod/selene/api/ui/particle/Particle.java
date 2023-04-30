package mod.selene.api.ui.particle;

import mod.selene.api.utils.ColorUtil;
import mod.selene.api.utils.render.RenderUtil;
import mod.selene.impl.modules.client.Particles;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.input.Mouse;

import javax.vecmath.Vector2f;
import java.util.concurrent.ThreadLocalRandom;

public class Particle {
    private final int maxAlpha;
    private Vector2f pos;
    private Vector2f velocity;
    private Vector2f acceleration;
    private int alpha;
    private float size;

    public Particle(Vector2f pos) {
        this.pos = pos;
        int lowVel = -1;
        boolean highVel = true;
        float resultXVel = -1.0f + ThreadLocalRandom.current().nextFloat() * 2.0f;
        float resultYVel = -1.0f + ThreadLocalRandom.current().nextFloat() * 2.0f;
        this.velocity = new Vector2f(resultXVel, resultYVel);
        this.acceleration = new Vector2f(0.0f, 0.35f);
        this.alpha = 0;
        this.maxAlpha = ThreadLocalRandom.current().nextInt(32, 192);
        this.size = 0.5f + ThreadLocalRandom.current().nextFloat() * 1.5f;
    }

    public static int changeAlpha(int origColor, int userInputedAlpha) {
        return userInputedAlpha << 24 | (origColor &= 0xFFFFFF);
    }

    public void respawn(ScaledResolution scaledResolution) {
        this.pos = new Vector2f((float) (Math.random() * (double) scaledResolution.getScaledWidth()), (float) (Math.random() * (double) scaledResolution.getScaledHeight()));
    }

    public void update() {
        if (this.alpha < this.maxAlpha) {
            this.alpha += 8;
        }
        if (this.acceleration.getX() > 0.35f) {
            this.acceleration.setX(this.acceleration.getX() * 0.975f);
        } else if (this.acceleration.getX() < -0.35f) {
            this.acceleration.setX(this.acceleration.getX() * 0.975f);
        }
        if (this.acceleration.getY() > 0.35f) {
            this.acceleration.setY(this.acceleration.getY() * 0.975f);
        } else if (this.acceleration.getY() < -0.35f) {
            this.acceleration.setY(this.acceleration.getY() * 0.975f);
        }
        this.pos.add(this.acceleration);
        this.pos.add(this.velocity);
    }

    public void render(int mouseX, int mouseY) {
        if (Mouse.isButtonDown(0)) {
            float deltaXToMouse = (float) mouseX - this.pos.getX();
            float deltaYToMouse = (float) mouseY - this.pos.getY();
            if (Math.abs(deltaXToMouse) < 50.0f && Math.abs(deltaYToMouse) < 50.0f) {
                this.acceleration.setX(this.acceleration.getX() + deltaXToMouse * 0.0015f);
                this.acceleration.setY(this.acceleration.getY() + deltaYToMouse * 0.0015f);
            }
        }
        int rainbow = Particles.getInstance().getRainbow();
        RenderUtil.drawRect(this.pos.x, this.pos.y, this.pos.x + this.size, this.pos.y + this.size, Particles.getInstance().particleRainbow.getValue() ? Particle.changeAlpha(ColorUtil.toRGBA(rainbow, rainbow, rainbow), alpha) : Particle.changeAlpha(ColorUtil.toRGBA(Particles.getInstance().particleC.getValue().getRed(), Particles.getInstance().particleC.getValue().getGreen(), Particles.getInstance().particleC.getValue().getBlue()), this.alpha));
    }

    public Vector2f getPos() {
        return this.pos;
    }

    public void setPos(Vector2f pos) {
        this.pos = pos;
    }

    public Vector2f getVelocity() {
        return this.velocity;
    }

    public void setVelocity(Vector2f velocity) {
        this.velocity = velocity;
    }

    public Vector2f getAcceleration() {
        return this.acceleration;
    }

    public void setAcceleration(Vector2f acceleration) {
        this.acceleration = acceleration;
    }

    public int getAlpha() {
        return this.alpha;
    }

    public void setAlpha(int alpha) {
        this.alpha = alpha;
    }

    public float getSize() {
        return this.size;
    }

    public void setSize(float size) {
        this.size = size;
    }
}
