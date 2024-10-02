package net.machiavelli.minecolonytax;

import com.minecolonies.api.colony.IColony;
import com.minecolonies.api.colony.buildings.IBuilding;
import com.minecolonies.api.colony.buildings.event.BuildingConstructionEvent;
import net.minecraft.core.BlockPos;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.*;

@Mod.EventBusSubscriber(modid = MineColonyTax.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ColonyEventListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(ColonyEventListener.class);

    // Map to track buildings under construction, mapping from BlockPos to BuildingInfo
    private static final Map<BlockPos, BuildingInfo> buildingsUnderConstruction = new HashMap<>();

    // Handle building construction events
    @SubscribeEvent
    public static void onBuildingConstruction(BuildingConstructionEvent event) {
        IBuilding building = event.getBuilding();
        IColony colony = building.getColony();
        BuildingConstructionEvent.EventType eventType = event.getEventType();

        // Log the building's name and event type
        LOGGER.info("Building Event: " + eventType + " for building: " + building.getBuildingDisplayName());

        BlockPos buildingPos = building.getPosition();

        if (eventType == BuildingConstructionEvent.EventType.BUILT || eventType == BuildingConstructionEvent.EventType.UPGRADED) {
            // Construction or upgrade has started; add building to tracking map
            buildingsUnderConstruction.put(buildingPos, new BuildingInfo(building, colony));
            LOGGER.info("Added building to tracking: " + building.getBuildingDisplayName());
        } else if (eventType == BuildingConstructionEvent.EventType.REMOVED) {
            // Building has been removed; remove from tracking map
            buildingsUnderConstruction.remove(buildingPos);
            LOGGER.info("Removed building from tracking: " + building.getBuildingDisplayName());
        }
    }

    // Periodically check buildings under construction
    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        // Only execute on the server end phase
        if (event.phase != TickEvent.Phase.END) {
            return;
        }

        if (buildingsUnderConstruction.isEmpty()) {
            return;
        }

        Set<BlockPos> completedBuildings = new HashSet<>();

        // Iterate over buildings under construction
        for (Map.Entry<BlockPos, BuildingInfo> entry : buildingsUnderConstruction.entrySet()) {
            BlockPos buildingPos = entry.getKey();
            BuildingInfo buildingInfo = entry.getValue();
            IBuilding building = buildingInfo.building;
            IColony colony = buildingInfo.colony;

            // Check if the building is built and has no pending work orders
            if (building.isBuilt() && !building.hasWorkOrder()) {
                int buildingLevel = building.getBuildingLevel();
                LOGGER.info("Building fully constructed: " + building.getBuildingDisplayName() + " Level: " + buildingLevel);

                // Update tax based on building and level
                TaxManager.updateTaxForBuilding(colony, building, buildingLevel);

                // Add to completed buildings set to remove after iteration
                completedBuildings.add(buildingPos);
            }
        }

        // Remove completed buildings from the tracking map
        for (BlockPos buildingPos : completedBuildings) {
            buildingsUnderConstruction.remove(buildingPos);
        }
    }

    // Inner class to store building and colony information
    private static class BuildingInfo {
        public final IBuilding building;
        public final IColony colony;

        public BuildingInfo(IBuilding building, IColony colony) {
            this.building = building;
            this.colony = colony;
        }
    }
}
