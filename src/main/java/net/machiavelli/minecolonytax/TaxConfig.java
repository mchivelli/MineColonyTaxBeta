package net.machiavelli.minecolonytax;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLPaths;

import java.util.HashMap;
import java.util.Map;

@Mod.EventBusSubscriber
public class TaxConfig {

    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static ForgeConfigSpec CONFIG;

    // Maps for storing building taxes and upgrade taxes
    public static final Map<String, ForgeConfigSpec.DoubleValue> BUILDING_TAXES = new HashMap<>();
    public static final Map<String, ForgeConfigSpec.DoubleValue> UPGRADE_TAXES = new HashMap<>();

    // Map to link full building class names to short config names
    private static final Map<String, String> CLASS_NAME_TO_SHORT_NAME = new HashMap<>();

    // Define the tax interval in minutes
    public static final ForgeConfigSpec.IntValue TAX_INTERVAL_MINUTES;

    static {
        BUILDER.push("Building Taxes");

        // Add base and upgrade taxes for all buildings
        BUILDING_TAXES.put("archery", BUILDER.comment("Base tax for Archery")
                .defineInRange("archery", 12.0, 0.0, Double.MAX_VALUE));
        UPGRADE_TAXES.put("archery", BUILDER.comment("Tax increase per level for Archery")
                .defineInRange("archeryUpgrade", 6.0, 0.0, Double.MAX_VALUE));

        BUILDING_TAXES.put("bakery", BUILDER.comment("Base tax for Bakery")
                .defineInRange("bakery", 10.0, 0.0, Double.MAX_VALUE));
        UPGRADE_TAXES.put("bakery", BUILDER.comment("Tax increase per level for Bakery")
                .defineInRange("bakeryUpgrade", 4.0, 0.0, Double.MAX_VALUE));

        BUILDING_TAXES.put("barracks", BUILDER.comment("Base tax for Barracks")
                .defineInRange("barracks", 15.0, 0.0, Double.MAX_VALUE));
        UPGRADE_TAXES.put("barracks", BUILDER.comment("Tax increase per level for Barracks")
                .defineInRange("barracksUpgrade", 7.0, 0.0, Double.MAX_VALUE));

        BUILDING_TAXES.put("barrackstower", BUILDER.comment("Base tax for Barracks Tower")
                .defineInRange("barrackstower", 14.0, 0.0, Double.MAX_VALUE));
        UPGRADE_TAXES.put("barrackstower", BUILDER.comment("Tax increase per level for Barracks Tower")
                .defineInRange("barrackstowerUpgrade", 6.0, 0.0, Double.MAX_VALUE));

        BUILDING_TAXES.put("blacksmith", BUILDER.comment("Base tax for Blacksmith")
                .defineInRange("blacksmith", 18.0, 0.0, Double.MAX_VALUE));
        UPGRADE_TAXES.put("blacksmith", BUILDER.comment("Tax increase per level for Blacksmith")
                .defineInRange("blacksmithUpgrade", 8.0, 0.0, Double.MAX_VALUE));

        BUILDING_TAXES.put("builder", BUILDER.comment("Base tax for Builder")
                .defineInRange("builder", 8.0, 0.0, Double.MAX_VALUE));
        UPGRADE_TAXES.put("builder", BUILDER.comment("Tax increase per level for Builder")
                .defineInRange("builderUpgrade", 4.0, 0.0, Double.MAX_VALUE));

        BUILDING_TAXES.put("chickenherder", BUILDER.comment("Base tax for Chicken Herder")
                .defineInRange("chickenherder", 9.0, 0.0, Double.MAX_VALUE));
        UPGRADE_TAXES.put("chickenherder", BUILDER.comment("Tax increase per level for Chicken Herder")
                .defineInRange("chickenherderUpgrade", 3.0, 0.0, Double.MAX_VALUE));

        BUILDING_TAXES.put("combatacademy", BUILDER.comment("Base tax for Combat Academy")
                .defineInRange("combatacademy", 14.0, 0.0, Double.MAX_VALUE));
        UPGRADE_TAXES.put("combatacademy", BUILDER.comment("Tax increase per level for Combat Academy")
                .defineInRange("combatacademyUpgrade", 6.0, 0.0, Double.MAX_VALUE));

        BUILDING_TAXES.put("composter", BUILDER.comment("Base tax for Composter")
                .defineInRange("composter", 6.0, 0.0, Double.MAX_VALUE));
        UPGRADE_TAXES.put("composter", BUILDER.comment("Tax increase per level for Composter")
                .defineInRange("composterUpgrade", 2.0, 0.0, Double.MAX_VALUE));

        BUILDING_TAXES.put("cook", BUILDER.comment("Base tax for Cook")
                .defineInRange("cook", 12.0, 0.0, Double.MAX_VALUE));
        UPGRADE_TAXES.put("cook", BUILDER.comment("Tax increase per level for Cook")
                .defineInRange("cookUpgrade", 5.0, 0.0, Double.MAX_VALUE));

        BUILDING_TAXES.put("cowboy", BUILDER.comment("Base tax for Cowboy")
                .defineInRange("cowboy", 9.0, 0.0, Double.MAX_VALUE));
        UPGRADE_TAXES.put("cowboy", BUILDER.comment("Tax increase per level for Cowboy")
                .defineInRange("cowboyUpgrade", 4.0, 0.0, Double.MAX_VALUE));

        BUILDING_TAXES.put("crusher", BUILDER.comment("Base tax for Crusher")
                .defineInRange("crusher", 13.0, 0.0, Double.MAX_VALUE));
        UPGRADE_TAXES.put("crusher", BUILDER.comment("Tax increase per level for Crusher")
                .defineInRange("crusherUpgrade", 6.0, 0.0, Double.MAX_VALUE));

        BUILDING_TAXES.put("deliveryman", BUILDER.comment("Base tax for Deliveryman")
                .defineInRange("deliveryman", 12.0, 0.0, Double.MAX_VALUE));
        UPGRADE_TAXES.put("deliveryman", BUILDER.comment("Tax increase per level for Deliveryman")
                .defineInRange("deliverymanUpgrade", 5.0, 0.0, Double.MAX_VALUE));

        BUILDING_TAXES.put("farmer", BUILDER.comment("Base tax for Farmer")
                .defineInRange("farmer", 11.0, 0.0, Double.MAX_VALUE));
        UPGRADE_TAXES.put("farmer", BUILDER.comment("Tax increase per level for Farmer")
                .defineInRange("farmerUpgrade", 5.0, 0.0, Double.MAX_VALUE));

        BUILDING_TAXES.put("fisherman", BUILDER.comment("Base tax for Fisherman")
                .defineInRange("fisherman", 10.0, 0.0, Double.MAX_VALUE));
        UPGRADE_TAXES.put("fisherman", BUILDER.comment("Tax increase per level for Fisherman")
                .defineInRange("fishermanUpgrade", 4.0, 0.0, Double.MAX_VALUE));

        BUILDING_TAXES.put("guardtower", BUILDER.comment("Base tax for Guard Tower")
                .defineInRange("guardtower", 10.0, 0.0, Double.MAX_VALUE));
        UPGRADE_TAXES.put("guardtower", BUILDER.comment("Tax increase per level for Guard Tower")
                .defineInRange("guardtowerUpgrade", 5.0, 0.0, Double.MAX_VALUE));

        BUILDING_TAXES.put("home", BUILDER.comment("Base tax for Residence")
                .defineInRange("home", 5.0, 0.0, Double.MAX_VALUE));
        UPGRADE_TAXES.put("home", BUILDER.comment("Tax increase per level for Residence")
                .defineInRange("homeUpgrade", 2.0, 0.0, Double.MAX_VALUE));

        BUILDING_TAXES.put("library", BUILDER.comment("Base tax for Library")
                .defineInRange("library", 13.0, 0.0, Double.MAX_VALUE));
        UPGRADE_TAXES.put("library", BUILDER.comment("Tax increase per level for Library")
                .defineInRange("libraryUpgrade", 6.0, 0.0, Double.MAX_VALUE));

        BUILDING_TAXES.put("university", BUILDER.comment("Base tax for University")
                .defineInRange("university", 20.0, 0.0, Double.MAX_VALUE));
        UPGRADE_TAXES.put("university", BUILDER.comment("Tax increase per level for University")
                .defineInRange("universityUpgrade", 10.0, 0.0, Double.MAX_VALUE));

        // Additional buildings
        BUILDING_TAXES.put("warehouse", BUILDER.comment("Base tax for Warehouse")
                .defineInRange("warehouse", 10.0, 0.0, Double.MAX_VALUE));
        UPGRADE_TAXES.put("warehouse", BUILDER.comment("Tax increase per level for Warehouse")
                .defineInRange("warehouseUpgrade", 4.0, 0.0, Double.MAX_VALUE));

        BUILDING_TAXES.put("tavern", BUILDER.comment("Base tax for Tavern")
                .defineInRange("tavern", 14.0, 0.0, Double.MAX_VALUE));
        UPGRADE_TAXES.put("tavern", BUILDER.comment("Tax increase per level for Tavern")
                .defineInRange("tavernUpgrade", 6.0, 0.0, Double.MAX_VALUE));

        BUILDING_TAXES.put("miner", BUILDER.comment("Base tax for Miner")
                .defineInRange("miner", 11.0, 0.0, Double.MAX_VALUE));
        UPGRADE_TAXES.put("miner", BUILDER.comment("Tax increase per level for Miner")
                .defineInRange("minerUpgrade", 5.0, 0.0, Double.MAX_VALUE));

        BUILDER.pop();

        // Add mapping for full class names to short names used in config
        CLASS_NAME_TO_SHORT_NAME.put("com.minecolonies.building.barracks", "barracks");
        CLASS_NAME_TO_SHORT_NAME.put("com.minecolonies.building.guardtower", "guardtower");
        CLASS_NAME_TO_SHORT_NAME.put("com.minecolonies.building.archery", "archery");
        CLASS_NAME_TO_SHORT_NAME.put("com.minecolonies.building.bakery", "bakery");
        CLASS_NAME_TO_SHORT_NAME.put("com.minecolonies.building.blacksmith", "blacksmith");

        CLASS_NAME_TO_SHORT_NAME.put("com.minecolonies.building.builder", "builder");
        CLASS_NAME_TO_SHORT_NAME.put("com.minecolonies.building.chickenherder", "chickenherder");
        CLASS_NAME_TO_SHORT_NAME.put("com.minecolonies.building.combatacademy", "combatacademy");
        CLASS_NAME_TO_SHORT_NAME.put("com.minecolonies.building.composter", "composter");
        CLASS_NAME_TO_SHORT_NAME.put("com.minecolonies.building.cook", "cook");

        CLASS_NAME_TO_SHORT_NAME.put("com.minecolonies.building.cowboy", "cowboy");
        CLASS_NAME_TO_SHORT_NAME.put("com.minecolonies.building.crusher", "crusher");
        CLASS_NAME_TO_SHORT_NAME.put("com.minecolonies.building.deliveryman", "deliveryman");
        CLASS_NAME_TO_SHORT_NAME.put("com.minecolonies.building.farmer", "farmer");
        CLASS_NAME_TO_SHORT_NAME.put("com.minecolonies.building.fisherman", "fisherman");

        CLASS_NAME_TO_SHORT_NAME.put("com.minecolonies.building.home", "residence");
        CLASS_NAME_TO_SHORT_NAME.put("com.minecolonies.building.library", "library");
        CLASS_NAME_TO_SHORT_NAME.put("com.minecolonies.building.university", "university");
        CLASS_NAME_TO_SHORT_NAME.put("com.minecolonies.building.warehouse", "warehouse");
        CLASS_NAME_TO_SHORT_NAME.put("com.minecolonies.building.tavern", "tavern");

        CLASS_NAME_TO_SHORT_NAME.put("com.minecolonies.building.miner", "miner");


        // Add other mappings as needed for all buildings

        // Define general settings
        TAX_INTERVAL_MINUTES = BUILDER.comment("Tax generation interval in minutes")
                .defineInRange("TaxIntervalMinutes", 60, 1, 1440); // Default 60 minutes, min 1, max 1440 (1 day)

        CONFIG = BUILDER.build();
    }

    /**
     * Loads the configuration file.
     */
    public static void loadConfig(ForgeConfigSpec config, String path) {
        final CommentedFileConfig file = CommentedFileConfig.builder(FMLPaths.CONFIGDIR.get().resolve(path)).sync().autosave().build();
        file.load();
        config.setConfig(file);
    }

    /**
     * Retrieves the base tax for a given building type using its full class name.
     *
     * @param fullClassName The full class name of the building type.
     * @return The base tax amount.
     */
    public static double getBaseTaxForBuilding(String fullClassName) {
        String shortName = getShortBuildingName(fullClassName);
        ForgeConfigSpec.DoubleValue taxValue = BUILDING_TAXES.get(shortName);
        return (taxValue != null) ? taxValue.get() : 0.0;
    }

    /**
     * Retrieves the upgrade tax for a given building type using its full class name.
     *
     * @param fullClassName The full class name of the building type.
     * @return The upgrade tax amount per level.
     */
    public static double getUpgradeTaxForBuilding(String fullClassName) {
        String shortName = getShortBuildingName(fullClassName);
        ForgeConfigSpec.DoubleValue upgradeValue = UPGRADE_TAXES.get(shortName);
        return (upgradeValue != null) ? upgradeValue.get() : 0.0;
    }

    /**
     * Retrieves the tax interval in minutes.
     *
     * @return The tax interval in minutes.
     */
    public static int getTaxIntervalInMinutes() {
        return TAX_INTERVAL_MINUTES.get();
    }

    /**
     * Helper method to convert full class name to short config name.
     *
     * @param fullClassName Full class name of the building (e.g., com.minecolonies.building.barracks).
     * @return The corresponding short name (e.g., barracks).
     */
    private static String getShortBuildingName(String fullClassName) {
        return CLASS_NAME_TO_SHORT_NAME.getOrDefault(fullClassName, "unknown");
    }
}
