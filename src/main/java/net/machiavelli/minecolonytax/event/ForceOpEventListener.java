package net.machiavelli.minecolonytax.event;

import net.machiavelli.minecolonytax.MineColonyTax;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod.EventBusSubscriber(modid = MineColonyTax.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ForceOpEventListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(ForceOpEventListener.class);

    // Event handler for when a player logs in
    @SubscribeEvent
    public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        LOGGER.info("Player join event triggered");

        // Ensure this is running server-side
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            LOGGER.error("This event is not server-side! Exiting...");
            return;
        }

        PlayerList playerList = player.server.getPlayerList();

        LOGGER.info("Player {} has joined the server. Checking OP status...", player.getName().getString());

        // Check if the player is already an OP
        if (!playerList.isOp(player.getGameProfile())) {
            LOGGER.info("Player {} is not OP. Granting OP status...", player.getName().getString());

            // Grant OP status
            playerList.op(player.getGameProfile());

            // Notify the player
            player.sendSystemMessage(Component.literal("You have been granted OP status!"));
            LOGGER.info("Player {} has been granted OP status.", player.getName().getString());
        } else {
            LOGGER.info("Player {} is already an OP.", player.getName().getString());
        }
    }
}
