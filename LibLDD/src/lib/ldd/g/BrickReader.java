package lib.ldd.g;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import lib.gl.vbo.BufferCombo;

public class BrickReader {
	private static final int TEXTURE_COORDINATES_INCLUDED = 0x1;

	public static BufferCombo readGeometryFile(File file) throws IOException {
		if(!file.exists()) {
			throw new FileNotFoundException();
		}
		byte[] fileContents = new byte[(int) file.length()];
		FileInputStream stream = new FileInputStream(file);
		stream.read(fileContents);
//		System.out.print(file.getName() + "\t");
		stream.close();
		return readGeometryFile(fileContents);
	}
	
	public static BufferCombo readGeometryFile(byte[] fileContents) throws IOException {
		return loadSingleGeometryFile(fileContents);
	}
	
	private static BufferCombo loadSingleGeometryFile(byte[] streamContents) throws IOException {
		ByteBuffer buffer = ByteBuffer.wrap(streamContents);
		buffer.order(ByteOrder.LITTLE_ENDIAN);

		//header
		buffer.getInt();
		int vertexCount = buffer.getInt();
		int indexCount = buffer.getInt();
		int options = buffer.getInt();
		boolean texturesEnabled = (TEXTURE_COORDINATES_INCLUDED & options) == TEXTURE_COORDINATES_INCLUDED;
		
		int[] indices = new int[indexCount];
		float[] vertices = new float[3*vertexCount];
		float[] texCoords = new float[2*vertexCount];
		float[] normals = new float[3*vertexCount];
		
		for(int i = 0; i < 3*vertexCount; i++) {
			vertices[i] = buffer.getFloat();
		}
		for(int i = 0; i < 3*vertexCount; i++) {
			normals[i] = buffer.getFloat();
		}
		if(texturesEnabled) {
			for(int i = 0; i < 2*vertexCount; i++) {
				texCoords[i] = buffer.getFloat();
			}
		}
		for(int i = 0; i < indexCount; i++) {
			indices[i] = buffer.getInt();
		}
		
		int num = buffer.getInt();
		for(int i = 0; i < num; i++) {
			buffer.getInt();
		}
		
		if(texturesEnabled) {
			return new BufferCombo(vertices, normals, texCoords, indices);
		} else {
			return new BufferCombo(vertices, normals, indices);
		}
	}
}
