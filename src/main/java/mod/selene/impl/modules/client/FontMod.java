package mod.selene.impl.modules.client;

import mod.selene.api.utils.TextUtil;
import mod.selene.impl.Module;
import mod.selene.loader.SeleneLoader;
import mod.selene.system.Command;
import mod.selene.system.Setting;
import mod.selene.world.ClientEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.*;

public class FontMod extends Module {

    private static FontMod INSTANCE = new FontMod();

    private final Setting<pages> pagesF = register(new Setting<>("Settings", pages.Font));
    public Setting<String> fontName = register(new Setting("FontName", "Verdana", v -> pagesF.getValue() == pages.Font));
    public Setting<Integer> fontSize = register(new Setting("FontSize", 14, v -> pagesF.getValue() == pages.Size));
    public Setting<Integer> fontStyle = register(new Setting("FontStyle", 1, v -> pagesF.getValue() == pages.Size));
    public Setting<Boolean> antiAlias = register(new Setting("AntiAlias", true, v -> pagesF.getValue() == pages.Misc));
    public Setting<Boolean> fractionalMetrics = register(new Setting("Metrics", true, v -> pagesF.getValue() == pages.Misc));
    public Setting<Boolean> shadow = register(new Setting("Shadow", true, v -> pagesF.getValue() == pages.Misc));
    public Setting<Boolean> showFonts = register(new Setting("AvailableFonts", false, v -> pagesF.getValue() == pages.Font));

    public Setting<Boolean> fontColor = register(new Setting<Boolean>("FontColor", true, v -> pagesF.getValue() == pages.Color));
    public Setting<Color> tEnableColor = register(new Setting<Color>("Enabled", new Color(248, 82, 255, 255), v -> fontColor.getValue() && pagesF.getValue() == pages.Color));
    public Setting<Color> tDisabledColor = register(new Setting<Color>("Disabled", new Color(255, 255, 255, 255), v -> fontColor.getValue() && pagesF.getValue() == pages.Color));
    private boolean reloadFont = false;

    public FontMod() {
        super("Font", "CustomFont for all of the clients text. Use the font command.", Category.CLIENT, true, false, false);
        FontMod.INSTANCE = this;
    }

    public static FontMod getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new FontMod();
        }
        return INSTANCE;
    }

    public static boolean checkFont(String font, boolean message) {
        String[] fonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
        for (String s : fonts) {
            if (!message && s.equals(font)) {
                return true;
            } else if (message) {
                Command.sendMessage(s);
            }
        }
        return false;
    }
    public Color getTRed() {
        return new Color(this.tEnableColor.getValue().getRed());
    }

    public Color getTGreen() {
        return new Color(tEnableColor.getValue().getGreen());
    }

    public Color getTBlue() {
        return new Color(tEnableColor.getValue().getBlue());
    }


    private void setInstance() {
        INSTANCE = this;
    }

    @SubscribeEvent
    public void onSettingChange(ClientEvent event) {
        if (event.getStage() == 2) {
            Setting setting = event.getSetting();
            if (setting != null && setting.getFeature().equals(this)) {
                if (setting.getName().equals("FontName")) {
                    if (!checkFont(setting.getPlannedValue().toString(), false)) {
                        Command.sendMessage(TextUtil.RED + "That font doesnt exist.");
                        event.setCanceled(true);
                        return;
                    }
                }
                reloadFont = true;
            }
        }
    }

    @Override
    public void onRenderTick() {

    }

    @Override
    public void onTick() {
        if (showFonts.getValue()) {
            checkFont("Hello", true);
            Command.sendMessage("Current Font: " + fontName.getValue());
            showFonts.setValue(false);
        }

        if (reloadFont) {
            SeleneLoader.textManager.init(false);
            reloadFont = false;
        }
    }

    public enum pages {
        Font,
        Size,
        Misc,
        Color
    }
}
