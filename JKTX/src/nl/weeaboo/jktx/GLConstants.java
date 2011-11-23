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

class GLConstants {

	//Internal formats
	public static final int GL_RGB8  = 0x8051;
	public static final int GL_RGBA8 = 0x8058;
	
	//Formats & base internal formats
	public static final int GL_RGB  = 0x1907;
	public static final int GL_RGBA = 0x1908;
	public static final int GL_BGRA = 0x80E1;
	
	//Type
	public static final int GL_UNSIGNED_BYTE = 0x1401;
	public static final int GL_UNSIGNED_SHORT = 0x1403;
	public static final int GL_UNSIGNED_INT_8_8_8_8_REV = 0x8367;
	
	private GLConstants() {		
	}
	
}
