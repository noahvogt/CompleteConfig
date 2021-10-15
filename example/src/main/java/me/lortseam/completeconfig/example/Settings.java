package me.lortseam.completeconfig.example;

import me.lortseam.completeconfig.api.ConfigContainer;
import me.lortseam.completeconfig.api.ConfigEntries;
import me.lortseam.completeconfig.api.ConfigEntry;
import me.lortseam.completeconfig.api.ConfigGroup;
import me.lortseam.completeconfig.data.Config;
import me.shedaniel.clothconfig2.api.ModifierKeyCode;
import me.shedaniel.math.Color;
import net.minecraft.client.util.InputUtil;

import java.util.Arrays;
import java.util.List;

public final class Settings extends Config implements ConfigContainer {

    Settings() {
        super(ExampleMod.MOD_ID);
    }

    @Transitive
    @ConfigEntries
    public static class DataTypes implements ConfigGroup {

        private boolean bool;
        private int integer;
        @ConfigEntry.BoundedInteger(min = 0, max = 10)
        @ConfigEntry.Slider
        private int boundedInt;
        private long aLong;
        @ConfigEntry.BoundedLong(min = 0, max = 100)
        @ConfigEntry.Slider
        private long boundedLong;
        private float aFloat;
        private double aDouble;
        private String string = "";
        @ConfigEntry.Dropdown
        private AnEnum anEnum = AnEnum.FOO;
        private List<String> list = Arrays.asList("First entry", "Second entry");
        private String[] array = new String[0];
        private Color color = Color.ofRGB(0, 255, 0);
        private InputUtil.Key key = InputUtil.UNKNOWN_KEY;
        private ModifierKeyCode modKey = ModifierKeyCode.unknown();

    }

    public enum AnEnum {
        FOO, BAR, BAZ
    }

    @Transitive
    @ConfigEntries
    public static class Tooltips implements ConfigGroup {

        private boolean defaultOneLine;

        private boolean defaultMultiLine;

        @ConfigEntry(tooltipTranslationKeys = {"customTooltipLine1", "customTooltipLine2"})
        private boolean custom;

    }

}
