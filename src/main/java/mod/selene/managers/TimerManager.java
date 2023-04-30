package mod.selene.managers;

import mod.selene.loader.Feature;

public class TimerManager
        extends Feature {
    private float timer = 1.0f;

    public void unload() {
        this.timer = 1.0f;
        mc.timer.tickLength = 50.0f;
    }

    @Override
    public void reset() {
        this.timer = 1.0f;
    }

    public float getTimer() {
        return this.timer;
    }

    public void setTimer(float f) {
        if (f > 0.0f) {
            this.timer = f;
        }
    }

    public void update() {
        TimerManager.mc.timer.tickLength = 50.0f / (this.timer <= 0.0f ? 0.1f : this.timer);
    }
}