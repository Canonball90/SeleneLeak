package mod.selene.loader;

import net.minecraftforge.fml.common.eventhandler.Event;

public class EventHandler extends Event {

    private int stage;

    public EventHandler() {
    }

    public EventHandler(int stage) {
        this.stage = stage;
    }

    public int getStage() {
        return stage;
    }

    public void setStage(int stage) {
        this.stage = stage;
    }
}
