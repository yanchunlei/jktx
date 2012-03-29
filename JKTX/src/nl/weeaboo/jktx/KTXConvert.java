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

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import nl.weeaboo.dds.DDSFile;
import nl.weeaboo.dds.DDSFormatException;

public class KTXConvert {

	public static void main(String[] args) throws IOException {		
		File srcF = null, dstF = null;
		boolean info = false;
		
		int opt = 0;
		while (opt < args.length) {
			String arg = args[opt++];
			if (arg.equals("-info")) {
				info = true;
			} else {
				if (srcF == null) srcF = new File(arg);
				else if (dstF == null) dstF = new File(arg);
			}
		}
		
		if (args.length < 1 || srcF == null) {
			printUsage();
			return;			
		}
        
		if (dstF == null) {
			String base = srcF.getName();
			if (base.lastIndexOf('.') >= 0) {
				base = base.substring(0, base.lastIndexOf('.'));
			}
			dstF = new File(srcF.getParentFile(), base + ".ktx");
		}
				
		if (info) {
			printInfo(srcF);
			return;
		}
				
		//Create folder for output
		File dstFolder = dstF.getParentFile();
		if (dstFolder != null && !dstFolder.exists()) {
			if (!dstFolder.mkdirs() && !dstFolder.exists()) {
				throw new IOException("Unable to create destination folder: " + dstFolder);
			}
		}
		
		KTXFile ktx = convertToKTX(srcF, dstF);
		System.out.println("----------------------------------------");
		System.out.println(ktx);
		System.out.println("----------------------------------------");
		System.out.println("KTX file written to: " + dstF + " (" + (dstF.length()>>10) + " KiB)");
	}
	
	protected static void printUsage() {
		System.err.println("Usage: java -jar jktx.jar infile outfile.ktx"
				+ "\n         Where infile is in DDS format or any format supported by Java ImageIO"
				+ "\n   or: java -jar jktx.jar -info infile"
				+ "\n         Where infile is a DDS or KTX file");
	}
	
	public static void printInfo(File srcF) throws IOException {
		String name = srcF.getName();

		System.out.println("Printing info for file: " + srcF + " (" + (srcF.length()>>10) + " KiB)");
		System.out.println("----------------------------------------");

		if (name.endsWith(".ktx")) {
			try {
				KTXFile ktx = new KTXFile();
				ktx.read(srcF);
				System.out.println(ktx);		
			} catch (KTXFormatException e) {
				throw new IOException("Parse error for file: " + srcF, e);
			}
		} else if (name.endsWith(".dds")) {
			try {
				DDSFile dds = new DDSFile();
				dds.read(srcF);
				System.out.println(dds);		
			} catch (DDSFormatException e) {
				throw new IOException("Parse error for file: " + srcF, e);
			}
		} else {
			System.out.println("No info for file format: " + name);
		}
		
		System.out.println("----------------------------------------");
	}

	public static KTXFile convertToKTX(File srcF, File dstF) throws IOException {
		KTXFile ktx;
		if (srcF.getName().endsWith(".dds")) {
			DDSFile file = new DDSFile();
			try {
				file.read(srcF);
				ktx = file.toKTX();
			} catch (DDSFormatException e) {
				throw new IOException("Error parsing DDS file: " + srcF, e);
			}
		} else {
			BufferedImage image = ImageIO.read(srcF);
			ktx = new KTXFile();
			ktx.initFromImage(image);
		}
		
		ktx.write(dstF);		
		return ktx;
	}
}
