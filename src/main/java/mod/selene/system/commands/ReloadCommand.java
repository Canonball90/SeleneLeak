package mod.selene.system.commands;

import mod.selene.loader.SeleneLoader;
import mod.selene.system.Command;

public class ReloadCommand extends Command {

    public ReloadCommand() {
        super("reload", new String[]{});
    }

    @Override
    public void execute(String[] commands) {
        SeleneLoader.reload();
    }
}
