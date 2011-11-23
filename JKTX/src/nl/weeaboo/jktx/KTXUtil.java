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

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

final class KTXUtil {

	private KTXUtil() {		
	}
	
	public static int readInt(InputStream in, ByteOrder order) throws IOException {
		ByteBuffer buf = ByteBuffer.allocate(4);
		buf.order(order);
		readFully(in, buf);
		return buf.getInt();
	}
	
	public static void readFully(InputStream in, ByteBuffer out) throws IOException {
		out.mark();
		if (out.hasArray()) {
			while (out.hasRemaining()) {
				int r = in.read(out.array(), out.arrayOffset()+out.position(), out.remaining());
				if (r < 0) throw new EOFException();
				out.position(out.position() + r);
			}
		} else {
			byte[] temp = new byte[Math.min(out.remaining(), 8192)];
			while (out.hasRemaining()) {
				int r = in.read(temp, 0, Math.min(temp.length, out.remaining()));
				if (r < 0) throw new EOFException();
				out.get(temp, 0, r);
			}
		}
		out.reset();
	}
	
	public static String asString(byte[] b) {
		if (b == null) return null;
		return asString(b, 0, b.length);
	}
	public static String asString(byte[] b, int off, int len) {
		if (b == null) return null;
		try {
			return new String(b, off, len, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			//This should never happen
			return new String(b, off, len);
		}		
	}
	
	public static int align4(int i) {
		return (i+3) & ~3;
	}
	
	public static void swapEndian16(ByteBuffer buf) {
		int elems = buf.remaining() / 2;
		for (int n = 0, t = buf.position(); n < elems; n++, t += 2) {
			short s = buf.getShort(t);
			s = (short)((s << 8) | (s >>> 8));
			buf.putShort(t, s);
		}
	}
	
	public static void swapEndian32(ByteBuffer buf) {
		int elems = buf.remaining() / 4;
		for (int n = 0, t = buf.position(); n < elems; n++, t += 4) {
			int i = buf.getInt(t);
			i = (i << 24) | ((i & 0xFF00) << 8) | ((i & 0xFF0000) >> 8) | (i >>> 24);
			buf.putInt(t, i);
		}
	}
	
}