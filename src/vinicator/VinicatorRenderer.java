package vinicator;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.graphics.gl.*;
import arc.math.geom.*;
import arc.struct.*;
import mindustry.game.EventType.*;
import mindustry.gen.*;
import mindustry.graphics.*;

import static arc.Core.*;
import static mindustry.Vars.*;

/**
 * Hides units near the configured anchor point.
 *
 * Each frame, affected units are temporarily removed from {@link Groups#draw} before the
 * vanilla renderer iterates it, and put back right after rendering ({@link Trigger#postDraw}).
 * If the opacity setting is above zero, the removed units are drawn manually into a
 * framebuffer which is then blitted over the screen with the chosen alpha — the same
 * capture-and-blit pattern the vanilla renderer uses for animated shields.
 */
public class VinicatorRenderer{
    /** Z layer the faded units are drawn at: above flying units, below the overlay UI. */
    public static final float fadeLayer = Layer.flyingUnit + 1f;

    /** Units removed from the draw group this frame. */
    private final Seq<Unit> faded = new Seq<>();
    private final Vec2 anchor = new Vec2();

    private FrameBuffer buffer;
    private AlphaShader shader;

    public VinicatorRenderer(){
        Events.run(Trigger.draw, this::draw);
        Events.run(Trigger.postDraw, this::restore);
    }

    void draw(){
        //if the previous frame ended abnormally and postDraw never ran, put units back now
        restore();

        if(!VinicatorSettings.enabled() || !state.isGame() || !findAnchor(anchor)) return;

        float range = VinicatorSettings.range() * tilesize;
        Unit ownUnit = player.unit();

        Groups.unit.each(unit ->
            unit != ownUnit
            && unit.within(anchor.x, anchor.y, range)
            && VinicatorSettings.affects(unit.type),
        faded::add);

        if(faded.isEmpty()) return;

        for(Unit unit : faded){
            Groups.draw.remove(unit);
        }

        float opacity = VinicatorSettings.opacity();
        if(opacity <= 0.001f) return;

        if(buffer == null){
            buffer = new FrameBuffer();
            shader = new AlphaShader();
        }
        buffer.resize(graphics.getWidth(), graphics.getHeight());

        float prevZ = Draw.z();
        Draw.draw(fadeLayer, () -> {
            buffer.begin(Color.clear);
            Draw.reset();
            for(Unit unit : faded){
                unit.draw();
            }
            Draw.reset();
            buffer.end();

            shader.alpha = opacity;
            buffer.blit(shader);
        });
        Draw.z(prevZ);
    }

    void restore(){
        for(Unit unit : faded){
            //skip units that were removed from the world in the (abnormal) case where
            //restoration happens a frame late
            if(unit.isAdded()){
                Groups.draw.add(unit);
            }
        }
        faded.clear();
    }

    /** @return whether an anchor point exists for the current mode; result is stored in {@code out}. */
    private boolean findAnchor(Vec2 out){
        switch(VinicatorSettings.mode()){
            case VinicatorSettings.modeCursor -> out.set(input.mouseWorld());
            case VinicatorSettings.modeCamera -> out.set(camera.position);
            default -> {
                Unit unit = player.unit();
                if(unit == null || unit.dead || !unit.isAdded()) return false;
                out.set(unit.x, unit.y);
            }
        }
        return true;
    }

    /** Draws a fullscreen buffer multiplied by a constant alpha. */
    static class AlphaShader extends Shader{
        public float alpha = 1f;

        AlphaShader(){
            super("""
            attribute vec4 a_position;
            attribute vec2 a_texCoord0;

            varying vec2 v_texCoords;

            void main(){
                v_texCoords = a_texCoord0;
                gl_Position = a_position;
            }
            """, """
            uniform sampler2D u_texture;
            uniform float u_alpha;

            varying vec2 v_texCoords;

            void main(){
                vec4 color = texture2D(u_texture, v_texCoords);
                gl_FragColor = vec4(color.rgb, color.a * u_alpha);
            }
            """);
        }

        @Override
        public void apply(){
            setUniformf("u_alpha", alpha);
        }
    }
}
