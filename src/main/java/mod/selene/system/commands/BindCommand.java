package mod.selene.system.commands;

import mod.selene.api.utils.TextUtil;
import mod.selene.impl.Module;
import mod.selene.loader.SeleneLoader;
import mod.selene.system.Command;
import mod.selene.system.impl.Bind;
import org.lwjgl.input.Keyboard;

public class BindCommand extends Command {

    public BindCommand() {
        super("bind", new String[]{"<module>", "<bind>"});
    }

    @Override
    public void execute(String[] commands) {
        if (commands.length == 1) {
            sendMessage(TextUtil.coloredString("Specify a module!", TextUtil.Color.RED));
            return;
        }

        String rkey = commands[1];
        String moduleName = commands[0];

        Module module = SeleneLoader.moduleManager.getModuleByName(moduleName);

        if (module == null) {
            sendMessage(TextUtil.coloredString("Unknown module '" + module + "'!", TextUtil.Color.RED));
            return;
        }

        if (rkey == null) {
            sendMessage(module.getName() + " is bound to &6" + module.getBind().toString());
            return;
        }

        int key = Keyboard.getKeyIndex(rkey.toUpperCase());

        if (rkey.equalsIgnoreCase("none")) {
            key = -1;
        }

        if (key == 0) {
            sendMessage("Unknown key '" + rkey + "'!");
            return;
        }

        module.bind.setValue(new Bind(key));
        sendMessage("Bind for &6" + module.getName() + "&r set to &6" + rkey.toUpperCase());
    }
}
