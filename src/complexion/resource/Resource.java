package complexion.resource;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.zip.CRC32;

/**
 * Base class for all Resource type objects, such as sprites, sounds, etc.
 */
public class Resource {
	/// Stores the filename from which this resource was created.
	public String filename;
	
	/// HashID relating to this resource, usually generated with a CRC32
	public long hashID;
	
	/// Compute the CRC32 of the resource file specified by filename
	public static long getCRC32(String filename)
	{
		// Attempt to read the file from disk
		try
		{
			RandomAccessFile file = new RandomAccessFile(filename, "r");
			
			// Convert the file to a buffer
			byte[] buffer = new byte[(int)file.length()];
			file.read(buffer);
			
			// Generate the CRC from the buffer
			CRC32 crc = new CRC32();
			crc.update(buffer);
			file.close();
			// Return the generated CRC
			return crc.getValue();
		}
		catch(IOException e)
		{
			// If the file doesn't exist, return 0 as CRC
			return 0;
		}
		
	}
}
