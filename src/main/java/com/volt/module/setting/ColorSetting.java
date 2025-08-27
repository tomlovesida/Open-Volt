package com.volt.module.setting;

import java.awt.Color;

public class ColorSetting extends Setting {
    private Color value;
    private boolean hasAlpha;
    
    public ColorSetting(String name, Color defaultValue) {
        this(name, defaultValue, false);
    }
    
    public ColorSetting(String name, Color defaultValue, boolean hasAlpha) {
        super(name);
        this.value = defaultValue;
        this.hasAlpha = hasAlpha;
    }
    
    public ColorSetting(String name, int rgb) {
        this(name, new Color(rgb), false);
    }
    
    public ColorSetting(String name, int rgb, boolean hasAlpha) {
        this(name, new Color(rgb, hasAlpha), hasAlpha);
    }
    
    public Color getValue() {
        return value;
    }
    
    public void setValue(Color value) {
        this.value = value;
    }
    
    public void setValue(int rgb) {
        this.value = new Color(rgb, hasAlpha);
    }
    
    public void setValue(int r, int g, int b) {
        this.value = new Color(r, g, b);
    }
    
    public void setValue(int r, int g, int b, int a) {
        this.value = new Color(r, g, b, a);
    }
    
    public int getRGB() {
        return value.getRGB();
    }
    
    public int getRed() {
        return value.getRed();
    }
    
    public int getGreen() {
        return value.getGreen();
    }
    
    public int getBlue() {
        return value.getBlue();
    }
    
    public int getAlpha() {
        return value.getAlpha();
    }
    
    public boolean hasAlpha() {
        return hasAlpha;
    }
    
    public void setHasAlpha(boolean hasAlpha) {
        this.hasAlpha = hasAlpha;
    }
    
    public float[] getHSB() {
        return Color.RGBtoHSB(value.getRed(), value.getGreen(), value.getBlue(), null);
    }
    
    public void setFromHSB(float hue, float saturation, float brightness) {
        int rgb = Color.HSBtoRGB(hue, saturation, brightness);
        if (hasAlpha) {
            this.value = new Color(rgb | (value.getAlpha() << 24), true);
        } else {
            this.value = new Color(rgb);
        }
    }
}