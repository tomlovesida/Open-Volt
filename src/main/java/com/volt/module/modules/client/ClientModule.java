package com.volt.module.modules.client;

import com.volt.module.Category;
import com.volt.module.Module;
import com.volt.module.setting.BooleanSetting;

public final class ClientModule extends Module {
    public static final BooleanSetting title = new BooleanSetting("Title", true);

    public ClientModule() {
        super("Client", "Settings for the client", -1, Category.CLIENT);
        this.addSettings(title);
    }
}
