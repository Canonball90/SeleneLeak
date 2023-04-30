package mod.selene.injections;

import mod.selene.world.SoulSandEvent;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSoulSand;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings("unused")
@Mixin(BlockSoulSand.class)
public class MixinBlockSoulSand extends Block {

    public MixinBlockSoulSand() {
        super(Material.SAND, MapColor.BROWN);
    }

    @Inject(method = "onEntityCollision", at = @At("HEAD"), cancellable = true)
    public void onEntityCollidedWithBlock(World world, BlockPos blockPos, IBlockState iBlockState, Entity entity, CallbackInfo info) {
        SoulSandEvent soulSandEvent = new SoulSandEvent();
        MinecraftForge.EVENT_BUS.post(soulSandEvent);

        if (soulSandEvent.isCanceled())
            info.cancel();
    }
}
