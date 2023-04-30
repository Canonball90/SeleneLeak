package mod.selene.impl.modules.movement;

import mod.selene.api.utils.DamageUtil;
import mod.selene.api.utils.Timer;
import mod.selene.api.utils.interfaces.Util;
import mod.selene.api.utils.math.MathUtil;
import mod.selene.impl.Module;
import mod.selene.injections.inj.IMinecraft;
import mod.selene.injections.inj.ITimer;
import mod.selene.system.Setting;
import mod.selene.world.EntityRemovedEvent;
import mod.selene.world.MoveEvent;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.init.MobEffects;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Objects;

public class Strafe extends Module {
    public static Strafe INSTANCE;
    private final Timer timer = new Timer();
    private final Timer damageTimer = new Timer();
    Setting<Boolean> useTimer = register(new Setting<Boolean>("UseTimer", true));
    Setting<Boolean> autoSprint = register(new Setting<Boolean>("AutoSprint", true));
    Setting<Boolean> addSpeed = register(new Setting<Boolean>("Custom", false));
    Setting<String> speedAddition = register(new Setting<String>("SpeedAddition", "0.272", v -> addSpeed.getValue()));
    Setting<Boolean> extraSpeed = register(new Setting<Boolean>("Extra", false));
    Setting<Boolean> damageBoost = register(new Setting<Boolean>("DMGBoost", false));
    Setting<String> sneakAddition = register(new Setting<String>("SneakSpeed", "1.00"));
    Setting<Double> vanillaSpeed = register(new Setting<Double>("VanillaSpeed", 6.0d, 0.1d, 10.0d, vis -> extraSpeed.getValue()));
    Setting<SkipMode> skipModeSetting = register(new Setting<SkipMode>("Skips", SkipMode.TICK));
    Setting<Integer> skipTickHops = register(new Setting<Integer>("SkipTicks", 1, 1, 10, vis -> skipModeSetting.getValue() == SkipMode.TICK));
    Setting<Integer> skipDelayHops = register(new Setting<Integer>("SkipDelay", 25, 1, 100, vis -> skipModeSetting.getValue() == SkipMode.MS));
    Setting<Boolean> fastSwim = register(new Setting<Boolean>("FastSwim", true));
    Setting<Double> fastSwimXZWater = register(new Setting<Double>("WaterXZ", 4.4D, 1.0D, 10.0D, v -> fastSwim.getValue()));
    Setting<Double> fastSwimYWater = register(new Setting<Double>("WaterY", 1.5D, 1.0D, 10.0D, v -> fastSwim.getValue()));
    Setting<Double> fastSwimXZLava = register(new Setting<Double>("LavaXZ", 4.4D, 1.0D, 10.0D, v -> fastSwim.getValue()));
    Setting<Double> fastSwimYLava = register(new Setting<Double>("LavaY", 1.5D, 1.0D, 10.0D, v -> fastSwim.getValue()));
    private int stage = 0;
    private int cooldown = 0;
    private double moveSpeed, lastDist;
    private double damageMultiplier = 0;

    public Strafe() {
        super("Strafe", "makes you faster", Category.MOVEMENT, true, false, false);
        INSTANCE = this;
    }

    @SubscribeEvent
    public void onEntityRemove(EntityRemovedEvent event) {
        if (Util.mc.world != null && Util.mc.player != null && damageBoost.getValue() && event.getEntity() != null && event.getEntity() instanceof EntityEnderCrystal) {
            EntityEnderCrystal crystal = (EntityEnderCrystal) event.getEntity();
            if (Util.mc.player.getDistance(crystal) >= 13) {
                return;
            }
            float playerDMG = DamageUtil.calculateDamage(crystal.posX, crystal.posY, crystal.posZ, Util.mc.player);
            if (playerDMG >= 3.5f && ((1 + (playerDMG / 36) > damageMultiplier))) {
                damageMultiplier = 1 + (playerDMG / 36);
                damageTimer.reset();
            }
        }
    }

    @SubscribeEvent
    public void onDamageTaken(LivingAttackEvent event) {
        if (Util.mc.player == null || Util.mc.world == null || event.getEntity() != Util.mc.player) {
            return;
        }
        if (!(event.getAmount() >= 36) && event.getAmount() >= 3.5f && ((1 + (event.getAmount() / 36) > damageMultiplier))) {
            damageMultiplier = 1 + (event.getAmount() / 36);
            damageTimer.reset();
        }
    }


    @Override
    public void onUpdate() {
        if (Util.mc == null || Util.mc.player == null) {
            return;
        }

        if (damageTimer.passedMs(1750)) {
            damageMultiplier = 1.0d;
            damageTimer.reset();
        }

        lastDist = Math.sqrt(((Util.mc.player.posX - Util.mc.player.prevPosX) * (Util.mc.player.posX - Util.mc.player.prevPosX)) + ((Util.mc.player.posZ - Util.mc.player.prevPosZ) * (Util.mc.player.posZ - Util.mc.player.prevPosZ)));
        if (canSprint() && autoSprint.getValue()) {
            Util.mc.player.setSprinting(true);
        }
    }

    @SubscribeEvent
    public void onMove(MoveEvent event) {
        if (Util.mc.player.isElytraFlying() || Util.mc.player.isInLava() || Util.mc.player.isInWater()) {
            return;
        }
        //if(mc.player.isInWeb) {
        //    return;
        //}
        cooldown++;
        switch (skipModeSetting.getValue()) {
            case TICK:
                if (cooldown < skipTickHops.getValue()) {
                    return;
                }
                break;
            case MS:
                if (!timer.passedMs(skipDelayHops.getValue())) {
                    return;
                }
                break;
            default:
                break;
        }
        switch (stage) {
            case 0:
                ++stage;
                lastDist = 0.0D;
                break;
            case 2:
                double motionY = 0.40123128;
                if ((Util.mc.player.moveForward != 0.0F || Util.mc.player.moveStrafing != 0.0F) && Util.mc.player.onGround) {
                    if (Util.mc.player.isPotionActive(MobEffects.JUMP_BOOST))
                        motionY += ((Util.mc.player.getActivePotionEffect(MobEffects.JUMP_BOOST).getAmplifier() + 1) * 0.1F);
                    event.setY(Util.mc.player.motionY = motionY);
                    moveSpeed *= 2.149;
                }
                break;
            case 3:
                moveSpeed = lastDist - (0.76 * (lastDist - getBaseMoveSpeed()));
                break;
            default:
                if ((Util.mc.world.getCollisionBoxes(Util.mc.player, Util.mc.player.getEntityBoundingBox().offset(0.0D, Util.mc.player.motionY, 0.0D)).size() > 0 || Util.mc.player.collidedVertically) && stage > 0) {
                    stage = Util.mc.player.moveForward == 0.0F && Util.mc.player.moveStrafing == 0.0F ? 0 : 1;
                }
                moveSpeed = lastDist - lastDist / 159.0D;
                break;
        }
        moveSpeed = Math.max(moveSpeed, getBaseMoveSpeed());
        double forward = Util.mc.player.movementInput.moveForward, strafe = Util.mc.player.movementInput.moveStrafe, yaw = Util.mc.player.rotationYaw;
        if (forward != 0 && strafe != 0) {
            forward = forward * Math.sin(Math.PI / 4);
            strafe = strafe * Math.cos(Math.PI / 4);
        } else {
            if (useTimer.getValue()) {
                ((ITimer) ((IMinecraft) Util.mc).getTimer()).setTickLength(50);
            }
            event.setX(0);
            event.setZ(0);
        }
        event.setX((forward * moveSpeed * -Math.sin(Math.toRadians(yaw)) + strafe * moveSpeed * Math.cos(Math.toRadians(yaw))) * 0.99D);
        event.setZ((forward * moveSpeed * Math.cos(Math.toRadians(yaw)) - strafe * moveSpeed * -Math.sin(Math.toRadians(yaw))) * 0.99D);
        if (extraSpeed.getValue()) {
            double[] vanillaCalc = MathUtil.directionSpeed(vanillaSpeed.getValue() / 10);
            event.setX(vanillaCalc[0]);
            event.setZ(vanillaCalc[1]);
        }
        ++stage;
        cooldown = 0;
        timer.reset();
    }

    @SubscribeEvent
    public void onMoveSwim(MoveEvent event) {
        if (!(Util.mc.player.isInWater() || Util.mc.player.isInLava())) {
            return;
        }
        if (!fastSwim.getValue()) {
            return;
        }
        if (Util.mc.player.isInWater()) {
            event.setX(event.getX() * fastSwimXZWater.getValue());
            event.setY(event.getY() * fastSwimYWater.getValue());
        }
        event.setZ(event.getZ() * fastSwimXZWater.getValue());
        if (Util.mc.player.isInLava()) {
            event.setX(event.getX() * fastSwimXZLava.getValue());
            event.setY(event.getY() * fastSwimYLava.getValue());
        }
        event.setZ(event.getZ() * fastSwimXZLava.getValue());
    }

    @Override
    public void onToggle() {
        ((ITimer) ((IMinecraft) Util.mc).getTimer()).setTickLength(50);
        cooldown = 0;
        damageMultiplier = 1.0d;
        timer.reset();
    }

    public double getBaseMoveSpeed() {
        double baseSpeed = 0.0d;
        if (addSpeed.getValue()) {
            try {
                baseSpeed = Double.valueOf(speedAddition.getValue());
            } catch (Exception e) {
                baseSpeed = 0.272;
            }
        } else {
            baseSpeed = 0.272;
        }
        if (Util.mc.player.isSneaking()) {
            try {
                baseSpeed = Double.valueOf(sneakAddition.getValue());
            } catch (Exception e) {
                baseSpeed = 0.354;
            }
        }
        if (Util.mc.player.isPotionActive(MobEffects.SPEED)) {
            final int amplifier = Objects.requireNonNull(Util.mc.player.getActivePotionEffect(MobEffects.SPEED)).getAmplifier();
            baseSpeed *= 1.0 + (0.2 * (amplifier + 1));
        }
        if (useTimer.getValue()) {
            ((ITimer) ((IMinecraft) Util.mc).getTimer()).setTickLength(50 / 1.088f);
        }
        if (damageBoost.getValue()) {
            baseSpeed *= damageMultiplier;
        }
        return baseSpeed;
    }

    private boolean canSprint() {
        return ((Util.mc.player.movementInput.moveStrafe != 0.0F || Util.mc.player.moveForward != 0.0F) && !Util.mc.player.isActiveItemStackBlocking() && !Util.mc.player.isOnLadder() && !Util.mc.player.collidedHorizontally && Util.mc.player.getFoodStats().getFoodLevel() > 6);
    }


    private enum SkipMode {
        TICK,
        MS,
        NONE
    }
}
