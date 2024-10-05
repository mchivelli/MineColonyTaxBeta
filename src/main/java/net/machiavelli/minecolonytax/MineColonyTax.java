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

    public MineColonyTax() {
        MinecraftForge.EVENT_BUS.register(this);
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::setup);
        modEventBus.addListener(this::clientSetup);

        // Ensure the config is loaded during the mod initialization
        TaxConfig.loadConfig();
    }

    private void setup(final FMLCommonSetupEvent event) {
        LOGGER.info("Initializing MineColony Tax System");
    }

    private void clientSetup(final FMLClientSetupEvent event) {
        LOGGER.info("Client setup for MineColonyTax Mod");
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        LOGGER.info("Server starting: Initializing Tax System");
        TaxManager.initialize(event.getServer());
    }
}
