package hideunits;

import arc.scene.style.*;
import arc.scene.ui.*;
import arc.struct.*;
import arc.util.*;
import mindustry.type.*;
import mindustry.ui.*;
import mindustry.ui.dialogs.*;

import static mindustry.Vars.*;

/** Grid of unit type icons; checked types are hidden when in range. */
public class UnitTypesDialog extends BaseDialog{

    public UnitTypesDialog(){
        super("@hideunits.units.title");
        addCloseButton();
        shown(this::rebuild);
        onResize(this::rebuild);
    }

    void rebuild(){
        cont.clear();

        Seq<UnitType> types = content.units().select(type -> !type.isHidden());

        cont.add("@hideunits.units.info").wrap().width(460f).left().padBottom(8f).row();

        cont.table(buttons -> {
            buttons.defaults().size(150f, 48f).pad(4f);
            buttons.button("@hideunits.units.all", () -> {
                for(UnitType type : types) HideUnitsSettings.affects(type, true);
            });
            buttons.button("@hideunits.units.none", () -> {
                for(UnitType type : types) HideUnitsSettings.affects(type, false);
            });
        }).padBottom(8f).row();

        cont.pane(grid -> {
            int i = 0, cols = 8;
            for(UnitType type : types){
                ImageButton button = grid.button(new TextureRegionDrawable(type.uiIcon), Styles.clearTogglei, 40f, () -> {
                    HideUnitsSettings.affects(type, !HideUnitsSettings.affects(type));
                }).size(56f).pad(2f).tooltip(type.localizedName).get();

                button.getImage().setScaling(Scaling.fit);
                button.update(() -> button.setChecked(HideUnitsSettings.affects(type)));

                if(++i % cols == 0) grid.row();
            }
        }).growY();
    }
}
