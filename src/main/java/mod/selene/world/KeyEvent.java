package mod.selene.world;

import mod.selene.loader.EventHandler;

public class KeyEvent extends EventHandler {

    public boolean info;
    public boolean pressed;

    public KeyEvent(int stage, boolean info, boolean pressed) {
        super(stage);
        this.info = info;
        this.pressed = pressed;
    }
}
