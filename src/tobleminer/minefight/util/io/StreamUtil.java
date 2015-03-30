package tobleminer.minefight.util.io;

import java.io.BufferedReader;
import java.io.InputStream;

public class StreamUtil 
{
	public static byte[] readAllBytes(InputStream is)
	{
		try
		{
			byte[] data = new byte[is.available()];
			is.read(data);
			return data;
		}
		catch(Exception ex){}
		return null;
	}
	
	public static String getDataAsUTF8(BufferedReader br)
	{
		try
		{
			char[] data = new char[0];
			int offset = 0;
			while(true)
			{
				char[] data_sub = new char[1024];
				int bread = br.read(data_sub, 0, data_sub.length);
				if(bread < 0) break;
				char[] datatmp = new char[data.length];
				System.arraycopy(data, 0, datatmp, 0, data.length);
				data = new char[data.length+bread];
				System.arraycopy(datatmp, 0, data, 0, datatmp.length);
				System.arraycopy(data_sub, 0, data, offset, bread);
				offset += bread;
				if(bread < data_sub.length) break;
			}
			return String.valueOf(data);
		}
		catch(Exception ex){ex.printStackTrace();}
		return null;
	}
}


