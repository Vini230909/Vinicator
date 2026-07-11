package hideunits;

import arc.*;
import mindustry.game.EventType.*;
import mindustry.gen.*;
import mindustry.mod.*;
import mindustry.ui.dialogs.SettingsMenuDialog.*;
import mindustry.ui.dialogs.SettingsMenuDialog.SettingsTable.*;

import static arc.Core.*;
import static mindustry.Vars.*;

public class HideUnitsMod extends Mod{
    public static HideUnitsRenderer fadeRenderer;
    public static UnitTypesDialog unitsDialog;

    public HideUnitsMod(){
        if(headless) return;

        fadeRenderer = new HideUnitsRenderer();

        Events.on(ClientLoadEvent.class, e -> {
            HideUnitsSettings.load();
            unitsDialog = new UnitTypesDialog();

            ui.settings.addCategory(bundle.get("hideunits.category"), Icon.eyeSmall, table -> {
                table.checkPref(HideUnitsSettings.enabledKey, false);
                table.sliderPref(HideUnitsSettings.modeKey, HideUnitsSettings.modePlayer, 0, 3, 1, i -> bundle.get("hideunits.mode." + i));
                table.sliderPref(HideUnitsSettings.rangeKey, HideUnitsSettings.defaultRange, 1, 100, 1, i -> bundle.format("hideunits.tiles", i));
                table.sliderPref(HideUnitsSettings.opacityKey, HideUnitsSettings.defaultOpacity, 0, 100, 5, i -> i + "%");
                table.pref(new Setting(HideUnitsSettings.unitsKey){
                    @Override
                    public void add(SettingsTable table){
                        addDesc(table.button(title, () -> unitsDialog.show()).margin(14).width(240f).pad(6).left().get());
                        table.row();
                    }
                });
            });
        });
    }
}
