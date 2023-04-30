package mod.selene.world;

import mod.selene.loader.EventHandler;
import net.minecraft.client.gui.ScaledResolution;

public class Render2DEvent extends EventHandler {

    public float partialTicks;
    public ScaledResolution scaledResolution;

    public Render2DEvent(float partialTicks, ScaledResolution scaledResolution) {
        super();
        this.partialTicks = partialTicks;
        this.scaledResolution = scaledResolution;
    }

    public void setPartialTicks(float partialTicks) {
        this.partialTicks = partialTicks;
    }

    public void setScaledResolution(ScaledResolution scaledResolution) {
        this.scaledResolution = scaledResolution;
    }

    public double getScreenWidth() {
        return scaledResolution.getScaledWidth_double();
    }

    public double getScreenHeight() {
        return scaledResolution.getScaledHeight_double();
    }
}
