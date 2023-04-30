package mod.selene.impl.modules.other;

import com.mojang.realmsclient.gui.ChatFormatting;
import mod.selene.impl.Module;
import mod.selene.loader.SeleneLoader;
import mod.selene.system.Command;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderPearl;
import net.minecraft.entity.player.EntityPlayer;

import java.util.HashMap;
import java.util.UUID;

public class PearlNotify
        extends Module {
    private final HashMap<EntityPlayer, UUID> list = new HashMap();
    private Entity enderPearl;
    private boolean flag;

    public PearlNotify() {
        super("Pearl", "Notify pearl throws.", Category.OTHER, true, false, false);
    }

    @Override
    public void onEnable() {
        this.flag = true;
    }

    @Override
    public void onUpdate() {
        if (PearlNotify.mc.world == null || PearlNotify.mc.player == null) {
            return;
        }
        this.enderPearl = null;
        for (Object e : PearlNotify.mc.world.loadedEntityList) {
            if (!(e instanceof EntityEnderPearl)) continue;
            this.enderPearl = (Entity) e;
            break;
        }
        if (this.enderPearl == null) {
            this.flag = true;
            return;
        }
        EntityPlayer closestPlayer = null;
        for (EntityPlayer entity : PearlNotify.mc.world.playerEntities) {
            if (closestPlayer == null) {
                closestPlayer = entity;
                continue;
            }
            if (closestPlayer.getDistance(this.enderPearl) <= entity.getDistance(this.enderPearl)) continue;
            closestPlayer = entity;
        }
        if (closestPlayer == PearlNotify.mc.player) {
            this.flag = false;
        }
        if (closestPlayer != null && this.flag) {
            String faceing = this.enderPearl.getHorizontalFacing().toString();
            if (faceing.equals("west")) {
                faceing = "east";
            } else if (faceing.equals("east")) {
                faceing = "west";
            }
            Command.sendMessage(SeleneLoader.friendManager.isFriend(closestPlayer.getName()) ? ChatFormatting.AQUA + closestPlayer.getName() + ChatFormatting.DARK_GRAY + " has just thrown a pearl heading " + faceing + "!" : ChatFormatting.RED + closestPlayer.getName() + ChatFormatting.DARK_GRAY + " has just thrown a pearl heading " + faceing + "!");
            this.flag = false;
        }
    }
}

