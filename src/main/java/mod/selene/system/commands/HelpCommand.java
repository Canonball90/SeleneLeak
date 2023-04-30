package mod.selene.system.commands;

import mod.selene.loader.SeleneLoader;
import mod.selene.system.Command;

public class HelpCommand extends Command {

    public HelpCommand() {
        super("commands");
    }

    @Override
    public void execute(String[] commands) {
        sendMessage("You can use following commands: ");
        for (Command command : SeleneLoader.commandManager.getCommands()) {
            sendMessage(SeleneLoader.commandManager.getPrefix() + command.getName());
        }
    }
}
