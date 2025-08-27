package com.volt.module.modules.render;

import com.volt.Volt;
import com.volt.event.impl.render.EventRender2D;
import com.volt.module.Category;
import com.volt.module.Module;
import com.volt.module.setting.BooleanSetting;
import com.volt.module.setting.ColorSetting;
import com.volt.module.setting.ModeSetting;
import com.volt.module.setting.NumberSetting;
import com.volt.module.setting.StringSetting;
import com.volt.utils.font.FontManager;
import com.volt.utils.font.fonts.FontRenderer;
import com.volt.gui.utils.render.MSAARoundedRectShader;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Formatting;

import java.awt.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class HUDModule extends Module {
    public static final BooleanSetting watermark = new BooleanSetting("Watermark", true);
    public static final ModeSetting watermarkMode = new ModeSetting("Watermark Mode", "Simple", "Simple", "Gamesense");
    public static final StringSetting watermarkText = new StringSetting("Watermark Text", "Volt");
    public static final ModeSetting watermarkSimpleFontMode = new ModeSetting("Watermark Font", "MC", "MC", "Inter", "JetbrainsMono", "Poppins");
    public static final BooleanSetting arrayList = new BooleanSetting("ArrayList", true);
    public static final NumberSetting arrayListScale = new NumberSetting("ArrayList Scale", 0.5, 3.0, 1.0, 0.1);
    public static final ModeSetting colorMode = new ModeSetting("Color Mode", "Astolfo", "Astolfo", "Theme", "Custom");
    public static final ColorSetting customColor = new ColorSetting("Custom Color", new Color(255, 255, 255));
    public static final ModeSetting fontMode = new ModeSetting("ArrayList Font", "MC", "MC", "Inter", "JetbrainsMono", "Poppins");
    public static final ModeSetting suffixMode = new ModeSetting("Suffix", "Space", "Space", "-", ">", "[ ]");
    public static final BooleanSetting hideVisuals = new BooleanSetting("Hide visuals", true);
    public static final BooleanSetting lowercase = new BooleanSetting("Lowercase", false);
    public static final ModeSetting backBarMode = new ModeSetting("Backbar Mode", "None", "None", "Full", "Rise");
    public static final NumberSetting padding = new NumberSetting("Offset", 0, 40, 5, 1);
    public static final NumberSetting opacity = new NumberSetting("BG Opacity", 0, 255, 80, 1);
    public static final BooleanSetting info = new BooleanSetting("Info", true);
    public static final BooleanSetting bpsCounter = new BooleanSetting("BPS", true);
    public static final BooleanSetting fpsCounter = new BooleanSetting("FPS", true);
    public static final NumberSetting scale = new NumberSetting("Scale", 0.5, 3.0, 1.0, 0.1);

    private final DecimalFormat decimalFormat = new DecimalFormat("0.00");

    public HUDModule() {
        super("HUD", "Renders information", -1, Category.RENDER);
        addSettings(watermark, watermarkMode, watermarkText, watermarkSimpleFontMode, arrayList, arrayListScale, colorMode, customColor, fontMode, suffixMode, hideVisuals, lowercase, backBarMode, padding, opacity, info, bpsCounter, fpsCounter, scale);
    }

    @EventHandler
    private void onEventRender2D(EventRender2D event) {
        if (mc.player == null || mc.world == null) {
            return;
        }
        if (mc.currentScreen != null) {
            return;
        }

        if (watermark.getValue()) {
            switch (watermarkMode.getMode()) {
                case "Simple":
                    if (!watermarkText.getValue().isEmpty()) {
                        String watermark = watermarkText.getValue();

                        char firstCharacter = watermark.charAt(0);
                        String restOfString = watermark.substring(1);

                        String totalWatermarkText = firstCharacter + Formatting.WHITE.toString() + restOfString;

                         int scaledX = 3;
                         int scaledY = 3;
                         
                         Color watermarkColor = switch (colorMode.getMode()) {
                             case "Astolfo" -> new Color(getAstolfo(0));
                             case "Theme" -> getThemeColor();
                             case "Custom" -> getCustomColor();
                             default -> getThemeColor();
                         };
                         
                         switch (watermarkSimpleFontMode.getMode()) {
                             case "MC":
                                 event.getContext().drawText(mc.textRenderer, totalWatermarkText, scaledX, scaledY, watermarkColor.getRGB(), true);
                                 break;
                             default:
                                 getWatermarkFontRenderer(watermarkSimpleFontMode.getMode()).drawString(event.getContext().getMatrices(), totalWatermarkText, scaledX, scaledY, watermarkColor);
                                 break;
                         }
                    }
                    break;

                case "Gamesense":
                     String text = "§f" + watermarkText.getValue() + "§rsense §8| §f " + ("free") + "§7 (" + ("0000") + ") §8 | §f " + getIP();

                     int padding = 2;
                     int offsetX = 4;
                     int offsetY = 4;

                     FontRenderer gamesenseFont = getWatermarkFontRenderer("Inter");
                     int textWidth = (int) gamesenseFont.getStringWidth(text);
                     int textHeight = (int) gamesenseFont.getStringHeight(text);
                     int backgroundWidth = textWidth + padding * 2;
                     int backgroundHeight = textHeight + padding * 2;

                     event.getContext().fill(
                             offsetX - padding - 1,
                             offsetY - padding - 1,
                             offsetX + backgroundWidth + 1,
                             offsetY + backgroundHeight + 1,
                             new Color(96, 96, 96).getRGB()
                     );

                     event.getContext().fill(
                             offsetX - padding,
                             offsetY - padding,
                             offsetX + backgroundWidth,
                             offsetY + backgroundHeight,
                             new Color(25, 25, 25).getRGB()
                     );

                     Color gamesenseColor = switch (colorMode.getMode()) {
                         case "Astolfo" -> new Color(getAstolfo(0));
                         case "Theme" -> getThemeColor();
                         case "Custom" -> getCustomColor();
                         default -> getThemeColor();
                     };
                     
                     gamesenseFont.drawString(
                             event.getContext().getMatrices(),
                             text,
                             offsetX,
                             offsetY,
                             gamesenseColor
                     );
                     break;
            }
        }

        if (arrayList.getValue()) {
             List<Module> enabledModules = new ArrayList<>();
             for (Module module : Volt.INSTANCE.getModuleManager().getModules()) {
                 if (!module.isEnabled()) continue;
                 if (hideVisuals.getValue() && module.getModuleCategory() == Category.RENDER) continue;
                 enabledModules.add(module);
             }
             
             switch (fontMode.getMode()) {
                 case "MC":
                     enabledModules.sort(Comparator.comparingDouble(ri -> -mc.textRenderer.getWidth(getFullName(ri))));
                     break;
                 default:
                     enabledModules.sort(Comparator.comparingDouble(ri -> -getCustomFontRenderer(fontMode.getMode()).getStringWidth(getFullName(ri))));
                     break;
             }

             int i = padding.getValueInt();
            int totalWidth = event.getWidth();

             for (Module m : enabledModules) {
                 int moduleHeight;

                 switch (fontMode.getMode()) {
                     case "MC":
                         moduleHeight = mc.textRenderer.fontHeight;
                         break;
                     default:
                         moduleHeight = (int) getCustomFontRenderer(fontMode.getMode()).getStringHeight(getFullName(m));
                         break;
                 }

                 i += moduleHeight + (int) (3 * arrayListScale.getValue());
             }

            i = padding.getValueInt();

             for (Module m : enabledModules) {
                 int color = switch (colorMode.getMode()) {
                     case "Astolfo" -> getAstolfo(i * 3);
                     case "Theme" -> getThemeColor().getRGB();
                     case "Custom" -> getCustomColor().getRGB();
                     default -> 0;
                 };

                 int moduleWidth;
                 int moduleHeight;

                 switch (fontMode.getMode()) {
                     case "MC":
                         moduleWidth = mc.textRenderer.getWidth(getFullName(m));
                         moduleHeight = mc.textRenderer.fontHeight;
                         break;
                     default:
                         moduleWidth = (int) getCustomFontRenderer(fontMode.getMode()).getStringWidth(getFullName(m));
                         moduleHeight = (int) getCustomFontRenderer(fontMode.getMode()).getStringHeight(getFullName(m));
                         break;
                 }

                 int scaledPadding2 = (int) (2 * arrayListScale.getValue());
                 int scaledPadding1 = (int) (1 * arrayListScale.getValue());
                 int scaledPadding3 = (int) (3 * arrayListScale.getValue());
                 int scaledPadding5 = (int) (5 * arrayListScale.getValue());
                 int bgX = totalWidth - moduleWidth - padding.getValueInt() - scaledPadding2;
                 int bgY = i - scaledPadding2;
                 int bgW = (totalWidth - padding.getValueInt() + scaledPadding2) - bgX;
                 int bgH = (i + moduleHeight + scaledPadding1) - bgY;

                 int cornerRadius = Math.max(3, (int) Math.round(3 * arrayListScale.getValue()));

                 MSAARoundedRectShader shader = MSAARoundedRectShader.getInstance();
                 float spread = 1.5f;
                 Color shadowCol = new Color(0, 0, 0, Math.min(70, (int) (opacity.getValueInt() * 0.5)));
                 shader.drawRoundedRect(bgX - spread / 2f, bgY - spread / 2f, bgW + spread, bgH + spread, cornerRadius + spread / 2f, shadowCol, 8);
                 shader.drawRoundedRect(bgX, bgY, bgW, bgH, cornerRadius, new Color(0, 0, 0, opacity.getValueInt()), 8);
                 switch (backBarMode.getMode()) {
                     case "Full":
                         event.getContext().fill(totalWidth - padding.getValueInt() + scaledPadding3, i - scaledPadding2, totalWidth - padding.getValueInt() + scaledPadding5, i + moduleHeight + scaledPadding1, color);
                         break;
                     case "Rise":
                         event.getContext().fill(totalWidth - padding.getValueInt() + scaledPadding3, i - scaledPadding1, totalWidth - padding.getValueInt() + scaledPadding5, i + moduleHeight, color);
                         break;
                 }

                 switch (fontMode.getMode()) {
                     case "MC":
                         event.getContext().drawText(mc.textRenderer, getFullName(m), totalWidth - moduleWidth - padding.getValueInt(), i, color, true);
                         break;
                     default:
                         getCustomFontRenderer(fontMode.getMode()).drawString(event.getContext().getMatrices(), getFullName(m), totalWidth - moduleWidth - padding.getValueInt(), i, new Color(color));
                         break;
                 }
                 i += moduleHeight + (int) (3 * arrayListScale.getValue());
             }
        }

        if (info.getValue()) {
            String bps = String.valueOf(decimalFormat.format(Math.hypot(mc.player.getX() - mc.player.prevX, mc.player.getZ() - mc.player.prevZ) * 20.0F));
            String fps = String.valueOf(MinecraftClient.getInstance().getCurrentFps());

            int x = (int) (10 * scale.getValue());

            int lineHeight = (int) (mc.textRenderer.fontHeight * scale.getValue());

            int y = event.getHeight() - (fpsCounter.getValue() && bpsCounter.getValue() ? 2 * lineHeight : lineHeight) - (int) (10 * scale.getValue());

            if (fpsCounter.getValue()) {
                int color = switch (colorMode.getMode()) {
                    case "Astolfo" -> getAstolfo(3);
                    case "Theme" -> getThemeColor(1).getRGB();
                    case "Custom" -> getCustomColor().getRGB();
                    default -> 0;
                };
                event.getContext().drawText(mc.textRenderer, "FPS: " + fps, x, y, color, true);
                y -= lineHeight;
            }

            if (bpsCounter.getValue()) {
                int color = switch (colorMode.getMode()) {
                    case "Astolfo" -> getAstolfo(6);
                    case "Theme" -> getThemeColor(2).getRGB();
                    case "Custom" -> getCustomColor().getRGB();
                    default -> 0;
                };
                event.getContext().drawText(mc.textRenderer, "BPS: " + bps, x, y, color, true);
            }
        }
    };

    private FontRenderer getCustomFontRenderer(String name) {
        int scaledSize = (int) (16 * arrayListScale.getValue());
        return switch (name) {
            case "Inter" -> Volt.INSTANCE.fontManager.getSize(scaledSize, FontManager.Type.Inter);
            case "JetbrainsMono" -> Volt.INSTANCE.fontManager.getSize(scaledSize, FontManager.Type.JetbrainsMono);
            case "Poppins" -> Volt.INSTANCE.fontManager.getSize(scaledSize, FontManager.Type.Poppins);
            default -> Volt.INSTANCE.fontManager.getSize(scaledSize, FontManager.Type.Inter);
        };
    }

    private FontRenderer getWatermarkFontRenderer(String name) {
        int scaledSize = 16;
        return switch (name) {
            case "Inter" -> Volt.INSTANCE.fontManager.getSize(scaledSize, FontManager.Type.Inter);
            case "JetbrainsMono" -> Volt.INSTANCE.fontManager.getSize(scaledSize, FontManager.Type.JetbrainsMono);
            case "Poppins" -> Volt.INSTANCE.fontManager.getSize(scaledSize, FontManager.Type.Poppins);
            default -> Volt.INSTANCE.fontManager.getSize(scaledSize, FontManager.Type.Inter);
        };
    }

    private String getIP() {
        if (mc.world == null) {
            return "NULL";
        } else {
            if (mc.isInSingleplayer()) {
                return "Singleplayer";
            } else {
                return mc.getCurrentServerEntry().address;
            }
        }
    }

    private String getFullName(Module m) {
        if (lowercase.getValue()) {
            if (m.getSuffix() == null) {
                return m.getName().toLowerCase();
            } else {
                switch (suffixMode.getMode()) {
                    case "Space":
                        return (m.getName() + " " + Formatting.GRAY + m.getSuffix()).toLowerCase();
                    case "-":
                        return (m.getName() + Formatting.GRAY + " - " + m.getSuffix()).toLowerCase();
                    case ">":
                        return (m.getName() + Formatting.GRAY + " > " + m.getSuffix()).toLowerCase();
                    case "[ ]":
                        return (m.getName() + Formatting.DARK_GRAY + " [" + Formatting.GRAY + m.getSuffix() + Formatting.DARK_GRAY + "]").toLowerCase();
                }
            }
        } else {
            if (m.getSuffix() == null) {
                return m.getName();
            } else {
                switch (suffixMode.getMode()) {
                    case "Space":
                        return m.getName() + " " + Formatting.GRAY + m.getSuffix();
                    case "-":
                        return m.getName() + Formatting.GRAY + " - " + m.getSuffix();
                    case ">":
                        return m.getName() + Formatting.GRAY + " > " + m.getSuffix();
                    case "[ ]":
                        return m.getName() + Formatting.DARK_GRAY + " [" + Formatting.GRAY + m.getSuffix() + Formatting.DARK_GRAY + "]";
                }
            }
        }
        return "";
    }

    public static int getAstolfo(int offset) {
        int i = (int) ((System.currentTimeMillis() / 11 + offset) % 360);
        i = (i > 180 ? 360 - i : i) + 180;
        return Color.HSBtoRGB(i / 360f, 0.55f, 1f);
    }

    private Color getThemeColor() {
        return new Color(150, 0, 255);
    }

    private Color getThemeColor(int variant) {
        switch (variant) {
            case 1:
                return new Color(200, 0, 255);
            case 2:
                return new Color(100, 0, 255);
            default:
                return getThemeColor();
        }
    }

    private Color getCustomColor() {
        return customColor.getValue();
    }
}