package com.centerofminecraft.model;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ObjParser {
    public record Face(String group, float[] positions) {
        public int vertexCount() {
            return positions.length / 3;
        }

        public float x(int i) { return positions[i * 3]; }
        public float y(int i) { return positions[i * 3 + 1]; }
        public float z(int i) { return positions[i * 3 + 2]; }
    }

    public static List<Face> parse(InputStream stream) {
        List<float[]> vertices = new ArrayList<>();
        List<Face> faces = new ArrayList<>();
        String currentGroup = "default";

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("v ")) {
                    String[] parts = line.split("\\s+");
                    vertices.add(new float[]{
                            Float.parseFloat(parts[1]),
                            Float.parseFloat(parts[2]),
                            Float.parseFloat(parts[3])
                    });
                } else if (line.startsWith("g ")) {
                    currentGroup = line.substring(2).trim();
                } else if (line.startsWith("f ")) {
                    String[] parts = line.split("\\s+");
                    int[] indices = new int[parts.length - 1];
                    for (int i = 1; i < parts.length; i++) {
                        indices[i - 1] = Integer.parseInt(parts[i].split("/")[0]) - 1;
                    }

                    if (indices.length == 3) {
                        float[] v0 = vertices.get(indices[0]);
                        float[] v1 = vertices.get(indices[1]);
                        float[] v2 = vertices.get(indices[2]);
                        faces.add(new Face(currentGroup, new float[]{
                                v0[0], v0[1], v0[2],
                                v1[0], v1[1], v1[2],
                                v2[0], v2[1], v2[2]
                        }));
                    } else if (indices.length == 4) {
                        float[] v0 = vertices.get(indices[0]);
                        float[] v1 = vertices.get(indices[1]);
                        float[] v2 = vertices.get(indices[2]);
                        float[] v3 = vertices.get(indices[3]);
                        faces.add(new Face(currentGroup, new float[]{
                                v0[0], v0[1], v0[2],
                                v1[0], v1[1], v1[2],
                                v2[0], v2[1], v2[2],
                                v3[0], v3[1], v3[2]
                        }));
                    } else {
                        float[] v0 = vertices.get(indices[0]);
                        for (int i = 1; i < indices.length - 1; i++) {
                            float[] v1 = vertices.get(indices[i]);
                            float[] v2 = vertices.get(indices[i + 1]);
                            faces.add(new Face(currentGroup, new float[]{
                                    v0[0], v0[1], v0[2],
                                    v1[0], v1[1], v1[2],
                                    v2[0], v2[1], v2[2]
                            }));
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse OBJ", e);
        }

        return faces;
    }
}
