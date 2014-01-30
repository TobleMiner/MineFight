package TobleMiner.MineFight.Util.Hashing;

import java.security.MessageDigest;

public class HashUtil 
{
	/*
	 * IMPORTANT NOTICE!
	 * This class is NOT designed to be secure. It allows timing attacks and over pretty horrible stuff.
	 * Please, don't use this class in any security sensitive project!
	 */
	
	public static byte[] getSHA256UTF8(String s)
	{
		try
		{
			return getSHA256(s.getBytes("UTF8"));
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return null;
	}
	
	public static byte[] getSHA256(byte b[])
	{
		try
		{
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			md.update(b);
			return md.digest();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return null;
	}
	
	private static boolean hashEquals(byte[] hash1, byte[] hash2)
	{
		return MessageDigest.isEqual(hash1, hash2);
	}
	
	public static boolean hashMatchBin(byte[] b1, byte[] b2)
	{
		return hashEquals(getSHA256(b1), getSHA256(b2));
	}
	
	public static boolean hashMatchStringUTF8(String s1, String s2)
	{
		return hashEquals(getSHA256UTF8(s1), getSHA256UTF8(s2));
	}	
}
