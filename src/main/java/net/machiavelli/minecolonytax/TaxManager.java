package net.machiavelli.minecolonytax;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.minecolonies.api.IMinecoloniesAPI;
import com.minecolonies.api.colony.IColony;
import com.minecolonies.api.colony.IColonyManager;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.minecraftforge.common.MinecraftForge;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

@Mod.EventBusSubscriber
public class TaxManager {

    private static final Logger LOGGER = LogManager.getLogger(TaxManager.class);
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Map<Integer, Integer> colonyTaxData = new HashMap<>();
    private static final String TAX_DATA_FILE = "config/colonyTaxData.json";
    private static MinecraftServer serverInstance;
    private static int tickCount = 0;  // Keep track of ticks

    // Initialize Tax Manager
    public static void initialize(MinecraftServer server) {
        LOGGER.info("Initializing Tax Manager...");
        serverInstance = server;
        loadTaxData(server);  // Load tax data on server start

        // Register to handle ticks for generating tax every minute (1200 ticks)
        MinecraftForge.EVENT_BUS.register(new TickEventHandler());
    }

    // Save tax data before the server stops
    @SubscribeEvent
    public static void onServerStopping(ServerStoppingEvent event) {
        LOGGER.info("Server stopping. Saving tax data...");
        saveTaxData();  // Save tax data when server stops
    }

    // Inner class for handling tick events
    public static class TickEventHandler {
        @SubscribeEvent
        public void onServerTick(TickEvent.ServerTickEvent event) {
            if (event.phase == TickEvent.Phase.END) {
                tickCount++;
                // Generate tax based on interval set in config
                int taxIntervalInTicks = TaxConfig.getTaxIntervalInMinutes() * 1200;
                if (tickCount >= taxIntervalInTicks) {
                    TaxManager.generateTaxesForAllColonies();
                    tickCount = 0;  // Reset the tick counter
                }
            }
        }
    }

    // Generate taxes for all colonies every interval
    public static void generateTaxesForAllColonies() {
        if (serverInstance != null) {
            serverInstance.getAllLevels().forEach(world -> {
                IColonyManager colonyManager = IMinecoloniesAPI.getInstance().getColonyManager();
                colonyManager.getColonies(world).forEach(colony -> {
                    int buildingCount = colony.getBuildingManager().getBuildings().size();
                    int generatedTax = buildingCount * 5; // Example: Generate 5 tax per building.
                    incrementTaxRevenue(colony, generatedTax);
                    LOGGER.info("Generated {} tax for colony {}", generatedTax, colony.getName());

                    // Immediately save the updated tax data
                    saveTaxData();
                });
            });
        }
    }

    // Update tax for a building when a new level is reached
    public static void updateTaxForBuilding(IColony colony, int buildingLevel) {
        int colonyId = colony.getID();
        int currentTax = colonyTaxData.getOrDefault(colonyId, 0);
        int taxAmount = 10 * buildingLevel;  // Example: Tax is 10 per building level

        currentTax += taxAmount;
        colonyTaxData.put(colonyId, currentTax);

        // Immediately save the updated tax data to ensure persistence
        saveTaxData();
        LOGGER.info("Updated tax for colony {}. Total tax now: {}", colony.getName(), currentTax);
    }

    // Retrieve the stored tax amount for a colony
    public static int getStoredTaxForColony(IColony colony) {
        return colonyTaxData.getOrDefault(colony.getID(), 0);
    }

    // Claim the tax for a colony and reset it to zero
    public static int claimTax(IColony colony) {
        int colonyId = colony.getID();
        int taxAmount = colonyTaxData.getOrDefault(colonyId, 0);

        if (taxAmount > 0) {
            colonyTaxData.put(colonyId, 0); // Reset the tax after claiming
            saveTaxData(); // Save updated tax data to file
            LOGGER.info("Claimed tax for colony {}. Amount claimed: {}", colony.getName(), taxAmount);
        } else {
            LOGGER.info("No tax available to claim for colony {}", colony.getName());
        }

        return taxAmount;
    }

    // Save tax data to a JSON file
    private static void saveTaxData() {
        try (FileWriter writer = new FileWriter(TAX_DATA_FILE)) {
            GSON.toJson(colonyTaxData, writer);
            LOGGER.info("Saved tax data to file.");
        } catch (IOException e) {
            LOGGER.error("Error saving tax data", e);
        }
    }

    // Load tax data from a JSON file
    private static void loadTaxData(MinecraftServer server) {
        File taxFile = new File(server.getServerDirectory(), TAX_DATA_FILE);
        if (taxFile.exists()) {
            try (FileReader reader = new FileReader(taxFile)) {
                Type taxDataType = new TypeToken<Map<Integer, Integer>>() {}.getType();
                Map<Integer, Integer> loadedData = GSON.fromJson(reader, taxDataType);
                if (loadedData != null) {
                    colonyTaxData.putAll(loadedData); // Load saved tax data
                    LOGGER.info("Loaded tax data from file.");
                }
            } catch (IOException e) {
                LOGGER.error("Error loading tax data", e);
            } catch (Exception e) {
                LOGGER.error("Unexpected error while parsing tax data. Please check the file format.", e);
            }
        } else {
            LOGGER.info("No existing tax data file found at: {}", taxFile.getAbsolutePath());
        }
    }

    // Increment tax revenue for a colony
    private static void incrementTaxRevenue(IColony colony, int taxAmount) {
        int currentTax = colonyTaxData.getOrDefault(colony.getID(), 0);
        colonyTaxData.put(colony.getID(), currentTax + taxAmount);
    }
}
