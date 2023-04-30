package mod.selene.system.commands;

import mod.selene.api.utils.TextUtil;
import mod.selene.loader.SeleneLoader;
import mod.selene.managers.FriendManager;
import mod.selene.system.Command;

public class FriendCommand extends Command {

    public FriendCommand() {
        super("friend", new String[]{"<add/del/name/clear>", "<name>"});
    }

    @Override
    public void execute(String[] commands) {
        if (commands.length == 1) {
            if (SeleneLoader.friendManager.getFriends().isEmpty()) {
                sendMessage(TextUtil.coloredString("You currently dont have any friends added.", TextUtil.Color.RED));
            } else {
                String f = "Friends: ";
                for (FriendManager.Friend friend : SeleneLoader.friendManager.getFriends()) {
                    try {
                        f += friend.getUsername() + ", ";
                    } catch (Exception e) {
                        continue;
                    }
                }
                sendMessage(f);
            }
            return;
        }

        if (commands.length == 2) {
            if (commands[0].equals("reset")) {
                SeleneLoader.friendManager.onLoad();
                sendMessage("Friends got reset.");
            } else {
                sendMessage(commands[0] + (SeleneLoader.friendManager.isFriend(commands[0]) ? " is friended." : " isnt friended."));
            }
            return;
        }

        if (commands.length >= 2) {
            switch (commands[0]) {
                case "add":
                    SeleneLoader.friendManager.addFriend(commands[1]);
                    sendMessage(TextUtil.GREEN + commands[1] + " has been friended");
                    break;
                case "del":
                    SeleneLoader.friendManager.removeFriend(commands[1]);
                    sendMessage(TextUtil.RED + commands[1] + " has been unfriended");
                    break;
                default:
                    sendMessage(TextUtil.RED + "Bad Command, try: friend <add/del/name> <name>.");
            }
        }
    }
}
