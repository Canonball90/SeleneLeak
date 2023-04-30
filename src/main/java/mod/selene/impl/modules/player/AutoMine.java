package mod.selene.impl.modules.player;

import mod.selene.loader.SeleneLoader;
import mod.selene.impl.Module;
import mod.selene.impl.modules.hidden.InstantMine;
import mod.selene.system.Setting;
import mod.selene.api.utils.BlockUtil;
import mod.selene.api.utils.EntityUtil;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;

public class AutoMine
extends Module {
    public static EntityPlayer target;
    private final Setting<Float> range;
    private final Setting<Boolean> toggle;
    public final Setting<Boolean> db = this.register(new Setting<Boolean>("Double Mode", false));

    private void surroundMine(BlockPos blockPos) {
        if (InstantMine.breakPos != null) {
            if (InstantMine.breakPos.equals((Object)blockPos)) {
                return;
            }
            if (InstantMine.breakPos.equals((Object)new BlockPos(AutoMine.target.posX, AutoMine.target.posY, AutoMine.target.posZ)) && AutoMine.mc.world.getBlockState(new BlockPos(AutoMine.target.posX, AutoMine.target.posY, AutoMine.target.posZ)).getBlock() != Blocks.AIR) {
                return;
            }
            if (InstantMine.breakPos.equals((Object)new BlockPos(AutoMine.mc.player.posX, AutoMine.mc.player.posY + 2.0, AutoMine.mc.player.posZ))) {
                return;
            }
            if (InstantMine.breakPos.equals((Object)new BlockPos(AutoMine.mc.player.posX, AutoMine.mc.player.posY - 1.0, AutoMine.mc.player.posZ))) {
                return;
            }
            if (AutoMine.mc.world.getBlockState(InstantMine.breakPos).getBlock() == Blocks.WEB) {
                return;
            }
        }
        AutoMine.mc.playerController.onPlayerDamageBlock(blockPos, BlockUtil.getRayTraceFacing(blockPos));
    }

    @Override
    public String getDisplayInfo() {
        if (target != null) {
            return target.getName();
        }
        return null;
    }

    @Override
    public void onUpdate() {
        target = this.getTarget(this.range.getValue().floatValue());
        if (target == null) {
            return;
        }
        BlockPos blockPos = new BlockPos(AutoMine.target.posX, AutoMine.target.posY, AutoMine.target.posZ);
        if (!this.detection(target)) {
            if (this.db.getValue().booleanValue()) {
                if (this.getBlock(blockPos.add(0, 1, 2)).getBlock() == Blocks.AIR && this.getBlock(blockPos.add(0, 0, 1)).getBlock() != Blocks.AIR && this.getBlock(blockPos.add(0, 0, 2)).getBlock() == Blocks.AIR && this.getBlock(blockPos.add(0, 0, 1)).getBlock() != Blocks.BEDROCK && this.getBlock(blockPos.add(0, 0, 1)).getBlock() != Blocks.BEDROCK) {
                    this.surroundMine(blockPos.add(0, 0, 1));
                } else if (this.getBlock(blockPos.add(0, 1, -2)).getBlock() == Blocks.AIR && this.getBlock(blockPos.add(0, 0, -1)).getBlock() != Blocks.AIR && this.getBlock(blockPos.add(0, 0, -2)).getBlock() == Blocks.AIR && this.getBlock(blockPos.add(0, 0, -1)).getBlock() != Blocks.BEDROCK && this.getBlock(blockPos.add(0, 0, -1)).getBlock() != Blocks.BEDROCK) {
                    this.surroundMine(blockPos.add(0, 0, -1));
                } else if (this.getBlock(blockPos.add(2, 1, 0)).getBlock() == Blocks.AIR && this.getBlock(blockPos.add(1, 0, 0)).getBlock() != Blocks.AIR && this.getBlock(blockPos.add(2, 0, 0)).getBlock() == Blocks.AIR && this.getBlock(blockPos.add(1, 0, 0)).getBlock() != Blocks.BEDROCK && this.getBlock(blockPos.add(1, 0, 0)).getBlock() != Blocks.BEDROCK) {
                    this.surroundMine(blockPos.add(1, 0, 0));
                } else if (this.getBlock(blockPos.add(-2, 1, 0)).getBlock() == Blocks.AIR && this.getBlock(blockPos.add(-1, 0, 0)).getBlock() != Blocks.AIR && this.getBlock(blockPos.add(-2, 0, 0)).getBlock() == Blocks.AIR && this.getBlock(blockPos.add(-1, 0, 0)).getBlock() != Blocks.BEDROCK && this.getBlock(blockPos.add(-1, 0, 0)).getBlock() != Blocks.BEDROCK) {
                    this.surroundMine(blockPos.add(-1, 0, 0));
                } else if (this.getBlock(blockPos.add(2, 1, 0)).getBlock() == Blocks.AIR && this.getBlock(blockPos.add(2, 0, 0)).getBlock() != Blocks.AIR && this.getBlock(blockPos.add(1, 0, 0)).getBlock() == Blocks.AIR && this.getBlock(blockPos.add(1, 0, 0)).getBlock() != Blocks.BEDROCK && this.getBlock(blockPos.add(2, 0, 0)).getBlock() != Blocks.BEDROCK) {
                    this.surroundMine(blockPos.add(2, 0, 0));
                } else if (this.getBlock(blockPos.add(-2, 1, 0)).getBlock() == Blocks.AIR && this.getBlock(blockPos.add(-2, 0, 0)).getBlock() != Blocks.AIR && this.getBlock(blockPos.add(-1, 0, 0)).getBlock() == Blocks.AIR && this.getBlock(blockPos.add(-1, 0, 0)).getBlock() != Blocks.BEDROCK && this.getBlock(blockPos.add(-2, 0, 0)).getBlock() != Blocks.BEDROCK) {
                    this.surroundMine(blockPos.add(-2, 0, 0));
                } else if (this.getBlock(blockPos.add(0, 1, -2)).getBlock() == Blocks.AIR && this.getBlock(blockPos.add(0, 0, -2)).getBlock() != Blocks.AIR && this.getBlock(blockPos.add(0, 0, -1)).getBlock() == Blocks.AIR && this.getBlock(blockPos.add(0, 0, -1)).getBlock() != Blocks.BEDROCK && this.getBlock(blockPos.add(0, 0, -2)).getBlock() != Blocks.BEDROCK) {
                    this.surroundMine(blockPos.add(0, 0, -2));
                } else if (this.getBlock(blockPos.add(0, 1, 2)).getBlock() == Blocks.AIR && this.getBlock(blockPos.add(0, 0, 2)).getBlock() != Blocks.AIR && this.getBlock(blockPos.add(0, 0, 1)).getBlock() == Blocks.AIR && this.getBlock(blockPos.add(0, 0, 1)).getBlock() != Blocks.BEDROCK && this.getBlock(blockPos.add(0, 0, 2)).getBlock() != Blocks.BEDROCK) {
                    this.surroundMine(blockPos.add(0, 0, 2));
                } else if (this.getBlock(blockPos.add(2, 1, 0)).getBlock() == Blocks.AIR && this.getBlock(blockPos.add(1, 0, 0)).getBlock() != Blocks.AIR && this.getBlock(blockPos.add(2, 0, 0)).getBlock() != Blocks.AIR && this.getBlock(blockPos.add(1, 0, 0)).getBlock() != Blocks.BEDROCK && this.getBlock(blockPos.add(2, 0, 0)).getBlock() != Blocks.BEDROCK) {
                    this.surroundMine(blockPos.add(2, 0, 0));
                    if (InstantMine.breakPos2 == null) {
                        this.surroundMine(blockPos.add(1, 0, 0));
                    }
                } else if (this.getBlock(blockPos.add(-2, 1, 0)).getBlock() == Blocks.AIR && this.getBlock(blockPos.add(-1, 0, 0)).getBlock() != Blocks.AIR && this.getBlock(blockPos.add(-2, 0, 0)).getBlock() != Blocks.AIR && this.getBlock(blockPos.add(-1, 0, 0)).getBlock() != Blocks.BEDROCK && this.getBlock(blockPos.add(-2, 0, 0)).getBlock() != Blocks.BEDROCK) {
                    this.surroundMine(blockPos.add(-2, 0, 0));
                    if (InstantMine.breakPos2 == null) {
                        this.surroundMine(blockPos.add(-1, 0, 0));
                    }
                } else if (this.getBlock(blockPos.add(0, 1, -2)).getBlock() == Blocks.AIR && this.getBlock(blockPos.add(0, 0, -1)).getBlock() != Blocks.AIR && this.getBlock(blockPos.add(0, 0, -2)).getBlock() != Blocks.AIR && this.getBlock(blockPos.add(0, 0, -1)).getBlock() != Blocks.BEDROCK && this.getBlock(blockPos.add(0, 0, -2)).getBlock() != Blocks.BEDROCK) {
                    this.surroundMine(blockPos.add(0, 0, -2));
                    if (InstantMine.breakPos2 == null) {
                        this.surroundMine(blockPos.add(0, 0, -1));
                    }
                } else if (this.getBlock(blockPos.add(0, 1, 2)).getBlock() == Blocks.AIR && this.getBlock(blockPos.add(0, 0, 1)).getBlock() != Blocks.AIR && this.getBlock(blockPos.add(0, 0, 2)).getBlock() != Blocks.AIR && this.getBlock(blockPos.add(0, 0, 1)).getBlock() != Blocks.BEDROCK && this.getBlock(blockPos.add(0, 0, 2)).getBlock() != Blocks.BEDROCK) {
                    this.surroundMine(blockPos.add(0, 0, 2));
                    if (InstantMine.breakPos2 == null) {
                        this.surroundMine(blockPos.add(0, 0, 1));
                    }
                } else if (this.getBlock(blockPos.add(0, 2, 1)).getBlock() == Blocks.AIR && this.getBlock(blockPos.add(0, 1, 1)).getBlock() != Blocks.AIR && this.getBlock(blockPos.add(0, 0, 1)).getBlock() == Blocks.AIR && this.getBlock(blockPos.add(0, 1, 1)).getBlock() != Blocks.BEDROCK && this.getBlock(blockPos.add(0, 1, 1)).getBlock() != Blocks.BEDROCK) {
                    this.surroundMine(blockPos.add(0, 1, 1));
                } else if (this.getBlock(blockPos.add(0, 2, 1)).getBlock() == Blocks.AIR && this.getBlock(blockPos.add(0, 0, 1)).getBlock() != Blocks.AIR && this.getBlock(blockPos.add(0, 1, 1)).getBlock() == Blocks.AIR && this.getBlock(blockPos.add(0, 0, 1)).getBlock() != Blocks.BEDROCK && this.getBlock(blockPos.add(0, 0, 1)).getBlock() != Blocks.BEDROCK) {
                    this.surroundMine(blockPos.add(0, 0, 1));
                } else if (this.getBlock(blockPos.add(0, 2, -1)).getBlock() == Blocks.AIR && this.getBlock(blockPos.add(0, 0, -1)).getBlock() != Blocks.AIR && this.getBlock(blockPos.add(0, 1, -1)).getBlock() == Blocks.AIR && this.getBlock(blockPos.add(0, 0, -1)).getBlock() != Blocks.BEDROCK && this.getBlock(blockPos.add(0, 0, -1)).getBlock() != Blocks.BEDROCK) {
                    this.surroundMine(blockPos.add(0, 0, -1));
                } else if (this.getBlock(blockPos.add(1, 2, 0)).getBlock() == Blocks.AIR && this.getBlock(blockPos.add(1, 0, 0)).getBlock() != Blocks.AIR && this.getBlock(blockPos.add(1, 1, 0)).getBlock() == Blocks.AIR && this.getBlock(blockPos.add(1, 0, 0)).getBlock() != Blocks.BEDROCK && this.getBlock(blockPos.add(1, 0, 0)).getBlock() != Blocks.BEDROCK) {
                    this.surroundMine(blockPos.add(1, 0, 0));
                } else if (this.getBlock(blockPos.add(-1, 2, 0)).getBlock() == Blocks.AIR && this.getBlock(blockPos.add(-1, 0, 0)).getBlock() != Blocks.AIR && this.getBlock(blockPos.add(-1, 1, 0)).getBlock() == Blocks.AIR && this.getBlock(blockPos.add(-1, 0, 0)).getBlock() != Blocks.BEDROCK && this.getBlock(blockPos.add(-1, 0, 0)).getBlock() != Blocks.BEDROCK) {
                    this.surroundMine(blockPos.add(-1, 0, 0));
                } else if (this.getBlock(blockPos.add(1, 2, 0)).getBlock() == Blocks.AIR && this.getBlock(blockPos.add(1, 1, 0)).getBlock() != Blocks.AIR && this.getBlock(blockPos.add(1, 0, 0)).getBlock() == Blocks.AIR && this.getBlock(blockPos.add(1, 1, 0)).getBlock() != Blocks.BEDROCK) {
                    this.surroundMine(blockPos.add(1, 1, 0));
                } else if (this.getBlock(blockPos.add(-1, 2, 0)).getBlock() == Blocks.AIR && this.getBlock(blockPos.add(-1, 1, 0)).getBlock() != Blocks.AIR && this.getBlock(blockPos.add(-1, 0, 0)).getBlock() == Blocks.AIR && this.getBlock(blockPos.add(-1, 1, 0)).getBlock() != Blocks.BEDROCK) {
                    this.surroundMine(blockPos.add(-1, 1, 0));
                } else if (this.getBlock(blockPos.add(0, 2, -1)).getBlock() == Blocks.AIR && this.getBlock(blockPos.add(0, 1, -1)).getBlock() != Blocks.AIR && this.getBlock(blockPos.add(0, 0, -1)).getBlock() == Blocks.AIR && this.getBlock(blockPos.add(0, 1, -1)).getBlock() != Blocks.BEDROCK) {
                    this.surroundMine(blockPos.add(0, 1, -1));
                } else if (this.getBlock(blockPos.add(1, 2, 0)).getBlock() == Blocks.AIR && this.getBlock(blockPos.add(1, 0, 0)).getBlock() != Blocks.AIR && this.getBlock(blockPos.add(1, 1, 0)).getBlock() != Blocks.AIR && this.getBlock(blockPos.add(1, 0, 0)).getBlock() != Blocks.BEDROCK && this.getBlock(blockPos.add(1, 1, 0)).getBlock() != Blocks.BEDROCK) {
                    this.surroundMine(blockPos.add(1, 1, 0));
                    if (InstantMine.breakPos2 == null) {
                        this.surroundMine(blockPos.add(1, 0, 0));
                    }
                } else if (this.getBlock(blockPos.add(-1, 2, 0)).getBlock() == Blocks.AIR && this.getBlock(blockPos.add(-1, 0, 0)).getBlock() != Blocks.AIR && this.getBlock(blockPos.add(-1, 1, 0)).getBlock() != Blocks.AIR && this.getBlock(blockPos.add(-1, 0, 0)).getBlock() != Blocks.BEDROCK && this.getBlock(blockPos.add(-1, 1, 0)).getBlock() != Blocks.BEDROCK) {
                    this.surroundMine(blockPos.add(-1, 1, 0));
                    if (InstantMine.breakPos2 == null) {
                        this.surroundMine(blockPos.add(-1, 0, 0));
                    }
                } else if (this.getBlock(blockPos.add(0, 2, -1)).getBlock() == Blocks.AIR && this.getBlock(blockPos.add(0, 0, -1)).getBlock() != Blocks.AIR && this.getBlock(blockPos.add(0, 1, -1)).getBlock() != Blocks.AIR && this.getBlock(blockPos.add(0, 0, -1)).getBlock() != Blocks.BEDROCK && this.getBlock(blockPos.add(0, 1, -1)).getBlock() != Blocks.BEDROCK) {
                    this.surroundMine(blockPos.add(0, 1, -1));
                    if (InstantMine.breakPos2 == null) {
                        this.surroundMine(blockPos.add(0, 0, -1));
                    }
                } else if (this.getBlock(blockPos.add(0, 2, 1)).getBlock() == Blocks.AIR && this.getBlock(blockPos.add(0, 0, 1)).getBlock() != Blocks.AIR && this.getBlock(blockPos.add(0, 1, 1)).getBlock() != Blocks.AIR && this.getBlock(blockPos.add(0, 0, 1)).getBlock() != Blocks.BEDROCK && this.getBlock(blockPos.add(0, 1, 1)).getBlock() != Blocks.BEDROCK) {
                    this.surroundMine(blockPos.add(0, 1, 1));
                    if (InstantMine.breakPos2 == null) {
                        this.surroundMine(blockPos.add(0, 0, 1));
                    }
                } else if (this.getBlock(blockPos.add(-1, 0, 0)).getBlock() != Blocks.BEDROCK && this.getBlock(blockPos.add(-2, 0, 0)).getBlock() != Blocks.BEDROCK && this.getBlock(blockPos.add(-2, 1, 0)).getBlock() != Blocks.AIR && this.getBlock(blockPos.add(-2, 1, 0)).getBlock() != Blocks.BEDROCK) {
                    this.surroundMine(blockPos.add(-2, 1, 0));
                } else if (this.getBlock(blockPos.add(1, 0, 0)).getBlock() != Blocks.BEDROCK && this.getBlock(blockPos.add(2, 0, 0)).getBlock() != Blocks.BEDROCK && this.getBlock(blockPos.add(2, 1, 0)).getBlock() != Blocks.AIR && this.getBlock(blockPos.add(2, 1, 0)).getBlock() != Blocks.BEDROCK) {
                    this.surroundMine(blockPos.add(2, 1, 0));
                } else if (this.getBlock(blockPos.add(0, 0, 1)).getBlock() != Blocks.BEDROCK && this.getBlock(blockPos.add(0, 0, 2)).getBlock() != Blocks.BEDROCK && this.getBlock(blockPos.add(0, 1, 2)).getBlock() != Blocks.AIR && this.getBlock(blockPos.add(0, 1, 2)).getBlock() != Blocks.BEDROCK) {
                    this.surroundMine(blockPos.add(0, 1, 2));
                } else if (this.getBlock(blockPos.add(0, 0, -1)).getBlock() != Blocks.BEDROCK && this.getBlock(blockPos.add(0, 0, -2)).getBlock() != Blocks.BEDROCK && this.getBlock(blockPos.add(0, 1, -2)).getBlock() != Blocks.AIR && this.getBlock(blockPos.add(0, 1, -2)).getBlock() != Blocks.BEDROCK) {
                    this.surroundMine(blockPos.add(0, 1, -2));
                } else if (this.getBlock(blockPos.add(-1, 0, 0)).getBlock() != Blocks.BEDROCK && this.getBlock(blockPos.add(-1, 1, 0)).getBlock() != Blocks.BEDROCK && this.getBlock(blockPos.add(-1, 2, 0)).getBlock() != Blocks.AIR && this.getBlock(blockPos.add(-1, 2, 0)).getBlock() != Blocks.BEDROCK) {
                    this.surroundMine(blockPos.add(-1, 2, 0));
                } else if (this.getBlock(blockPos.add(1, 0, 0)).getBlock() != Blocks.BEDROCK && this.getBlock(blockPos.add(1, 1, 0)).getBlock() != Blocks.BEDROCK && this.getBlock(blockPos.add(1, 2, 0)).getBlock() != Blocks.AIR && this.getBlock(blockPos.add(1, 2, 0)).getBlock() != Blocks.BEDROCK) {
                    this.surroundMine(blockPos.add(1, 2, 0));
                } else if (this.getBlock(blockPos.add(0, 0, 1)).getBlock() != Blocks.BEDROCK && this.getBlock(blockPos.add(0, 1, 1)).getBlock() != Blocks.BEDROCK && this.getBlock(blockPos.add(0, 2, 1)).getBlock() != Blocks.AIR && this.getBlock(blockPos.add(0, 2, 1)).getBlock() != Blocks.BEDROCK) {
                    this.surroundMine(blockPos.add(0, 2, 1));
                } else if (this.getBlock(blockPos.add(0, 0, -1)).getBlock() != Blocks.BEDROCK && this.getBlock(blockPos.add(0, 1, -1)).getBlock() != Blocks.BEDROCK && this.getBlock(blockPos.add(0, 2, -1)).getBlock() != Blocks.AIR && this.getBlock(blockPos.add(0, 2, -1)).getBlock() != Blocks.BEDROCK) {
                    this.surroundMine(blockPos.add(0, 2, -1));
                }
            } else if (this.getBlock(blockPos.add(0, 1, 2)).getBlock() == Blocks.AIR && this.getBlock(blockPos.add(0, 0, 1)).getBlock() != Blocks.AIR && this.getBlock(blockPos.add(0, 0, 2)).getBlock() == Blocks.AIR && this.getBlock(blockPos.add(0, 0, 1)).getBlock() != Blocks.BEDROCK && this.getBlock(blockPos.add(0, 0, 1)).getBlock() != Blocks.BEDROCK) {
                this.surroundMine(blockPos.add(0, 0, 1));
            } else if (this.getBlock(blockPos.add(0, 1, -2)).getBlock() == Blocks.AIR && this.getBlock(blockPos.add(0, 0, -1)).getBlock() != Blocks.AIR && this.getBlock(blockPos.add(0, 0, -2)).getBlock() == Blocks.AIR && this.getBlock(blockPos.add(0, 0, -1)).getBlock() != Blocks.BEDROCK && this.getBlock(blockPos.add(0, 0, -1)).getBlock() != Blocks.BEDROCK) {
                this.surroundMine(blockPos.add(0, 0, -1));
            } else if (this.getBlock(blockPos.add(2, 1, 0)).getBlock() == Blocks.AIR && this.getBlock(blockPos.add(1, 0, 0)).getBlock() != Blocks.AIR && this.getBlock(blockPos.add(2, 0, 0)).getBlock() == Blocks.AIR && this.getBlock(blockPos.add(1, 0, 0)).getBlock() != Blocks.BEDROCK && this.getBlock(blockPos.add(1, 0, 0)).getBlock() != Blocks.BEDROCK) {
                this.surroundMine(blockPos.add(1, 0, 0));
            } else if (this.getBlock(blockPos.add(-2, 1, 0)).getBlock() == Blocks.AIR && this.getBlock(blockPos.add(-1, 0, 0)).getBlock() != Blocks.AIR && this.getBlock(blockPos.add(-2, 0, 0)).getBlock() == Blocks.AIR && this.getBlock(blockPos.add(-1, 0, 0)).getBlock() != Blocks.BEDROCK && this.getBlock(blockPos.add(-1, 0, 0)).getBlock() != Blocks.BEDROCK) {
                this.surroundMine(blockPos.add(-1, 0, 0));
            } else if (this.getBlock(blockPos.add(2, 1, 0)).getBlock() == Blocks.AIR && this.getBlock(blockPos.add(2, 0, 0)).getBlock() != Blocks.AIR && this.getBlock(blockPos.add(1, 0, 0)).getBlock() == Blocks.AIR && this.getBlock(blockPos.add(1, 0, 0)).getBlock() != Blocks.BEDROCK && this.getBlock(blockPos.add(2, 0, 0)).getBlock() != Blocks.BEDROCK) {
                this.surroundMine(blockPos.add(2, 0, 0));
            } else if (this.getBlock(blockPos.add(-2, 1, 0)).getBlock() == Blocks.AIR && this.getBlock(blockPos.add(-2, 0, 0)).getBlock() != Blocks.AIR && this.getBlock(blockPos.add(-1, 0, 0)).getBlock() == Blocks.AIR && this.getBlock(blockPos.add(-1, 0, 0)).getBlock() != Blocks.BEDROCK && this.getBlock(blockPos.add(-2, 0, 0)).getBlock() != Blocks.BEDROCK) {
                this.surroundMine(blockPos.add(-2, 0, 0));
            } else if (this.getBlock(blockPos.add(0, 1, -2)).getBlock() == Blocks.AIR && this.getBlock(blockPos.add(0, 0, -2)).getBlock() != Blocks.AIR && this.getBlock(blockPos.add(0, 0, -1)).getBlock() == Blocks.AIR && this.getBlock(blockPos.add(0, 0, -1)).getBlock() != Blocks.BEDROCK && this.getBlock(blockPos.add(0, 0, -2)).getBlock() != Blocks.BEDROCK) {
                this.surroundMine(blockPos.add(0, 0, -2));
            } else if (this.getBlock(blockPos.add(0, 1, 2)).getBlock() == Blocks.AIR && this.getBlock(blockPos.add(0, 0, 2)).getBlock() != Blocks.AIR && this.getBlock(blockPos.add(0, 0, 1)).getBlock() == Blocks.AIR && this.getBlock(blockPos.add(0, 0, 1)).getBlock() != Blocks.BEDROCK && this.getBlock(blockPos.add(0, 0, 2)).getBlock() != Blocks.BEDROCK) {
                this.surroundMine(blockPos.add(0, 0, 2));
            } else if (this.getBlock(blockPos.add(2, 1, 0)).getBlock() == Blocks.AIR && this.getBlock(blockPos.add(1, 0, 0)).getBlock() != Blocks.AIR && this.getBlock(blockPos.add(2, 0, 0)).getBlock() != Blocks.AIR && this.getBlock(blockPos.add(1, 0, 0)).getBlock() != Blocks.BEDROCK && this.getBlock(blockPos.add(2, 0, 0)).getBlock() != Blocks.BEDROCK) {
                this.surroundMine(blockPos.add(2, 0, 0));
            } else if (this.getBlock(blockPos.add(-2, 1, 0)).getBlock() == Blocks.AIR && this.getBlock(blockPos.add(-1, 0, 0)).getBlock() != Blocks.AIR && this.getBlock(blockPos.add(-2, 0, 0)).getBlock() != Blocks.AIR && this.getBlock(blockPos.add(-1, 0, 0)).getBlock() != Blocks.BEDROCK && this.getBlock(blockPos.add(-2, 0, 0)).getBlock() != Blocks.BEDROCK) {
                this.surroundMine(blockPos.add(-2, 0, 0));
            } else if (this.getBlock(blockPos.add(0, 1, -2)).getBlock() == Blocks.AIR && this.getBlock(blockPos.add(0, 0, -1)).getBlock() != Blocks.AIR && this.getBlock(blockPos.add(0, 0, -2)).getBlock() != Blocks.AIR && this.getBlock(blockPos.add(0, 0, -1)).getBlock() != Blocks.BEDROCK && this.getBlock(blockPos.add(0, 0, -2)).getBlock() != Blocks.BEDROCK) {
                this.surroundMine(blockPos.add(0, 0, -2));
            } else if (this.getBlock(blockPos.add(0, 1, 2)).getBlock() == Blocks.AIR && this.getBlock(blockPos.add(0, 0, 1)).getBlock() != Blocks.AIR && this.getBlock(blockPos.add(0, 0, 2)).getBlock() != Blocks.AIR && this.getBlock(blockPos.add(0, 0, 1)).getBlock() != Blocks.BEDROCK && this.getBlock(blockPos.add(0, 0, 2)).getBlock() != Blocks.BEDROCK) {
                this.surroundMine(blockPos.add(0, 0, 2));
            } else if (this.getBlock(blockPos.add(0, 2, 1)).getBlock() == Blocks.AIR && this.getBlock(blockPos.add(0, 1, 1)).getBlock() != Blocks.AIR && this.getBlock(blockPos.add(0, 0, 1)).getBlock() == Blocks.AIR && this.getBlock(blockPos.add(0, 1, 1)).getBlock() != Blocks.BEDROCK && this.getBlock(blockPos.add(0, 1, 1)).getBlock() != Blocks.BEDROCK) {
                this.surroundMine(blockPos.add(0, 1, 1));
            } else if (this.getBlock(blockPos.add(0, 2, 1)).getBlock() == Blocks.AIR && this.getBlock(blockPos.add(0, 0, 1)).getBlock() != Blocks.AIR && this.getBlock(blockPos.add(0, 1, 1)).getBlock() == Blocks.AIR && this.getBlock(blockPos.add(0, 0, 1)).getBlock() != Blocks.BEDROCK && this.getBlock(blockPos.add(0, 0, 1)).getBlock() != Blocks.BEDROCK) {
                this.surroundMine(blockPos.add(0, 0, 1));
            } else if (this.getBlock(blockPos.add(0, 2, -1)).getBlock() == Blocks.AIR && this.getBlock(blockPos.add(0, 0, -1)).getBlock() != Blocks.AIR && this.getBlock(blockPos.add(0, 1, -1)).getBlock() == Blocks.AIR && this.getBlock(blockPos.add(0, 0, -1)).getBlock() != Blocks.BEDROCK && this.getBlock(blockPos.add(0, 0, -1)).getBlock() != Blocks.BEDROCK) {
                this.surroundMine(blockPos.add(0, 0, -1));
            } else if (this.getBlock(blockPos.add(1, 2, 0)).getBlock() == Blocks.AIR && this.getBlock(blockPos.add(1, 0, 0)).getBlock() != Blocks.AIR && this.getBlock(blockPos.add(1, 1, 0)).getBlock() == Blocks.AIR && this.getBlock(blockPos.add(1, 0, 0)).getBlock() != Blocks.BEDROCK && this.getBlock(blockPos.add(1, 0, 0)).getBlock() != Blocks.BEDROCK) {
                this.surroundMine(blockPos.add(1, 0, 0));
            } else if (this.getBlock(blockPos.add(-1, 2, 0)).getBlock() == Blocks.AIR && this.getBlock(blockPos.add(-1, 0, 0)).getBlock() != Blocks.AIR && this.getBlock(blockPos.add(-1, 1, 0)).getBlock() == Blocks.AIR && this.getBlock(blockPos.add(-1, 0, 0)).getBlock() != Blocks.BEDROCK && this.getBlock(blockPos.add(-1, 0, 0)).getBlock() != Blocks.BEDROCK) {
                this.surroundMine(blockPos.add(-1, 0, 0));
            } else if (this.getBlock(blockPos.add(1, 2, 0)).getBlock() == Blocks.AIR && this.getBlock(blockPos.add(1, 1, 0)).getBlock() != Blocks.AIR && this.getBlock(blockPos.add(1, 0, 0)).getBlock() == Blocks.AIR && this.getBlock(blockPos.add(1, 1, 0)).getBlock() != Blocks.BEDROCK) {
                this.surroundMine(blockPos.add(1, 1, 0));
            } else if (this.getBlock(blockPos.add(-1, 2, 0)).getBlock() == Blocks.AIR && this.getBlock(blockPos.add(-1, 1, 0)).getBlock() != Blocks.AIR && this.getBlock(blockPos.add(-1, 0, 0)).getBlock() == Blocks.AIR && this.getBlock(blockPos.add(-1, 1, 0)).getBlock() != Blocks.BEDROCK) {
                this.surroundMine(blockPos.add(-1, 1, 0));
            } else if (this.getBlock(blockPos.add(0, 2, -1)).getBlock() == Blocks.AIR && this.getBlock(blockPos.add(0, 1, -1)).getBlock() != Blocks.AIR && this.getBlock(blockPos.add(0, 0, -1)).getBlock() == Blocks.AIR && this.getBlock(blockPos.add(0, 1, -1)).getBlock() != Blocks.BEDROCK) {
                this.surroundMine(blockPos.add(0, 1, -1));
            } else if (this.getBlock(blockPos.add(1, 2, 0)).getBlock() == Blocks.AIR && this.getBlock(blockPos.add(1, 0, 0)).getBlock() != Blocks.AIR && this.getBlock(blockPos.add(1, 1, 0)).getBlock() != Blocks.AIR && this.getBlock(blockPos.add(1, 0, 0)).getBlock() != Blocks.BEDROCK && this.getBlock(blockPos.add(1, 1, 0)).getBlock() != Blocks.BEDROCK) {
                this.surroundMine(blockPos.add(1, 1, 0));
            } else if (this.getBlock(blockPos.add(-1, 2, 0)).getBlock() == Blocks.AIR && this.getBlock(blockPos.add(-1, 0, 0)).getBlock() != Blocks.AIR && this.getBlock(blockPos.add(-1, 1, 0)).getBlock() != Blocks.AIR && this.getBlock(blockPos.add(-1, 0, 0)).getBlock() != Blocks.BEDROCK && this.getBlock(blockPos.add(-1, 1, 0)).getBlock() != Blocks.BEDROCK) {
                this.surroundMine(blockPos.add(-1, 1, 0));
            } else if (this.getBlock(blockPos.add(0, 2, -1)).getBlock() == Blocks.AIR && this.getBlock(blockPos.add(0, 0, -1)).getBlock() != Blocks.AIR && this.getBlock(blockPos.add(0, 1, -1)).getBlock() != Blocks.AIR && this.getBlock(blockPos.add(0, 0, -1)).getBlock() != Blocks.BEDROCK && this.getBlock(blockPos.add(0, 1, -1)).getBlock() != Blocks.BEDROCK) {
                this.surroundMine(blockPos.add(0, 1, -1));
            } else if (this.getBlock(blockPos.add(0, 2, 1)).getBlock() == Blocks.AIR && this.getBlock(blockPos.add(0, 0, 1)).getBlock() != Blocks.AIR && this.getBlock(blockPos.add(0, 1, 1)).getBlock() != Blocks.AIR && this.getBlock(blockPos.add(0, 0, 1)).getBlock() != Blocks.BEDROCK && this.getBlock(blockPos.add(0, 1, 1)).getBlock() != Blocks.BEDROCK) {
                this.surroundMine(blockPos.add(0, 1, 1));
            } else if (this.getBlock(blockPos.add(-1, 0, 0)).getBlock() != Blocks.BEDROCK && this.getBlock(blockPos.add(-2, 0, 0)).getBlock() != Blocks.BEDROCK && this.getBlock(blockPos.add(-2, 1, 0)).getBlock() != Blocks.AIR && this.getBlock(blockPos.add(-2, 1, 0)).getBlock() != Blocks.BEDROCK) {
                this.surroundMine(blockPos.add(-2, 1, 0));
            } else if (this.getBlock(blockPos.add(1, 0, 0)).getBlock() != Blocks.BEDROCK && this.getBlock(blockPos.add(2, 0, 0)).getBlock() != Blocks.BEDROCK && this.getBlock(blockPos.add(2, 1, 0)).getBlock() != Blocks.AIR && this.getBlock(blockPos.add(2, 1, 0)).getBlock() != Blocks.BEDROCK) {
                this.surroundMine(blockPos.add(2, 1, 0));
            } else if (this.getBlock(blockPos.add(0, 0, 1)).getBlock() != Blocks.BEDROCK && this.getBlock(blockPos.add(0, 0, 2)).getBlock() != Blocks.BEDROCK && this.getBlock(blockPos.add(0, 1, 2)).getBlock() != Blocks.AIR && this.getBlock(blockPos.add(0, 1, 2)).getBlock() != Blocks.BEDROCK) {
                this.surroundMine(blockPos.add(0, 1, 2));
            } else if (this.getBlock(blockPos.add(0, 0, -1)).getBlock() != Blocks.BEDROCK && this.getBlock(blockPos.add(0, 0, -2)).getBlock() != Blocks.BEDROCK && this.getBlock(blockPos.add(0, 1, -2)).getBlock() != Blocks.AIR && this.getBlock(blockPos.add(0, 1, -2)).getBlock() != Blocks.BEDROCK) {
                this.surroundMine(blockPos.add(0, 1, -2));
            } else if (this.getBlock(blockPos.add(-1, 0, 0)).getBlock() != Blocks.BEDROCK && this.getBlock(blockPos.add(-1, 1, 0)).getBlock() != Blocks.BEDROCK && this.getBlock(blockPos.add(-1, 2, 0)).getBlock() != Blocks.AIR && this.getBlock(blockPos.add(-1, 2, 0)).getBlock() != Blocks.BEDROCK) {
                this.surroundMine(blockPos.add(-1, 2, 0));
            } else if (this.getBlock(blockPos.add(1, 0, 0)).getBlock() != Blocks.BEDROCK && this.getBlock(blockPos.add(1, 1, 0)).getBlock() != Blocks.BEDROCK && this.getBlock(blockPos.add(1, 2, 0)).getBlock() != Blocks.AIR && this.getBlock(blockPos.add(1, 2, 0)).getBlock() != Blocks.BEDROCK) {
                this.surroundMine(blockPos.add(1, 2, 0));
            } else if (this.getBlock(blockPos.add(0, 0, 1)).getBlock() != Blocks.BEDROCK && this.getBlock(blockPos.add(0, 1, 1)).getBlock() != Blocks.BEDROCK && this.getBlock(blockPos.add(0, 2, 1)).getBlock() != Blocks.AIR && this.getBlock(blockPos.add(0, 2, 1)).getBlock() != Blocks.BEDROCK) {
                this.surroundMine(blockPos.add(0, 2, 1));
            } else if (this.getBlock(blockPos.add(0, 0, -1)).getBlock() != Blocks.BEDROCK && this.getBlock(blockPos.add(0, 1, -1)).getBlock() != Blocks.BEDROCK && this.getBlock(blockPos.add(0, 2, -1)).getBlock() != Blocks.AIR && this.getBlock(blockPos.add(0, 2, -1)).getBlock() != Blocks.BEDROCK) {
                this.surroundMine(blockPos.add(0, 2, -1));
            }
        }
        if (this.toggle.getValue().booleanValue()) {
            this.disable();
        }
    }

    private IBlockState getBlock(BlockPos blockPos) {
        return AutoMine.mc.world.getBlockState(blockPos);
    }

    public AutoMine() {
        super("AutoMine", "Automatically breaks the enemy's hole", Category.PLAYER, true, false, false);
        this.range = this.register(new Setting<Float>("Range", Float.valueOf(5.0f), Float.valueOf(1.0f), Float.valueOf(8.0f)));
        this.toggle = this.register(new Setting<Boolean>("Toggle", false));
    }

    private EntityPlayer getTarget(double d) {
        EntityPlayer entityPlayer = null;
        double d2 = Math.pow(d, 2.0) + 1.0;
        for (EntityPlayer entityPlayer2 : AutoMine.mc.world.playerEntities) {
            if (EntityUtil.isntValid((Entity)entityPlayer2, d) || SeleneLoader.speedManager.getPlayerSpeed(entityPlayer2) > 10.0) continue;
            if (entityPlayer == null) {
                entityPlayer = entityPlayer2;
                d2 = AutoMine.mc.player.getDistanceSq((Entity)entityPlayer2);
                continue;
            }
            if (AutoMine.mc.player.getDistanceSq((Entity)entityPlayer2) >= d2) continue;
            entityPlayer = entityPlayer2;
            d2 = AutoMine.mc.player.getDistanceSq((Entity)entityPlayer2);
        }
        return entityPlayer;
    }

    private boolean detection(EntityPlayer entityPlayer) {
        return AutoMine.mc.world.getBlockState(new BlockPos(entityPlayer.posX + 1.2, entityPlayer.posY, entityPlayer.posZ)).getBlock() == Blocks.AIR & AutoMine.mc.world.getBlockState(new BlockPos(entityPlayer.posX + 1.2, entityPlayer.posY + 1.0, entityPlayer.posZ)).getBlock() == Blocks.AIR || AutoMine.mc.world.getBlockState(new BlockPos(entityPlayer.posX - 1.2, entityPlayer.posY, entityPlayer.posZ)).getBlock() == Blocks.AIR & AutoMine.mc.world.getBlockState(new BlockPos(entityPlayer.posX - 1.2, entityPlayer.posY + 1.0, entityPlayer.posZ)).getBlock() == Blocks.AIR || AutoMine.mc.world.getBlockState(new BlockPos(entityPlayer.posX, entityPlayer.posY, entityPlayer.posZ + 1.2)).getBlock() == Blocks.AIR & AutoMine.mc.world.getBlockState(new BlockPos(entityPlayer.posX, entityPlayer.posY + 1.0, entityPlayer.posZ + 1.2)).getBlock() == Blocks.AIR || AutoMine.mc.world.getBlockState(new BlockPos(entityPlayer.posX, entityPlayer.posY, entityPlayer.posZ - 1.2)).getBlock() == Blocks.AIR & AutoMine.mc.world.getBlockState(new BlockPos(entityPlayer.posX, entityPlayer.posY + 1.0, entityPlayer.posZ - 1.2)).getBlock() == Blocks.AIR || AutoMine.mc.world.getBlockState(new BlockPos(entityPlayer.posX + 2.2, entityPlayer.posY + 1.0, entityPlayer.posZ)).getBlock() == Blocks.AIR & AutoMine.mc.world.getBlockState(new BlockPos(entityPlayer.posX + 2.2, entityPlayer.posY, entityPlayer.posZ)).getBlock() == Blocks.AIR & AutoMine.mc.world.getBlockState(new BlockPos(entityPlayer.posX + 1.2, entityPlayer.posY, entityPlayer.posZ)).getBlock() == Blocks.AIR || AutoMine.mc.world.getBlockState(new BlockPos(entityPlayer.posX - 2.2, entityPlayer.posY + 1.0, entityPlayer.posZ)).getBlock() == Blocks.AIR & AutoMine.mc.world.getBlockState(new BlockPos(entityPlayer.posX - 2.2, entityPlayer.posY, entityPlayer.posZ)).getBlock() == Blocks.AIR & AutoMine.mc.world.getBlockState(new BlockPos(entityPlayer.posX - 1.2, entityPlayer.posY, entityPlayer.posZ)).getBlock() == Blocks.AIR || AutoMine.mc.world.getBlockState(new BlockPos(entityPlayer.posX, entityPlayer.posY + 1.0, entityPlayer.posZ + 2.2)).getBlock() == Blocks.AIR & AutoMine.mc.world.getBlockState(new BlockPos(entityPlayer.posX, entityPlayer.posY, entityPlayer.posZ + 2.2)).getBlock() == Blocks.AIR & AutoMine.mc.world.getBlockState(new BlockPos(entityPlayer.posX, entityPlayer.posY, entityPlayer.posZ + 1.2)).getBlock() == Blocks.AIR || AutoMine.mc.world.getBlockState(new BlockPos(entityPlayer.posX, entityPlayer.posY + 1.0, entityPlayer.posZ - 2.2)).getBlock() == Blocks.AIR & AutoMine.mc.world.getBlockState(new BlockPos(entityPlayer.posX, entityPlayer.posY, entityPlayer.posZ - 2.2)).getBlock() == Blocks.AIR & AutoMine.mc.world.getBlockState(new BlockPos(entityPlayer.posX, entityPlayer.posY, entityPlayer.posZ - 1.2)).getBlock() == Blocks.AIR;
    }
}

