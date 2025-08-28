package com.volt.gui;

import com.volt.Volt;
import com.volt.gui.animation.AnimationManager;
import com.volt.gui.components.SettingsRenderer;
import com.volt.gui.components.UIRenderer;
import com.volt.gui.events.GuiEventHandler;
import com.volt.gui.utils.SearchUtils;
import com.volt.module.Category;
import com.volt.module.Module;
import com.volt.module.modules.client.ClickGUIModule;
import com.volt.module.setting.NumberSetting;
import com.volt.utils.font.FontManager;
import com.volt.utils.font.fonts.FontRenderer;
import com.volt.utils.render.RenderUtils;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.glfw.GLFW;

import java.awt.Color;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class ClickGui extends Screen {
    private static Category lastSelectedCategory = Category.COMBAT;
    private static final Map<Module, Boolean> lastModuleExpanded = new HashMap<>();
    private static int lastScrollOffset = 0;
    private static final int SIDEBAR_WIDTH = 180;
    private static final int CONTAINER_WIDTH = SIDEBAR_WIDTH + 600;
    private static final int COLOR_PICKER_PANEL_WIDTH = 250;
    private static final int HEADER_HEIGHT = 60;
    private static final int MODULE_HEIGHT = 35;
    private static final int PADDING = 18;
    private final Map<Module, Boolean> moduleExpanded = new HashMap<>();
    private final Map<NumberSetting, Boolean> sliderDragging = new HashMap<>();
    private final ColorPickerManager colorPickerManager;
    private static final int SETTING_HEIGHT = 28;
    private static final int SEARCH_BAR_WIDTH = 200;
    private static final int SEARCH_BAR_HEIGHT = 25;
    
    private final AnimationManager animationManager;
    private final GuiEventHandler eventHandler;
    private long lastCursorBlink = 0;
    
    private final FontRenderer titleFont;
    private final FontRenderer regularFont;
    private final FontRenderer smallFont;
    
    public ClickGui() {
        super(Text.empty());
        
        FontManager fontManager = Volt.INSTANCE.getFontManager();
        this.titleFont = fontManager.getSize(24, FontManager.Type.Poppins);
        this.regularFont = fontManager.getSize(14, FontManager.Type.Inter);
        this.smallFont = fontManager.getSize(12, FontManager.Type.Inter);
        
        this.animationManager = new AnimationManager();
        this.eventHandler = new GuiEventHandler(moduleExpanded, sliderDragging, lastSelectedCategory);
        this.colorPickerManager = new ColorPickerManager(eventHandler);
        
        animationManager.initializeGuiAnimation();
        
        for (Module module : Volt.INSTANCE.getModuleManager().getModules()) {
            moduleExpanded.put(module, lastModuleExpanded.getOrDefault(module, false));
            animationManager.initializeModuleAnimations(module);
        }
        eventHandler.setScrollOffset(lastScrollOffset);
    }
    
    @Override
    public void init() {
        super.init();
        animationManager.initializeGuiAnimation();
    }
    
    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        animationManager.updateAnimations(delta);
        animationManager.updateGuiAnimations(delta);
        
        if (animationManager.shouldCloseGui()) {
            lastSelectedCategory = eventHandler.getSelectedCategory();
            lastScrollOffset = eventHandler.getScrollOffset();
            lastModuleExpanded.clear();
            lastModuleExpanded.putAll(moduleExpanded);
            Volt.INSTANCE.getModuleManager().getModule(ClickGUIModule.class).get().setEnabled(false);
            super.close();
            return;
        }
        
        int screenWidth = width;
        int screenHeight = height;
        
        renderBackground(context, mouseX, mouseY, delta);
        
        MatrixStack matrices = context.getMatrices();
        matrices.push();
        
        float centerX = screenWidth / 2f;
        float centerY = screenHeight / 2f;
        
        matrices.translate(centerX, centerY, 0);
        matrices.scale(animationManager.getScaleAnimation(), animationManager.getScaleAnimation(), 1f);
        matrices.translate(-centerX, -centerY, 0);
        
        int containerX = (screenWidth - CONTAINER_WIDTH) / 2;
        int containerY = (screenHeight - 500) / 2;
        int containerWidth = CONTAINER_WIDTH;
        int containerHeight = 500;
        
        int alpha = (int)(animationManager.getGuiAnimation() * 240);
        context.fill(containerX, containerY, containerX + containerWidth, containerY + containerHeight, 
            new Color(25, 25, 35, alpha).getRGB());
        
        renderSidebar(context, containerX, containerY, containerHeight, mouseX, mouseY);
        
        renderContent(context, containerX + SIDEBAR_WIDTH, containerY, CONTAINER_WIDTH - SIDEBAR_WIDTH, containerHeight, mouseX, mouseY);
        
        if (eventHandler.isAnyColorPickerExpanded()) {
            int colorPickerPanelX = containerX + containerWidth + 10;
            colorPickerManager.renderColorPickerPanel(context, colorPickerPanelX, containerY, COLOR_PICKER_PANEL_WIDTH, containerHeight, mouseX, mouseY);
        }
        
        renderHeader(context, containerX, containerY, containerWidth);
        
        matrices.pop();
        
       
    }
    
    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
        int backgroundAlpha = (int)(animationManager.getGuiAnimation() * 120);
        RenderUtils.drawGradientRect(context, 0, 0, width, height, 
            new Color(0, 0, 0, backgroundAlpha).getRGB(), new Color(0, 0, 0, (int)(backgroundAlpha * 0.67f)).getRGB());
    }
    
    private void renderHeader(DrawContext context, int x, int y, int width) {
        int headerAlpha = (int)(animationManager.getGuiAnimation() * 255);
        context.fill(x, y, x + width, y + HEADER_HEIGHT, 
            new Color(35, 35, 50, headerAlpha).getRGB());
        
        MatrixStack matrices = context.getMatrices();
        
        String title = "Volt";
        int titleX = x + PADDING;
        int titleY = y + 15;
        int textAlpha = (int)(animationManager.getGuiAnimation() * 255);
        titleFont.drawString(matrices, title, 
            titleX, titleY, new Color(255, 255, 255, textAlpha));
        
        renderSearchBar(context, x, y, width);
    }
    
    private void renderSearchBar(DrawContext context, int x, int y, int width) {
        MatrixStack matrices = context.getMatrices();
        
        int searchX = x + width - SEARCH_BAR_WIDTH - PADDING;
        int searchY = y + (HEADER_HEIGHT - SEARCH_BAR_HEIGHT) / 2;
        
        String displayText = eventHandler.getSearchQuery().isEmpty() ? "Search modules..." : eventHandler.getSearchQuery();
        Color textColor = eventHandler.getSearchQuery().isEmpty() ? new Color(100, 100, 100) : new Color(255, 255, 255);
        int textX = searchX + 8;
        int textY = searchY + (SEARCH_BAR_HEIGHT - 12) / 2 + 2;
        
        String clippedText = displayText;
        float maxTextWidth = SEARCH_BAR_WIDTH - 16;
        while (smallFont.getStringWidth(clippedText) > maxTextWidth && clippedText.length() > 0) {
            clippedText = clippedText.substring(0, clippedText.length() - 1);
        }
        context.fill(searchX, searchY, searchX + SEARCH_BAR_WIDTH, (searchY + SEARCH_BAR_HEIGHT) - 2, 
            new Color(40, 40, 50, 200).getRGB());
        smallFont.drawString(matrices, clippedText, textX, textY - 4, textColor);
        
        if (eventHandler.isSearchFocused() && !eventHandler.getSearchQuery().isEmpty()) {
            long currentTime = System.currentTimeMillis();
            if ((currentTime - lastCursorBlink) % 1000 < 500) {
                int cursorX = textX + (int)smallFont.getStringWidth(clippedText);
                context.fill(cursorX, textY - 1, cursorX + 1, textY + 11, new Color(255, 255, 255).getRGB());
            }
        }
    }
    
    private void renderSidebar(DrawContext context, int x, int y, int height, int mouseX, int mouseY) {
        int sidebarAlpha = (int)(animationManager.getGuiAnimation() * 255);
        context.fill(x, y + HEADER_HEIGHT, x + SIDEBAR_WIDTH, y + height - 12, 
            new Color(30, 30, 40, sidebarAlpha).getRGB());
        context.fill(x + 12, y + height - 12, x + SIDEBAR_WIDTH, y + height, 
            new Color(30, 30, 40, sidebarAlpha).getRGB());
        
        int sidebarX = x;
        int sidebarY = y + HEADER_HEIGHT;
        
        MatrixStack matrices = context.getMatrices();
        int categoryY = sidebarY + PADDING;
        
        for (Category category : Category.values()) {
            boolean isSelected = category == eventHandler.getSelectedCategory();
            boolean isHovered = mouseX >= sidebarX && mouseX <= sidebarX + SIDEBAR_WIDTH && 
                              mouseY >= categoryY && mouseY <= categoryY + 35;
            
            float targetAnimation = isSelected ? 1f : (isHovered ? 0.3f : 0f);
            float currentAnimation = animationManager.getCategoryAnimation(category);
            float newAnimation = MathHelper.lerp(0.15f, currentAnimation, targetAnimation);
            animationManager.setCategoryAnimation(category, newAnimation);
            Color textColor = isSelected ? Color.WHITE : new Color(180, 180, 180);
            regularFont.drawString(matrices, category.getName(), sidebarX + 20, categoryY + 13, textColor);
            
            categoryY += 45;
        }
    }
    
    private void renderContent(DrawContext context, int x, int y, int width, int height, int mouseX, int mouseY) {
        int contentAlpha = (int)(animationManager.getGuiAnimation() * 255);
        context.fill(x, y + HEADER_HEIGHT, x + width, y + height - 12, 
            new Color(20, 20, 30, contentAlpha).getRGB());
        context.fill(x, y + height - 12, x + width - 12, y + height, 
            new Color(20, 20, 30, contentAlpha).getRGB());
        
        MatrixStack matrices = context.getMatrices();
        
        context.enableScissor(x, y + HEADER_HEIGHT, x + width, y + height);
        
        List<Module> allModules;
        if (eventHandler.getSearchQuery().isEmpty()) {
            allModules = Volt.INSTANCE.getModuleManager().getModulesInCategory(eventHandler.getSelectedCategory());
        } else {
            allModules = Volt.INSTANCE.getModuleManager().getModules();
        }
        List<Module> modules = filterModulesBySearch(allModules);
        
        int moduleY = y + HEADER_HEIGHT + PADDING - eventHandler.getScrollOffset();
        
        for (Module module : modules) {
            int moduleHeight = MODULE_HEIGHT + 5;
            if (moduleExpanded.get(module)) {
                moduleHeight += (int)(animationManager.getDropdownAnimation(module) * SettingsRenderer.getModuleSettingsHeight(module, SETTING_HEIGHT, eventHandler));
            }
            
            if (moduleY + moduleHeight < y + HEADER_HEIGHT) {
                moduleY += moduleHeight;
                continue;
            }
            
            if (moduleY > y + height) break;
            
            boolean isHovered = mouseX >= x + PADDING && mouseX <= x + width - PADDING && 
                              mouseY >= moduleY && mouseY <= moduleY + MODULE_HEIGHT;
            boolean isEnabled = module.isEnabled();
            boolean hasSettings = module.getSettings() != null && !module.getSettings().isEmpty();
            
            float targetAnimation = isEnabled ? 1f : (isHovered ? 0.2f : 0f);
            float currentAnimation = animationManager.getModuleAnimation(module);
            float newAnimation = MathHelper.lerp(0.12f, currentAnimation, targetAnimation);
            animationManager.setModuleAnimation(module, newAnimation);
            
            float targetDropdown = moduleExpanded.get(module) ? 1f : 0f;
            float currentDropdown = animationManager.getDropdownAnimation(module);
            float newDropdown = MathHelper.lerp(0.15f, currentDropdown, targetDropdown);
            animationManager.setDropdownAnimation(module, newDropdown);
            
			Color bgColor = isHovered ? new Color(40, 40, 50, 200) : new Color(25, 25, 35, 100);
			context.fill(x + PADDING, moduleY, x + width - PADDING, moduleY + MODULE_HEIGHT, bgColor.getRGB());
			int indicatorAlpha = isEnabled ? (int)(newAnimation * 255) : 0;
			if (indicatorAlpha > 0) {
				Color indicator = new Color(150, 100, 255, indicatorAlpha);
				context.fill(x + PADDING, moduleY, x + PADDING + 4, moduleY + MODULE_HEIGHT, indicator.getRGB());
			}
            Color textColor = isEnabled ? Color.WHITE : new Color(160, 160, 160);
            regularFont.drawString(matrices, module.getName(), x + PADDING + 10, moduleY + 8, textColor);
            if (hasSettings) {
                UIRenderer.renderDropdownArrow(context, x + width - PADDING - 30, moduleY + MODULE_HEIGHT / 2, 
                    moduleExpanded.get(module), new Color(160, 160, 160));
            }
            
            if (module.getDescription() != null && !module.getDescription().isEmpty()) {
                float descWidth = smallFont.getStringWidth(module.getDescription());
                float maxDescWidth = width - PADDING - 30 - 100;
                if (descWidth > maxDescWidth) {
                    String truncated = module.getDescription();
                    while (smallFont.getStringWidth(truncated + "...") > maxDescWidth && truncated.length() > 1) {
                        truncated = truncated.substring(0, truncated.length() - 1);
                    }
                    truncated += "...";
                    descWidth = smallFont.getStringWidth(truncated);
                    smallFont.drawString(matrices, truncated, 
                        x + width - PADDING - descWidth - (hasSettings ? 40 : 10), moduleY + 10, new Color(120, 120, 120));
                } else {
                    smallFont.drawString(matrices, module.getDescription(), 
                        x + width - PADDING - descWidth - (hasSettings ? 40 : 10), moduleY + 10, new Color(120, 120, 120));
                }
            }
            
            moduleY += MODULE_HEIGHT + 5;
            
            if (hasSettings && newDropdown > 0.05f) {
                int settingsHeight = renderModuleSettings(context, module, x, moduleY, width, newDropdown);
                moduleY += (int)(newDropdown * settingsHeight);
            }
        }
      
        context.disableScissor();
    }
    
    private List<Module> filterModulesBySearch(List<Module> modules) {
        return SearchUtils.filterModulesBySearch(modules, eventHandler.getSearchQuery());
    }
    
    private int renderModuleSettings(DrawContext context, Module module, int x, int moduleY, int width, float animation) {
        return SettingsRenderer.renderModuleSettings(context, module, x, moduleY, width, animation, smallFont, eventHandler.getDropdownExpanded(), eventHandler);
    }
    
    private boolean handleColorPickerClicks(double mouseX, double mouseY, int button) {
        return colorPickerManager.handleColorPickerClicks(mouseX, mouseY, button);
    }
    
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button < 0 || button > 8) return false;
        
        if (handleColorPickerClicks(mouseX, mouseY, button)) {
            return true;
        }
        
        List<Module> allModules;
        if (eventHandler.getSearchQuery().isEmpty()) {
            allModules = Volt.INSTANCE.getModuleManager().getModulesInCategory(eventHandler.getSelectedCategory());
        } else {
            allModules = Volt.INSTANCE.getModuleManager().getModules();
        }
        List<Module> modules = filterModulesBySearch(allModules);
        
        return eventHandler.handleMouseClick(mouseX, mouseY, button, width, height, modules) || 
               super.mouseClicked(mouseX, mouseY, button);
    }
    
    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (colorPickerManager.handleColorPickerDrag(mouseX, mouseY, button, deltaX, deltaY)) {
            return true;
        }
        
        return eventHandler.handleMouseDrag(mouseX, mouseY, button, deltaX, deltaY, width, height);
    }
    
    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (colorPickerManager.handleColorPickerRelease(mouseX, mouseY, button)) {
            return true;
        }
        
        return eventHandler.handleMouseRelease(mouseX, mouseY, button);
     }
    
    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        return eventHandler.handleMouseScroll(mouseX, mouseY, horizontalAmount, verticalAmount, height) ||
               super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }
    
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        
        if (colorPickerManager.handleColorPickerKeyPress(keyCode)) {
            return true;
        }
        if (eventHandler.handleKeyPress(keyCode, scanCode, modifiers)) {
            return true;
        }
        
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            lastSelectedCategory = eventHandler.getSelectedCategory();
            lastScrollOffset = eventHandler.getScrollOffset();
            lastModuleExpanded.clear();
            lastModuleExpanded.putAll(moduleExpanded);
            close();
            return true;
        }
        
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
    
    @Override
    public boolean charTyped(char chr, int modifiers) {
        if (colorPickerManager.handleColorPickerCharTyped(chr)) return true;
        return eventHandler.handleCharTyped(chr, modifiers) || super.charTyped(chr, modifiers);
    }
    
    @Override
    public void close() {
        animationManager.startClosingAnimation();
    }
}