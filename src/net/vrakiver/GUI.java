package net.vrakiver;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import org.apache.commons.io.IOUtils;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class GUI extends JFrame {
	private static final long serialVersionUID = 1L;
	private static final int WIDTH = 250;
	private static final int HEIGHT = 100;
	private JButton update;
	private ChangeButtonHandler Handler;
	private JLabel msg;
	private JLabel doneversion;
	private JLabel newversion;
	private JLabel dv;
	private JLabel nv;
	public static String currentVersion = "0";
	public static String version = "0";

	public GUI() {
		setTitle("Vrakiver Downloader");
		Container pane = getContentPane();
		pane.setLayout(new GridLayout(3, 2));

		msg = new JLabel("");
		doneversion = new JLabel("Current Version:", SwingConstants.CENTER);
		newversion = new JLabel("Latest Version:", SwingConstants.CENTER);
		dv = new JLabel("None");
		nv = new JLabel("0");
		update = new JButton("Install ModPack");
		Handler = new ChangeButtonHandler();
		update.addActionListener(Handler);

		pane.add(doneversion);
		pane.add(dv);
		pane.add(newversion);
		pane.add(nv);
		pane.add(update);
		pane.add(msg);
		setSize(WIDTH, HEIGHT);
		setVisible(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		dv.setText(getCurrentVersion());
		nv.setText(getVersion());
	}

	private class ChangeButtonHandler implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			try {
				final URL link = new URL(new URL(Main.link), Main.name + ".zip");
				final File zip = new File(Main.MCDir(), Main.name + ".zip");
				msg.setText("Deleting");
				if (zip.exists())
					zip.delete();
				Runnable r = new Runnable() {
					public void run() {
						try {
							Main.get(link, zip);
							msg.setText("Installing");
							Main.install(zip);
							msg.setText("Done!");
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						dv.setText(getCurrentVersion());
						nv.setText(getVersion());
						Main.receipt();
					}
				};
				Thread t = new Thread(r);
				t.start();
				msg.setText("Downloading");

			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

		}
	}

	private static String getVersion() {
		try {
			URL versionFile = new URL(new URL(Main.link), Main.name
					+ "_version.txt");
			InputStream in = versionFile.openStream();
			version = IOUtils.toString(in);
			IOUtils.closeQuietly(in);

		} catch (IOException e) {

			e.printStackTrace();
		}
		return version;
	}

	private static String getCurrentVersion() {
		File versionFile = new File(Main.MCDir(), "mods");
		versionFile = new File(versionFile, Main.name + "_version.txt");
		if (versionFile.exists() || versionFile.isFile()) {
			try {
				FileInputStream in = new FileInputStream(versionFile);
				currentVersion = IOUtils.toString(in);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			currentVersion = "Not Installed";
		}
		return currentVersion;
	}
}
