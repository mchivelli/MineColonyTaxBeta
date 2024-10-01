package net.machiavelli.minecolonytax;

import com.minecolonies.api.colony.IColony;
import com.minecolonies.api.colony.IColonyManager;
import com.minecolonies.api.colony.buildings.IBuilding;
import com.minecolonies.api.colony.buildings.event.BuildingConstructionEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod.EventBusSubscriber(modid = MineColonyTax.MOD_ID)
public class ColonyEventListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(ColonyEventListener.class);

    // Handle building construction events
    @SubscribeEvent
    public static void onBuildingConstruction(BuildingConstructionEvent event) {
        IBuilding building = event.getBuilding();
        IColony colony = building.getColony();
        BuildingConstructionEvent.EventType eventType = event.getEventType();

        // Log the building's name and level
        LOGGER.info("Building Event: " + eventType + " for building: " + building.getBuildingDisplayName());

        int buildingLevel = building.getBuildingLevel();
        LOGGER.info("Building Level: " + buildingLevel);


        if (colony != null) {
            // Update tax based on building and level
            TaxManager.updateTaxForBuilding(colony, building, eventType, buildingLevel);
        }
    }

    // Helper method to get the colony from a building's world and position
    private static IColony getColonyFromBuilding(IBuilding building, IColony colony) {
        return IColonyManager.getInstance().getColonyByPosFromWorld(colony.getWorld(), building.getPosition());
    }
}
