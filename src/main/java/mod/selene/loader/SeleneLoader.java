package mod.selene.loader;

import mod.selene.managers.*;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.Display;

@Mod(modid = SeleneLoader.MODID, name = SeleneLoader.MODNAME, version = SeleneLoader.MODVER)
public class SeleneLoader {

    public static final String MODID = "selene";
    public static final String MODNAME = "Selene";
    public static final String MODVER = "0.6.1";
    public static final String NAME_UNICODE = "3\u1D00\u0280\u1D1B\u029C\u029C4\u1D04\u1D0B"; //"\u1D18\u029C\u1D0F\u0299\u1D0F\uA731.\u1D07\u1D1C";
    public static final String SELENE_UNICODE = "\u1D18\u029C\u1D0F\u0299\u1D0F\uA731";
    public static final String CHAT_SUFFIX = " \u23D0 " + NAME_UNICODE;
    public static final String SELENE_SUFFIX = " \u23D0 " + SELENE_UNICODE;
    public static final Logger LOGGER = LogManager.getLogger("Selene");
    public static float TICK_TIMER = 1f;
    public static ModuleManager moduleManager;
    public static SpeedManager speedManager;
    public static PositionManager positionManager;
    public static RotationManager rotationManager;
    public static RotationManager2 rotationManager2;
    public static CommandManager commandManager;
    public static EventManager eventManager;
    public static ConfigManager configManager;

    public static SafetyManager safetyManager;
    public static FileManager fileManager;
    public static FriendManager friendManager;
    public static TextManager textManager;
    public static ColorManager colorManager;
    public static ServerManager serverManager;
    public static PotionManager potionManager;
    public static InventoryManager inventoryManager;
    public static InventoryManager2 inventoryManager2;
    public static PacketManager packetManager;
    public static ReloadManager reloadManager;
    public static TotemPopManager totemPopManager;
    public static HoleManager holeManager;
    public static TimerManager timerManager;
    public static TickManager tickManager;
    @Mod.Instance
    public static SeleneLoader INSTANCE;
    private static boolean unloaded = false;

    public static void load() {
        LOGGER.info("\n\nLoading Selene " + MODVER);
        unloaded = false;
        if (reloadManager != null) {
            reloadManager.unload();
            reloadManager = null;
        }

        totemPopManager = new TotemPopManager();
        packetManager = new PacketManager();
        serverManager = new ServerManager();
        colorManager = new ColorManager();
        textManager = new TextManager();
        moduleManager = new ModuleManager();
        speedManager = new SpeedManager();
        rotationManager = new RotationManager();
        rotationManager2 = new RotationManager2();
        positionManager = new PositionManager();
        commandManager = new CommandManager();
        eventManager = new EventManager();
        configManager = new ConfigManager();
        fileManager = new FileManager();
        safetyManager = new SafetyManager();
        friendManager = new FriendManager();
        potionManager = new PotionManager();
        inventoryManager = new InventoryManager();
        inventoryManager2 = new InventoryManager2();
        holeManager = new HoleManager();
        timerManager = new TimerManager();
        tickManager = new TickManager();
        LOGGER.info("Initialized Managers");

        moduleManager.init();
        LOGGER.info("Modules loaded.");
        configManager.init();
        eventManager.init();
        LOGGER.info("EventManager loaded.");
        textManager.init(true);
        moduleManager.onLoad();
        totemPopManager.init();
        LOGGER.info("Selene initialized!\n");
    }

    public static void unload(boolean unload) {
        LOGGER.info("\n\nUnloading Selene " + MODVER);
        if (unload) {
            reloadManager = new ReloadManager();
            reloadManager.init(commandManager != null ? commandManager.getPrefix() : ".");
        }
        onUnload();
        unloaded = true;
        eventManager = null;
        holeManager = null;
        moduleManager = null;
        totemPopManager = null;
        serverManager = null;
        colorManager = null;
        textManager = null;
        speedManager = null;
        rotationManager = null;
        rotationManager2 = null;
        positionManager = null;
        commandManager = null;
        configManager = null;
        fileManager = null;
        friendManager = null;
        potionManager = null;
        inventoryManager = null;
        inventoryManager2 = null;
        safetyManager = null;
        timerManager = null;
        tickManager = null;
        LOGGER.info("Selene unloaded!\n");
    }

    public static void reload() {
        unload(false);
        load();
    }

    public static void onUnload() {
        if (!unloaded) {
            eventManager.onUnload();
            moduleManager.onUnload();
            configManager.saveConfig();
        }
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        //preInit
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        Display.setTitle("Selene - v." + MODVER);
        load();
    }
}