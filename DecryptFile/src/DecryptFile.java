import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Scanner;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

public class DecryptFile {

	public static void main(String[] args) {

		Scanner reader = new Scanner(System.in);  
		System.out.println("Decrypt etmek istediginiz dosyayi bulundugu path ile birlikte eksiksiz bir sekilde yaziniz :  ");
		String filePath = reader.nextLine();
		String key = "infinaYazilim";
		
		File file = new File(filePath);
		while(!file.exists())
		{
			System.out.println("Decrypt etmek istediginiz dosyanin bulundugu path i hatali yazdiniz. Lutfen tekrar yaziniz.");
			filePath = reader.nextLine();
			file = new File(filePath);
		}
		

		FileInputStream fileToDecrypt;
		FileOutputStream decryptedFile;

		try {
			fileToDecrypt = new FileInputStream(file);
			decryptedFile = new FileOutputStream(file.getParent() + "\\INFINA.ini");
			decrypt(key, fileToDecrypt, decryptedFile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (Throwable e) {
			e.printStackTrace();
		}

	}

	public static void decrypt(String key, InputStream is, OutputStream os) throws Throwable {
		encryptOrDecrypt(key, Cipher.DECRYPT_MODE, is, os);
	}

	public static void encryptOrDecrypt(String key, int mode, InputStream is, OutputStream os) throws Throwable {

		DESKeySpec dks = new DESKeySpec(key.getBytes());
		SecretKeyFactory skf = SecretKeyFactory.getInstance("DES");
		SecretKey desKey = skf.generateSecret(dks);
		Cipher cipher = Cipher.getInstance("DES"); // DES/ECB/PKCS5Padding for
													// SunJCE

		if (mode == Cipher.ENCRYPT_MODE) {
			cipher.init(Cipher.ENCRYPT_MODE, desKey);
			CipherInputStream cis = new CipherInputStream(is, cipher);
			doCopy(cis, os);
		} else if (mode == Cipher.DECRYPT_MODE) {
			cipher.init(Cipher.DECRYPT_MODE, desKey);
			CipherOutputStream cos = new CipherOutputStream(os, cipher);
			doCopy(is, cos);
		}
	}

	public static void doCopy(InputStream is, OutputStream os) throws IOException {
		byte[] bytes = new byte[64];
		int numBytes;
		while ((numBytes = is.read(bytes)) != -1) {
			os.write(bytes, 0, numBytes);
		}
		os.flush();
		os.close();
		is.close();
	}

}