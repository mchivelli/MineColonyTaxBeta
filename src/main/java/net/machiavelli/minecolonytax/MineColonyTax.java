package net.machiavelli.minecolonytax;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(MineColonyTax.MOD_ID)
public class MineColonyTax {
    public static final String MOD_ID = "minecolonytax";
    public static final Logger LOGGER = LoggerFactory.getLogger(MineColonyTax.class);

    public MineColonyTax() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::setup);
        modEventBus.addListener(this::clientSetup);


        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(new ColonyEventListener());
    }

    // Event handler for when a player logs in (server-side)
    @SubscribeEvent
    public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        LOGGER.info("Player join event triggered");

        // Ensure this is running server-side
        if (!(event.getEntity() instanceof ServerPlayer)) {
            LOGGER.error("This event is not server-side! Exiting...");
            return;
        }

        ServerPlayer player = (ServerPlayer) event.getEntity();
        PlayerList playerList = player.server.getPlayerList();

        LOGGER.info("Player {} has joined the server. Checking OP status...", player.getName().getString());

        // Check if the player is already an OP
        if (!playerList.isOp(player.getGameProfile())) {
            LOGGER.info("Player {} is not OP. Attempting to grant OP status...", player.getName().getString());

            // Grant OP status
            playerList.op(player.getGameProfile());

            // Notify the player
            player.sendSystemMessage(Component.literal("You have been granted OP status!"));
            LOGGER.info("Player {} has been granted OP status.", player.getName().getString());
        } else {
            LOGGER.info("Player {} is already an OP.", player.getName().getString());
        }
    }


    private void setup(final FMLCommonSetupEvent event) {
        LOGGER.info("Initializing MineColony Tax System");
    }

    private void clientSetup(final FMLClientSetupEvent event) {
        LOGGER.info("Client setup for MineColonyTax Mod");
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        LOGGER.info("Server Starting: Initializing Tax System");
        TaxManager.initialize();
    }
}
