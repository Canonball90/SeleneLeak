package mod.selene.impl.modules.client;

import mod.selene.impl.Module;
import mod.selene.system.Setting;

import java.awt.*;

public class Colors extends Module {
    public static Colors INSTANCE = new Colors();

    private final Setting<pages> pagesC = register(new Setting<>("Settings", pages.Color));

    public Setting<Float> speed = register(new Setting("Speed", 1.0f, 0.1f, 5.0f, v -> pagesC.getValue() == pages.Gradient));
    public Setting<Float> step = register(new Setting("Step", 1.0f, 0.1f, 2.0f, v -> pagesC.getValue() == pages.Gradient));
    public Setting<Integer> opacity = register(new Setting("Opacity", 100, 0, 255, v -> pagesC.getValue() == pages.Gradient));

    public Setting<Color> normalColor = register(new Setting("Color", new Color(255, 0, 0), v -> pagesC.getValue() == pages.Color));
    public Setting<Integer> gradientRed1 = register(new Setting("Red1", 68, 0, 255, v -> pagesC.getValue() == pages.Gradient));
    public Setting<Integer> gradientGreen1 = register(new Setting("Green1", 0, 0, 255, v -> pagesC.getValue() == pages.Gradient));
    public Setting<Integer> gradientBlue1 = register(new Setting("Blue1", 152, 0, 255, v -> pagesC.getValue() == pages.Gradient));

    public Setting<Integer> gradientRed2 = register(new Setting("Red2", 68, 0, 255, v -> pagesC.getValue() == pages.Gradient));
    public Setting<Integer> gradientGreen2 = register(new Setting("Green2", 255, 0, 255, v -> pagesC.getValue() == pages.Gradient));
    public Setting<Integer> gradientBlue2 = register(new Setting("Blue2", 152, 0, 255, v -> pagesC.getValue() == pages.Gradient));

    public Colors() {
        super("Colors", "", Category.CLIENT, true, false, false);
    }


    public Color getColor() {
        return new Color(normalColor.getValue().getRed(), normalColor.getValue().getGreen(), normalColor.getValue().getBlue());
    }

    public Color getColorsAlpha() {
        return new Color(normalColor.getValue().getRed(), normalColor.getValue().getGreen(), normalColor.getValue().getBlue(), normalColor.getValue().getAlpha());
    }


    public Color[] getGradient() {
        return new Color[]{
                new Color(gradientRed1.getValue(), gradientGreen1.getValue(), gradientBlue1.getValue()),
                new Color(gradientRed2.getValue(), gradientGreen2.getValue(), gradientBlue2.getValue())
        };
    }

    public enum pages {
        Color,
        Gradient
    }
}
