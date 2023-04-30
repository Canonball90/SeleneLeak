package mod.selene.impl.modules.client;

import mod.selene.api.ui.ClickGUI;
import mod.selene.api.utils.TextUtil;
import mod.selene.api.utils.interfaces.Util;
import mod.selene.impl.Module;
import mod.selene.impl.modules.combat.AutoCrystal;
import mod.selene.loader.SeleneLoader;
import mod.selene.system.Command;
import mod.selene.system.Setting;
import mod.selene.world.ClientEvent;
import net.minecraft.client.settings.GameSettings;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.*;

public class ClickGui extends Module {

    private static ClickGui INSTANCE = new ClickGui();

    private final Setting<pages> pagesSetting = register(new Setting<>("Settings", pages.Main));
    public Setting<String> prefix = register(new Setting("Prefix", ".", v -> pagesSetting.getValue() == pages.Main));
    public Setting<Integer> red = new Setting("Red", 255, 0, 255, v -> pagesSetting.getValue() == pages.Main);
    public Setting<Integer> green = new Setting("Green", 0, 0, 255, v -> pagesSetting.getValue() == pages.Main);
    public Setting<Integer> blue = new Setting("Blue", 0, 0, 255, v -> pagesSetting.getValue() == pages.Main);
    public Setting<Boolean> rainbowRolling = new Setting<>("RollingRainbow", false);
    public Setting<Boolean> rainbow = new Setting<Boolean>("Rainbow", Boolean.valueOf(false), "Makes things rainbow.");
    public Setting<Integer> rainbowHue = new Setting<Integer>("Delay", 240, 0, 600, v -> this.rainbow.getValue(), "Sets the delay of the rainbow.");
    public Setting<Float> rainbowSaturation = new Setting<>("Saturation", Float.valueOf(150.0f), Float.valueOf(1.0f), Float.valueOf(255.0f), v -> this.rainbow.getValue(), "Sets the saturation of the rainbow.");
    public Setting<Float> rainbowBrightness = new Setting<>("Brightness ", Float.valueOf(150.0f), Float.valueOf(1.0f), Float.valueOf(255.0f), v -> this.rainbow.getValue(), "Sets the brightness of the rainbow");
    public Setting<Integer> hoverAlpha = (new Setting("Alpha", 180, 0, 255, v -> pagesSetting.getValue() == pages.Main));
    public Setting<Integer> alpha = (new Setting("Module Alpha", 240, 0, 255, v -> pagesSetting.getValue() == pages.Module));
    public Setting<Boolean> customFov = register(new Setting("CustomFov", false, v -> pagesSetting.getValue() == pages.Misc));
    public Setting<Float> fov = register(new Setting("Fov", 150.0f, -180.0f, 180.0f, v -> customFov.getValue()));
    public Setting<String> moduleButton = register(new Setting("Buttons", "", v -> pagesSetting.getValue() == pages.Misc));
    public Setting<Boolean> devSettings = register(new Setting("Category", true, v -> pagesSetting.getValue() == pages.Category));
    public Setting<Boolean> categoryTextCenter = (new Setting<Boolean>("TextCenter", true, v -> pagesSetting.getValue() == pages.Category));
    public Setting<Color> topColor = register(new Setting<Color>("CategoryColor", new Color(248, 82, 255, 255), v -> pagesSetting.getValue() == pages.Category));
    public Setting<Boolean> gradiant = this.register(new Setting<Boolean>("Gradiant", true, v -> pagesSetting.getValue() == pages.Background));
    public Setting<Color> gradiantColor = register(new Setting<Color>("GradiantColor", new Color(248, 82, 255, 200), v -> gradiant.getValue() && pagesSetting.getValue() == pages.Background));
    public Setting<Boolean> moduleColors = register(new Setting<Boolean>("ModuleMainColors", true, v -> pagesSetting.getValue() == pages.Module));
    public Setting<Color> moduleColor = register(new Setting<Color>("ModuleMainColor", new Color(114, 0, 0, 10), v -> moduleColors.getValue() && pagesSetting.getValue() == pages.Module));
    public Setting<Boolean> buttonTextCenter = (new Setting<Boolean>("ButtonTextCenter", false)); //not working. prob fix
    public Setting<Boolean> moduleMainE = register(new Setting<Boolean>("EnableColor", true, v -> pagesSetting.getValue() == pages.Module));
    public Setting<Color> moduleMainC = register(new Setting<Color>("EnableColors", new Color(23, 23, 23, 10), v -> moduleMainE.getValue() && pagesSetting.getValue() == pages.Module));
    public Setting<Boolean> moduleSeperate = this.register(new Setting<Boolean>("SeperateLine", true, v -> pagesSetting.getValue() == pages.Lines));
    public Setting<Color> moduleSeparateColor = register(new Setting<Color>("LineColors", new Color(255, 255, 255, 0), v -> moduleSeperate.getValue() && pagesSetting.getValue() == pages.Lines));


    public ClickGui() {
        super("ClickGui", "Opens the ClickGui", Category.CLIENT, true, false, false);
        setInstance();
    }

    public static ClickGui getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ClickGui();
        }
        return INSTANCE;
    }

    private void setInstance() {
        INSTANCE = this;
    }


    @Override
    public void onUpdate() {
        if (customFov.getValue()) {
            Util.mc.gameSettings.setOptionFloatValue(GameSettings.Options.FOV, fov.getValue());
        }
    }

    @SubscribeEvent
    public void onSettingChange(ClientEvent event) {
        if (event.getStage() == 2) {
            if (event.getSetting().getFeature().equals(this)) {
                if (event.getSetting().equals(this.prefix)) {
                    SeleneLoader.commandManager.setPrefix(this.prefix.getPlannedValue());
                    Command.sendMessage("Prefix set to " + TextUtil.GREEN + SeleneLoader.commandManager.getPrefix());
                }
                SeleneLoader.colorManager.setColor(this.moduleMainC.getPlannedValue().getRed(), this.moduleMainC.getPlannedValue().getGreen(), this.moduleMainC.getPlannedValue().getBlue(), this.moduleMainC.getPlannedValue().getAlpha());
            }
        }
    }

    @Override
    public void onRenderTick() {

    }

    @Override
    public void onEnable() {
        Util.mc.displayGuiScreen(new ClickGUI());
    }

    @Override
    public void onLoad() {
        SeleneLoader.colorManager.setColor(this.moduleMainC.getValue().getRed(), this.moduleMainC.getValue().getGreen(), this.moduleMainC.getValue().getBlue(), this.moduleMainC.getValue().getAlpha());
        SeleneLoader.commandManager.setPrefix(this.prefix.getValue());
    }

    @Override
    public void onTick() {
        if (!(Util.mc.currentScreen instanceof ClickGUI)) {
            this.disable();
        }
    }

    @Override
    public void onDisable() {
        if (Util.mc.currentScreen instanceof ClickGUI) {
            Util.mc.displayGuiScreen(null);
        }
    }

    public enum pages {
        Main,
        Lines,
        Misc,
        Category,
        Background,
        Module
    }
}
