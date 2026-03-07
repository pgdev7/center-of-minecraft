package com.centerofminecraft.model;

import com.centerofminecraft.Centerofminecraft;
import com.centerofminecraft.block.ModBlocks;
import net.fabricmc.fabric.api.client.model.loading.v1.BlockStateResolver;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ResolvableModel;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class RobotModelPlugin implements ModelLoadingPlugin {

    private static final String TEX_ALUMINUM = "minecraft:block/iron_block";
    private static final String TEX_DARK = "minecraft:block/deepslate";
    private static final String TEX_RED = "minecraft:block/red_concrete";
    private static final String TEX_BLUE = "minecraft:block/blue_concrete";
    private static final String TEX_GREEN = "minecraft:block/green_concrete";
    private static final String TEX_ORANGE = "minecraft:block/orange_concrete";
    private static final String TEX_YELLOW = "minecraft:block/yellow_concrete";
    private static final String TEX_BLACK = "minecraft:block/black_concrete";
    private static final String TEX_WHITE = "minecraft:block/white_concrete";
    private static final String TEX_GRAY = "minecraft:block/gray_concrete";

    @Override
    public void initialize(Context pluginContext) {
        pluginContext.registerBlockStateResolver(ModBlocks.SOS_ROBOT, new RobotBlockStateResolver(
                "models/block/sos_robot.obj"
        ));
        pluginContext.registerBlockStateResolver(ModBlocks.RB26_ROBOT, new RobotBlockStateResolver(
                "models/block/rb26_robot.obj"
        ));
    }

    private static String categoryColor(String group) {
        if (group == null || group.isEmpty()) return TEX_ALUMINUM;
        String g = group.toLowerCase();

        // Bumpers
        if (g.contains("bumper")) return TEX_RED;
        // Motors
        if (g.contains("kraken") || g.contains("neo") || g.contains("motor")) return TEX_BLACK;
        // Wheels / rollers
        if (g.contains("wheel") || g.contains("omni") || g.contains("ion") || g.contains("trick plate")) return TEX_DARK;
        // Belts
        if (g.contains("belt")) return TEX_BLACK;
        // Bearings / collars
        if (g.contains("bearing") || g.contains("collar") || g.contains("fr8zz")) return TEX_GRAY;
        // Gearboxes
        if (g.contains("maxplanetary") || g.contains("slice") || g.contains("coupler")) return TEX_GRAY;
        // Electronics
        if (g.contains("radio") || g.contains("pdh") || g.contains("breaker") || g.contains("rsl") || g.contains("switch")) return TEX_GREEN;
        // Battery
        if (g.contains("battery")) return TEX_YELLOW;
        // Stress ball (game piece)
        if (g.contains("stress ball")) return TEX_ORANGE;
        // Intake parts
        if (g.contains("intake") || g.contains("spiderman") || g.contains("pivot") || g.contains("funnel")) return TEX_GREEN;
        if (g.startsWith("p03")) return TEX_GREEN;
        // Conveyor / slider / hopper
        if (g.contains("slider") || g.contains("conveyor") || g.contains("hopper")) return TEX_BLUE;
        if (g.startsWith("p04")) return TEX_BLUE;
        // Shooter / flywheel / backspin
        if (g.contains("shooter") || g.contains("flywheel") || g.contains("backspin")) return TEX_RED;
        if (g.startsWith("p05")) return TEX_RED;
        // Indexer
        if (g.contains("indexer")) return TEX_ORANGE;
        // Climber / hook / winch
        if (g.contains("climber") || g.contains("hook") || g.contains("winch")) return TEX_YELLOW;
        if (g.startsWith("p06")) return TEX_YELLOW;
        // Gears / pulleys
        if (g.contains("gear") || g.contains("pulley")) return TEX_DARK;
        // Rollers (sushi)
        if (g.contains("roller") || g.contains("sushi")) return TEX_GREEN;
        // Hardware (screws, nuts, shoulders)
        if (g.contains("shcs") || g.contains("bhcs") || g.contains("locknut") || g.contains("shoulder")) return TEX_DARK;
        if (g.contains("217-") || g.contains("planetary")) return TEX_DARK;
        // Spacers / standoffs
        if (g.contains("spacer") || g.contains("standoff")) return TEX_WHITE;
        // Poly / plastic parts
        if (g.contains("poly")) return TEX_WHITE;
        // Frame tubes
        if (g.contains("tube") || g.contains("spine") || g.contains("truss") || g.contains("cross") || g.contains("post")) return TEX_ALUMINUM;
        // Plates / brackets / gussets
        if (g.contains("plate") || g.contains("bracket") || g.contains("gusset") || g.contains("bellypan")) return TEX_ALUMINUM;
        // Versahub
        if (g.contains("versahub")) return TEX_GRAY;
        return TEX_ALUMINUM;
    }

    private static class RobotBlockStateResolver implements BlockStateResolver {
        private final String objPath;

        RobotBlockStateResolver(String objPath) {
            this.objPath = objPath;
        }

        @Override
        public void resolveBlockStates(Context context) {
            Block block = context.block();
            RobotUnbakedModel unbaked = new RobotUnbakedModel(objPath);
            for (BlockState state : block.getStateDefinition().getPossibleStates()) {
                context.setModel(state, unbaked.asRoot());
            }
        }
    }

    private static class RobotUnbakedModel implements BlockStateModel.Unbaked {
        private final String objPath;

        RobotUnbakedModel(String objPath) {
            this.objPath = objPath;
        }

        @Override
        public void resolveDependencies(ResolvableModel.Resolver resolver) {
        }

        @Override
        public BlockStateModel bake(ModelBaker baker) {
            Map<String, TextureAtlasSprite> spriteCache = new HashMap<>();

            Function<String, TextureAtlasSprite> spriteForGroup = group -> {
                String texId = categoryColor(group);
                return spriteCache.computeIfAbsent(texId, id -> {
                    Identifier tex = Identifier.fromNamespaceAndPath(
                            id.split(":")[0], id.split(":")[1]);
                    Material material = new Material(TextureAtlas.LOCATION_BLOCKS, tex);
                    return baker.sprites().get(material, () -> "robot_" + objPath);
                });
            };

            String resourcePath = "assets/" + Centerofminecraft.MOD_ID + "/" + objPath;
            InputStream stream = RobotModelPlugin.class.getClassLoader().getResourceAsStream(resourcePath);
            if (stream == null) {
                Centerofminecraft.LOGGER.error("Failed to load OBJ: {}", resourcePath);
                TextureAtlasSprite fallback = spriteForGroup.apply("default");
                return new ObjBlockModel(List.of(), s -> fallback, fallback);
            }

            List<ObjParser.Face> faces = ObjParser.parse(stream);
            Centerofminecraft.LOGGER.info("Loaded {} faces from {}", faces.size(), objPath);

            TextureAtlasSprite particleSprite = spriteForGroup.apply("default");
            return new ObjBlockModel(faces, spriteForGroup, particleSprite);
        }
    }
}
