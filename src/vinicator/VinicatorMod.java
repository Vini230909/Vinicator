package vinicator;

import arc.*;
import mindustry.game.EventType.*;
import mindustry.gen.*;
import mindustry.mod.*;
import mindustry.ui.dialogs.SettingsMenuDialog.*;
import mindustry.ui.dialogs.SettingsMenuDialog.SettingsTable.*;

import static arc.Core.*;
import static mindustry.Vars.*;

public class VinicatorMod extends Mod{
    public static VinicatorRenderer fadeRenderer;
    public static UnitTypesDialog unitsDialog;

    public VinicatorMod(){
        if(headless) return;

        fadeRenderer = new VinicatorRenderer();

        Events.on(ClientLoadEvent.class, e -> {
            VinicatorSettings.load();
            unitsDialog = new UnitTypesDialog();

            ui.settings.addCategory(bundle.get("vinicator.category"), Icon.eyeSmall, table -> {
                table.checkPref(VinicatorSettings.enabledKey, false);
                table.sliderPref(VinicatorSettings.modeKey, VinicatorSettings.modePlayer, 0, 2, 1, i -> bundle.get("vinicator.mode." + i));
                table.sliderPref(VinicatorSettings.rangeKey, VinicatorSettings.defaultRange, 1, 100, 1, i -> bundle.format("vinicator.tiles", i));
                table.sliderPref(VinicatorSettings.opacityKey, VinicatorSettings.defaultOpacity, 0, 100, 5, i -> i + "%");
                table.pref(new Setting(VinicatorSettings.unitsKey){
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
