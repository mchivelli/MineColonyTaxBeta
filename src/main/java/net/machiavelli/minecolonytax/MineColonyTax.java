package net.machiavelli.minecolonytax;

import net.minecraft.server.MinecraftServer;
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
        // Register event listeners on the mod event bus
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::setup);
        modEventBus.addListener(this::clientSetup);

        // Register this class to listen to Forge events
        MinecraftForge.EVENT_BUS.register(this);
    }

    // Setup method for common configurations
    private void setup(final FMLCommonSetupEvent event) {
        LOGGER.info("Initializing MineColony Tax System");
        // Initialize any server-side setup required for the tax system
    }

    // Setup method for client-specific configurations
    private void clientSetup(final FMLClientSetupEvent event) {
        LOGGER.info("Client setup for MineColonyTax Mod");
        // Add client-specific setup, like rendering handlers or key bindings
    }

    // Method to handle server starting event
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        LOGGER.info("Server Starting: Initializing Tax System");
        MinecraftServer server = event.getServer();  // Get the MinecraftServer instance
        TaxManager.initialize(server);  // Pass the server instance to initialize the tax manager
    }
}
