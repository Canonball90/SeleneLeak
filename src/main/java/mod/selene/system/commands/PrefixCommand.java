package mod.selene.system.commands;

import mod.selene.api.utils.TextUtil;
import mod.selene.impl.modules.client.ClickGui;
import mod.selene.loader.SeleneLoader;
import mod.selene.system.Command;

public class PrefixCommand extends Command {

    public PrefixCommand() {
        super("prefix", new String[]{"<char>"});
    }

    @Override
    public void execute(String[] commands) {
        if (commands.length == 1) {
            Command.sendMessage(TextUtil.RED + "Specify a new prefix.");
            return;
        }

        (SeleneLoader.moduleManager.getModuleByClass(ClickGui.class)).prefix.setValue(commands[0]);
        Command.sendMessage("Prefix set to " + TextUtil.GREEN + SeleneLoader.commandManager.getPrefix());
    }
}
