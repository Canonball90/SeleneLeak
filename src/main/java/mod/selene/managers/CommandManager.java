package mod.selene.managers;

import mod.selene.api.utils.TextUtil;
import mod.selene.loader.Feature;
import mod.selene.system.Command;
import mod.selene.system.commands.*;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class CommandManager extends Feature {

    //public Setting<String> clientMessage = register(new Setting("clientMessage", "<Selene.es>"));
    //public Setting<String> prefix = register(new Setting("prefix", "."));
    private final ArrayList<Command> commands;
    //TODO: space at the end of "<Selene.es> " doesnt save!
    private String clientMessage = TextUtil.coloredString("[", TextUtil.Color.GOLD) + TextUtil.coloredString("Selene", TextUtil.Color.GOLD) + TextUtil.coloredString("]", TextUtil.Color.GOLD);
    private String prefix = ".";

    public CommandManager() {
        super("Command");
        commands = new ArrayList<>();
        commands.add(new BindCommand());
        commands.add(new ModuleCommand());
        commands.add(new PrefixCommand());
        commands.add(new ConfigCommand());
        commands.add(new FriendCommand());
        commands.add(new HelpCommand());
        commands.add(new ReloadCommand());
        commands.add(new UnloadCommand());
        commands.add(new ReloadSoundCommand());
        commands.add(new BookCommand());
    }

    public static String[] removeElement(String[] input, int indexToDelete) {
        List result = new LinkedList();
        for (int i = 0; i < input.length; i++) {
            if (i != indexToDelete) result.add(input[i]);
        }
        return (String[]) result.toArray(input);
    }

    private static String strip(String str, String key) {
        if (str.startsWith(key) && str.endsWith(key)) return str.substring(key.length(), str.length() - key.length());
        return str;
    }

    public void executeCommand(String command) {
        String[] parts = command.split(" (?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
        String name = parts[0].substring(1);
        String[] args = removeElement(parts, 0);
        for (int i = 0; i < args.length; i++) {
            if (args[i] == null) continue;
            args[i] = strip(args[i], "\"");
        }
        for (Command c : commands) {
            if (c.getName().equalsIgnoreCase(name)) {
                c.execute(parts);
                return;
            }
        }
        Command.sendMessage(TextUtil.coloredString("Unknown command. try 'commands' to see the list of commands.", TextUtil.Color.RED));
    }

    public Command getCommandByName(String name) {
        for (Command command : commands) {
            if (command.getName().equals(name)) {
                return command;
            }
        }
        return null;
    }

    public ArrayList<Command> getCommands() {
        return commands;
    }

    public String getClientMessage() {
        return clientMessage;
    }

    public void setClientMessage(String clientMessage) {
        this.clientMessage = clientMessage;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }
}
