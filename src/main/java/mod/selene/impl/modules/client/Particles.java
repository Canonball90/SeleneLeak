package mod.selene.impl.modules.client;

import mod.selene.impl.Module;
import mod.selene.system.Setting;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class Particles extends Module {

    public static float hue;
    private static Particles INSTANCE = new Particles();
    public Setting<Color> particleC = register(new Setting<Color>("ParticlesColor", new Color(248, 82, 255, 255)));
    public Setting<Integer> particleLength = register(new Setting<Integer>("ParticleLength", 0, 0, 300));
    public Setting<Boolean> particleRainbow = new Setting<Boolean>("ParticleRainbow", false);
    private final Setting<Float> particleRainbowSpeed = new Setting<>("ParticleRainbowSpeed", 1f, 0f, 100f, v -> particleRainbow.getValue());
    public Map<Integer, Integer> colorHeightMap = new HashMap<Integer, Integer>();

    public Particles() {
        super("Particles", "Particles for ui", Category.CLIENT, true, false, false);
        setInstance();
        this.enable();
    }

    public static Particles getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Particles();
        }
        return INSTANCE;
    }

    public static int getRainbow() {
        return Color.HSBtoRGB(hue, 150 / 255.0F, 150 / 255.0F);
    }

    private void setInstance() {
        INSTANCE = this;
    }

    @Override
    public void onUpdate() {
        int colorSpeed = (int) (101 - this.particleRainbowSpeed.getValue());
        hue = (float) (System.currentTimeMillis() % (360L * colorSpeed)) / (360.0f * (float) colorSpeed);
        for (int i = 0; i <= 510; ++i) {
            this.colorHeightMap.put(i, Color.HSBtoRGB(hue, (float) 150 / 255.0f, (float) 150 / 255.0f));
            hue += 0.0013071896f;
        }
    }
}
