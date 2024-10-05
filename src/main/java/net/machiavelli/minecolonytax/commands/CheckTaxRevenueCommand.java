package net.machiavelli.minecolonytax.commands;

import com.minecolonies.api.IMinecoloniesAPI;
import com.minecolonies.api.colony.IColony;
import com.minecolonies.api.colony.IColonyManager;
import com.minecolonies.api.colony.buildings.IBuilding;
import com.minecolonies.api.colony.permissions.Rank;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.machiavelli.minecolonytax.MineColonyTax;  // Import the main mod class for MOD_ID
import net.machiavelli.minecolonytax.TaxManager;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

@Mod.EventBusSubscriber(modid = MineColonyTax.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CheckTaxRevenueCommand {

    private static final Logger LOGGER = LogManager.getLogger(CheckTaxRevenueCommand.class);

    // Register the command
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("checktax")
                        .requires(source -> source.hasPermission(0))  // Set to 0 for testing; change to 2 for OP
                        .executes(CheckTaxRevenueCommand::execute)
        );
    }

    // Subscribe to the command registration event
    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        CheckTaxRevenueCommand.register(event.getDispatcher());
    }

    private static int execute(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();
        ServerPlayer player = source.getPlayerOrException();
        MinecraftServer server = player.getServer();  // Retrieve the server instance

        if (server == null) {
            source.sendFailure(Component.literal("Unable to retrieve server instance."));
            return 0;
        }

        LOGGER.info("Executing /checktax command for player: {}", player.getName().getString());

        IColonyManager colonyManager = IMinecoloniesAPI.getInstance().getColonyManager();
        List<IColony> colonies = colonyManager.getAllColonies();  // Retrieve all colonies, not just specific to the world

        boolean foundColonies = false;

        for (IColony colony : colonies) {
            // Verify the player is a manager of the colony
            Rank playerRank = colony.getPermissions().getRank(player.getUUID());
            LOGGER.info("Checking colony: {}, Player rank: {}", colony.getName(), playerRank);

            if (playerRank.isColonyManager()) {
                foundColonies = true;

                // Get the stored tax revenue for the colony
                int taxRevenue = TaxManager.getStoredTaxForColony(colony);
                LOGGER.info("Player {} is a manager of colony '{}'. Tax revenue: {}", player.getName().getString(), colony.getName(), taxRevenue);

                // Send the tax information to the player via chat
                source.sendSuccess(() -> Component.literal("Colony: " + colony.getName() + " - Stored Tax Revenue: " + taxRevenue), false);
            }
        }

        if (!foundColonies) {
            LOGGER.warn("Player {} is not a manager of any colonies.", player.getName().getString());
            source.sendFailure(Component.literal("You are not an owner or officer of any colonies."));
        }

        return 1;
    }
}
