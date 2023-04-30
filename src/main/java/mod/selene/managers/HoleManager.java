package mod.selene.managers;

import mod.selene.api.utils.BlockUtil;
import mod.selene.api.utils.EntityUtil;
import mod.selene.impl.modules.client.Managers;
import mod.selene.loader.Feature;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

public class HoleManager extends Feature {

    private static final BlockPos[] surroundOffset = BlockUtil.toBlockPos(EntityUtil.getOffsets(0, true));
    private final AtomicBoolean shouldInterrupt = new AtomicBoolean(false);
    private final List<BlockPos> holes = new ArrayList<>();
    private final List<BlockPos> midSafety = new ArrayList<>();
    private ScheduledExecutorService executorService;
    private Thread thread;

    public List<BlockPos> getHoles() {
        return this.holes;
    }

    public List<BlockPos> getMidSafety() {
        return this.midSafety;
    }

    public List<BlockPos> getSortedHoles() {
        this.holes.sort(Comparator.comparingDouble(hole -> mc.player.getDistanceSq(hole)));
        return getHoles();
    }

    public List<BlockPos> calcHoles() {
        List<BlockPos> safeSpots = new ArrayList<>();
        midSafety.clear();
        List<BlockPos> positions = BlockUtil.getSphere(EntityUtil.getPlayerPos(mc.player), Managers.getInstance().holeRange.getValue(), Managers.getInstance().holeRange.getValue().intValue(), false, true, 0);
        for (BlockPos pos : positions) {
            if (!mc.world.getBlockState(pos).getBlock().equals(Blocks.AIR)) {
                continue;
            }

            if (!mc.world.getBlockState(pos.add(0, 1, 0)).getBlock().equals(Blocks.AIR)) {
                continue;
            }

            if (!mc.world.getBlockState(pos.add(0, 2, 0)).getBlock().equals(Blocks.AIR)) {
                continue;
            }

            boolean isSafe = true;
            boolean midSafe = true;
            for (BlockPos offset : surroundOffset) {
                Block block = mc.world.getBlockState(pos.add(offset)).getBlock();
                if (BlockUtil.isBlockUnSolid(block)) {
                    midSafe = false;
                }

                if (block != Blocks.BEDROCK && block != Blocks.OBSIDIAN && block != Blocks.ENDER_CHEST && block != Blocks.ANVIL) {
                    isSafe = false;
                }
            }

            if (isSafe) {
                safeSpots.add(pos);
            }

            if (midSafe) {
                midSafety.add(pos);
            }
        }
        return safeSpots;
    }

    public void settingChanged() {
        if (this.executorService != null) {
            this.executorService.shutdown();
        }
        if (this.thread != null) {
            this.shouldInterrupt.set(true);
        }
    }


    public boolean isSafe(BlockPos pos) {
        boolean isSafe = true;
        for (BlockPos offset : surroundOffset) {
            Block block = mc.world.getBlockState(pos.add(offset)).getBlock();
            if (block != Blocks.BEDROCK) {
                isSafe = false;
                break;
            }
        }
        return isSafe;
    }
}
