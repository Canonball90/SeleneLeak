package mod.selene.impl.modules.client;

import mod.selene.api.utils.TextUtil;
import mod.selene.api.utils.Timer;
import mod.selene.api.utils.interfaces.Util;
import mod.selene.impl.Module;
import mod.selene.loader.SeleneLoader;
import mod.selene.managers.FileManager;
import mod.selene.system.Command;
import mod.selene.system.Setting;
import mod.selene.world.ClientEvent;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Notifications extends Module {

    private static final String fileName = "selene/utils/ModuleMessage_List.txt";
    private static final List<String> modules = new ArrayList();
    private static Notifications INSTANCE = new Notifications();
    private final Timer timer = new Timer();
    public Setting<Boolean> totemPops = register(new Setting("TotemPops", false));
    public Setting<Integer> delay = register(new Setting("Delay", 2000, 0, 5000, v -> totemPops.getValue(), "Delays messages."));
    public Setting<Boolean> clearOnLogout = (new Setting("LogoutClear", false));
    public Setting<Boolean> moduleMessage = register(new Setting("ModuleMessage", false));
    private final Setting<Boolean> readfile = register(new Setting("LoadFile", false, v -> moduleMessage.getValue()));
    public Setting<Boolean> list = register(new Setting("List", false, v -> moduleMessage.getValue()));
    public Setting<Boolean> visualRange = register(new Setting("VisualRange", false));
    public Setting<Boolean> leaving = register(new Setting("Leaving", false, v -> visualRange.getValue()));
    public Setting<Boolean> crash = (new Setting("Crash", false));
    public Timer totemAnnounce = new Timer();
    private List<String> knownPlayers = new ArrayList<>();
    private boolean check;

    public Notifications() {
        super("Display", "Sends Messages.", Category.CLIENT, true, false, false);
        setInstance();
    }

    public static Notifications getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Notifications();
        }
        return INSTANCE;
    }

    public static void displayCrash(Exception e) {
        Command.sendMessage(TextUtil.RED + "Exception caught: " + e.getMessage());
    }

    private void setInstance() {
        INSTANCE = this;
    }

    @Override
    public void onLoad() {
        check = true;
        loadFile();
        check = false;
    }

    @Override
    public void onRenderTick() {

    }

    @Override
    public void onEnable() {
        this.knownPlayers = new ArrayList<>();
        if (!check) {
            loadFile();
        }
    }

    @Override
    public void onUpdate() {
        if (readfile.getValue()) {
            if (!check) {
                Command.sendMessage("Loading File...");
                timer.reset();
                loadFile();
            }
            check = true;
        }

        if (check && timer.passedMs(750)) {
            readfile.setValue(false);
            check = false;
        }

        if (visualRange.getValue()) {
            List<String> tickPlayerList = new ArrayList<>();
            for (Entity entity : Util.mc.world.playerEntities) {
                tickPlayerList.add(entity.getName());
            }
            if (tickPlayerList.size() > 0) {
                for (String playerName : tickPlayerList) {
                    if (playerName.equals(Util.mc.player.getName())) {
                        continue;
                    }
                    if (!knownPlayers.contains(playerName)) {
                        knownPlayers.add(playerName);
                        if (SeleneLoader.friendManager.isFriend(playerName)) {
                            Command.sendMessage("Player " + TextUtil.GREEN + playerName + TextUtil.RESET + " entered your visual range!");
                        } else {
                            Command.sendMessage("Player " + TextUtil.RED + playerName + TextUtil.RESET + " entered your visual range!");
                        }
                        return;
                    }
                }
            }

            if (knownPlayers.size() > 0) {
                for (String playerName : knownPlayers) {
                    if (!tickPlayerList.contains(playerName)) {
                        knownPlayers.remove(playerName);
                        if (leaving.getValue()) {
                            if (SeleneLoader.friendManager.isFriend(playerName)) {
                                Command.sendMessage("Player " + TextUtil.GREEN + playerName + TextUtil.RESET + " left your visual range!");
                            } else {
                                Command.sendMessage("Player " + TextUtil.RED + playerName + TextUtil.RESET + " left your visual range!");
                            }
                        }
                        return;
                    }
                }
            }
        }
    }

    public void loadFile() {
        List<String> fileInput = FileManager.readTextFileAllLines(fileName);
        Iterator<String> i = fileInput.iterator();
        modules.clear();
        while (i.hasNext()) {
            String s = i.next();
            if (!s.replaceAll("\\s", "").isEmpty()) {
                modules.add(s);
            }
        }
    }

    @SubscribeEvent
    public void onToggleModule(ClientEvent event) {
        if (!moduleMessage.getValue()) {
            return;
        }

        if (event.getStage() == 0) {
            Module module = (Module) event.getFeature();
            if (!module.equals(this) && (modules.contains(module.getDisplayName()) || !list.getValue())) {
                Command.sendMessage(TextUtil.RED + module.getDisplayName() + " disabled.");
            }
        }

        if (event.getStage() == 1) {
            Module module = (Module) event.getFeature();
            if (modules.contains(module.getDisplayName()) || !list.getValue()) {
                Command.sendMessage(TextUtil.GREEN + module.getDisplayName() + " enabled.");
            }
        }
    }
}
