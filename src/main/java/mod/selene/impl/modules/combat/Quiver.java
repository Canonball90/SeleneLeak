package mod.selene.impl.modules.combat;

import mod.selene.api.utils.Timer;
import mod.selene.impl.Module;
import mod.selene.injections.inj.IEntityPlayerSP;
import mod.selene.system.Setting;
import mod.selene.world.StopUsingItemEvent;
import mod.selene.world.UpdateWalkingPlayerEvent;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Quiver extends Module {


    public final Setting<Boolean> speed = this.register(new Setting<>("Swiftness", false));
    public final Setting<Boolean> strength = this.register(new Setting<>("Strength", false));
    public final Setting<Boolean> toggelable = this.register(new Setting<>("Toggelable", false));
    public final Setting<Boolean> autoSwitch = this.register(new Setting<>("AutoSwitch", false));
    public final Setting<Boolean> rearrange = this.register(new Setting<>("Rearrange", false));
    public final Setting<Boolean> noGapSwitch = this.register(new Setting<>("NoGapSwitch", false));
    public final Setting<Integer> health = this.register(new Setting<>("MinHealth", 20, 0, 36));
    private final Timer timer = new Timer();
    private boolean cancelStopUsingItem = false;

    public Quiver() {
        super("Quiver", "Apply effects on yourself with a bow.", Category.COMBAT, false, false, true);
    }

    @SubscribeEvent
    public void onUpdateWalkingPlayer(UpdateWalkingPlayerEvent event) {
        if (event.getStage() == 1 || mc.player == null || mc.world == null) return;

        if (!timer.passedMs(2500)) return;

        if (mc.player.getHealth() + mc.player.getAbsorptionAmount() < health.getValue()) return;

        if (noGapSwitch.getValue() && mc.player.getActiveItemStack().getItem() instanceof ItemFood) return;

        if (strength.getValue() && !mc.player.isPotionActive(MobEffects.STRENGTH)) {
            if (isFirstAmmoValid("Arrow of Strength")) {
                shootBow();
            } else if (toggelable.getValue()) {
                toggle();
            }
        }

        if (speed.getValue() && !mc.player.isPotionActive(MobEffects.SPEED)) {
            if (isFirstAmmoValid("Arrow of Swiftness")) {
                shootBow();
            } else if (toggelable.getValue()) {
                toggle();
            }
        }
    }

    @SubscribeEvent
    public void onStopUsingItem(StopUsingItemEvent event) {
        if (cancelStopUsingItem) {
            event.setCanceled(true);
        }
    }

    @Override
    public void onEnable() {
        cancelStopUsingItem = false;
    }

    private void shootBow() {
        if (mc.player.inventory.getCurrentItem().getItem() == Items.BOW) {
            mc.player.connection.sendPacket(new CPacketPlayer.Rotation(0, -90, mc.player.onGround));
            ((IEntityPlayerSP) mc.player).setLastReportedYaw(0);
            ((IEntityPlayerSP) mc.player).setLastReportedPitch(-90);
            if (mc.player.getItemInUseMaxCount() >= 3) {
                cancelStopUsingItem = false;
                mc.playerController.onStoppedUsingItem(mc.player);
                if (toggelable.getValue()) {
                    toggle();
                }
                timer.reset();
            } else if (mc.player.getItemInUseMaxCount() == 0) {
                mc.playerController.processRightClick(mc.player, mc.world, EnumHand.MAIN_HAND);
                cancelStopUsingItem = true;
            }
        } else if (autoSwitch.getValue()) {
            int bowSlot = getBowSlot();
            if (bowSlot != -1 && bowSlot != mc.player.inventory.currentItem) {
                mc.player.inventory.currentItem = bowSlot;
                mc.playerController.updateController();
            }
        }
    }

    public int getBowSlot() {
        int bowSlot = -1;

        if (mc.player.getHeldItemMainhand().getItem() == Items.BOW) {
            bowSlot = Module.mc.player.inventory.currentItem;
        }


        if (bowSlot == -1) {
            for (int l = 0; l < 9; ++l) {
                if (mc.player.inventory.getStackInSlot(l).getItem() == Items.BOW) {
                    bowSlot = l;
                    break;
                }
            }
        }

        return bowSlot;
    }

    private boolean isFirstAmmoValid(String type) {
        for (int i = 0; i < 36; i++) {
            ItemStack itemStack = mc.player.inventory.getStackInSlot(i);
            if (itemStack.getItem() == Items.TIPPED_ARROW) {
                boolean matches = itemStack.getDisplayName().equalsIgnoreCase(type);
                if (matches) {
                    return true;
                } else if (rearrange.getValue()) {
                    return rearrangeArrow(i, type);
                } else {
                    return false;
                }
            }
        }
        return false;
    }

    private boolean rearrangeArrow(int fakeSlot, String type) {
        for (int i = 0; i < 36; i++) {
            ItemStack itemStack = mc.player.inventory.getStackInSlot(i);
            if (itemStack.getItem() == Items.TIPPED_ARROW) {
                if (itemStack.getDisplayName().equalsIgnoreCase(type)) {
                    mc.playerController.windowClick(0, fakeSlot, 0, ClickType.PICKUP, mc.player);
                    mc.playerController.windowClick(0, i, 0, ClickType.PICKUP, mc.player);
                    mc.playerController.windowClick(0, fakeSlot, 0, ClickType.PICKUP, mc.player);
                    return true;
                }
            }
        }
        return false;
    }

}