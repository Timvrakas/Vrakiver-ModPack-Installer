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
	private JLabel message;
	private JLabel currentVersionTextLabel;
	private JLabel newVersionTextLabel;
	private JLabel currentVersionLabel;
	private JLabel newVersionLabel;
	public static String currentVersion = "None";
	public static String newVersion = "Loading...";

	public GUI() {
		setTitle("Vrakiver ModPack Installer");
		Container pane = getContentPane();
		pane.setLayout(new GridLayout(3, 2));

		message = new JLabel("");
		currentVersionTextLabel = new JLabel("Current Version:", SwingConstants.CENTER);
		newVersionTextLabel = new JLabel("Latest Version:", SwingConstants.CENTER);
		currentVersionLabel = new JLabel(currentVersion);
		newVersionLabel = new JLabel(newVersion);
		update = new JButton("Install ModPack");
		Handler = new ChangeButtonHandler();
		update.addActionListener(Handler);

		pane.add(currentVersionTextLabel);
		pane.add(currentVersionLabel);
		pane.add(newVersionTextLabel);
		pane.add(newVersionLabel);
		pane.add(update);
		pane.add(message);
		setSize(WIDTH, HEIGHT);
		setVisible(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		currentVersionLabel.setText(getCurrentVersion());
		newVersionLabel.setText(getVersion());
	}

	private class ChangeButtonHandler implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			try {
				final URL link = new URL(new URL(Main.link), Main.name + ".zip");
				final File zip = new File(Main.MCDir(), Main.name + ".zip");
				message.setText("Deleting");
				if (zip.exists())
					zip.delete();
				Runnable r = new Runnable() {
					public void run() {
						try {
							Main.get(link, zip);
							message.setText("Installing");
							Main.install(zip);
							message.setText("Done!");
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						currentVersionLabel.setText(getCurrentVersion());
						newVersionLabel.setText(getVersion());
						Main.receipt();
					}
				};
				Thread t = new Thread(r);
				t.start();
				message.setText("Downloading");

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
			newVersion = IOUtils.toString(in);
			IOUtils.closeQuietly(in);

		} catch (IOException e) {

			e.printStackTrace();
		}
		return newVersion;
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
