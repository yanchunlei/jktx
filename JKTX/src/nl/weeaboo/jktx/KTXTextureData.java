/* JKTX
 * 
 * Copyright (c) 2011 Timon Bijlsma
 *   
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Library General Public License
 * as published by the Free Software Foundation; either version 2 of
 * the License, or (at your option) any later version.
   
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Library General Public License for more details.
 * 
 * You should have received a copy of the GNU Library General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */

package nl.weeaboo.jktx;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

public class KTXTextureData {

	private ByteBuffer[][] data;
	private int mipmapLevels;
	private int faceCount;
	private long totalBytes;
	
	public KTXTextureData() {
		data = new ByteBuffer[0][];
	}
	
	//Functions
	public void readMipmaps(KTXHeader header, InputStream in)
		throws KTXFormatException, IOException
	{
		for (int level = 0; level < header.getNumberOfMipmapLevels(); level++) {
			readMipmapLevel(header, in, level);
		}
	}
	
	public void readMipmapLevel(KTXHeader header, InputStream in, int level)
		throws KTXFormatException, IOException
	{
		int len = KTXUtil.readInt(in, header.getByteOrder());
		
		ByteBuffer[] faces = new ByteBuffer[header.getNumberOfFaces()];
		for (int n = 0; n < header.getNumberOfFaces(); n++) {
			ByteBuffer buf = ByteBuffer.allocateDirect(KTXUtil.align4(len));
			buf.order(ByteOrder.nativeOrder());
			readFace(header, in, buf);
			faces[n] = buf;
		}
		setMipmapLevel(level, faces);
	}
	
	public void readFace(KTXHeader header, InputStream in, ByteBuffer out)
		throws KTXFormatException, IOException
	{
		KTXUtil.readFully(in, out);
		if (header.getByteOrder() != ByteOrder.nativeOrder()) {
			switch (header.getGLTypeSize()) {
			case 1: break;
			case 2: KTXUtil.swapEndian16(out); break;
			case 4: KTXUtil.swapEndian32(out); break;
			default: throw new RuntimeException("Unimplemented glTypeSize: " + header.getGLTypeSize());
			}
		}
	}
	
	@Override
	public String toString() {
		return String.format("%s[mipmapLevels=%d, faces=%d, totalBytes=%d]", getClass().getSimpleName(),
				getMipmapLevels(), getFaceCount(), getTotalBytes());
	}
	
	//Getters
	public ByteBuffer getFace(int mipmapLevel, int faceIndex) {		
		return data[mipmapLevel][faceIndex];
	}
	public int getMipmapLevels() {
		return mipmapLevels;
	}
	public int getFaceCount() {
		return faceCount;
	}
	public long getTotalBytes() {
		return totalBytes;
	}
	
	private static long getTotalBytes(ByteBuffer[] buffers) {
		long total = 0;
		for (ByteBuffer b : buffers) {
			total += (b != null ? b.remaining() : 0);
		}
		return total;
	}
	
	//Setters
	private void setMipmapLevel(int level, ByteBuffer[] faces) {
		mipmapLevels = Math.max(mipmapLevels, level+1);
		faceCount = Math.max(faceCount, faces.length);
		
		if (data.length < mipmapLevels) {
			data = Arrays.copyOf(data, mipmapLevels);
		}
		
		if (data[level] != null) {
			totalBytes -= getTotalBytes(data[level]);
		}
		data[level] = faces;
		if (data[level] != null) {
			totalBytes += getTotalBytes(data[level]);
		}
	}
	
}
