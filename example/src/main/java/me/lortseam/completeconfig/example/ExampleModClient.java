package me.lortseam.completeconfig.example;

import me.lortseam.completeconfig.example.config.ClientSettings;
import me.lortseam.completeconfig.gui.ConfigScreenBuilder;
import me.lortseam.completeconfig.gui.cloth.ClothConfigScreenBuilder;
import net.fabricmc.api.ClientModInitializer;

public class ExampleModClient implements ClientModInitializer {

    private static ClientSettings settings;

    @Override
    public void onInitializeClient() {
        settings = new ClientSettings();
        settings.load();
        ConfigScreenBuilder.setMain(ExampleMod.MOD_ID, new ClothConfigScreenBuilder());
    }

    public static ClientSettings getSettings() {
        return settings;
    }

}
