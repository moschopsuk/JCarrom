package me.moscrop.Helpers;

import java.io.*;
import java.nio.FloatBuffer;

import javax.vecmath.*;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.*;

public class ModelImporter {
	
	private static Vector3f parseVector3(String line) {
        String[] xyz = line.split(" ");        
        float x = Float.valueOf(xyz[1]);
        float y = Float.valueOf(xyz[2]);
        float z = Float.valueOf(xyz[3]);
        return new Vector3f(x, y, z);
    }

	private static Vector2f parseVector2(String line) {
        String[] uv = line.split(" ");
        float u = Float.valueOf(uv[1]);
        float v = Float.valueOf(uv[2]);
        return new Vector2f(u, v);
    }

	private static Model.Face parseFace(boolean hasUVCoords, boolean hasNormals, String line) {
		String[] faceIndices = line.split(" ");
		
        int[] vertexIndicesArray = {Integer.parseInt(faceIndices[1].split("/")[0]),
                Integer.parseInt(faceIndices[2].split("/")[0]), Integer.parseInt(faceIndices[3].split("/")[0])};
        
        int[] textureCoordinateIndicesArray = {-1, -1, -1};
        int[] normalIndicesArray = {0, 0, 0};
        
        if (hasUVCoords) {
            textureCoordinateIndicesArray[0] = Integer.parseInt(faceIndices[1].split("/")[1]);
            textureCoordinateIndicesArray[1] = Integer.parseInt(faceIndices[2].split("/")[1]);
            textureCoordinateIndicesArray[2] = Integer.parseInt(faceIndices[3].split("/")[1]);
        }
        
        if (hasNormals) {
            normalIndicesArray[0] = Integer.parseInt(faceIndices[1].split("/")[2]);
            normalIndicesArray[1] = Integer.parseInt(faceIndices[2].split("/")[2]);
            normalIndicesArray[2] = Integer.parseInt(faceIndices[3].split("/")[2]);
        }
        
        return new Model.Face(vertexIndicesArray, normalIndicesArray, textureCoordinateIndicesArray);
	}
	
	private static FloatBuffer reserveData(int size) {
        return BufferUtils.createFloatBuffer(size);
    }

    private static float[] as3Floats(Vector3f v) {
        return new float[]{v.x, v.y, v.z};
    }
    
    private static float[] as2Floats(Vector2f v) {
        return new float[]{v.x, v.y};
    }
	
	public static FloatBuffer convertModel(Model model) {
					
		FloatBuffer vertices = reserveData(model.getFaces().size() * 54);
		
		for (Model.Face face : model.getFaces()) {
			
            if (face.hasTextureCoordinates()) {
            	vertices.put(as2Floats(model.getUVCoords().get(face.getTextureCoordinateIndices()[0] - 1)));
            }				
            vertices.put(as3Floats(model.getVertices().get(face.getVertexIndices()[0] - 1)));
            
            
            if (face.hasTextureCoordinates()) {
            	vertices.put(as2Floats(model.getUVCoords().get(face.getTextureCoordinateIndices()[1] - 1)));
            }          
            vertices.put(as3Floats(model.getVertices().get(face.getVertexIndices()[1] - 1)));
            
            
            if (face.hasTextureCoordinates()) {
            	vertices.put(as2Floats(model.getUVCoords().get(face.getTextureCoordinateIndices()[2] - 1)));
            }
            vertices.put(as3Floats(model.getVertices().get(face.getVertexIndices()[2] - 1)));
                       
		}
		
		vertices.rewind();
		
		return vertices;
	}
	
	public static Model ProcessOBJ(InputStream stream) throws IOException {
		
		Reader reader = new InputStreamReader(stream);
		BufferedReader buffer = new BufferedReader(reader);
				
        Model m = new Model();
        String line;
        
		//We now loop over every line
		while ((line = buffer.readLine()) != null) {
			
			//Grab the first few chars
			String prefix = line.split(" ")[0];
			
			switch(prefix) {
				case "v":
					m.getVertices().add(parseVector3(line));
					break;
					
				case "vt":
					m.getUVCoords().add(parseVector2(line));
					break;
					
				case "vn":
					m.getNormals().add(parseVector3(line));
					break;
					
				case "f":
					m.getFaces().add(parseFace(m.hasUVCoords(), m.hasNormals(), line));
					break;
			}
        }
		
		//Close any file handlers
		buffer.close();
		
		return m;
	}
}
