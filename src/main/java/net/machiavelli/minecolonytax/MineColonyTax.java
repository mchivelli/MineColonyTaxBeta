package net.machiavelli.minecolonytax;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(MineColonyTax.MOD_ID)
public class MineColonyTax {

    public static final String MOD_ID = "minecolonytax";
    public static final Logger LOGGER = LogManager.getLogger(MineColonyTax.class);

    // Constructor
    public MineColonyTax() {
        // Register to Forge event bus
        MinecraftForge.EVENT_BUS.register(this);

        // Register to the mod event bus
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::setup);
        modEventBus.addListener(this::clientSetup);
    }

    // Setup method for common configurations
    private void setup(final FMLCommonSetupEvent event) {
        LOGGER.info("Initializing MineColony Tax System");
        TaxConfig.loadConfig(TaxConfig.CONFIG, "minecolonytax.toml");
    }

    // Setup method for client-specific configurations
    private void clientSetup(final FMLClientSetupEvent event) {
        LOGGER.info("Client setup for MineColonyTax Mod");
        // Add client-specific setup, like rendering handlers or key bindings
    }

    // Method to handle server starting event
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        LOGGER.info("Server starting: Initializing Tax System");
        // Initialize TaxManager after config is loaded
        TaxManager.initialize(event.getServer());
    }
}
