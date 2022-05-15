package at.fhv.sysarch.lab3.obj;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.hackoeur.jglm.Vec3;
import com.hackoeur.jglm.Vec4;

public class ObjLoader {

    private final static String COMMENT = "#";
    private final static String VERTEX = "v ";
    private final static String TEXTURE = "vt ";
    private final static String NORMAL = "vn ";
    private final static String FACE = "f ";
    private final static String GROUP = "g";
    private final static String SHADING = "s";

    public static Optional<Model> loadModel(File modelFile) throws IOException {
        try (BufferedReader in = new BufferedReader(new FileReader(modelFile))) {
            List<Vec4> vertices  = new ArrayList<>();
            List<Vec4> normals   = new ArrayList<>();
            List<Face> faces     = new ArrayList<>();

            int lineIndex = 0;

            while (true) {
                String line = in.readLine();
                if (null == line) {
                    break;
                }

                lineIndex++;
    
                if (line.startsWith(COMMENT) | line.trim().isBlank() | line.startsWith(GROUP) | line.startsWith(SHADING)) {
                    continue;

                } else if (line.startsWith(VERTEX)) {
                    String tail       = ObjLoader.removeLineIdentifier(line);
                    Optional<Vec4> ov = ObjLoader.parseVertex(tail, false);

                    ov.ifPresent(v -> vertices.add(v));
                    
                    if (ov.isEmpty()) {
                        System.out.println("Failed parsing vertex in line " + lineIndex + ": " + line);
                        return Optional.empty();
                    }

                } else if (line.startsWith(FACE)) {
                    String tail       = ObjLoader.removeLineIdentifier(line);
                    Optional<Face> of = ObjLoader.parseFace(tail, vertices, normals);

                    of.ifPresent(f -> faces.add(f));

                    if (of.isEmpty()) {
                        System.out.println("Failed parsing face in line " + lineIndex + ": " + line);
                        return Optional.empty();
                    }
                } else if (line.startsWith(NORMAL)) {
                    String tail       = ObjLoader.removeLineIdentifier(line);
                    Optional<Vec4> on = ObjLoader.parseVertex(tail, true);

                    // make sure to normalise
                    on.ifPresent(v -> normals.add(v.getUnitVector()));
                    
                    if (on.isEmpty()) {
                        System.out.println("Failed parsing normal in line " + lineIndex + ": " + line);
                        return Optional.empty();
                    }

                } else if (line.startsWith(TEXTURE)) {
                    // ignoring texture coordinates

                } else {
                    System.out.println("Unknown line identifier in line " + lineIndex + ": " + line);
                    return Optional.empty();
                }
            }

            return Optional.of(new Model(faces));
        }
    }

    private static String removeLineIdentifier(String line) {
        // look for first space
        int idx = line.indexOf(' ');
        if (idx != -1) {
            // also skip the space itself and do a trim
            return line.substring(idx + 1).trim();
        }

        return line;
    }

    private static Optional<Vec4> parseVertex(String line, boolean isNormal) {
        String[] vs = Arrays.stream(line.split(" ")).filter(s -> !s.isBlank()).toArray(String[]::new);
        if (vs.length < 3) {
            System.out.println("ERROR: invalid .obj file, vertex has less than 3 dimensions.");
            return Optional.empty();
        }
        
        try {
            float x = Float.parseFloat(vs[0]);
            float y = Float.parseFloat(vs[1]);
            float z = Float.parseFloat(vs[2]);
            // set w coordinate of a point to 1.0!
            float w = isNormal ? 0.0f : 1.0f;
            
            return Optional.of(new Vec4(x, y, z, w));
        } catch (NumberFormatException e) {
            System.out.println("ERROR: invalid .obj file, couldn't parse vertex dimension.");
            return Optional.empty();
        }
    }

    private static Optional<Face> parseFace(String line, List<Vec4> vertices, List<Vec4> normals) {
        String[] is = Arrays.stream(line.split(" ")).filter(s -> !s.isBlank()).toArray(String[]::new);
        if (is.length != 3) {
            System.out.println("ERROR: invalid .obj file, this parser supports only triangle faces.");
            return Optional.empty();
        }

        try {
            // There are 4 cases https://en.wikipedia.org/wiki/Wavefront_.obj_file: 
            // 1. face vertices only - no texture coords and no normals
            // 2. face vertices and texture coords - no normals
            // 3. face vertices, texture coords and normals
            // 4. face vertices and normals - no texture coords
            
            String[] e1 = is[0].split("/");
            String[] e2 = is[1].split("/");
            String[] e3 = is[2].split("/");
          
            // face vertices are never optional
            int v1Idx = Integer.parseInt(e1[0]);
            int v2Idx = Integer.parseInt(e2[0]);
            int v3Idx = Integer.parseInt(e3[0]);

            // vertex indices start with 1 
            Vec4 v1 = vertices.get(v1Idx - 1);
            Vec4 v2 = vertices.get(v2Idx - 1);
            Vec4 v3 = vertices.get(v3Idx - 1);

            Vec4 n1;
            Vec4 n2;
            Vec4 n3;

            // face definition has normals
            if (e1.length == 3) {
                int n1Idx = Integer.parseInt(e1[2]);
                int n2Idx = Integer.parseInt(e2[2]);
                int n3Idx = Integer.parseInt(e3[2]);

                // normal indices start with 1 
                n1 = vertices.get(n1Idx - 1);
                n2 = vertices.get(n2Idx - 1);
                n3 = vertices.get(n3Idx - 1);
           
            // no normals, compute from cross product and use same for all
            } else {
                // following https://www.khronos.org/opengl/wiki/Calculating_a_Surface_Normal
                Vec3 u = v2.subtract(v1).toVec3();
                Vec3 v = v3.subtract(v1).toVec3();
                // make sure to normalise!
                Vec4 n = u.cross(v).getUnitVector().toDirection();
               
                n1 = n;
                n2 = n;
                n3 = n;
            }

            return Optional.of(new Face(v1, v2, v3, n1, n2, n3));

        } catch (NumberFormatException e) {
            System.out.println("ERROR: invalid .obj file, couldn't parse face indices.");
            return Optional.empty();

        } catch (IndexOutOfBoundsException e) {
            System.out.println("ERROR: invalid .obj file, couldn't find vertex for index.");
            return Optional.empty();
        }
    }
}