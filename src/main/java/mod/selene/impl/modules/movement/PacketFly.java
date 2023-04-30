package mod.selene.impl.modules.movement;

import mod.selene.api.utils.EntityUtil;
import mod.selene.impl.Module;
import mod.selene.injections.inj.INetworkManager;
import mod.selene.injections.inj.ISPacketPlayerPosLook;
import mod.selene.loader.SeleneLoader;
import mod.selene.system.Setting;
import mod.selene.world.MoveEvent;
import mod.selene.world.PacketEvent;
import net.minecraft.network.play.client.CPacketConfirmTeleport;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.HashMap;
import java.util.Map;

public class PacketFly
        extends Module {
    private static final double MOVE_FACTOR;
    private static final double CONCEAL = 0.0624;
    public static PacketFly INSTANCE;

    static {
        MOVE_FACTOR = 1.0 / StrictMath.sqrt(2.0);
    }

    private final Map<Integer, Vec3d> predictions = new HashMap<Integer, Vec3d>();
    public Setting<Mode> mode = register(new Setting<Mode>("Mode", Mode.FACTOR));
    public Setting<Float> factor = register(new Setting<Float>("Factor", Float.valueOf(1.0f), Float.valueOf(0.1f), Float.valueOf(15.0f), v -> mode.getValue() == Mode.FACTOR));
    public Setting<Integer> increaseTicks = register(new Setting<Integer>("IncreaseTicks", 10, 0, 20));
    public Setting<Bounds> bounds = register(new Setting<Bounds>("Bounds", Bounds.DOWN));
    public Setting<Phase> phase = register(new Setting<Phase>("Phase", Phase.NCP));
    public Setting<Boolean> conceal = register(new Setting<Boolean>("Conceal", false));
    public Setting<Boolean> antiKick = register(new Setting<Boolean>("AntiKick", true));
    public Setting<Boolean> boost = register(new Setting<Boolean>("Boost", false));
    public Setting<Integer> fallTicks = register(new Setting<Integer>("FallTicks", 10, 0, 40, v -> antiKick.getValue()));
    private int tpId = 0;
    private int lagTime = 0;

    public PacketFly() {
        super("PacketFly", "Uses packets to allow you to fly and move.", Category.MOVEMENT, true, false, false);
        INSTANCE = this;
    }

    public static double[] getMoveSpeed(double d) {
        float f = mc.player.movementInput.moveForward;
        float f2 = mc.player.movementInput.moveStrafe;
        float f3 = mc.player.rotationYaw;
        if (f != 0.0f) {
            if (f2 >= 1.0f) {
                f3 += (float) (f > 0.0f ? -45 : 45);
                f2 = 0.0f;
            } else if (f2 <= -1.0f) {
                f3 += (float) (f > 0.0f ? 45 : -45);
                f2 = 0.0f;
            }
            if (f > 0.0f) {
                f = 1.0f;
            } else if (f < 0.0f) {
                f = -1.0f;
            }
        }
        double d2 = Math.sin(Math.toRadians(f3 + 90.0f));
        double d3 = Math.cos(Math.toRadians(f3 + 90.0f));
        double d4 = (double) f * d * d3 + (double) f2 * d * d2;
        double d5 = (double) f * d * d2 - (double) f2 * d * d3;
        if (!isMovingMomentum()) {
            d4 = 0.0;
            d5 = 0.0;
        }
        return new double[]{d4, d5};
    }

    public static boolean isMovingMomentum() {
        return (double) mc.player.moveForward != 0.0 || (double) mc.player.moveStrafing != 0.0;
    }

    private void send(int n, double d, double d2, boolean bl) {
        if (n == 0) {
            mc.player.setVelocity(0.0, 0.0, 0.0);
            return;
        }
        double[] arrd = getMoveSpeed(d);
        for (int i = 1; i < n + 1; ++i) {
            double d3 = arrd[0] * (double) i;
            double d4 = arrd[1] * (double) i;
            double d5 = d2;
            if (!bl) {
                d5 *= i;
            }
            mc.player.motionX = d3;
            mc.player.motionY = d5;
            mc.player.motionZ = d4;
            Vec3d vec3d = mc.player.getPositionVector();
            Vec3d vec3d2 = vec3d.add(d3, d5, d4);
            send(vec3d2);
            send(bounds.getValue().modify(vec3d));
            if (mode.getValue().equals(Mode.SETBACK)) continue;
            predictions.put(++tpId, vec3d2);
            mc.player.connection.sendPacket(new CPacketConfirmTeleport(tpId));
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onPacketReceive(PacketEvent.Receive event) {
        if (event.getPacket() instanceof SPacketPlayerPosLook) {
            SPacketPlayerPosLook sPacketPlayerPosLook = event.getPacket();
            Vec3d vec3d = predictions.get(sPacketPlayerPosLook.getTeleportId());
            if (vec3d != null && vec3d.x == sPacketPlayerPosLook.getX() && vec3d.y == sPacketPlayerPosLook.getY() && vec3d.z == sPacketPlayerPosLook.getZ()) {
                if (!mode.getValue().equals(Mode.SETBACK)) {
                    event.setCanceled(true);
                }
                mc.player.connection.sendPacket(new CPacketConfirmTeleport(sPacketPlayerPosLook.getTeleportId()));
                return;
            }
            ((ISPacketPlayerPosLook) sPacketPlayerPosLook).setYaw(mc.player.rotationYaw);
            ((ISPacketPlayerPosLook) sPacketPlayerPosLook).setPitch(mc.player.rotationPitch);
            mc.player.connection.sendPacket(new CPacketConfirmTeleport(sPacketPlayerPosLook.getTeleportId()));
            lagTime = 10;
            tpId = sPacketPlayerPosLook.getTeleportId();
        }
    }

    private boolean isPhased() {
        return !mc.world.getCollisionBoxes(mc.player, mc.player.getEntityBoundingBox().expand(-0.0625, -0.0625, -0.0625)).isEmpty();
    }

    @SubscribeEvent
    public void onMove(MoveEvent event) {
        int n = (int) Math.floor(factor.getValue().floatValue());
        if (mode.getValue().equals(Mode.FACTOR)) {
            if ((double) mc.player.ticksExisted % increaseTicks.getValue().intValue() < increaseTicks.getValue().intValue() * ((double) factor.getValue().floatValue() - Math.floor(factor.getValue().floatValue()))) {
                ++n;
            }
        } else {
            n = 1;
        }
        double d = conceal.getValue() || --lagTime > 0 || isPhased() ? 0.0624 : 0.2873;
        double d2 = 0.0;
        boolean bl = false;
        if (mc.gameSettings.keyBindJump.isKeyDown()) {
            d2 = 0.0624;
            if (EntityUtil.isMoving()) {
                d *= MOVE_FACTOR;
                d2 *= MOVE_FACTOR;
            }
        } else if (mc.gameSettings.keyBindSneak.isKeyDown()) {
            d2 = -0.0624;
            if (EntityUtil.isMoving()) {
                d *= MOVE_FACTOR;
                d2 *= MOVE_FACTOR;
            }
        } else {
            boolean bl2 = bl = antiKick.getValue() && mc.player.ticksExisted % fallTicks.getValue().intValue() == 0 && !isPhased() && !mc.world.collidesWithAnyBlock(mc.player.getEntityBoundingBox()) && !EntityUtil.isMoving();
            if (bl) {
                n = 1;
                d2 = -0.04;
            }
        }
        send(n, d, d2, bl);
        event.setX(mc.player.motionX);
        event.setY(mc.player.motionY);
        event.setZ(mc.player.motionZ);
        if (!phase.getValue().equals(Phase.NONE)) {
            mc.player.noClip = true;
        }
    }

    @Override
    public void onLogout() {
        disable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
        predictions.clear();
        tpId = 0;
        lagTime = 0;
        mc.player.noClip = false;
    }

    @Override
    public void onUpdate() {
        if (boost.getValue()) {
            SeleneLoader.TICK_TIMER = 1.088f;
        } else {
            SeleneLoader.TICK_TIMER = 1.0f;
        }
    }

    private void send(Vec3d vec3d) {
        ((INetworkManager) mc.player.connection.getNetworkManager()).hookDispatchPacket(new CPacketPlayer.Position(vec3d.x, vec3d.y, vec3d.z, true), null);
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onPacketSend(PacketEvent.Send send) {
        if (send.getPacket() instanceof CPacketPlayer) {
            send.setCanceled(true);
        }
    }

    public enum Bounds {
        UP(1337.0),
        DOWN(-1337.0),
        MIN(512.0);
        private final double yOffset;

        Bounds(double d) {
            yOffset = d;
        }

        public Vec3d modify(Vec3d vec3d) {
            return vec3d.add(0.0, yOffset, 0.0);
        }
    }

    public enum Mode {
        FACTOR,
        FAST,
        SETBACK
    }

    public enum Phase {
        NONE,
        VANILLA,
        NCP
    }
}