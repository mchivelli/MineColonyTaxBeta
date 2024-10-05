package net.machiavelli.minecolonytax.event;

import com.minecolonies.api.colony.IColony;
import com.minecolonies.api.colony.IColonyManager;
import com.minecolonies.api.colony.buildings.IBuilding;
import net.machiavelli.minecolonytax.MineColonyTax;
import net.machiavelli.minecolonytax.TaxManager;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mod.EventBusSubscriber(modid = MineColonyTax.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ColonyEventListener {

    private static final Logger LOGGER = LogManager.getLogger(ColonyEventListener.class);

    // Map to track building levels in each colony
    private static final Map<Integer, Map<IBuilding, Integer>> colonyBuildingLevels = new HashMap<>();

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }

        // Get all colonies in the world
        List<IColony> colonies = IColonyManager.getInstance().getAllColonies();

        for (IColony colony : colonies) {
            int colonyId = colony.getID();
            Map<IBuilding, Integer> buildingLevels = colonyBuildingLevels.computeIfAbsent(colonyId, k -> new HashMap<>());

            // Iterate through all buildings in the colony
            for (IBuilding building : colony.getBuildingManager().getBuildings().values()) {
                int currentLevel = building.getBuildingLevel();

                // Check if the building is new or has been upgraded
                if (!buildingLevels.containsKey(building) || buildingLevels.get(building) < currentLevel) {
                    LOGGER.info("Detected new or upgraded building: {} at level {} in colony {}", building.getBuildingDisplayName(), currentLevel, colony.getName());

                    // Update tax based on new or upgraded building
                    TaxManager.updateTaxForBuilding(colony, building, currentLevel); // Pass the building as well

                    // Update the tracked building level
                    buildingLevels.put(building, currentLevel);
                }
            }
        }
    }
}
