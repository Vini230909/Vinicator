package hideunits;

import arc.struct.*;
import mindustry.type.*;

import static arc.Core.*;

/** Reads and writes all Hide-Units settings through {@link arc.Core#settings}. */
public class HideUnitsSettings{
    public static final String
        enabledKey = "hideunits-enabled",
        modeKey = "hideunits-mode",
        rangeKey = "hideunits-range",
        opacityKey = "hideunits-opacity",
        unitsKey = "hideunits-units",
        excludedKey = "hideunits-excluded";

    public static final int defaultRange = 15, defaultOpacity = 0;
    public static final int modePlayer = 0, modeCursor = 1, modeCamera = 2, modeAll = 3;

    /** Names of unit types the player chose NOT to hide; every other type is affected.
     * Storing exclusions means newly added (or modded) unit types are hidden by default. */
    private static final ObjectSet<String> excluded = new ObjectSet<>();

    public static void load(){
        excluded.clear();
        for(String name : settings.getString(excludedKey, "").split(",")){
            if(!name.isEmpty()){
                excluded.add(name);
            }
        }
    }

    public static boolean enabled(){
        return settings.getBool(enabledKey, false);
    }

    public static int mode(){
        return settings.getInt(modeKey, modePlayer);
    }

    /** Hide range in tiles. */
    public static int range(){
        return settings.getInt(rangeKey, defaultRange);
    }

    /** Opacity of hidden units, 0 to 1. */
    public static float opacity(){
        return settings.getInt(opacityKey, defaultOpacity) / 100f;
    }

    public static boolean affects(UnitType type){
        return !excluded.contains(type.name);
    }

    public static void affects(UnitType type, boolean value){
        if(value ? excluded.remove(type.name) : excluded.add(type.name)){
            save();
        }
    }

    private static void save(){
        StringBuilder sb = new StringBuilder();
        for(String name : excluded){
            if(sb.length() > 0) sb.append(',');
            sb.append(name);
        }
        settings.put(excludedKey, sb.toString());
    }
}
