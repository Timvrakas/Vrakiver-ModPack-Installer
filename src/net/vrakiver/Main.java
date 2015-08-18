package net.vrakiver;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.nio.channels.FileChannel;
import org.apache.commons.io.FileUtils;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;

public class Main {
	public static String name = "vrakiver";
	public static String link = "https://googledrive.com/host/0B4ncak83gVoOSzM5elB6Zm1HZmM/";

	public static void main(String[] args) throws IOException {
		@SuppressWarnings("unused")
		GUI gui = new GUI();

	}

	static void copyFile(File sourceFile, File destFile) throws IOException {
		if (!destFile.exists()) {
			destFile.createNewFile();
		}
		FileInputStream sourceFileStream = new FileInputStream(sourceFile);
		FileOutputStream destFileStream = new FileOutputStream(destFile);
		FileChannel source;
		FileChannel destination;

		source = sourceFileStream.getChannel();
		destination = destFileStream.getChannel();
		long count = 0;
		long size = source.size();
		while ((count += destination.transferFrom(source, count, size - count)) < size)
			;
		sourceFileStream.close();
		destFileStream.close();
	}

	public static final class OS {
		public static final OS linux;
		public static final OS solaris;
		public static final OS windows;
		public static final OS macos;
		public static final OS unknown;
		public int id;
		static {
			linux = new OS("linux", 0);
			solaris = new OS("solaris", 1);
			windows = new OS("windows", 2);
			macos = new OS("macos", 3);
			unknown = new OS("unknown", 4);
			/*
			 * $VALUES = (new OS[] { linux, solaris, windows, macos, unknown });
			 */
		}

		public OS(String s, int i) {
			id = i;
		}
	}

	public static OS getPlatform() {
		String s = System.getProperty("os.name").toLowerCase();
		if (s.contains("win")) {
			return OS.windows;
		}
		if (s.contains("mac")) {
			return OS.macos;
		}
		if (s.contains("solaris")) {
			return OS.solaris;
		}
		if (s.contains("sunos")) {
			return OS.solaris;
		}
		if (s.contains("linux")) {
			return OS.linux;
		}
		if (s.contains("unix")) {
			return OS.linux;
		} else {
			return OS.unknown;
		}
	}

	public static File MCDir() {
		String user_home = System.getProperty("user.home", ".");
		File file = null;
		String name = new String("minecraft");
		String appdata = System.getenv("APPDATA");
		switch (getPlatform().id) {
		case 0: // null
		case 1: // Linux
			file = new File(user_home, "." + name);
			break;
		case 2: // PC
			if (appdata != null) {
				file = new File(appdata, "." + name);
				System.out.println("APPDATA");
			} else {
				System.out.println("APPDATA is NULL (using user.home)");
				file = new File(user_home, '.' + name);
			}
			break;
		case 3: // Mac
			file = new File(user_home, "Library/Application Support/" + name);
			break;
		default: // Other
			file = new File(user_home, name);
			break;
		}
		if (!file.isDirectory()) {
			throw new RuntimeException(
					"The Minecraft Working Dir Does Not Exist: " + file);
		}
		return file;
	}

	public static void get(URL link, File file) throws IOException {

		link.openConnection();
		InputStream in = link.openStream();

		FileOutputStream out = new FileOutputStream(file);
		byte[] buffer = new byte[153600];
		int bytesRead = 0;

		while ((bytesRead = in.read(buffer)) > 0) {
			out.write(buffer, 0, bytesRead);
			buffer = new byte[153600];
		}
		out.close();
	}

	public static void install(File zip) {
		File mods = new File(MCDir(), "mods");
		if (mods.isDirectory()) {
			try {
				FileUtils.deleteDirectory(mods);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		try {
			ZipFile zipFile = new ZipFile(zip);
			mods.mkdirs();
			zipFile.extractAll(mods.getPath());
		} catch (ZipException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void receipt() {
		File versionFile = new File(Main.MCDir(), "mods");
		versionFile = new File(versionFile, name + "_version.txt");
		versionFile.delete();

		try {
			versionFile.createNewFile();
			PrintWriter out = new PrintWriter(versionFile);
			out.print(GUI.newVersion);
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
