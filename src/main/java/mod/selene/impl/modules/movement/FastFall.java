package mod.selene.impl.modules.movement;

import mod.selene.api.utils.EntityUtil;
import mod.selene.api.utils.Timer;
import mod.selene.impl.Module;
import mod.selene.system.Setting;
import mod.selene.world.PacketEvent;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.HashMap;
import java.util.Map;

public class FastFall
        extends Module {
    private final Timer lagTimer = new Timer();
    public Setting<Mode> mode = this.register(new Setting<Mode>("Type", Mode.Motion));
    public Setting<Integer> height = this.register(new Setting<Integer>("Height", 10, 1, 20));
    public Setting<Boolean> noLag = this.register(new Setting<Boolean>("NoLag", true, v -> mode.getValue() == Mode.Motion));
    public Setting<Float> motionVal = this.register(new Setting<Float>("MotionSpeed", 2.5f, 1.0f, 10.0f, v -> mode.getValue() == Mode.Motion && !this.noLag.getValue()));
    public Setting<Float> timerVal = this.register(new Setting<Float>("TimerSpeed", 2.5f, 1.0f, 10.0f, v -> mode.getValue() == Mode.Timer));
    private boolean useTimer;

    public FastFall() {
        super("FastFall", "Makes you fall faster", Module.Category.MOVEMENT, true, false, false);
    }

    @Override
    public void onDisable() {
        mc.timer.tickLength = 50.0f;
        useTimer = false;
    }

    @Override
    public void onUpdate() {
        if (FastFall.fullNullCheck() || this.shouldReturn()) {
        }
    }

    @Override
    public void onRenderTick() {
        if ((this.height.getValue() > 0 && (traceDown() > this.height.getValue()))
                || mc.player.isEntityInsideOpaqueBlock()
                || mc.player.isInWater()
                || mc.player.isInLava()
                || mc.player.isOnLadder()
                || !lagTimer.passedMs(1000)
                || fullNullCheck()) {
            FastFall.mc.timer.tickLength = 50.0f;
            return;
        }
        if (mc.player.isInWeb) return;
        if (mc.player.onGround) {
            if (this.mode.getValue() == Mode.Motion) {
                if (this.noLag.getValue().booleanValue()) {
                    mc.player.motionY -= 0.62f;
                } else {
                    mc.player.motionY -= this.motionVal.getValue();
                }
            }
        }
        if (traceDown() != 0 && traceDown() <= this.height.getValue() && trace() && mc.player.onGround) {
            mc.player.motionX *= 0.05f;
            mc.player.motionZ *= 0.05f;
        }
        if (this.mode.getValue() == Mode.Timer) {
            if (!mc.player.onGround) {
                if (mc.player.motionY < 0 && useTimer) {
                    FastFall.mc.timer.tickLength = 50.0f / this.timerVal.getValue().floatValue();
                    return;
                } else {
                    useTimer = false;
                }
            } else {
                mc.player.motionY = -0.08;
                useTimer = true;
            }
        }
        FastFall.mc.timer.tickLength = 50.0f;
    }

    private boolean shouldReturn() {
        return FastFall.mc.player.isElytraFlying() || EntityUtil.isClipping() || EntityUtil.isInLiquid() || FastFall.mc.player.isOnLadder() || FastFall.mc.player.capabilities.isFlying || FastFall.mc.player.motionY > 0.0 || FastFall.mc.gameSettings.keyBindJump.isKeyDown() || FastFall.mc.player.isEntityInsideOpaqueBlock() || FastFall.mc.player.noClip || !FastFall.mc.player.onGround;
    }

    @SubscribeEvent
    public void onPacket(PacketEvent event) {
        if (!fullNullCheck()) {
            if (event.getPacket() instanceof SPacketPlayerPosLook) {
                lagTimer.reset();
            }
        }
    }

    private int traceDown() {
        int retval = 0;
        int y = (int) Math.round(mc.player.posY) - 1;
        for (int tracey = y; tracey >= 0; tracey--) {
            RayTraceResult trace = mc.world.rayTraceBlocks(
                    mc.player.getPositionVector(),
                    new Vec3d(mc.player.posX, tracey, mc.player.posZ),
                    false);
            if (trace != null && trace.typeOfHit == RayTraceResult.Type.BLOCK) return retval;
            retval++;
        }
        return retval;
    }

    private boolean trace() {
        AxisAlignedBB bbox = mc.player.getEntityBoundingBox();
        Vec3d basepos = bbox.getCenter();
        double minX = bbox.minX;
        double minZ = bbox.minZ;
        double maxX = bbox.maxX;
        double maxZ = bbox.maxZ;
        Map<Vec3d, Vec3d> positions = new HashMap<>();
        positions.put(
                basepos,
                new Vec3d(basepos.x, basepos.y - 1, basepos.z));
        positions.put(
                new Vec3d(minX, basepos.y, minZ),
                new Vec3d(minX, basepos.y - 1, minZ));
        positions.put(
                new Vec3d(maxX, basepos.y, minZ),
                new Vec3d(maxX, basepos.y - 1, minZ));
        positions.put(
                new Vec3d(minX, basepos.y, maxZ),
                new Vec3d(minX, basepos.y - 1, maxZ));
        positions.put(
                new Vec3d(maxX, basepos.y, maxZ),
                new Vec3d(maxX, basepos.y - 1, maxZ));
        for (Vec3d key : positions.keySet()) {
            RayTraceResult result = mc.world.rayTraceBlocks(key, positions.get(key), true);
            if (result != null && result.typeOfHit == RayTraceResult.Type.BLOCK) return false;
        }
        IBlockState state = mc.world.getBlockState(new BlockPos(mc.player.posX, mc.player.posY - 1, mc.player.posZ));
        return state.getBlock() == Blocks.AIR;
    }

    public enum Mode {
        Motion,
        Timer
    }
}