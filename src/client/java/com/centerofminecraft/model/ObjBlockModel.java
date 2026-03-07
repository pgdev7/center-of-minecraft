package com.centerofminecraft.model;

import net.minecraft.client.model.geom.builders.UVPair;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockModelPart;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class ObjBlockModel implements BlockStateModel {
    private final ObjModelPart part;

    public ObjBlockModel(List<ObjParser.Face> faces, Function<String, TextureAtlasSprite> spriteForGroup, TextureAtlasSprite particleSprite) {
        this.part = new ObjModelPart(faces, spriteForGroup, particleSprite);
    }

    @Override
    public void collectParts(RandomSource random, List<BlockModelPart> parts) {
        parts.add(part);
    }

    @Override
    public TextureAtlasSprite particleIcon() {
        return part.particleIcon();
    }

    private static class ObjModelPart implements BlockModelPart {
        private final List<BakedQuad> quads;
        private final TextureAtlasSprite particleSprite;

        ObjModelPart(List<ObjParser.Face> faces, Function<String, TextureAtlasSprite> spriteForGroup, TextureAtlasSprite particleSprite) {
            this.particleSprite = particleSprite;
            this.quads = buildQuads(faces, spriteForGroup);
        }

        private static List<BakedQuad> buildQuads(List<ObjParser.Face> faces, Function<String, TextureAtlasSprite> spriteForGroup) {
            List<BakedQuad> quads = new ArrayList<>();

            for (ObjParser.Face face : faces) {
                TextureAtlasSprite sprite = spriteForGroup.apply(face.group());

                float u0 = sprite.getU(0.0f);
                float v0 = sprite.getV(0.0f);
                float u1 = sprite.getU(0.5f);
                float v1 = sprite.getV(0.5f);

                long packedUV0 = UVPair.pack(u0, v0);
                long packedUV1 = UVPair.pack(u1, v0);
                long packedUV2 = UVPair.pack(u1, v1);
                long packedUV3 = UVPair.pack(u0, v1);

                Vector3fc p0 = new Vector3f(face.x(0), face.y(0), face.z(0));
                Vector3fc p1 = new Vector3f(face.x(1), face.y(1), face.z(1));
                Vector3fc p2 = new Vector3f(face.x(2), face.y(2), face.z(2));
                Vector3fc p3 = face.vertexCount() == 4
                        ? new Vector3f(face.x(3), face.y(3), face.z(3))
                        : p2;

                Vector3f edge1 = new Vector3f(face.x(1) - face.x(0), face.y(1) - face.y(0), face.z(1) - face.z(0));
                Vector3f edge2 = new Vector3f(face.x(2) - face.x(0), face.y(2) - face.y(0), face.z(2) - face.z(0));
                Vector3f normal = edge1.cross(edge2);
                Direction dir = Direction.getApproximateNearest(normal.x, normal.y, normal.z);

                quads.add(new BakedQuad(
                        p0, p1, p2, p3,
                        packedUV0, packedUV1, packedUV2, packedUV3,
                        -1, dir, sprite, true, 0
                ));
            }

            return quads;
        }

        @Override
        public List<BakedQuad> getQuads(Direction direction) {
            if (direction == null) {
                return quads;
            }
            return Collections.emptyList();
        }

        @Override
        public boolean useAmbientOcclusion() {
            return false;
        }

        @Override
        public TextureAtlasSprite particleIcon() {
            return particleSprite;
        }
    }
}
