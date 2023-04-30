package mod.selene.managers;

import mod.selene.injections.inj.IMinecraft;
import mod.selene.injections.inj.ITimer;
import mod.selene.loader.Feature;
import mod.selene.world.PacketEvent;
import net.minecraft.network.play.server.SPacketTimeUpdate;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * @author Rigamortis, linustouchtips
 * @since 06/08/2021
 */
public class TickManager extends Feature {

    // array of last 20 latestTicks calculations
    private final float[] latestTicks = new float[10];

    // time
    private long time = -1;
    private int tick;

    public static float roundFloat(double number, int scale) {
        BigDecimal bigDecimal = BigDecimal.valueOf(number);

        // round
        bigDecimal = bigDecimal.setScale(scale, RoundingMode.FLOOR);
        return bigDecimal.floatValue();
    }

    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive event) {

        // packet for server time updates
        if (event.getPacket() instanceof SPacketTimeUpdate) {

            // update our ticks
            if (time != -1) {
                latestTicks[tick % latestTicks.length] = (20 / ((float) (System.currentTimeMillis() - time) / 1000));
                tick++;
            }

            // mark as last response
            time = System.currentTimeMillis();
        }
    }

    /**
     * Gets the current ticks
     *
     * @param tps the ticks mode to use
     * @return The server ticks
     */
    public float getTPS(TPS tps) {

        // do not calculate ticks if we are not on a server
        if (mc.isSingleplayer() || tps.equals(TPS.NONE)) {
            return 20;
        } else {
            switch (tps) {
                case CURRENT:
                    // use the last ticks calculation
                    return roundFloat(latestTicks[0], 2);
                case AVERAGE:
                default:
                    int tickCount = 0;
                    float tickRate = 0;

                    // calculate the average ticks
                    for (float tick : latestTicks) {
                        if (tick > 0) {
                            tickRate += tick;
                            tickCount++;
                        }
                    }

                    return roundFloat((tickRate / tickCount), 2);
            }
        }
    }

    /**
     * Sets the client tick length
     *
     * @param ticks The new tick length
     */
    public void setClientTicks(float ticks) {
        ((ITimer) ((IMinecraft) mc).getTimer()).setTickLength((50 / ticks));
    }

    /**
     * Gets the client tick length
     *
     * @return The tick length
     */
    public float getTickLength() {
        return ((ITimer) ((IMinecraft) mc).getTimer()).getTickLength();
    }

    public enum TPS {

        /**
         * Uses the latest ticks calculation
         */
        CURRENT,

        /**
         * Uses the average ticks (over last 20 ticks) calculation
         */
        AVERAGE,

        /**
         * Does not calculate ticks
         */
        NONE
    }
}