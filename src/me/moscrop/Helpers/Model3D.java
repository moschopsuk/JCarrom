package me.moscrop.Helpers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;

import org.newdawn.slick.util.ResourceLoader;

/**
 * This is used for rendering .obj files
 * will only be able to render 3D models with
 * full index data, on vertices, normals and textures
 * 
 * @author Moschops
 *
 */
public class Model3D {
	
	//General 3D properties
	private ArrayList<Float> vertices;
	private ArrayList<Float> texturesUV;
	private ArrayList<Float> normals;
	private ArrayList<ModelFace> faces;
    
    /**
     * Loads an 3D model and collects
     * Information from the model and
     * sets up the render
     * 
     * @param stream
     * @throws IOException
     */
    public Model3D(String modelFileName) throws IOException {
    	
    	InputStream stream = ResourceLoader.getResourceAsStream(modelFileName);
    		
    	//Setup array lists must be done
    	//Separately so they can merged
    	//when all data is present
    	this.vertices = new ArrayList<Float>();
    	this.texturesUV = new ArrayList<Float>();
    	this.normals = new ArrayList<Float>();
    	this.faces = new ArrayList<ModelFace>();

    	
    	//Read the file
		Reader reader = new InputStreamReader(stream);
		BufferedReader buffer = new BufferedReader(reader);
		
		//Holds the temporary line data
		String line;
		String[] data, faceData;

		//We now loop over every line
		while ((line = buffer.readLine()) != null) {
			
			//Split the line into useful data
			data = line.split(" ");

			//Grab the first few chars
			//so we know what the line represents
            if (data[0].equals("v")) {
                for (int i = 1; i <= 3; i++) {
                    float v = Float.parseFloat(data[i]);
                    this.vertices.add(v);
                }

            } else if (data[0].equals("vt")) {
                for (int i = 1; i <= 2; i++) {
                    float uv = Float.parseFloat(data[i]);
                    this.texturesUV.add(uv);
                }

            } else if (data[0].equals("vn")) {
                for (int i = 1; i <= 3; i++) {
                    float n = Float.parseFloat(data[i]);
                    this.normals.add(n);
                }

            } else if (data[0].equals("f")) {
                for (int i = 1; i <= 3; i++) {
                    faceData = data[i].split("/");

                    long vi = Integer.parseInt(faceData[0]) - 1;
                    int ti = Integer.parseInt(faceData[1]) - 1;
                    int ni = Integer.parseInt(faceData[2]) - 1;

                    this.faces.add(new ModelFace(vi, ti, ni));
                }

            }
		}
		
		//Make sure we don't cross the streams
		//close all active file handlers
		//Must be done in order created to avoid IO errors
		buffer.close();
		reader.close();
		stream.close();
    }
    
    /**
     * Converts the array list of vertices
     * into a buffer of bytes
     * @return
     */
    public ByteBuffer getVertexBuffer() {
    	ByteBuffer buffer;
    	
    	//make a new buffer that is large enough to store
    	//all the data
    	buffer = ByteBuffer.allocateDirect(this.vertices.size() * 12 * 3);		
    	buffer.order(ByteOrder.nativeOrder());
    	buffer.clear();
    	
        for (int j = 0; j < this.faces.size(); j++) {
        	//Store the Vertices
        	buffer.putFloat(this.vertices.get((int) (this.faces.get(j).getVI() * 3)));
        	buffer.putFloat(this.vertices.get((int) (this.faces.get(j).getVI() * 3 + 1)));
        	buffer.putFloat(this.vertices.get((int) (this.faces.get(j).getVI() * 3 + 2)));
        }
        
        buffer.flip();
        return buffer;
    }
    
    /**
     * Converts the array list of indices
     * into a buffer of bytes
     * @return
     */
    public ByteBuffer getIndexBuffer() {
    	ByteBuffer buffer;
    	
    	//make a new buffer that is large enough to store
    	//all the data
    	buffer = ByteBuffer.allocateDirect(this.vertices.size() * 12 * 3);		
    	buffer.order(ByteOrder.nativeOrder());
    	buffer.clear();
    	
        for (int j = 0; j < faces.size(); j++) {       	
        	//Store the indices
        	buffer.putInt((int) faces.get(j).getVI() * 3);
        	buffer.putInt((int) (faces.get(j).getVI() * 3) + 1);
        	buffer.putInt((int) (faces.get(j).getVI() * 3) + 2);
        }
        
        buffer.flip();
        return buffer;
    }
         
    /**
     * @return 3D Model Vertices
     */
    public ArrayList<Float> getVertices() {
    	return this.vertices;
    }

    /**
     * @return 3D Model TextureUV Coords
     */  
    public ArrayList<Float> getTextureUV() {
    	return this.texturesUV;
    }
    
    /**
     * @return 3D Model Normals
     */   
    public ArrayList<Float> getNormals() {
    	return this.normals;
    }

    /**
     * @return 3D Model Faces
     */
    public ArrayList<ModelFace> getFaces() {
    	return this.faces;
    }
}