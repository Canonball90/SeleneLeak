package mod.selene.managers;

import mod.selene.api.ui.ClickGUI;
import mod.selene.impl.Module;
import mod.selene.impl.modules.client.*;
import mod.selene.impl.modules.combat.*;
import mod.selene.impl.modules.combat.godmodule.GodModule;
import mod.selene.impl.modules.hidden.InstantMine;
import mod.selene.impl.modules.misc.AutoBowRelease;
import mod.selene.impl.modules.misc.Burrow;
import mod.selene.impl.modules.misc.ElytraSwap;
import mod.selene.impl.modules.movement.*;
import mod.selene.impl.modules.other.ChatModifier;
import mod.selene.impl.modules.other.PearlNotify;
import mod.selene.impl.modules.other.PopLagger;
import mod.selene.impl.modules.other.Speedmine;
import mod.selene.impl.modules.player.AutoMine;
import mod.selene.impl.modules.player.FakePlayer;
import mod.selene.impl.modules.player.Replenish;
import mod.selene.impl.modules.player.SilentXP;
import mod.selene.impl.modules.render.*;
import mod.selene.loader.Feature;
import mod.selene.world.Render2DEvent;
import mod.selene.world.Render3DEvent;
import net.minecraftforge.common.MinecraftForge;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ModuleManager extends Feature {

    public ArrayList<Module> modules = new ArrayList<>();
    public List<Module> sortedModules = new ArrayList<>();

    public void init() {

        //OTHER
        modules.add(new ChatModifier());
        modules.add(new PearlNotify());
        //modules.add(new AntiUnicode()); not working (must be replaced or improved)
        modules.add(new PopLagger());
        modules.add(new Speedmine());

        //COMBAT
        modules.add(new Aura());
        modules.add(new AutoCrystal());
        modules.add(new Criticals());
        modules.add(new GodModule());
        modules.add(new Quiver());
        modules.add(new Surround());

        //MISC
        modules.add(new FakePlayer());
        modules.add(new AutoBowRelease());

        //MOVEMENT
        modules.add(new Burrow());
        modules.add(new Strafe());
        modules.add(new Velocity());
        modules.add(new FastFall());
        modules.add(new Sprint());
        modules.add(new PacketFly());
        modules.add(new Step());
        modules.add(new NoSlow());

        //PLAYER
        modules.add(new SilentXP());
        modules.add(new Replenish());
        modules.add(new ElytraSwap());
        modules.add(new AutoMine());


        //VISUAL
        modules.add(new NoRender());
        modules.add(new Ambience());
        modules.add(new PopChams());
        modules.add(new ImageESP());
        modules.add(new DamageNumbers());
        modules.add(new HitParticles());
        modules.add(new JumpCircle());
//        modules.add(new NameTags());


        //CLIENT
        modules.add(new Notifications());
        //modules.add(new HUD()); ultra ugly fobus hud
        modules.add(new FontMod());
        modules.add(new ClickGui());
        modules.add(new Colors());
        modules.add(new Particles());
        //modules.add(new Managers());
    }

    public Module getModuleByName(String name) {
        for (Module module : this.modules) {
            if (module.getName().equalsIgnoreCase(name)) {
                return module;
            }
        }
        return null;
    }

    public <T extends Module> T getModuleByClass(Class<T> clazz) {
        for (Module module : this.modules) {
            if (clazz.isInstance(module)) {
                return (T) module;
            }
        }
        return null;
    }

    public void enableModule(Class clazz) {
        Module module = getModuleByClass(clazz);
        if (module != null) {
            module.enable();
        }
    }

    public void disableModule(Class clazz) {
        Module module = getModuleByClass(clazz);
        if (module != null) {
            module.disable();
        }
    }

    public void enableModule(String name) {
        Module module = getModuleByName(name);
        if (module != null) {
            module.enable();
        }
    }

    public void disableModule(String name) {
        Module module = getModuleByName(name);
        if (module != null) {
            module.disable();
        }
    }

    public boolean isModuleEnabled(String name) {
        Module module = getModuleByName(name);
        return module != null && module.isOn();
    }

    public boolean isModuleEnabled(Class clazz) {
        Module module = getModuleByClass(clazz);
        return module != null && module.isOn();
    }

    public Module getModuleByDisplayName(String displayName) {
        for (Module module : this.modules) {
            if (module.getDisplayName().equalsIgnoreCase(displayName)) {
                return module;
            }
        }
        return null;
    }

    public ArrayList<Module> getEnabledModules() {
        ArrayList<Module> enabledModules = new ArrayList<>();
        for (Module module : modules) {
            if (module.isEnabled()) {
                enabledModules.add(module);
            }
        }
        return enabledModules;
    }

    public ArrayList<Module> getModulesByCategory(Module.Category category) {
        ArrayList<Module> modulesCategory = new ArrayList<>();
        this.modules.forEach(module -> {
            if (module.getCategory() == category) {
                modulesCategory.add(module);
            }
        });
        return modulesCategory;
    }

    public List<Module.Category> getCategories() {
        return Arrays.asList(Module.Category.values());
    }

    public void onLoad() {
        modules.stream().filter(Module::listening).forEach(MinecraftForge.EVENT_BUS::register);
        modules.forEach(Module::onLoad);
    }

    public void onUpdate() {
        modules.stream().filter(Feature::isEnabled).forEach(Module::onUpdate);
    }

    public void onRenderTick() {
        modules.stream().filter(Feature::isEnabled).forEach(Module::onRenderTick);
    }

    public void onTick() {
        modules.stream().filter(Feature::isEnabled).forEach(Module::onTick);
    }

    public void onExplosion() {
        modules.stream().filter(Feature::isEnabled).forEach(Module::onExplosion);
    }

    public void onRender2D(Render2DEvent event) {
        modules.stream().filter(Feature::isEnabled).forEach(module -> module.onRender2D(event));
    }

    public void onRender3D(Render3DEvent event) {
        modules.stream().filter(Feature::isEnabled).forEach(module -> module.onRender3D(event));
    }

    public void sortModules(boolean reverse) {
        this.sortedModules = getEnabledModules().stream().filter(Module::isDrawn)
                .sorted(Comparator.comparing(module -> renderer.getStringWidth(module.getFullArrayString()) * (reverse ? -1 : 1)))
                .collect(Collectors.toList());
    }

    public void onLogout() {
        modules.forEach(Module::onLogout);
    }

    public void onLogin() {
        modules.forEach(Module::onLogin);
    }

    public void onUnload() {
        modules.forEach(MinecraftForge.EVENT_BUS::unregister);
        modules.forEach(Module::onUnload);
    }

    public void onKeyPressed(int eventKey) {
        if (eventKey == 0 || !Keyboard.getEventKeyState() || mc.currentScreen instanceof ClickGUI) {
            return;
        }
        modules.forEach(module -> {
            if (module.getBind().getKey() == eventKey) {
                module.toggle();
            }
        });
    }
}
