package net.machiavelli.minecolonytax;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.minecolonies.api.colony.IColony;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.io.File;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class TaxManager {

    private static final Logger LOGGER = LogManager.getLogger(TaxManager.class);
    private static final Map<Integer, Integer> colonyTaxData = new HashMap<>();
    private static final Map<Integer, Map<BlockPos, Integer>> builtBuildings = new HashMap<>();
    private static final String TAX_DATA_FILE = "config/minecolonytax/taxData.json";
    private static final String BUILDING_DATA_FILE = "config/minecolonytax/builtBuildings.json";
    private static final Gson GSON = new Gson();

    public static void initialize(MinecraftServer server) {
        LOGGER.info("Initializing Tax Manager...");
        loadTaxData();
        loadBuildingData();
    }

    public static void updateTaxForBuilding(IColony colony, BlockPos buildingPos, int buildingLevel) {
        int colonyId = colony.getID();
        int currentTax = colonyTaxData.getOrDefault(colonyId, 0);
        int taxAmount = 10 * buildingLevel;

        currentTax += taxAmount;
        colonyTaxData.put(colonyId, currentTax);

        builtBuildings.putIfAbsent(colonyId, new HashMap<>());
        builtBuildings.get(colonyId).put(buildingPos, buildingLevel);

        saveTaxData();
        saveBuildingData();

        LOGGER.info("Updated tax for colony {}. Total tax: {}", colony.getName(), currentTax);
    }

    public static int getStoredTaxForColony(IColony colony) {
        return colonyTaxData.getOrDefault(colony.getID(), 0);
    }


    public static int claimTax(IColony colony) {
        int colonyId = colony.getID();
        int taxAmount = colonyTaxData.getOrDefault(colonyId, 0);
        LOGGER.info("Claiming tax for colony {}: {}", colony.getName(), taxAmount);
        colonyTaxData.put(colonyId, 0);
        saveTaxData();
        return taxAmount;
    }

    private static void saveTaxData() {
        try (FileWriter writer = new FileWriter(TAX_DATA_FILE)) {
            GSON.toJson(colonyTaxData, writer);
            LOGGER.info("Tax data saved to file.");
        } catch (IOException e) {
            LOGGER.error("Error saving tax data", e);
        }
    }

    private static void loadTaxData() {
        File taxFile = new File(TAX_DATA_FILE);
        if (!taxFile.exists()) {
            LOGGER.warn("No existing tax data file found at: {}", taxFile.getAbsolutePath());
            return;
        }

        try (FileReader reader = new FileReader(taxFile)) {
            Type taxDataType = new TypeToken<Map<Integer, Integer>>() {}.getType();
            Map<Integer, Integer> loadedData = GSON.fromJson(reader, taxDataType);
            if (loadedData != null) {
                colonyTaxData.putAll(loadedData);
                LOGGER.info("Loaded tax data from file.");
            }
        } catch (IOException e) {
            LOGGER.error("Error loading tax data", e);
        }
    }

    private static void saveBuildingData() {
        try (FileWriter writer = new FileWriter(BUILDING_DATA_FILE)) {
            GSON.toJson(builtBuildings, writer);
            LOGGER.info("Building data saved to file.");
        } catch (IOException e) {
            LOGGER.error("Error saving building data", e);
        }
    }

    private static void loadBuildingData() {
        File buildingFile = new File(BUILDING_DATA_FILE);
        if (!buildingFile.exists()) {
            LOGGER.warn("No existing building data file found at: {}", buildingFile.getAbsolutePath());
            return;
        }

        try (FileReader reader = new FileReader(buildingFile)) {
            Type buildingDataType = new TypeToken<Map<Integer, Map<BlockPos, Integer>>>() {}.getType();
            Map<Integer, Map<BlockPos, Integer>> loadedData = GSON.fromJson(reader, buildingDataType);
            if (loadedData != null) {
                builtBuildings.putAll(loadedData);
                LOGGER.info("Loaded building data from file.");
            }
        } catch (IOException e) {
            LOGGER.error("Error loading building data", e);
        }
    }
}
