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

package nl.weeaboo.jktx.test;

import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import nl.weeaboo.jktx.KTXFile;
import nl.weeaboo.jktx.KTXFormatException;

public class KTXTest {

	public static void main(String[] args) throws KTXFormatException, IOException {
		for (String arg : args) {
			File file = new File(arg);
			if (file.isDirectory()) {
				for (File f : file.listFiles()) {
					if (f.getName().endsWith("ktx")) {
						process(f);
					}
				}
			} else {
				process(file);
			}
		}
	}
	
	private static void process(File srcF) throws KTXFormatException, IOException {
		System.out.println(srcF);
		
		KTXFile file = new KTXFile();
		if (srcF.getName().endsWith("ktx")) {
			file.read(srcF);
		} else {
			file.initFromImage(ImageIO.read(srcF));
		}
		
		System.out.println(file);
		
		//File dstF = new File(srcF.getParent(), "conv-" + srcF.getName().substring(0, srcF.getName().lastIndexOf('.')+1) + "ktx");
		//file.write(dstF);
		
		System.out.println("----------------------------------------");		
	}
	
}
