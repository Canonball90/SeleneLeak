package mod.selene.system.commands;

import mod.selene.api.utils.TextUtil;
import mod.selene.loader.SeleneLoader;
import mod.selene.system.Command;

public class ConfigCommand extends Command {

    public ConfigCommand() {
        super("config", new String[]{"<save/load>"});
    }

    @Override
    public void execute(String[] commands) {
        if (commands.length == 1) {
            sendMessage(TextUtil.coloredString("You`ll find the config files in your gameProfile directory under selene/config", TextUtil.Color.GOLD));
            return;
        }

        if (commands.length >= 2) {
            switch (commands[0]) {
                case "save":
                    SeleneLoader.configManager.saveConfig();
                    sendMessage(TextUtil.GREEN + "Config has been saved.");
                    break;
                case "load":
                    SeleneLoader.configManager.loadConfig();
                    sendMessage(TextUtil.GREEN + "Config has been loaded.");
                    break;
                default:
                    sendMessage(TextUtil.RED + "Not a valid command... Possible usage: <save/load>");
                    break;
            }
        }
    }
}
