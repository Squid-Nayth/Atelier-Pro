package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

import personnel.Employe;
import personnel.GestionPersonnel;

public class ConnexionFrame extends JFrame
{
	private static final long serialVersionUID = 1L;

	private final GestionPersonnel gestionPersonnel;
	private final JTextField identifiantField;
	private final JPasswordField passwordField;

	public ConnexionFrame(GestionPersonnel gestionPersonnel)
	{
		super("M2L - Connexion");
		this.gestionPersonnel = gestionPersonnel;
		this.identifiantField = new JTextField(18);
		this.passwordField = new JPasswordField(18);

		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setResizable(false);
		setContentPane(buildContent());
		pack();
		setMinimumSize(new Dimension(420, 240));
		setLocationRelativeTo(null);
	}

	private JPanel buildContent()
	{
		JPanel root = new JPanel(new BorderLayout());
		root.setBackground(new Color(67, 115, 167));
		root.setBorder(BorderFactory.createEmptyBorder(18, 18, 14, 18));

		root.add(buildFormPanel(), BorderLayout.CENTER);
		root.add(buildFooter(), BorderLayout.SOUTH);
		return root;
	}

	private JPanel buildFormPanel()
	{
		JPanel card = new JPanel(new GridBagLayout());
		card.setBackground(Color.WHITE);
		card.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(new Color(160, 173, 190)),
				BorderFactory.createEmptyBorder(22, 28, 18, 28)));

		GridBagConstraints constraints = new GridBagConstraints();
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.gridwidth = 2;
		constraints.insets = new Insets(0, 0, 22, 0);

		JLabel title = new JLabel("Gestion du personnel des ligues");
		title.setFont(title.getFont().deriveFont(Font.BOLD, 24f));
		card.add(title, constraints);

		constraints.gridy++;
		constraints.gridwidth = 1;
		constraints.insets = new Insets(0, 0, 12, 10);
		constraints.anchor = GridBagConstraints.LINE_END;

		//  champ identifiant (mail pour employé, nom pour root)
		card.add(new JLabel("Identifiant :"), constraints);
		constraints.gridx = 1;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.weightx = 1.0;
		identifiantField.setToolTipText("Votre mail, ou \"root\" pour l'administrateur");
		identifiantField.addActionListener(new LoginAction());
		card.add(identifiantField, constraints);

		// Champ mot de passe (inchangé)
		constraints.gridx = 0;
		constraints.gridy++;
		constraints.weightx = 0;
		constraints.fill = GridBagConstraints.NONE;
		card.add(new JLabel("Mot de passe :"), constraints);
		constraints.gridx = 1;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.weightx = 1.0;
		passwordField.addActionListener(new LoginAction());
		card.add(passwordField, constraints);

		//  lien mot de passe oublié
		constraints.gridx = 0;
		constraints.gridy++;
		constraints.gridwidth = 2;
		constraints.weightx = 0;
		constraints.fill = GridBagConstraints.NONE;
		constraints.anchor = GridBagConstraints.CENTER;
		constraints.insets = new Insets(4, 0, 4, 0);
		JLabel forgotLabel = new JLabel("Mot de passe oublié ?");
		forgotLabel.setForeground(new Color(67, 115, 167));
		forgotLabel.setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.HAND_CURSOR));
		forgotLabel.addMouseListener(new java.awt.event.MouseAdapter() {
		    @Override
		    public void mouseClicked(java.awt.event.MouseEvent e) {
		        //  Ouvre le dialog de réinitialisation du mot de passe
		        new MotDePasseOublieDialog(ConnexionFrame.this, gestionPersonnel).setVisible(true);
		    }
		});
		card.add(forgotLabel, constraints);

		//  Le bouton doit être sur une ligne différente (gridy++)
		constraints.gridy++;
		constraints.insets = new Insets(6, 0, 8, 0);
		JButton loginButton = new JButton(new LoginAction());
		loginButton.setText("Se connecter");
		card.add(loginButton, constraints);

		return card;
	}

	private JLabel buildFooter()
	{
		JLabel footer = new JLabel(
				"\u00A9 2026, Lucresse Zeufack - Tous droits réservés",
				SwingConstants.CENTER);
		footer.setForeground(new Color(215, 225, 240));
		footer.setBorder(BorderFactory.createEmptyBorder(14, 0, 0, 0));
		return footer;
	}

	private void attemptLogin()
	{
		String identifiant = identifiantField.getText().trim();
	    String password = new String(passwordField.getPassword());

	    // Vérification que les champs ne sont pas vides
	    if (identifiant.isEmpty() || password.isEmpty())
	    {
	        JOptionPane.showMessageDialog(this,
	                "Veuillez remplir tous les champs.",
	                "Champs manquants",
	                JOptionPane.WARNING_MESSAGE);
	        return;
	    }

	    // ✅ Tentative connexion root : on compare le nom (pas le mail)
	    Employe root = gestionPersonnel.getRoot();
	    if (root != null && identifiant.equals(root.getNom()) && root.checkPassword(password))
	    {
	        LiguesFrame liguesFrame = new LiguesFrame(gestionPersonnel);
	        liguesFrame.setVisible(true);
	        dispose();
	        return;
	    }

	    //  Tentative connexion employé : on cherche par mail
	    Employe employe = gestionPersonnel.findByMail(identifiant);
	    if (employe != null && employe.checkPassword(password))
	    {
	        // Ouvre la vue employé (à créer ensuite)
	        EmployeAccueilFrame accueilFrame = new EmployeAccueilFrame(gestionPersonnel, employe);
	        accueilFrame.setVisible(true);
	        dispose();
	        return;
	    }

	    // Échec de la connexion
	    passwordField.setText("");
	    passwordField.requestFocusInWindow();
	    JOptionPane.showMessageDialog(this,
	            "Identifiant ou mot de passe incorrect.",
	            "Connexion refusée",
	            JOptionPane.ERROR_MESSAGE);
	}

	private final class LoginAction extends AbstractAction
	{
		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent event)
		{
			attemptLogin();
		}
	}
}