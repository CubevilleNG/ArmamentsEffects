package org.cubeville.effects.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import org.cubeville.commons.commands.*;
import org.cubeville.effects.managers.ParticleEffect;
import org.cubeville.effects.managers.ParticleEffectComponent;
import org.cubeville.effects.managers.ParticleEffectTimelineEntry;
import org.cubeville.effects.managers.modifier.*;
import org.cubeville.effects.managers.sources.coordinate.CircleCoordinateSource;
import org.cubeville.effects.managers.sources.coordinate.ConstantCoordinateSource;
import org.cubeville.effects.managers.sources.coordinate.CoordinateSource;
import org.cubeville.effects.managers.sources.coordinate.RandomCoordinateSource;
import org.cubeville.effects.managers.sources.value.ValueSource;
import org.cubeville.effects.util.WorldEditUtils;

public class ParticleCommandHelper
{
    public static void addCommandParameters(Command command) {
        command.addParameter("particle", true, new CommandParameterEnum(Particle.class));
        command.addParameter("constantsource", true, new CommandParameterListVector());
        command.addParameter("constantsource+", true, new CommandParameterListVector());
        command.addParameter("constantsource-", true, new CommandParameterListInteger(1));
        command.addParameter("presetconstantsource", true, new CommandParameterString());
        command.addParameter("presetconstantsource+", true, new CommandParameterString());
        command.addParameter("constantsourcescale", true, new CommandParameterDouble());
        command.addParameter("constantsourcescalexy", true, new CommandParameterDouble());
        command.addParameter("circlesource", true, new CommandParameterListDouble(3));
        command.addParameter("circlesourcexz", true, new CommandParameterListDouble(3));
        {
            List<CommandParameterType> pl = new ArrayList<>();
            pl.add(new CommandParameterVector());
            pl.add(new CommandParameterVector());
            pl.add(new CommandParameterInteger(1, 100));
            command.addParameter("randomsource", true, new CommandParameterList(pl));
        }
        command.addParameter("duration", true, new CommandParameterInteger());
        command.addParameter("repeat", true, new CommandParameterInteger());
        command.addParameter("repeatoffset", true, new CommandParameterInteger());        
        command.addParameter("spreadx", true, new CommandParameterValueSource());
        command.addParameter("spready", true, new CommandParameterValueSource());
        command.addParameter("spreadz", true, new CommandParameterValueSource());
        command.addParameter("count", true, new CommandParameterValueSource());
        command.addParameter("rotate+", true, new CommandParameterValueSource());
        {
            List<CommandParameterType> pl = new ArrayList<>();
            pl.add(new CommandParameterValueSource());
            pl.add(new CommandParameterBoolean());
            pl.add(new CommandParameterBoolean());
            pl.add(new CommandParameterBoolean());
            command.addParameter("move+", true, new CommandParameterList(pl));
        }
        {
            List<CommandParameterType> pl = new ArrayList<>();
            pl.add(new CommandParameterValueSource());
            pl.add(new CommandParameterString());
            command.addParameter("advrotate+", true, new CommandParameterList(pl));
        }
        command.addParameter("scale+", true, new CommandParameterValueSource());
        {
            List<CommandParameterType> pl = new ArrayList<>();
            pl.add(new CommandParameterValueSource());
            pl.add(new CommandParameterValueSource());
            command.addParameter("scale2d+", true, new CommandParameterList(pl));
        }
        command.addParameter("modifier-", true, new CommandParameterInteger());
        command.addParameter("material", true, new CommandParameterEnum(Material.class));
        command.addParameter("timeline+", true, new CommandParameterListInteger(4));
        command.addParameter("timeline-", true, new CommandParameterInteger());
        command.addParameter("red", true, new CommandParameterValueSource());
        command.addParameter("green", true, new CommandParameterValueSource());
        command.addParameter("blue", true, new CommandParameterValueSource());
        command.addParameter("tored", true, new CommandParameterValueSource());
        command.addParameter("togreen", true, new CommandParameterValueSource());
        command.addParameter("toblue", true, new CommandParameterValueSource());
        command.addParameter("size", true, new CommandParameterValueSource());
        command.addParameter("speed", true, new CommandParameterValueSource());
        command.addParameter("blockcollisioncheck", true, new CommandParameterBoolean());
        command.addParameter("entitycollisioncheck", true, new CommandParameterBoolean());
    }
    
    public static void setEffectValues(ParticleEffect effect, Map<String, Object> parameters) {
        if(parameters.containsKey("duration")) effect.setStepsLoop((int) parameters.get("duration"));
        if(parameters.containsKey("repeat")) effect.setRepeatCount((int) parameters.get("repeat"));
        if(parameters.containsKey("repeatoffset")) effect.setRepeatOffset((int) parameters.get("repeatoffset"));
    }

    public static void setComponentValues(ParticleEffectComponent component, Map<String, Object> parameters, Player player) {
        int numberOfSources = 0;
        if(parameters.containsKey("constantsource")) numberOfSources++;
        if(parameters.containsKey("constantsource+")) numberOfSources++;
        if(parameters.containsKey("constantsource-")) numberOfSources++;
        if(parameters.containsKey("presetconstantsource")) numberOfSources++;
        if(parameters.containsKey("presetconstantsource+")) numberOfSources++;
        if(parameters.containsKey("circlesource")) numberOfSources++;
        if(parameters.containsKey("randomsource")) numberOfSources++;
        if(parameters.containsKey("circlesourcexz")) numberOfSources++;
        if(parameters.containsKey("constantsourcescale")) numberOfSources++;
        if(parameters.containsKey("constantsourcescalexy")) numberOfSources++;
        if(numberOfSources > 1) throw new IllegalArgumentException("Only one coordinate source creation or modification parameter is possible.");

        if(parameters.containsKey("constantsource")) {
            List<Vector> coords = (List<Vector>) parameters.get("constantsource");
            component.setCoordinates(new ConstantCoordinateSource(coords));
        }

        if(parameters.containsKey("constantsource+")) {
            List<Vector> coords = (List<Vector>) parameters.get("constantsource+");
            CoordinateSource source = component.getCoordinates();
            if(source instanceof ConstantCoordinateSource) {
                ConstantCoordinateSource csource = (ConstantCoordinateSource) source;
                csource.addVertices(coords);
            }
            else {
                component.setCoordinates(new ConstantCoordinateSource(coords));
            }
        }

        if(parameters.containsKey("constantsource-")) {
            List<Integer> indices = (List<Integer>) parameters.get("constantsource-");
            CoordinateSource source = component.getCoordinates();
            if(source instanceof ConstantCoordinateSource) {
                ConstantCoordinateSource csource = (ConstantCoordinateSource) source;
                csource.removeVertices(indices);
            }
            else {
                throw new IllegalArgumentException("Can't remove coordinates, source is not constant!");
            }
        }

        if(parameters.containsKey("presetconstantsource")) {
            List<Vector> coords;
            Pattern selPattern = Pattern.compile("selection\\((.*)\\)");
            Matcher selMatcher = selPattern.matcher((String) parameters.get("presetconstantsource"));
            if (parameters.get("presetconstantsource").equals("selection")) {
                coords = WorldEditUtils.getSelectionCoordinates(player);
            } else if (selMatcher.find()) {
                String matArg = selMatcher.group(1);
                coords = WorldEditUtils.getSelectionCoordinates(player, Material.matchMaterial(matArg));
            } else {
                throw new IllegalArgumentException("Invalid preset argument!");
            }
            component.setCoordinates(new ConstantCoordinateSource(coords));
        }

        if(parameters.containsKey("presetconstantsource+")) {
            List<Vector> coords;
            Pattern selPattern = Pattern.compile("selection\\((.*)\\)");
            Matcher selMatcher = selPattern.matcher((String) parameters.get("presetconstantsource+"));
            if (parameters.get("presetconstantsource+").equals("selection")) {
                coords = WorldEditUtils.getSelectionCoordinates(player);
            } else if (selMatcher.find()) {
                String matArg = selMatcher.group(1);
                coords = WorldEditUtils.getSelectionCoordinates(player, Material.matchMaterial(matArg));
            } else {
                throw new IllegalArgumentException("Invalid preset argument!");
            }
            CoordinateSource source = component.getCoordinates();
            if(source instanceof ConstantCoordinateSource) {
                ConstantCoordinateSource csource = (ConstantCoordinateSource) source;
                csource.addVertices(coords);
            }
            else {
                component.setCoordinates(new ConstantCoordinateSource(coords));
            }
        }

        if(parameters.containsKey("circlesource") || parameters.containsKey("circlesourcexz")) {
            List<Double> pars = (List<Double>) parameters.get("circlesource");
            if(pars == null) pars = (List<Double>) parameters.get("circlesourcexz");
            int startAngle = 0;
            int endAngle = 360 - pars.get(1).intValue();
            component.setCoordinates(new CircleCoordinateSource(pars.get(0), pars.get(2), pars.get(1).intValue(), 0, endAngle, parameters.containsKey("circlesourcexz")));
        }

        if(parameters.containsKey("randomsource")) {
            List<Object> pars = (List<Object>) parameters.get("randomsource");
            Vector vec1 = (Vector) pars.get(0);
            Vector vec2 = (Vector) pars.get(1);
            int count = (Integer) pars.get(2);
            component.setCoordinates(new RandomCoordinateSource(count, vec1, vec2));
        }

        if(parameters.containsKey("constantsourcescale") || parameters.containsKey("constantsourcescalexy")) {
            boolean includeZ = parameters.containsKey("constantsourcescale");
            double factor;
            if(includeZ)
                factor = (double) parameters.get("constantsourcescale");
            else
                factor = (double) parameters.get("constantsourcescalexy");
            CoordinateSource source = component.getCoordinates();
            if(source instanceof ConstantCoordinateSource) {
                ConstantCoordinateSource csource = (ConstantCoordinateSource) source;
                csource.scaleVertices(factor, includeZ);
            }
            else {
                throw new IllegalArgumentException("Can't scale coordinates, source is not constant!");
            }
        }
        
        if(parameters.containsKey("particle")) component.setParticle((Particle) parameters.get("particle"));

        if(parameters.containsKey("spreadx")) component.setSpreadX((ValueSource) parameters.get("spreadx"));
        if(parameters.containsKey("spready")) component.setSpreadY((ValueSource) parameters.get("spready"));
        if(parameters.containsKey("spreadz")) component.setSpreadZ((ValueSource) parameters.get("spreadz"));
        if(parameters.containsKey("count")) component.setCount((ValueSource) parameters.get("count"));
        if(parameters.containsKey("material")) component.setMaterial((Material) parameters.get("material"));

        if(parameters.containsKey("rotate+")) {
            CoordinateModifierRotate modifier =
                new CoordinateModifierRotate((ValueSource) parameters.get("rotate+"));
            component.addModifier(modifier);
        }

        if(parameters.containsKey("advrotate+")) {
            List<Object> pars = (List<Object>) parameters.get("advrotate+");
            String axis = (String) pars.get(1);
            if (!axis.equals("xy") && !axis.equals("xz") && !axis.equals("yz")) {
                throw new IllegalArgumentException("Invalid rotation axis! Valid options: xy, xz, yz");
            }
            CoordinateModifierAdvRotate modifier =
                    new CoordinateModifierAdvRotate((ValueSource) pars.get(0), (String) pars.get(1));
            component.addModifier(modifier);
        }

        if(parameters.containsKey("move+")) {
            List<Object> pars = (List<Object>) parameters.get("move+");
            CoordinateModifierMove modifier =
                new CoordinateModifierMove((ValueSource) pars.get(0),
                                           (Boolean) pars.get(1),
                                           (Boolean) pars.get(2),
                                           (Boolean) pars.get(3));
            component.addModifier(modifier);
        }

        if(parameters.containsKey("scale+")) {
            CoordinateModifierScale modifier =
                new CoordinateModifierScale((ValueSource) parameters.get("scale+"));
            component.addModifier(modifier);
        }

        if(parameters.containsKey("scale2d+")) {
            List<Object> pars = (List<Object>) parameters.get("scale2d+");
            CoordinateModifierScale2d modifier =
                new CoordinateModifierScale2d((ValueSource) pars.get(0),
                                              (ValueSource) pars.get(1));
            component.addModifier(modifier);
        }
        
        if(parameters.containsKey("modifier-")) {
            int index = (int) parameters.get("modifier-");
            if(index > 0 && index <= component.getModifiers().size()) {
                component.getModifiers().remove(index - 1);
            }
        }
        
        if(parameters.containsKey("timeline+")) {
            List<Integer> p = (List<Integer>) parameters.get("timeline+");
            int tstart = p.get(0);
            int tcount = p.get(1);
            int trepeat = p.get(2);
            int tlocoffset = p.get(3);
            component.getTimeline().add(new ParticleEffectTimelineEntry(tstart, trepeat, tcount, tlocoffset));
        }

        if(parameters.containsKey("timeline-")) {
            int index = (int) parameters.get("timeline-");
            if(index > 0 && index <= component.getTimeline().size()) {
                component.getTimeline().remove(index - 1);
            }
        }

        if(parameters.containsKey("red"))     component.setColourRed(      (ValueSource) parameters.get("red")    );
        if(parameters.containsKey("green"))   component.setColourGreen(    (ValueSource) parameters.get("green")  );
        if(parameters.containsKey("blue"))    component.setColourBlue(     (ValueSource) parameters.get("blue")   );
        if(parameters.containsKey("tored"))   component.setColourToRed(    (ValueSource) parameters.get("tored")  );
        if(parameters.containsKey("togreen")) component.setColourToGreen(  (ValueSource) parameters.get("togreen"));
        if(parameters.containsKey("toblue"))  component.setColourToBlue(   (ValueSource) parameters.get("toblue") );
        if(parameters.containsKey("size"))    component.setSize(           (ValueSource) parameters.get("size")   );
        if(parameters.containsKey("speed"))   component.setSpeed(          (ValueSource) parameters.get("speed")  );
        if(parameters.containsKey("blockcollisioncheck")) component.setBlockCollisionCheck((Boolean) parameters.get("blockcollisioncheck"));
        if(parameters.containsKey("entitycollisioncheck")) component.setEntityCollisionCheck((Boolean) parameters.get("entitycollisioncheck"));
    }
}
