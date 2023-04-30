package mod.selene.impl.modules.render;

import mod.selene.impl.Module;
import mod.selene.system.Setting;

import java.awt.*;

public class Ambience
        extends Module {
    public final Setting<Color> color = this.register(new Setting<Color>("Color", new Color(40, 255, 83, 150)));

    public Ambience() {
        super("Ambience", "Allows you to change the ambience of your world", Module.Category.RENDER, true, false, false);
    }
}

