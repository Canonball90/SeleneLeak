package mod.selene.world;

import mod.selene.loader.EventHandler;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.Cancelable;

@Cancelable
public class PlayerDamageBlockEvent
extends EventHandler {
    public final BlockPos pos;
    public final EnumFacing facing;

    public PlayerDamageBlockEvent(int n, BlockPos blockPos, EnumFacing enumFacing) {
        super(n);
        this.pos = blockPos;
        this.facing = enumFacing;
    }

    public final BlockPos getPos() {
        return this.pos;
    }
}

