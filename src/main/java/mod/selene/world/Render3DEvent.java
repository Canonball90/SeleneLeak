package mod.selene.world;

import mod.selene.loader.EventHandler;

public class Render3DEvent extends EventHandler {

    private final float partialTicks;

    public Render3DEvent(float partialTicks) {
        this.partialTicks = partialTicks;
    }

    public float getPartialTicks() {
        return partialTicks;
    }
}
