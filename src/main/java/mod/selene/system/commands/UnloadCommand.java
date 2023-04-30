package mod.selene.system.commands;

import mod.selene.loader.SeleneLoader;
import mod.selene.system.Command;

public class UnloadCommand extends Command {

    public UnloadCommand() {
        super("unload", new String[]{});
    }

    @Override
    public void execute(String[] commands) {
        SeleneLoader.unload(true);
    }
}
