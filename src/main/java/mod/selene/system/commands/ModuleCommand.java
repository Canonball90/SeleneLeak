package mod.selene.system.commands;

import com.google.gson.JsonParser;
import mod.selene.api.utils.TextUtil;
import mod.selene.impl.Module;
import mod.selene.loader.SeleneLoader;
import mod.selene.managers.ConfigManager;
import mod.selene.system.Command;
import mod.selene.system.Setting;

public class ModuleCommand extends Command {

    public ModuleCommand() {
        super("module", new String[]{"<module>", "<set/reset>", "<setting>", "<value>"});
    }

    @Override
    public void execute(String[] commands) {
        if (commands.length == 1) {
            sendMessage("Modules: ");
            for (Module.Category category : SeleneLoader.moduleManager.getCategories()) {
                String modules = category.getName() + ": ";
                for (Module module : SeleneLoader.moduleManager.getModulesByCategory(category)) {
                    modules += (module.isEnabled() ? TextUtil.GREEN : TextUtil.RED) + module.getName() + TextUtil.RESET + ", ";
                }
                sendMessage(modules);
            }
            return;
        }

        Module module = SeleneLoader.moduleManager.getModuleByDisplayName(commands[0]);
        if (module == null) {
            module = SeleneLoader.moduleManager.getModuleByName(commands[0]);
            if (module == null) {
                sendMessage(TextUtil.RED + "This module doesn't exist.");
                return;
            }
            sendMessage(TextUtil.RED + " This is the original name of the module. Its current name is: " + module.getDisplayName());
            return;
        }

        if (commands.length == 2) {
            sendMessage(module.getDisplayName() + " : " + module.getDescription());
            for (Setting setting : module.getSettings()) {
                sendMessage(setting.getName() + " : " + setting.getValue() + ", " + setting.getDescription());
            }
            return;
        }

        if (commands.length == 3) {
            if (commands[1].equalsIgnoreCase("set")) {
                sendMessage(TextUtil.RED + "Please specify a setting.");
            } else if (commands[1].equalsIgnoreCase("reset")) {
                for (Setting setting : module.getSettings()) {
                    setting.setValue(setting.getDefaultValue());
                }
            } else {
                sendMessage(TextUtil.RED + "This command doesn't exist.");
            }
            return;
        }

        if (commands.length == 4) {
            sendMessage(TextUtil.GOLD + "Specify a value.");
            return;
        }

        if (commands.length == 5) {
            Setting setting = module.getSettingByName(commands[2]);
            if (setting != null) {
                JsonParser jp = new JsonParser();
                if (setting.getType().equalsIgnoreCase("String")) {
                    setting.setValue(commands[3]);
                    sendMessage(TextUtil.GOLD + module.getName() + " " + setting.getName() + " settled to " + commands[3] + ".");
                    return;
                }
                try {
                    if (setting.getName().equalsIgnoreCase("Enabled")) {
                        if (commands[3].equalsIgnoreCase("true")) {
                            module.enable();
                        }
                        if (commands[3].equalsIgnoreCase("false")) {
                            module.disable();
                        }
                    }
                    ConfigManager.setValueFromJson(module, setting, jp.parse(commands[3]));
                } catch (Exception e) {
                    sendMessage(TextUtil.RED + "Bad Value! This setting requires a: " + setting.getType() + " value.");
                    return;
                }
                sendMessage(TextUtil.GOLD + module.getName() + " " + setting.getName() + " settled to " + commands[3] + ".");
            }
        }
    }
}
