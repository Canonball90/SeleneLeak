package mod.selene.impl.modules.player;

import mod.selene.api.utils.Timer;
import mod.selene.impl.Module;
import mod.selene.system.Setting;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Replenish
        extends Module {
    public static Replenish INSTANCE;
    private final Setting<Integer> percent = this.register(new Setting<Integer>("Percent", 50, 1, 99));
    private final Setting<Boolean> wait = this.register(new Setting<Boolean>("Wait", false));
    private final Setting<Integer> delay = this.register(new Setting<Integer>("Delay", 100, 0, 1000));
    private final Timer timer;
    private final Map<Integer, ItemStack> hotbar;
    private int refillSlot = -1;

    public Replenish() {
        super("Replenish", "Replenishes your hotbar", Module.Category.PLAYER, false, false, false);
        this.hotbar = new ConcurrentHashMap<Integer, ItemStack>();
        this.timer = new Timer();
        INSTANCE = this;
    }

    @Override
    public void onTick() {
        if (this.refillSlot == -1) {
            for (int i = 0; i < 9; ++i) {
                ItemStack itemStack = Replenish.mc.player.inventory.getStackInSlot(i);
                if (this.hotbar.getOrDefault(i, null) == null) {
                    if (itemStack.getItem().equals(Items.AIR)) continue;
                    this.hotbar.put(i, itemStack);
                    continue;
                }
                double d = (double) itemStack.getCount() / (double) itemStack.getMaxStackSize() * 100.0;
                if (!(d <= (double) this.percent.getValue().intValue())) continue;
                if (!this.timer.passedMs(this.delay.getValue().intValue())) {
                    this.refillSlot = i;
                } else {
                    this.fillStack(i, itemStack);
                    this.timer.reset();
                }
                break;
            }
        } else if (this.timer.passedMs(this.delay.getValue().intValue())) {
            this.fillStack(this.refillSlot, this.hotbar.get(this.refillSlot));
            this.timer.reset();
            this.refillSlot = -1;
        }
    }

    @Override
    public void onDisable() {
        this.hotbar.clear();
        this.refillSlot = -1;
    }

    private void fillStack(int n, ItemStack itemStack) {
        if (n != -1 && itemStack != null) {
            int n2;
            int n3 = -1;
            for (n2 = 9; n2 < 36; ++n2) {
                ItemStack itemStack2 = Replenish.mc.player.inventory.getStackInSlot(n2);
                if (itemStack2.isEmpty() || !itemStack.getDisplayName().equals(itemStack2.getDisplayName())) continue;
                if (itemStack.getItem() instanceof ItemBlock) {
                    if (!(itemStack2.getItem() instanceof ItemBlock)) continue;
                    ItemBlock itemBlock = (ItemBlock) itemStack.getItem();
                    ItemBlock itemBlock2 = (ItemBlock) itemStack2.getItem();
                    if (!itemBlock.getBlock().equals(itemBlock2.getBlock())) {
                        continue;
                    }
                } else if (!itemStack.getItem().equals(itemStack2.getItem())) continue;
                n3 = n2;
            }
            if (n3 != -1) {
                n2 = itemStack.getCount() + Replenish.mc.player.inventory.getStackInSlot(n3).getCount();
                Replenish.mc.playerController.windowClick(0, n3, 0, ClickType.PICKUP, Replenish.mc.player);
                Replenish.mc.playerController.windowClick(0, n < 9 ? n + 36 : n, 0, ClickType.PICKUP, Replenish.mc.player);
                if (n2 >= itemStack.getMaxStackSize()) {
                    Replenish.mc.playerController.windowClick(0, n3, 0, ClickType.PICKUP, Replenish.mc.player);
                }
                this.refillSlot = -1;
            }
        }
    }
}