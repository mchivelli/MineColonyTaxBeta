package net.machiavelli.minecolonytax;

import com.minecolonies.api.colony.IColony;
import com.minecolonies.api.colony.buildings.IBuilding;
import com.minecolonies.api.colony.buildings.event.BuildingConstructionEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class TaxManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(TaxManager.class);
    private static final Map<IColony, Integer> colonyTaxData = new HashMap<>();
    private static final int BASE_TAX_PER_LEVEL = 10;

    public static void initialize() {
        LOGGER.info("Initializing Tax Manager...");
        colonyTaxData.clear();  // Clear any old tax data if necessary.
    }

    // Update tax for a building based on its level when built
    public static void updateTaxForBuilding(IColony colony, IBuilding building, BuildingConstructionEvent.EventType eventType, int buildingLevel) {
        int currentTax = colonyTaxData.getOrDefault(colony, 0);
        int taxAmount = BASE_TAX_PER_LEVEL * buildingLevel;  // Tax is calculated per building level
        currentTax += taxAmount;
        colonyTaxData.put(colony, currentTax);
        LOGGER.info("Updated tax for colony " + colony.getName() + ". Total tax: " + currentTax);
    }

    // Claim tax for a colony and reset the amount
    public static int claimTax(IColony colony) {
        int taxAmount = colonyTaxData.getOrDefault(colony, 0);
        colonyTaxData.put(colony, 0);  // Reset tax after claim
        LOGGER.info("Tax claimed for colony " + colony.getName() + ". Claimed amount: " + taxAmount);
        return taxAmount;
    }

    public static int getTaxForColony(IColony colony) {
        int totalTax = 0;

        // Iterate through all the buildings in the colony and calculate tax
        for (IBuilding building : colony.getBuildingManager().getBuildings().values()) {
            int buildingTax = 10; // Fixed tax rate for now (you can adjust this)
            totalTax += buildingTax;
        }

        return totalTax; // Return the total calculated tax for the colony
    }



}
