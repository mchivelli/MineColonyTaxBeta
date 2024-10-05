package net.machiavelli.minecolonytax;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLPaths;

@Mod.EventBusSubscriber
public class TaxConfig {

    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec CONFIG;

    public static final ForgeConfigSpec.IntValue TAX_INTERVAL_MINUTES;

    static {
        BUILDER.push("Tax System Settings");
        TAX_INTERVAL_MINUTES = BUILDER
                .comment("The interval in minutes for generating taxes for colonies.")
                .defineInRange("TaxIntervalMinutes", 60, 1, Integer.MAX_VALUE); // Default to 60 minutes
        BUILDER.pop();

        CONFIG = BUILDER.build();
    }

    public static void loadConfig() {
        final CommentedFileConfig file = CommentedFileConfig.builder(FMLPaths.CONFIGDIR.get().resolve("minecolonytax.toml"))
                .sync()
                .autosave()
                .build();
        file.load();
        CONFIG.setConfig(file);
    }

    public static int getTaxIntervalInMinutes() {
        return TAX_INTERVAL_MINUTES.get();
    }

    @SubscribeEvent
    public static void onServerStarting(ServerStartingEvent event) {
        loadConfig();  // Ensure config is loaded on server start
    }
}
