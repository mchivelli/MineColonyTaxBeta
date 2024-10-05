package net.machiavelli.minecolonytax.commands;

import com.minecolonies.api.IMinecoloniesAPI;
import com.minecolonies.api.colony.IColony;
import com.minecolonies.api.colony.IColonyManager;
import com.minecolonies.api.colony.permissions.Rank;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.machiavelli.minecolonytax.MineColonyTax; // Import the main mod class for MOD_ID
import net.machiavelli.minecolonytax.TaxManager;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod.EventBusSubscriber(modid = MineColonyTax.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ClaimTaxCommand {

    private static final Logger LOGGER = LogManager.getLogger(ClaimTaxCommand.class);

    // Register the command
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("claimtax")
                        .requires(source -> source.hasPermission(0))  // Set to 0 for testing; change to 2 for OP
                        .executes(ClaimTaxCommand::execute)
        );
    }

    // Subscribe to the command registration event
    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        ClaimTaxCommand.register(event.getDispatcher());
    }

    // Command execution logic
    private static int execute(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();
        ServerPlayer player = source.getPlayerOrException();
        IColonyManager colonyManager = IMinecoloniesAPI.getInstance().getColonyManager();
        Level world = source.getLevel();
        MinecraftServer server = source.getServer();

        List<IColony> colonies = colonyManager.getColonies(world);
        boolean foundColonies = false;

        for (IColony colony : colonies) {
            Rank playerRank = colony.getPermissions().getRank(player.getUUID());

            if (playerRank != null && playerRank.isColonyManager()) {
                foundColonies = true;

                // Claim the tax revenue for the colony
                int claimedAmount = TaxManager.claimTax(colony);

                if (claimedAmount > 0) {
                    // Send the claimed tax information to the player via chat
                    player.sendSystemMessage(Component.literal("You have claimed " + claimedAmount + " in tax revenue from colony " + colony.getName() + "."));

                    // Execute the /sdmshop add <player> <taxamount> command as the server
                    String playerName = player.getName().getString();
                    String commandString = String.format("sdmshop add %s %d", playerName, claimedAmount);
                    CommandSourceStack serverCommandSource = server.createCommandSourceStack();
                    ParseResults<CommandSourceStack> parseResults = server.getCommands().getDispatcher().parse(commandString, serverCommandSource);
                    server.getCommands().getDispatcher().execute(parseResults);
                    LOGGER.info("Executed command: {}", commandString);
                } else {
                    LOGGER.info("No tax available to claim for colony {}", colony.getName());
                }
            }
        }

        if (!foundColonies) {
            source.sendFailure(Component.literal("You are not an owner or officer of any colonies."));
        }

        return 1;
    }
}
