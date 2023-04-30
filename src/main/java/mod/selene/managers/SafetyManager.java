
/*
 * Decompiled with CFR 0.151.
 *
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.item.EntityEnderCrystal
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.utils.math.BlockPos
 */
package mod.selene.managers;

import mod.selene.api.utils.BlockUtil;
import mod.selene.api.utils.DamageUtil;
import mod.selene.api.utils.EntityUtil;
import mod.selene.api.utils.math.TimerUtil;
import mod.selene.impl.modules.client.Management;
import mod.selene.loader.Feature;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class SafetyManager
        extends Feature
        implements Runnable {
    private final TimerUtil syncTimer = new TimerUtil();
    private final AtomicBoolean SAFE = new AtomicBoolean(false);
    private ScheduledExecutorService service;


    public void doSafetyCheck() {
        if (!SafetyManager.fullNullCheck()) {
            EntityPlayer closest;
            boolean safe = true;
            EntityPlayer entityPlayer = closest = Management.getInstance().safety.getValue() ? EntityUtil.getClosestEnemy(18.0) : null;
            if (Management.getInstance().safety.getValue().booleanValue() && closest == null) {
                this.SAFE.set(true);
                return;
            }
            ArrayList<Entity> crystals = new ArrayList(SafetyManager.mc.world.loadedEntityList);
            for (Entity crystal : crystals) {
                if (!(crystal instanceof EntityEnderCrystal) || !((double) DamageUtil.calculateDamage(crystal, SafetyManager.mc.player) > 4.0) || closest != null && !(closest.getDistanceSq(crystal) < 40.0))
                    continue;
                safe = false;
                break;
            }
            if (safe) {
                for (BlockPos pos : BlockUtil.possiblePlacePositions2(4.0f, false, Management.getInstance().oneDot15.getValue(), false)) {
                    if (!((double) DamageUtil.calculateDamage(pos, SafetyManager.mc.player) > 4.0) || closest != null && !(closest.getDistanceSq(pos) < 40.0))
                        continue;
                    safe = false;
                    break;
                }
            }
            this.SAFE.set(safe);
        }
    }

    public void onUpdate() {
        this.run();
    }

    public String getSafetyString() {
        if (this.SAFE.get()) {
            return "\u00a7aSecure";
        }
        return "\u00a7cUnsafe";
    }

    public boolean isSafe() {
        return this.SAFE.get();
    }

    public ScheduledExecutorService getService() {
        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        service.scheduleAtFixedRate(this, 0L, Management.getInstance().safetyCheck.getValue().intValue(), TimeUnit.MILLISECONDS);
        return service;
    }

    @Override
    public void run() {

    }
}

