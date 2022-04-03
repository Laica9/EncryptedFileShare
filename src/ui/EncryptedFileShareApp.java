package ui;

import static javax.swing.JFileChooser.APPROVE_OPTION;
import static javax.swing.JFileChooser.DIRECTORIES_ONLY;
import static javax.swing.JOptionPane.ERROR_MESSAGE;
import static javax.swing.JOptionPane.INFORMATION_MESSAGE;
import static javax.swing.JOptionPane.YES_NO_OPTION;
import static javax.swing.JOptionPane.YES_OPTION;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.util.Collections;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;

import encryption.EncryptedSharedFile;
import encryption.EncryptedSharedFile.SharedFileContents;
import encryption.User;

/**

* @author Ioanna Pashalidi Kozelj - nnp18llf
 * 
 * GUI
 *  *
 */
public class EncryptedFileShareApp { 
	
	private JFrame frmEncryptedFileShare;

	private DefaultListModel<User> listModel = new DefaultListModel<User>();
	private File file, outFile, lockerFile, keyFile;
	private JTextField textField_saveTo;
	private JTextField textField_userID;
	private JTextField textField_lockerFile;
	private JTextField textField_privateKey;
	private JTextField textField_output;

	/**

* Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					EncryptedFileShareApp window = new EncryptedFileShareApp();
					window.frmEncryptedFileShare.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**

* Create the application.
	 */
	public EncryptedFileShareApp() {
		initialise();
	}

	/**

* Initialise the contents of the frame.
	 */
	private void initialise() {
		frmEncryptedFileShare = new JFrame();
		frmEncryptedFileShare.setTitle("Encrypted File Share");
		frmEncryptedFileShare.setBounds(100, 100, 474, 300);
		frmEncryptedFileShare.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		frmEncryptedFileShare.getContentPane().add(tabbedPane, BorderLayout.CENTER);

		// Share new file tabbed pane 
		JPanel sharePanel = new JPanel();
		tabbedPane.addTab("Share a New File", null, sharePanel, null);
		GridBagLayout gbl_sharePanel = new GridBagLayout();
		gbl_sharePanel.columnWidths = new int[] { 0, 0, 0 };
		gbl_sharePanel.rowHeights = new int[] { 0, 0, 0, 0, 0 };
		gbl_sharePanel.columnWeights = new double[] { 0.0, 1.0, Double.MIN_VALUE };
		gbl_sharePanel.rowWeights = new double[] { 0.0, 1.0, 0.0, 0.0, Double.MIN_VALUE };
		sharePanel.setLayout(gbl_sharePanel);

		JLabel fileToShareLabel = new JLabel("File to Share:");
		GridBagConstraints gbc_fileToShareLabel = new GridBagConstraints();
		gbc_fileToShareLabel.insets = new Insets(0, 0, 5, 5);
		gbc_fileToShareLabel.anchor = GridBagConstraints.EAST;
		gbc_fileToShareLabel.gridx = 0;
		gbc_fileToShareLabel.gridy = 0;
		sharePanel.add(fileToShareLabel, gbc_fileToShareLabel);

		JPanel panelFileToShare = new JPanel();
		GridBagConstraints gbc_panelFileToShare = new GridBagConstraints();
		gbc_panelFileToShare.insets = new Insets(0, 0, 5, 0);
		gbc_panelFileToShare.fill = GridBagConstraints.BOTH;
		gbc_panelFileToShare.gridx = 1;
		gbc_panelFileToShare.gridy = 0;
		sharePanel.add(panelFileToShare, gbc_panelFileToShare);
		GridBagLayout gbl_panelFileToShare = new GridBagLayout();
		gbl_panelFileToShare.columnWidths = new int[] { 0, 0, 0 };
		gbl_panelFileToShare.rowHeights = new int[] { 0, 0 };
		gbl_panelFileToShare.columnWeights = new double[] { 1.0, 0.0, Double.MIN_VALUE };
		gbl_panelFileToShare.rowWeights = new double[] { 0.0, Double.MIN_VALUE };
		panelFileToShare.setLayout(gbl_panelFileToShare);

		JTextField textField_fileToShare = new JTextField();
		textField_fileToShare.setEditable(false);
		GridBagConstraints gbc_textField_fileToShare = new GridBagConstraints();
		gbc_textField_fileToShare.insets = new Insets(0, 0, 0, 5);
		gbc_textField_fileToShare.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField_fileToShare.gridx = 0;
		gbc_textField_fileToShare.gridy = 0;
		panelFileToShare.add(textField_fileToShare, gbc_textField_fileToShare);
		textField_fileToShare.setColumns(255);

		JButton btnFileToShare = new JButton("...");
		btnFileToShare.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				file = fileChooser(false);
				if (file != null)
					textField_fileToShare.setText(file.getAbsolutePath());
				else
					textField_fileToShare.setText("");
			}
		});
		btnFileToShare.setToolTipText("Select a file.");
		GridBagConstraints gbc_btnFileToShare = new GridBagConstraints();
		gbc_btnFileToShare.gridx = 1;
		gbc_btnFileToShare.gridy = 0;
		panelFileToShare.add(btnFileToShare, gbc_btnFileToShare);

		JLabel shareWithLabel = new JLabel("Share with:");
		GridBagConstraints gbc_shareWithLabel = new GridBagConstraints();
		gbc_shareWithLabel.anchor = GridBagConstraints.NORTHEAST;
		gbc_shareWithLabel.insets = new Insets(0, 0, 5, 5);
		gbc_shareWithLabel.gridx = 0;
		gbc_shareWithLabel.gridy = 1;
		sharePanel.add(shareWithLabel, gbc_shareWithLabel);

		JPanel panelShareWith = new JPanel();
		GridBagConstraints gbc_panelShareWith = new GridBagConstraints();
		gbc_panelShareWith.insets = new Insets(0, 0, 5, 0);
		gbc_panelShareWith.fill = GridBagConstraints.BOTH;
		gbc_panelShareWith.gridx = 1;
		gbc_panelShareWith.gridy = 1;
		sharePanel.add(panelShareWith, gbc_panelShareWith);
		GridBagLayout gbl_panelShareWith = new GridBagLayout();
		gbl_panelShareWith.columnWidths = new int[] { 171, 0 };
		gbl_panelShareWith.rowHeights = new int[] { 1, 0, 0 };
		gbl_panelShareWith.columnWeights = new double[] { 1.0, 0.0 };
		gbl_panelShareWith.rowWeights = new double[] { 0.0, 1.0, Double.MIN_VALUE };
		panelShareWith.setLayout(gbl_panelShareWith);

		JList<User> list = new JList<User>(listModel);
		list.setBorder(new LineBorder(new Color(0, 0, 0)));
		GridBagConstraints gbc_list = new GridBagConstraints();
		gbc_list.gridheight = 2;
		gbc_list.insets = new Insets(0, 0, 0, 5);
		gbc_list.fill = GridBagConstraints.BOTH;
		gbc_list.gridx = 0;
		gbc_list.gridy = 0;
		panelShareWith.add(list, gbc_list);

		JButton btnAdd = new JButton("Add..");
		btnAdd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				NewUserDialog newDialog = new NewUserDialog();
				newDialog.setModal(true);
				newDialog.setVisible(true);
				if (newDialog.getUserDetails() != null) {
					listModel.addElement(newDialog.getUserDetails());
				}
			}
		});
		GridBagConstraints gbc_btnAdd = new GridBagConstraints();
		gbc_btnAdd.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnAdd.anchor = GridBagConstraints.NORTH;
		gbc_btnAdd.insets = new Insets(0, 0, 5, 5);
		gbc_btnAdd.gridx = 1;
		gbc_btnAdd.gridy = 0;
		panelShareWith.add(btnAdd, gbc_btnAdd);

		JButton btnRemove = new JButton("Remove...");
		btnRemove.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (list.getSelectedIndex() != -1) {
					int retVal = JOptionPane.showConfirmDialog(frmEncryptedFileShare, "Are you sure?", "Remove Sharer",
							YES_NO_OPTION);
					if (retVal == YES_OPTION)
						listModel.remove(list.getSelectedIndex());
				}
			}
		});
		GridBagConstraints gbc_btnRemove = new GridBagConstraints();
		gbc_btnRemove.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnRemove.insets = new Insets(0, 0, 0, 5);
		gbc_btnRemove.anchor = GridBagConstraints.NORTH;
		gbc_btnRemove.gridx = 1;
		gbc_btnRemove.gridy = 1;
		panelShareWith.add(btnRemove, gbc_btnRemove);

		JButton buttonShare = new JButton("Share...");
		buttonShare.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					byte[] pub = EncryptedFileShareApp.class.getResourceAsStream("/encryption/public.rsa")
							.readAllBytes();

					EncryptedSharedFile newFile = new EncryptedSharedFile(file.getName(),
							Files.readAllBytes(file.toPath()), Collections.list(listModel.elements()), pub);

					String file = newFile.store(outFile);
					JOptionPane.showMessageDialog(frmEncryptedFileShare,
							"Your shared file has been encrypted to " + file + ".", "Success",
							INFORMATION_MESSAGE);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		});

		JLabel saveToLabel = new JLabel("Save To:");
		GridBagConstraints gbc_saveToLabel = new GridBagConstraints();
		gbc_saveToLabel.anchor = GridBagConstraints.EAST;
		gbc_saveToLabel.insets = new Insets(0, 0, 5, 5);
		gbc_saveToLabel.gridx = 0;
		gbc_saveToLabel.gridy = 2;
		sharePanel.add(saveToLabel, gbc_saveToLabel);

		JPanel panelSaveTo = new JPanel();
		GridBagConstraints gbc_panelSaveTo = new GridBagConstraints();
		gbc_panelSaveTo.insets = new Insets(0, 0, 5, 0);
		gbc_panelSaveTo.fill = GridBagConstraints.BOTH;
		gbc_panelSaveTo.gridx = 1;
		gbc_panelSaveTo.gridy = 2;
		sharePanel.add(panelSaveTo, gbc_panelSaveTo);
		GridBagLayout gbl_panelSaveTo = new GridBagLayout();
		gbl_panelSaveTo.columnWidths = new int[] { 0, 0, 0 };
		gbl_panelSaveTo.rowHeights = new int[] { 0, 0 };
		gbl_panelSaveTo.columnWeights = new double[] { 1.0, 0.0, Double.MIN_VALUE };
		gbl_panelSaveTo.rowWeights = new double[] { 0.0, Double.MIN_VALUE };
		panelSaveTo.setLayout(gbl_panelSaveTo);

		textField_saveTo = new JTextField();
		textField_saveTo.setEditable(false);
		textField_saveTo.setColumns(255);
		GridBagConstraints gbc_textField_saveTo = new GridBagConstraints();
		gbc_textField_saveTo.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField_saveTo.insets = new Insets(0, 0, 0, 5);
		gbc_textField_saveTo.gridx = 0;
		gbc_textField_saveTo.gridy = 0;
		panelSaveTo.add(textField_saveTo, gbc_textField_saveTo);

		JButton btnSaveTo = new JButton("...");
		btnSaveTo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				outFile = fileChooser(true);
				if (outFile != null)
					textField_saveTo.setText(outFile.getAbsolutePath());
				else
					textField_saveTo.setText("");
			}
		});
		btnSaveTo.setToolTipText("Select a file.");
		GridBagConstraints gbc_btnSaveTo = new GridBagConstraints();
		gbc_btnSaveTo.gridx = 1;
		gbc_btnSaveTo.gridy = 0;
		panelSaveTo.add(btnSaveTo, gbc_btnSaveTo);
		GridBagConstraints gbc_buttonShare = new GridBagConstraints();
		gbc_buttonShare.anchor = GridBagConstraints.WEST;
		gbc_buttonShare.gridx = 1;
		gbc_buttonShare.gridy = 3;
		sharePanel.add(buttonShare, gbc_buttonShare);

		
		// unlock file tab pane
		JPanel unlockPanel = new JPanel();
		tabbedPane.addTab("Unlock a File", null, unlockPanel, null);
		GridBagLayout gbl_unlockPanel = new GridBagLayout();
		gbl_unlockPanel.columnWidths = new int[] { 0, 0, 0 };
		gbl_unlockPanel.rowHeights = new int[] { 0, 0, 0, 0, 0, 0 };
		gbl_unlockPanel.columnWeights = new double[] { 0.0, 1.0, Double.MIN_VALUE };
		gbl_unlockPanel.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
		unlockPanel.setLayout(gbl_unlockPanel);

		JLabel lockerFileLabel = new JLabel("Locker File:");
		GridBagConstraints gbc_lockerFileLabel = new GridBagConstraints();
		gbc_lockerFileLabel.anchor = GridBagConstraints.EAST;
		gbc_lockerFileLabel.insets = new Insets(0, 0, 5, 5);
		gbc_lockerFileLabel.gridx = 0;
		gbc_lockerFileLabel.gridy = 0;
		unlockPanel.add(lockerFileLabel, gbc_lockerFileLabel);

		JPanel panel_LockerFile = new JPanel();
		GridBagConstraints gbc_panel_LockerFile = new GridBagConstraints();
		gbc_panel_LockerFile.insets = new Insets(0, 0, 5, 0);
		gbc_panel_LockerFile.fill = GridBagConstraints.BOTH;
		gbc_panel_LockerFile.gridx = 1;
		gbc_panel_LockerFile.gridy = 0;
		unlockPanel.add(panel_LockerFile, gbc_panel_LockerFile);
		GridBagLayout gbl_panel_LockerFile = new GridBagLayout();
		gbl_panel_LockerFile.columnWidths = new int[] { 0, 0, 0 };
		gbl_panel_LockerFile.rowHeights = new int[] { 0, 0 };
		gbl_panel_LockerFile.columnWeights = new double[] { 1.0, 0.0, Double.MIN_VALUE };
		gbl_panel_LockerFile.rowWeights = new double[] { 0.0, Double.MIN_VALUE };
		panel_LockerFile.setLayout(gbl_panel_LockerFile);

		textField_lockerFile = new JTextField();
		GridBagConstraints gbc_textField_lockerFile = new GridBagConstraints();
		gbc_textField_lockerFile.insets = new Insets(0, 0, 0, 5);
		gbc_textField_lockerFile.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField_lockerFile.gridx = 0;
		gbc_textField_lockerFile.gridy = 0;
		panel_LockerFile.add(textField_lockerFile, gbc_textField_lockerFile);
		textField_lockerFile.setColumns(10);

		JButton btnLockerFile = new JButton("...");
		btnLockerFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				lockerFile = fileChooser(false);
				if (lockerFile != null)
					textField_lockerFile.setText(lockerFile.getAbsolutePath());
				else
					textField_lockerFile.setText("");
			}
		});
		GridBagConstraints gbc_btnLockerFile = new GridBagConstraints();
		gbc_btnLockerFile.gridx = 1;
		gbc_btnLockerFile.gridy = 0;
		panel_LockerFile.add(btnLockerFile, gbc_btnLockerFile);

		JLabel userIDLabel = new JLabel("User ID:");
		GridBagConstraints gbc_userIDLabel = new GridBagConstraints();
		gbc_userIDLabel.insets = new Insets(0, 0, 5, 5);
		gbc_userIDLabel.anchor = GridBagConstraints.EAST;
		gbc_userIDLabel.gridx = 0;
		gbc_userIDLabel.gridy = 1;
		unlockPanel.add(userIDLabel, gbc_userIDLabel);

		textField_userID = new JTextField();
		GridBagConstraints gbc_textField_userID = new GridBagConstraints();
		gbc_textField_userID.insets = new Insets(0, 0, 5, 0);
		gbc_textField_userID.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField_userID.gridx = 1;
		gbc_textField_userID.gridy = 1;
		unlockPanel.add(textField_userID, gbc_textField_userID);
		textField_userID.setColumns(10);

		JLabel privateKeyLabel = new JLabel("Private Key File:");
		GridBagConstraints gbc_privateKeyLabel = new GridBagConstraints();
		gbc_privateKeyLabel.insets = new Insets(0, 0, 5, 5);
		gbc_privateKeyLabel.gridx = 0;
		gbc_privateKeyLabel.gridy = 2;
		unlockPanel.add(privateKeyLabel, gbc_privateKeyLabel);

		JPanel panelPrivateKey = new JPanel();
		GridBagConstraints gbc_panelPrivateKey = new GridBagConstraints();
		gbc_panelPrivateKey.insets = new Insets(0, 0, 5, 0);
		gbc_panelPrivateKey.fill = GridBagConstraints.BOTH;
		gbc_panelPrivateKey.gridx = 1;
		gbc_panelPrivateKey.gridy = 2;
		unlockPanel.add(panelPrivateKey, gbc_panelPrivateKey);
		GridBagLayout gbl_panelPrivateKey = new GridBagLayout();
		gbl_panelPrivateKey.columnWidths = new int[] { 0, 0, 0 };
		gbl_panelPrivateKey.rowHeights = new int[] { 0, 0 };
		gbl_panelPrivateKey.columnWeights = new double[] { 1.0, 0.0, Double.MIN_VALUE };
		gbl_panelPrivateKey.rowWeights = new double[] { 0.0, Double.MIN_VALUE };
		panelPrivateKey.setLayout(gbl_panelPrivateKey);

		textField_privateKey = new JTextField();
		textField_privateKey.setColumns(10);
		GridBagConstraints gbc_textField_privateKey = new GridBagConstraints();
		gbc_textField_privateKey.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField_privateKey.insets = new Insets(0, 0, 0, 5);
		gbc_textField_privateKey.gridx = 0;
		gbc_textField_privateKey.gridy = 0;
		panelPrivateKey.add(textField_privateKey, gbc_textField_privateKey);

		JButton btnPrivateKey = new JButton("...");
		btnPrivateKey.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				keyFile = fileChooser(false);
				if (keyFile != null)
					textField_privateKey.setText(keyFile.getAbsolutePath());
				else
					textField_privateKey.setText("");
			}
		});
		GridBagConstraints gbc_btnPrivateKey = new GridBagConstraints();
		gbc_btnPrivateKey.gridx = 1;
		gbc_btnPrivateKey.gridy = 0;
		panelPrivateKey.add(btnPrivateKey, gbc_btnPrivateKey);

		JLabel outputLabel = new JLabel("Output To:");
		GridBagConstraints gbc_outputLabel = new GridBagConstraints();
		gbc_outputLabel.anchor = GridBagConstraints.EAST;
		gbc_outputLabel.insets = new Insets(0, 0, 5, 5);
		gbc_outputLabel.gridx = 0;
		gbc_outputLabel.gridy = 3;
		unlockPanel.add(outputLabel, gbc_outputLabel);

		JPanel panelOutput = new JPanel();
		GridBagConstraints gbc_panelOutput = new GridBagConstraints();
		gbc_panelOutput.insets = new Insets(0, 0, 5, 0);
		gbc_panelOutput.fill = GridBagConstraints.BOTH;
		gbc_panelOutput.gridx = 1;
		gbc_panelOutput.gridy = 3;
		unlockPanel.add(panelOutput, gbc_panelOutput);
		GridBagLayout gbl_panelOutput = new GridBagLayout();
		gbl_panelOutput.columnWidths = new int[] { 0, 0, 0 };
		gbl_panelOutput.rowHeights = new int[] { 0, 0 };
		gbl_panelOutput.columnWeights = new double[] { 1.0, 0.0, Double.MIN_VALUE };
		gbl_panelOutput.rowWeights = new double[] { 0.0, Double.MIN_VALUE };
		panelOutput.setLayout(gbl_panelOutput);

		textField_output = new JTextField();
		textField_output.setColumns(10);
		GridBagConstraints gbc_textField_output = new GridBagConstraints();
		gbc_textField_output.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField_output.insets = new Insets(0, 0, 0, 5);
		gbc_textField_output.gridx = 0;
		gbc_textField_output.gridy = 0;
		panelOutput.add(textField_output, gbc_textField_output);

		JButton btnOutput = new JButton("...");
		btnOutput.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				outFile = fileChooser(true);
				if (outFile != null)
					textField_output.setText(outFile.getAbsolutePath());
				else
					textField_output.setText("");
			}
		});
		GridBagConstraints gbc_btnOutput = new GridBagConstraints();
		gbc_btnOutput.gridx = 1;
		gbc_btnOutput.gridy = 0;
		panelOutput.add(btnOutput, gbc_btnOutput);

		JButton btnUnlock = new JButton("Unlock");
		btnUnlock.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					EncryptedSharedFile sharedFile = EncryptedSharedFile.load(lockerFile);
					byte[] key = Files.readAllBytes(keyFile.toPath());

					byte[] priv = EncryptedFileShareApp.class.getResourceAsStream("/encryption/private.rsa")
							.readAllBytes();

					SharedFileContents contents = sharedFile.decryptForUser(textField_userID.getText(), key, priv);
					File output = new File(outFile, contents.getFileName());
					FileOutputStream fos = new FileOutputStream(output);
					fos.write(contents.getContents());
					fos.flush();
					fos.close();
					JOptionPane.showMessageDialog(frmEncryptedFileShare,
							"Your shared file has been decrypted to " + output.getAbsolutePath() + ".", "Success",
							INFORMATION_MESSAGE);
				} catch (Exception e1) {
					JOptionPane.showMessageDialog(frmEncryptedFileShare, e1.getMessage(), "Unlock Error",
							ERROR_MESSAGE);
				}
			}
		});
		GridBagConstraints gbc_btnUnlock = new GridBagConstraints();
		gbc_btnUnlock.anchor = GridBagConstraints.WEST;
		gbc_btnUnlock.gridx = 1;
		gbc_btnUnlock.gridy = 4;
		unlockPanel.add(btnUnlock, gbc_btnUnlock);

	}

	private File fileChooser(boolean directoriesOnly) {
		JFileChooser fileChoice = new JFileChooser();
		if (directoriesOnly)
			fileChoice.setFileSelectionMode(DIRECTORIES_ONLY);

		int returnVal = fileChoice.showOpenDialog(frmEncryptedFileShare);
		if (returnVal == APPROVE_OPTION) {
			return fileChoice.getSelectedFile();
		}

		return null;
	}

}
