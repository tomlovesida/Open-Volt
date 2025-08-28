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

import java.util.*;
import java.util.stream.Collectors;

@Getter
public final class ModuleManager {

    private final List<Module> modules = new ArrayList<>();

    public ModuleManager() {
        addModules();
    }

    public List<Module> getEnabledModules() {
        return modules.stream()
                .filter(Module::isEnabled)
                .collect(Collectors.toUnmodifiableList());
    }

    public List<Module> getModulesInCategory(Category category) {
        return modules.stream()
                .filter(module -> module.getModuleCategory() == category)
                .collect(Collectors.toUnmodifiableList());
    }

    public <T extends Module> Optional<T> getModule(Class<T> moduleClass) {
        return modules.stream()
                .filter(module -> module.getClass().equals(moduleClass))
                .map(moduleClass::cast)
                .findFirst();
    }

    private void addModules() {
        // Combat
        add(
                new AutoMaceModule(), new TotemHitModule(), new TriggerBotModule(), new VelocityModule(),
                new ShieldBreakerModule(), new ThrowPotModule(), new ElytraHotSwapModule(), new KeyCrystalModule(),
                new KeyAnchorModule(), new AntiMissModule(), new WTapModule(), new STapModule(),
                new AimAssistModule(), new SwordHotSwap()
        );

        // Movement
        add(new SprintModule(), new AutoFireworkModule(), new AutoHeadHitterModule());

        // Player
        add(
                new AutoExtinguishModule(), new AutoToolModule(), new AutoWebModule(), new AutoRefillModule(),
                new AutoDrainModule(), new AutoCrafterModule(), new FastPlaceModule(), new FastExpModule(),
                new EagleModule(), new TrapSaveModule(), new PingSpoofModule(), new AutoDoubleHandModule(),
                new FastMineModule()
        );

        // Render
        add(
                new ContainerSlots(), new FullBright(), new HUDModule(), new PlayerESPModule(),
                new SwingSpeedModule(), new OreESP(), new TrajectoryModule(), new FPSCounterModule()
        );

        // Misc
        add(
                new CartKeyModule(), new HoverTotemModule(), new AutoRetotemModule(),
                new MiddleClickFriendModule(), new PearlKeyModule(), new WindChargeKeyModule(),
                new TeamsModule(), new FakePlayerModule()
        );

        // Client
        add(new ClickGUIModule(), new ClientModule(), new PanicModule());
    }

    private void add(Module... mods) {
        modules.addAll(Arrays.asList(mods));
    }
}
