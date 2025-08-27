package com.volt.module;


import com.volt.module.modules.client.ClickGUIModule;
import com.volt.module.modules.client.ClientModule;
import com.volt.module.modules.client.PanicModule;
import com.volt.module.modules.combat.*;
import com.volt.module.modules.misc.*;
import com.volt.module.modules.movement.AutoFireworkModule;
import com.volt.module.modules.movement.AutoHeadHitterModule;
import com.volt.module.modules.movement.SprintModule;
import com.volt.module.modules.player.*;
import com.volt.module.modules.render.*;
import com.volt.module.modules.render.FPSCounterModule;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public final class ModuleManager {

    private final List<Module> modules = new ArrayList<>();

    public ModuleManager() {
        addModules();
    }

    public List<Module> getEnabledModules() {
        List<Module> enabled = new ArrayList<>();

        for (Module module : modules) {
            if (module.isEnabled()) {
                enabled.add(module);
            }
        }

        return enabled;
    }

    public List<Module> getModulesInCategory(Category category) {
        List<Module> categoryModules = new ArrayList<>();

        for (Module module : modules) {
            if (module.getModuleCategory().equals(category)) {
                categoryModules.add(module);
            }
        }

        return categoryModules;
    }

    @SuppressWarnings("unchecked")
    public <T extends Module> T getModule(Class<T> moduleClass) {
        for (Module module : modules) {
            if (moduleClass.isAssignableFrom(module.getClass())) {
                return (T) module;
            }
        }

        return null;
    }

    public void addModules() {
        //Combat
        add(new AutoMaceModule());
        add(new TotemHitModule());
        add(new TriggerBotModule());
        add(new VelocityModule());
        add(new ShieldBreakerModule());
        add(new ThrowPotModule());
        add(new ElytraHotSwapModule());
        add(new KeyCrystalModule());
        add(new KeyAnchorModule());
        add(new AntiMissModule());
        add(new WTapModule());
        add(new STapModule());
        add(new AimAssistModule());
        add(new SwordHotSwap());
        //Movement
        add(new SprintModule());
        add(new AutoFireworkModule());
        add(new AutoHeadHitterModule());
        // Player
        add(new AutoExtinguishModule());
        add(new AutoToolModule());
        add(new AutoWebModule());
        add(new AutoRefillModule());
        add(new AutoDrainModule());
        add(new AutoCrafterModule());
        add(new FastPlaceModule());
        add(new FastExpModule());
        add(new EagleModule());
        add(new TrapSaveModule());
        add(new PingSpoofModule());
        add(new AutoDoubleHandModule());
        add(new FastMineModule());
        // Render
        add(new ContainerSlots());
        add(new FullBright());
        add(new HUDModule());
        add(new PlayerESPModule());
        add(new SwingSpeedModule());
        add(new OreESP());
        add(new TrajectoryModule());
        add(new FPSCounterModule());
        // Misc
        add(new CartKeyModule());
        add(new HoverTotemModule());
        add(new AutoRetotemModule());
        add(new MiddleClickFriendModule());
        add(new PearlKeyModule());
        add(new WindChargeKeyModule());
        add(new TeamsModule());
        add(new FakePlayerModule());

        //Client
        add(new ClickGUIModule());
        add(new ClientModule());
        add(new PanicModule());
    }

    public void add(Module module) {
        modules.add(module);
    }
}
