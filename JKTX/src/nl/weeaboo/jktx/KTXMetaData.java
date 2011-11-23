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
import java.io.UnsupportedEncodingException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class KTXMetaData implements Iterable<Entry<String, byte[]>> {

	private Map<String, byte[]> meta;
	
	public KTXMetaData() {
		meta = new HashMap<String, byte[]>();
	}
	
	public void read(KTXHeader header, InputStream in) throws KTXFormatException, IOException {
		ByteBuffer buf = ByteBuffer.allocate(header.getBytesOfKeyValueData());
		KTXUtil.readFully(in, buf);
		read(header, buf);
	}
	
	public void read(KTXHeader header, ByteBuffer buf) throws KTXFormatException, UnsupportedEncodingException {
		ByteOrder oldOrder = buf.order();
		int oldLimit = buf.limit();
		try {
			read0(header, buf);
		} catch (BufferUnderflowException bue) {
			throw new KTXFormatException("Unexpected end of input", bue);
		} finally {
			buf.order(oldOrder);
			buf.limit(oldLimit);
		}
	}
	
	private void read0(KTXHeader header, ByteBuffer buf) throws KTXFormatException, UnsupportedEncodingException {
		buf.order(header.getByteOrder());		
		buf.limit(header.getBytesOfKeyValueData());

		byte[] temp = new byte[128];
		while (buf.remaining() >= 4) {
			int keyAndValueByteSize = buf.getInt();

			//Read key
			int keyBytes = 0;
			byte b = 0;
			while ((b = buf.get()) != 0) {
				if (keyBytes >= temp.length) {
					temp = Arrays.copyOf(temp, temp.length * 2);
				}
				temp[keyBytes++] = b;
			}
			String key = KTXUtil.asString(temp, 0, keyBytes);
			
			//Read val
			byte[] val = new byte[keyAndValueByteSize - keyBytes];
			buf.get(val);
			
			//Store
			meta.put(key, val);
		}		
	}
	
	public void clear() {
		meta.clear();
	}
	
	/*@Override*/
	public Iterator<Entry<String, byte[]>> iterator() {
		return meta.entrySet().iterator();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (String key : getKeys()) {
			if (sb.length() > 0) sb.append(", ");
			sb.append(key);
			sb.append("=");
			sb.append(getString(key));
		}
		return sb.toString();
	}
	
	public Set<String> getKeys() {
		return Collections.unmodifiableSet(meta.keySet());
	}

	public byte[] get(String key) {
		return meta.get(key);
	}
	public String getString(String key) {
		return KTXUtil.asString(get(key));
	}
		
}
