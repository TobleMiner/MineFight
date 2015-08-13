package tobleminer.minefight.util.io.file;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import tobleminer.minefight.util.io.StreamUtil;

public class FileUtil
{
	public static byte[] getBytes(File f)
	{
		try
		{
			FileInputStream fis = new FileInputStream(f);
			byte[] data = new byte[fis.available()];
			fis.read(data);
			fis.close();
			return data;
		}
		catch (Exception ex)
		{}
		return null;
	}

	public static String getAllTextUTF8(File f)
	{
		try
		{
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(f), "UTF8"));
			String s = StreamUtil.getDataAsUTF8(br);
			br.close();
			return s;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		return null;
	}

	public static void copyFromInputStreamToFileUtf8(File f, InputStream is) throws IOException
	{
		if (!f.exists())
		{
			f.createNewFile();
		}
		BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF8"));
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f), "UTF8"));
		while (true)
		{
			int i = br.read();
			if (i < 0)
				break;
			bw.write(i);
		}
		br.close();
		bw.close();
	}
}
