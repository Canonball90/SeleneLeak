package mod.selene.managers;

import mod.selene.api.utils.interfaces.Util;
import mod.selene.injections.inj.IPlayerControllerMP;
import mod.selene.world.PacketEvent;
import net.minecraft.block.Block;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.Item;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class InventoryManager2
        implements Util {
    private int serverSlot;

    public InventoryManager2() {
        serverSlot = -1;
    }

    public void switchToBlock(Block block, Switch switch_) {
        switchToItem(Item.getItemFromBlock(block), switch_);
    }

    public void syncSlot() {
        if (serverSlot != mc.player.inventory.currentItem) {
            mc.player.inventory.currentItem = serverSlot;
        }
    }

    public int searchSlot(Class<? extends Item> class_, InventoryRegion inventoryRegion) {
        int n = -1;
        for (int i = inventoryRegion.getStart(); i <= inventoryRegion.getBound(); ++i) {
            if (!class_.isInstance(mc.player.inventory.getStackInSlot(i).getItem()))
                continue;
            n = i;
            break;
        }
        return n;
    }

    public void switchToSlot(int n, Switch switch_) {
        if (InventoryPlayer.isHotbar(n) && mc.player.inventory.currentItem != n) {
            switch (switch_) {
                case NORMAL: {
                    mc.player.inventory.currentItem = n;
                    mc.player.connection.sendPacket(new CPacketHeldItemChange(n));
                    break;
                }
                case PACKET: {
                    mc.player.connection.sendPacket(new CPacketHeldItemChange(n));
                    ((IPlayerControllerMP) mc.playerController).setCurrentPlayerItem(n);
                }
            }
        }
    }

    public void switchToItem(Item[] arritem, Switch switch_) {
        int n = -1;
        for (int i = InventoryRegion.HOTBAR.getStart(); i < InventoryRegion.HOTBAR.getBound(); ++i) {
            for (Item item : arritem) {
                if (!mc.player.inventory.getStackInSlot(i).getItem().equals(item)) continue;
                n = i;
                break;
            }
            if (n != -1) break;
        }
        switchToSlot(n, switch_);
    }

    public void switchToBlock(Block[] arrblock, Switch switch_) {
        Item[] arritem = new Item[arrblock.length];
        for (int i = 0; i < arrblock.length; ++i) {
            arritem[i] = Item.getItemFromBlock(arrblock[i]);
        }
        switchToItem(arritem, switch_);
    }

    @SubscribeEvent
    public void onPacketSend(PacketEvent.Send send) {
        if (send.getPacket() instanceof CPacketHeldItemChange) {
            if (!InventoryPlayer.isHotbar(((CPacketHeldItemChange) send.getPacket()).getSlotId())) {
                send.setCanceled(true);
                return;
            }
            serverSlot = ((CPacketHeldItemChange) send.getPacket()).getSlotId();
        }
    }

    public int searchSlot(Item item, InventoryRegion inventoryRegion) {
        int n = -1;
        for (int i = inventoryRegion.getStart(); i < inventoryRegion.getBound(); ++i) {
            if (!mc.player.inventory.getStackInSlot(i).getItem().equals(item)) continue;
            n = i;
            break;
        }
        return n;
    }

    public void switchToItem(Item item, Switch switch_) {
        int n = searchSlot(item, InventoryRegion.HOTBAR);
        switchToSlot(n, switch_);
    }

    public void switchToItem(Class<? extends Item> class_, Switch switch_) {
        int n = searchSlot(class_, InventoryRegion.HOTBAR);
        switchToSlot(n, switch_);
    }

    public int searchSlot(Block[] arrblock, InventoryRegion inventoryRegion) {
        int n = -1;
        block0:
        for (int i = inventoryRegion.getStart(); i < inventoryRegion.getBound(); ++i) {
            for (Block block : arrblock) {
                if (n != -1 || !mc.player.inventory.getStackInSlot(i).getItem().equals(Item.getItemFromBlock(block)))
                    continue;
                n = i;
                continue block0;
            }
        }
        return n;
    }

    public int getServerSlot() {
        return serverSlot;
    }

    public enum InventoryRegion {
        INVENTORY(0, 45),
        HOTBAR(0, 8),
        CRAFTING(80, 83),
        ARMOR(100, 103);

        private final int start;
        private final int bound;

        InventoryRegion(int n2, int n3) {
            start = n2;
            bound = n3;
        }

        public int getBound() {
            return bound;
        }

        public int getStart() {
            return start;
        }
    }

    public enum Switch {
        NORMAL,
        PACKET,
        NONE

    }
}