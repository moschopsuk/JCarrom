package me.moscrop.Helpers;

import java.util.ArrayList;
import java.util.List;

import javax.vecmath.*;

public class Model {
	
	private final List<Vector3f> vertices = new ArrayList<Vector3f>();
    private final List<Vector2f> textureCoordinates = new ArrayList<Vector2f>();
    private final List<Vector3f> normals = new ArrayList<Vector3f>();
    private final List<Face> faces = new ArrayList<Face>();
    
    public List<Vector3f> getVertices() {
        return vertices;
    }
    
    public boolean hasUVCoords() {
        return getUVCoords().size() > 0;
    }

    public List<Vector2f> getUVCoords() {
        return textureCoordinates;
    }
    
    public boolean hasNormals() {
        return getNormals().size() > 0;
    }

    public List<Vector3f> getNormals() {
        return normals;
    }
    
    public List<Face> getFaces() {
        return faces;
    }
    
    public static class Face {

        private final int[] vertexIndices = {-1, -1, -1};
        private final int[] normalIndices = {-1, -1, -1};
        private final int[] textureCoordinateIndices = {-1, -1, -1};

        public boolean hasNormals() {
            return normalIndices[0] != -1;
        }

        public boolean hasTextureCoordinates() {
            return textureCoordinateIndices[0] != -1;
        }

        public int[] getVertexIndices() {
            return vertexIndices;
        }

        public int[] getTextureCoordinateIndices() {
            return textureCoordinateIndices;
        }

        public int[] getNormalIndices() {
            return normalIndices;
        }

        public Face(int[] vertexIndices) {
            this.vertexIndices[0] = vertexIndices[0];
            this.vertexIndices[1] = vertexIndices[1];
            this.vertexIndices[2] = vertexIndices[2];
        }

        public Face(int[] vertexIndices, int[] normalIndices) {
            this.vertexIndices[0] = vertexIndices[0];
            this.vertexIndices[1] = vertexIndices[1];
            this.vertexIndices[2] = vertexIndices[2];
            this.normalIndices[0] = normalIndices[0];
            this.normalIndices[1] = normalIndices[1];
            this.normalIndices[2] = normalIndices[2];
        }

        public Face(int[] vertexIndices, int[] normalIndices, int[] textureCoordinateIndices) {
            this.vertexIndices[0] = vertexIndices[0];
            this.vertexIndices[1] = vertexIndices[1];
            this.vertexIndices[2] = vertexIndices[2];
            this.textureCoordinateIndices[0] = textureCoordinateIndices[0];
            this.textureCoordinateIndices[1] = textureCoordinateIndices[1];
            this.textureCoordinateIndices[2] = textureCoordinateIndices[2];
            this.normalIndices[0] = normalIndices[0];
            this.normalIndices[1] = normalIndices[1];
            this.normalIndices[2] = normalIndices[2];
        }
    }
}
