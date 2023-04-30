package mod.selene.impl.modules.combat;

import mod.selene.api.utils.EntityUtil;
import mod.selene.api.utils.Timer;
import mod.selene.impl.Module;
import mod.selene.injections.inj.ICPacketPlayer;
import mod.selene.injections.inj.IEntity;
import mod.selene.injections.inj.INetworkManager;
import mod.selene.system.Setting;
import mod.selene.world.PacketEvent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.init.MobEffects;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.network.play.client.CPacketUseEntity.Action;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Criticals extends Module {
    // criticals timer
    private static final Timer criticalTimer = new Timer();
    public static Criticals INSTANCE;

    // **************************** anticheat ****************************
    public Setting<Mode> mode = register(new Setting<>("Mode", Mode.PACKET));

    public Setting<Double> motion = register(new Setting<>("Motion", 0.42D, 0.0D, 1.0D,
            v -> mode.getValue().equals(Mode.MOTION)));

    // **************************** general ****************************

    public Setting<Double> delay = register(new Setting<>("Delay", 200.0D, 0.0D, 2000.0D));
    // packet info
    private CPacketUseEntity resendAttackPacket;
    private CPacketAnimation resendAnimationPacket;
    // critical entity
    private Entity criticalEntity;

    // cosmos
    public Criticals() {
        super("Criticals", "Ensures all hits are criticals", Module.Category.COMBAT, true, false, false);
        INSTANCE = this;
    }

    @Override
    public void onUpdate() {

        // resend our attack packets
        if (resendAttackPacket != null) {
            mc.player.connection.sendPacket(resendAttackPacket);
            resendAttackPacket = null;

            // resend our animation packets
            if (resendAnimationPacket != null) {
                mc.player.connection.sendPacket(resendAnimationPacket);
                resendAnimationPacket = null;
            }
        }
    }

    @SubscribeEvent
    public void onPacketSend(PacketEvent.Send event) {

        // packet for attacks
        if (event.getPacket() instanceof CPacketUseEntity && ((CPacketUseEntity) event.getPacket()).getAction().equals(Action.ATTACK)) {

            // entity we attacked, if there was one
            Entity attackEntity = ((CPacketUseEntity) event.getPacket()).getEntityFromWorld(mc.world);

            // pause in liquid, ladders, blindness, webs, and air, since strict anticheats will flag as irregular movement
            if (EntityUtil.isInLiquid() || mc.player.isRiding() || mc.player.isPotionActive(MobEffects.BLINDNESS) || mc.player.isOnLadder() || !mc.player.onGround || ((IEntity) mc.player).getInWeb()) {
                return;
            }

            // pause if attacking a crystal, helps compatability with AutoCrystal
            if (attackEntity instanceof EntityEnderCrystal) {
                return;
            }

            // critical hits on 32k's are insignificant
            if (EntityUtil.holding32k(mc.player)) {
                return;
            }

            // make sure the attacked entity exists
            if (attackEntity != null && attackEntity.isEntityAlive()) {

                // destroying a vehicle takes 5 hits -> regardless of damage
                if (EntityUtil.isVehicle(attackEntity)) {

                    // attack 5 times
                    if (mc.getConnection() != null) {
                        for (int i = 0; i < 5; i++) {
                            ((INetworkManager) mc.getConnection().getNetworkManager()).hookDispatchPacket(new CPacketUseEntity(attackEntity), null);
                            ((INetworkManager) mc.getConnection().getNetworkManager()).hookDispatchPacket(new CPacketAnimation(((CPacketUseEntity) event.getPacket()).getHand()), null);
                        }
                    }
                } else {
                    // send position packets after attack if we didn't modify them
                    if (!mode.getValue().equals(Mode.VANILLA) && !mode.getValue().equals(Mode.VANILLA_STRICT)) {

                        // attempt motion criticals
                        if (mode.getValue().equals(Mode.MOTION)) {

                            // jump
                            mc.player.motionY = motion.getValue();

                            // cancel the attack, we'll resend it next tick
                            event.setCanceled(true);
                            resendAttackPacket = event.getPacket();
                        }

                        // if our timer has cleared the delay, then we are cleared to attempt another critical attack
                        if (criticalTimer.passedMs(delay.getValue().longValue())) {
                            if (mode.getValue().equals(Mode.PACKET_STRICT)) {

                                // cancel the attack, we'll resend it after packets
                                event.setCanceled(true);
                            }

                            if (mode.getValue().equals(Mode.PACKET) || mode.getValue().equals(Mode.PACKET_STRICT)) {

                                // send packets for each of the offsets
                                for (float offset : mode.getValue().getOffsets()) {

                                    // last packet on strict should confirm player position
                                    mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.getEntityBoundingBox().minY + offset, mc.player.posZ, false));
                                }

                                // set our attacked entity
                                criticalEntity = attackEntity;
                            }

                            // resend attack packet
                            if (mode.getValue().equals(Mode.PACKET_STRICT)) {
                                if (mc.getConnection() != null) {
                                    ((INetworkManager) mc.getConnection().getNetworkManager()).hookDispatchPacket(new CPacketUseEntity(attackEntity), null);
                                }
                            }
                        }

                        criticalTimer.reset();

                        // add critical effects to the hit
                        mc.player.onCriticalHit(attackEntity);
                    }
                }
            }
        }

        // packet for swing animation
        if (event.getPacket() instanceof CPacketAnimation) {
            if (mode.getValue().equals(Mode.MOTION)) {

                // cancel our swing animation, we'll resend it next tick
                event.setCanceled(true);
                resendAnimationPacket = event.getPacket();
            }
        }

        // packet for player updates
        if (event.getPacket() instanceof CPacketPlayer) {

            // check if packet is updating motion
            if (((ICPacketPlayer) event.getPacket()).isMoving()) {

                // we have attacked an entity
                if (criticalEntity != null) {

                    // make sure entity is hurt
                    if (criticalEntity.hurtResistantTime <= 16) {
                        criticalEntity = null;
                        return;
                    }

                    event.setCanceled(true);

                    // modify packets
                    if (mode.getValue().equals(Mode.VANILLA)) {

                        // all vanilla packets are off ground
                        ((ICPacketPlayer) event.getPacket()).setOnGround(false);

                        // modify packets based on entity hurt time
                        switch (criticalEntity.hurtResistantTime) {
                            case 20:
                                ((ICPacketPlayer) event.getPacket()).setY(mc.player.getEntityBoundingBox().minY + 0.5F);
                                break;
                            case 19:
                            case 17:
                                ((ICPacketPlayer) event.getPacket()).setY(mc.player.getEntityBoundingBox().minY);
                                break;
                            case 18:
                                ((ICPacketPlayer) event.getPacket()).setY(mc.player.getEntityBoundingBox().minY + 0.3F);
                                break;
                        }
                    }

                    // strict has dynamic onGround packets
                    else if (mode.getValue().equals(Mode.VANILLA_STRICT)) {

                        // all vanilla packets are off ground
                        ((ICPacketPlayer) event.getPacket()).setOnGround(false);

                        // modify packets based on entity hurt time
                        switch (criticalEntity.hurtResistantTime) {
                            case 19:
                                ((ICPacketPlayer) event.getPacket()).setY(mc.player.getEntityBoundingBox().minY + 0.11F);
                                break;
                            case 18:
                                ((ICPacketPlayer) event.getPacket()).setY(mc.player.getEntityBoundingBox().minY + 0.1100013579F);
                                break;
                            case 17:
                                ((ICPacketPlayer) event.getPacket()).setY(mc.player.getEntityBoundingBox().minY + 0.0000013579F);
                                break;
                        }
                    }
                }
            }
        }
    }

    public enum Mode {

        /**
         * Attempts changing hit to a critical via packets
         */
        PACKET(0.05F, 0, 0.03F, 0),

        /**
         * Attempts changing hit to a critical via packets for Updated NCP
         */
        PACKET_STRICT(0.11F, 0.1100013579F, 0.0000013579F),

        /**
         * Attempts changing hit to a critical via modifying vanilla packets
         */
        VANILLA(),

        /**
         * Attempts changing hit to a critical via modifying vanilla packets for Updated NCP
         */
        VANILLA_STRICT(),

        /**
         * Attempts critical via a jump
         */
        MOTION();

        // packet offsets
        private final float[] offsets;

        Mode(float... offsets) {
            this.offsets = offsets;
        }

        /**
         * Gets the packet y offsets for the mode
         *
         * @return The packet y offsets for the mode
         */
        public float[] getOffsets() {
            return offsets;
        }
    }
}