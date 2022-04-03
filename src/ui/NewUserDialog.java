package ui;

import static javax.swing.JFileChooser.APPROVE_OPTION;
import static javax.swing.JOptionPane.ERROR_MESSAGE;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import encryption.User;
/**

* @author Ioanna Pashalidi Kozelj - nnp18llf
 * 
 * prompts user to get the details of the sharing user
 *  *
 */
public class NewUserDialog extends JDialog {

	private static final long serialVersionUID = 8756358888866345456L;

	private final JPanel contentPanel = new JPanel();
	private JTextField textField_userID;
	private JTextField textField_publicKey;	
	
	private User user;
	private File file;

	/**

* Create the dialog.
	 */
	public NewUserDialog() {
		setTitle("Add New User to Share");
		setBounds(100, 100, 450, 300);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		GridBagLayout gbl_contentPanel = new GridBagLayout();
		gbl_contentPanel.columnWidths = new int[]{0, 0, 0};
		gbl_contentPanel.rowHeights = new int[]{0, 0, 0, 0, 0};
		gbl_contentPanel.columnWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
		gbl_contentPanel.rowWeights = new double[]{1.0, 0.0, 0.0, 1.0, Double.MIN_VALUE};
		contentPanel.setLayout(gbl_contentPanel);
		{
			JLabel userLabel = new JLabel("User ID:");
			GridBagConstraints gbc_userLabel = new GridBagConstraints();
			gbc_userLabel.anchor = GridBagConstraints.EAST;
			gbc_userLabel.insets = new Insets(0, 0, 5, 5);
			gbc_userLabel.gridx = 0;
			gbc_userLabel.gridy = 1;
			contentPanel.add(userLabel, gbc_userLabel);
		}
		{
			textField_userID = new JTextField();
			GridBagConstraints gbc_textField_userID = new GridBagConstraints();
			gbc_textField_userID.anchor = GridBagConstraints.SOUTH;
			gbc_textField_userID.insets = new Insets(0, 0, 5, 0);
			gbc_textField_userID.fill = GridBagConstraints.HORIZONTAL;
			gbc_textField_userID.gridx = 1;
			gbc_textField_userID.gridy = 1;
			contentPanel.add(textField_userID, gbc_textField_userID);
			textField_userID.setColumns(10);
		}
		{
			JLabel publicKeyLabel = new JLabel("Public Key File:");
			GridBagConstraints gbc_publicKeyLabel = new GridBagConstraints();
			gbc_publicKeyLabel.anchor = GridBagConstraints.EAST;
			gbc_publicKeyLabel.insets = new Insets(0, 0, 5, 5);
			gbc_publicKeyLabel.gridx = 0;
			gbc_publicKeyLabel.gridy = 2;
			contentPanel.add(publicKeyLabel, gbc_publicKeyLabel);
		}
		{
			JPanel panel = new JPanel();
			GridBagConstraints gbc_panel = new GridBagConstraints();
			gbc_panel.insets = new Insets(0, 0, 5, 0);
			gbc_panel.fill = GridBagConstraints.BOTH;
			gbc_panel.gridx = 1;
			gbc_panel.gridy = 2;
			contentPanel.add(panel, gbc_panel);
			GridBagLayout gbl_panel = new GridBagLayout();
			gbl_panel.columnWidths = new int[]{0, 0, 0};
			gbl_panel.rowHeights = new int[]{0, 0};
			gbl_panel.columnWeights = new double[]{1.0, 0.0, Double.MIN_VALUE};
			gbl_panel.rowWeights = new double[]{0.0, Double.MIN_VALUE};
			panel.setLayout(gbl_panel);
			{
				textField_publicKey = new JTextField();
				GridBagConstraints gbc_textField_publicKey = new GridBagConstraints();
				gbc_textField_publicKey.insets = new Insets(0, 0, 0, 5);
				gbc_textField_publicKey.fill = GridBagConstraints.HORIZONTAL;
				gbc_textField_publicKey.gridx = 0;
				gbc_textField_publicKey.gridy = 0;
				panel.add(textField_publicKey, gbc_textField_publicKey);
				textField_publicKey.setColumns(10);
			}
			{
				JButton btnPublicKey = new JButton("...");
				btnPublicKey.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						JFileChooser fileChoice = new JFileChooser();
						if (file != null) {
							fileChoice.setCurrentDirectory(file.getParentFile());
						}
						int returnVal = fileChoice.showOpenDialog(NewUserDialog.this);
					    if(returnVal == APPROVE_OPTION) {
					    	file = fileChoice.getSelectedFile();
					    	textField_publicKey.setText(file.getAbsolutePath());
					    }
					}
				});
				GridBagConstraints gbc_btnPublicKey = new GridBagConstraints();
				gbc_btnPublicKey.gridx = 1;
				gbc_btnPublicKey.gridy = 0;
				panel.add(btnPublicKey, gbc_btnPublicKey);
			}
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						try {
							user = new User(Files.readAllBytes(file.toPath()),
									textField_userID.getText());
							
							NewUserDialog.this.setVisible(false);
						} catch (IOException e1) {
							JOptionPane.showMessageDialog(null, "An error occurred reading the file.", "Error", ERROR_MESSAGE);
							return;
						}
					}
				});
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						user = null;
						NewUserDialog.this.setVisible(false);
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
	}
	
	/**

* @return user details
	 */
	public User getUserDetails() {
		return user;
	}

}
