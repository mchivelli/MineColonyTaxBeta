package net.machiavelli.minecolonytax;

import com.minecolonies.api.colony.IColony;
import com.minecolonies.api.colony.buildings.IBuilding;
import net.minecraft.core.BlockPos;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class TaxManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(TaxManager.class);
    private static final Map<IColony, Integer> colonyTaxData = new HashMap<>();
    private static final int BASE_TAX_PER_LEVEL = 10;

    // Map to track taxed buildings and their levels using BlockPos as the key
    private static final Map<BlockPos, Integer> buildingTaxedLevels = new HashMap<>();

    public static void initialize() {
        LOGGER.info("Initializing Tax Manager...");
        //colonyTaxData.clear();
        //buildingTaxedLevels.clear();
    }

    public static void updateTaxForBuilding(IColony colony, IBuilding building, int buildingLevel) {
        BlockPos buildingPos = building.getPosition(); // Get the building's position
        int lastTaxedLevel = buildingTaxedLevels.getOrDefault(buildingPos, 0);

        // Only update tax if the building has reached a new level
        if (buildingLevel > lastTaxedLevel) {
            int currentTax = colonyTaxData.getOrDefault(colony, 0);
            int taxAmount = BASE_TAX_PER_LEVEL * (buildingLevel - lastTaxedLevel);
            currentTax += taxAmount;
            colonyTaxData.put(colony, currentTax);

            // Update the taxed level for this building
            buildingTaxedLevels.put(buildingPos, buildingLevel);

            LOGGER.info("Updated tax for colony {}. Total tax: {}", colony.getName(), currentTax);
        }
    }


    // Claim tax for a colony and reset the amount
    public static int claimTax(IColony colony) {
        int taxAmount = colonyTaxData.getOrDefault(colony, 0);
        colonyTaxData.put(colony, 0);  // Reset tax after claim
        LOGGER.info("Tax claimed for colony {}. Claimed amount: {}", colony.getName(), taxAmount);
        return taxAmount;
    }


    public static int getTaxForColony(IColony colony) {
        int totalTax = 0;

        // Iterate through all the buildings in the colony and calculate tax
        for (IBuilding building : colony.getBuildingManager().getBuildings().values()) {
            // Check if the building is fully built and has no pending work orders
            if (building.isBuilt() && !building.hasWorkOrder()) {
                int buildingTax = 10; // Fixed tax rate per building (adjust as needed)
                totalTax += buildingTax;
            }
        }

        return totalTax; // Return the total calculated tax for the colony
    }





}
