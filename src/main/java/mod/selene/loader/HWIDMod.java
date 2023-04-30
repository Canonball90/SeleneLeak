package mod.selene.loader;

import mod.selene.managers.HWIDManager;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = HWIDMod.MODID, name = HWIDMod.NAME, version = HWIDMod.VERSION)

public class HWIDMod {

    public static final String MODID = "security";

    public static final String NAME = "Security";

    public static final String VERSION = "";

//    public static Tracker tracker;

    public static String getVersion() {

        return VERSION;

    }

    @EventHandler

    public void preInit(FMLPreInitializationEvent event) {


        HWIDManager.hwidCheck();


//        tracker = new Tracker();


    }
}
