package net.machiavelli.minecolonytax.commands;

import com.minecolonies.api.IMinecoloniesAPI;
import com.minecolonies.api.colony.IColony;
import com.minecolonies.api.colony.IColonyManager;
import com.minecolonies.api.colony.permissions.Rank;
import com.minecolonies.core.colony.ColonyManager;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.machiavelli.minecolonytax.TaxManager;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

public class CheckTaxRevenueCommand {

    // Register the command
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("checktax")
                        .requires(source -> source.hasPermission(0)) // Players with permission level 2 or higher can use this command
                        .executes(CheckTaxRevenueCommand::execute)
        );
    }

    @Mod.EventBusSubscriber
    public class ModEventSubscriber {

        @SubscribeEvent
        public static void onServerStarting(RegisterCommandsEvent event) {
            CheckTaxRevenueCommand.register(event.getDispatcher());
        }
    }

    // Command execution logic
    private static int execute(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();
        ServerPlayer player = source.getPlayerOrException(); // Get the player executing the command
        IColonyManager colonyManager = IMinecoloniesAPI.getInstance().getColonyManager(); // Access colony manager
// Access colony manager
        Level world = source.getLevel(); // Retrieve the world from the command source
        List<IColony> colonies = colonyManager.getColonies(world); // Get colonies in this world
        // Get all colonies
        boolean foundColonies = false;

        // Loop through each colony to check if the player is an owner or officer
        for (IColony colony : colonies) {
            // Check if the player is an owner or officer based on their rank
            Rank playerRank = colony.getPermissions().getRank(player.getUUID());
            if (playerRank.isColonyManager()) {
                foundColonies = true;

                // Get the tax revenue for the colony from the TaxManager
                int taxRevenue = TaxManager.getTaxForColony(colony);

                // Send the tax information to the player via chat
                source.sendSuccess(() -> Component.literal("Colony: " + colony.getName() + " - Tax Revenue: " + taxRevenue), false);
            }
        }

        if (!foundColonies) {
            source.sendSuccess(() -> Component.literal("You are not an owner or officer of any colonies."), false);
        }

        return 1;
    }
}
