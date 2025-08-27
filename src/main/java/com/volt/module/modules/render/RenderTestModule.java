package com.volt.module.modules.render;

import com.volt.event.impl.render.EventRender2D;
import com.volt.module.Category;
import com.volt.module.Module;
import meteordevelopment.orbit.EventHandler;
import java.awt.*;

public class RenderTestModule extends Module {

    public RenderTestModule() {
        super("Render Test", "this is a render test for dev purposes ignore", Category.RENDER);
    }

    @EventHandler
    private void onRender2D(EventRender2D event) {
        if (mc.player == null || mc.world == null) {
            return;
        }
        
        int screenWidth = event.getWidth();
        int screenHeight = event.getHeight();
        
        int squareSize = 100;
        int x = (screenWidth - squareSize) / 2;
        int y = (screenHeight - squareSize) / 2;
        
        event.getContext().fill(
            x, y, 
            x + squareSize, y + squareSize, 
            new Color(255, 100, 100, 200).getRGB()
        );
    }
}