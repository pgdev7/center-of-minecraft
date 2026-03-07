#!/usr/bin/env python3
"""Simplify OBJ files using vertex clustering for Minecraft block models.
Preserves group names for per-part coloring."""

def simplify_obj(input_path, output_path, grid_resolution=32):
    vertices = []
    faces = []  # (group_name, [vertex_indices])
    current_group = "default"

    with open(input_path) as f:
        for line in f:
            if line.startswith('v '):
                parts = line.split()
                vertices.append((float(parts[1]), float(parts[2]), float(parts[3])))
            elif line.startswith('g '):
                current_group = line.strip()[2:].strip()
            elif line.startswith('f '):
                parts = line.split()[1:]
                face_verts = []
                for p in parts:
                    vi = int(p.split('/')[0])
                    face_verts.append(vi)
                faces.append((current_group, face_verts))

    if not vertices:
        print("No vertices found!")
        return

    # OBJ axes: X=left/right, Y=front/back, Z=up
    # Minecraft axes: X=left/right, Y=up, Z=front/back
    # So swap: OBJ Z -> MC Y, OBJ Y -> MC Z
    xs = [v[0] for v in vertices]
    ys = [v[1] for v in vertices]  # OBJ Y = front/back -> MC Z
    zs = [v[2] for v in vertices]  # OBJ Z = up -> MC Y
    xmin, xmax = min(xs), max(xs)
    ymin, ymax = min(ys), max(ys)
    zmin, zmax = min(zs), max(zs)

    span = max(xmax - xmin, ymax - ymin, zmax - zmin)
    if span == 0:
        span = 1.0

    scale = 0.875 / span
    cx = (xmin + xmax) / 2
    cy = (ymin + ymax) / 2

    def normalize(v):
        obj_x, obj_y, obj_z = v
        return (
            (obj_x - cx) * scale + 0.5,       # MC X (centered)
            (obj_z - zmin) * scale + 0.0625,   # MC Y (up), bottom-aligned
            (obj_y - cy) * scale + 0.5         # MC Z (centered)
        )

    grid = grid_resolution
    def quantize(v):
        nv = normalize(v)
        return (
            round(nv[0] * grid) / grid,
            round(nv[1] * grid) / grid,
            round(nv[2] * grid) / grid
        )

    quantized = [quantize(v) for v in vertices]

    unique_verts = {}
    old_to_new = {}
    for i, qv in enumerate(quantized):
        if qv not in unique_verts:
            unique_verts[qv] = len(unique_verts) + 1
        old_to_new[i + 1] = unique_verts[qv]

    # Rebuild faces, grouped
    new_faces = []  # (group, [indices])
    seen_faces = set()
    for group, face in faces:
        new_face = []
        seen_in_face = set()
        for vi in face:
            ni = old_to_new.get(vi, 1)
            if ni not in seen_in_face:
                new_face.append(ni)
                seen_in_face.add(ni)
        if len(new_face) >= 3:
            if len(new_face) <= 4:
                face_key = tuple(sorted(new_face))
                if face_key not in seen_faces:
                    seen_faces.add(face_key)
                    new_faces.append((group, new_face))
            else:
                for i in range(1, len(new_face) - 1):
                    tri = [new_face[0], new_face[i], new_face[i+1]]
                    face_key = tuple(sorted(tri))
                    if face_key not in seen_faces:
                        seen_faces.add(face_key)
                        new_faces.append((group, tri))

    sorted_verts = sorted(unique_verts.items(), key=lambda x: x[1])

    print(f"  Original: {len(vertices)} vertices, {len(faces)} faces")
    print(f"  Simplified: {len(sorted_verts)} vertices, {len(new_faces)} faces")

    # Write output with groups
    with open(output_path, 'w') as f:
        f.write(f"# Simplified OBJ for Minecraft (grid={grid})\n")
        for (x, y, z), idx in sorted_verts:
            f.write(f"v {x:.6f} {y:.6f} {z:.6f}\n")
        f.write("\n")
        current = None
        for group, face in new_faces:
            if group != current:
                f.write(f"g {group}\n")
                current = group
            f.write("f " + " ".join(str(v) for v in face) + "\n")

if __name__ == "__main__":
    models = [
        ("A0000-467-SOS-Robot.obj", "src/client/resources/assets/centerofminecraft/models/block/sos_robot.obj", 48),
        ("A0000-467-RB26-Robot.obj", "src/client/resources/assets/centerofminecraft/models/block/rb26_robot.obj", 24),
    ]
    for inp, out, res in models:
        print(f"Simplifying {inp} -> {out}")
        simplify_obj(inp, out, res)
        print()
