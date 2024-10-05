package net.machiavelli.minecolonytax;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.minecolonies.api.colony.IColony;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class TaxManager {

    private static final Logger LOGGER = LogManager.getLogger(TaxManager.class);
    private static final String TAX_DATA_FILE = "taxData.json";
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Map<Integer, Integer> colonyTaxData = new HashMap<>();

    // Initialize Tax Manager
    public static void initialize(MinecraftServer server) {
        LOGGER.info("Initializing Tax Manager...");
        loadTaxData(server);
    }

    // Update tax for a building when a new level is reached
    public static void updateTaxForBuilding(IColony colony, BlockPos buildingPos, int buildingLevel) {
        int colonyId = colony.getID();
        int currentTax = colonyTaxData.getOrDefault(colonyId, 0);
        int taxAmount = 10 * buildingLevel; // Example tax calculation

        currentTax += taxAmount;
        colonyTaxData.put(colonyId, currentTax);

        saveTaxData(); // Save updated tax data to file
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
                // Rename or delete corrupted tax file to avoid future issues
                File corruptedFile = new File(server.getServerDirectory(), TAX_DATA_FILE + ".corrupted");
                if (taxFile.renameTo(corruptedFile)) {
                    LOGGER.info("Renamed corrupted tax data file to: {}", corruptedFile.getName());
                } else {
                    LOGGER.error("Failed to rename corrupted tax data file.");
                }
            }
        } else {
            LOGGER.info("No existing tax data file found at: {}", taxFile.getAbsolutePath());
        }
    }
}
