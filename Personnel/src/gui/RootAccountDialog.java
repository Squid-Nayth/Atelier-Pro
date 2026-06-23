package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import personnel.Employe;
import personnel.GestionPersonnel;
import personnel.SauvegardeImpossible;

public class RootAccountDialog extends JDialog
{
    private static final long serialVersionUID = 1L;

    private final GestionPersonnel gestionPersonnel;
    private final Employe root;
    private final JTextField nomField;
    private final JTextField prenomField;
    private final JTextField mailField;
    private final JPasswordField passwordField;
    private final JPasswordField newPasswordField;
    private boolean isEditing = false;

    public RootAccountDialog(java.awt.Frame parent, GestionPersonnel gestionPersonnel)
    {
        super(parent);
        this.gestionPersonnel = gestionPersonnel;
        this.root = gestionPersonnel.getRoot();
        this.nomField = new JTextField(20);
        this.prenomField = new JTextField(20);
        this.mailField = new JTextField(20);
        this.passwordField = new JPasswordField(20);
        this.newPasswordField = new JPasswordField(20);

        setTitle("M2L — Mon compte (administrateur root)");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);
        setContentPane(buildContent());

        populateFields();
        setEditingMode(false);

        pack();
        setLocationRelativeTo(parent);
        setModal(true);
    }

    private JPanel buildContent()
    {
        JPanel root = new JPanel(new BorderLayout(0, 10));
        root.setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));
        root.setBackground(new Color(245, 247, 250));

        root.add(buildFormPanel(), BorderLayout.CENTER);
        root.add(buildButtonPanel(), BorderLayout.SOUTH);
        return root;
    }

    private JPanel buildFormPanel()
    {
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(160, 173, 190)),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)));

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(8, 8, 8, 8);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.LINE_END;

        JLabel titleLabel = new JLabel("Compte administrateur");
        titleLabel.setFont(titleLabel.getFont().deriveFont(14f));
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = 2;
        constraints.anchor = GridBagConstraints.LINE_START;
        card.add(titleLabel, constraints);

        constraints.gridwidth = 1;
        constraints.anchor = GridBagConstraints.LINE_END;

        // Nom
        constraints.gridy++;
        constraints.gridx = 0;
        card.add(new JLabel("Nom :"), constraints);
        constraints.gridx = 1;
        constraints.weightx = 1.0;
        nomField.setEditable(false);
        card.add(nomField, constraints);

        // Prénom
        constraints.gridy++;
        constraints.gridx = 0;
        constraints.weightx = 0;
        card.add(new JLabel("Prénom :"), constraints);
        constraints.gridx = 1;
        constraints.weightx = 1.0;
        prenomField.setEditable(false);
        card.add(prenomField, constraints);

        // Mail
        constraints.gridy++;
        constraints.gridx = 0;
        constraints.weightx = 0;
        card.add(new JLabel("Mail :"), constraints);
        constraints.gridx = 1;
        constraints.weightx = 1.0;
        mailField.setEditable(false);
        card.add(mailField, constraints);

        // Password actuel
        constraints.gridy++;
        constraints.gridx = 0;
        constraints.weightx = 0;
        card.add(new JLabel("Mot de passe :"), constraints);
        constraints.gridx = 1;
        constraints.weightx = 1.0;
        passwordField.setEditable(false);
        card.add(passwordField, constraints);

        // Nouveau password
        constraints.gridy++;
        constraints.gridx = 0;
        constraints.weightx = 0;
        JLabel newPwdLabel = new JLabel("Nouveau mot de passe :");
        newPwdLabel.setVisible(false);
        card.add(newPwdLabel, constraints);
        constraints.gridx = 1;
        constraints.weightx = 1.0;
        newPasswordField.setVisible(false);
        card.add(newPasswordField, constraints);

        return card;
    }

    private JPanel buildButtonPanel()
    {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setOpaque(false);

        JButton editButton = new JButton("Modifier");
        JButton saveButton = new JButton("Enregistrer");
        JButton cancelButton = new JButton("Annuler");

        editButton.addActionListener(event -> {
            toggleEditMode();
            editButton.setVisible(false);      // ✅ Cache "Modifier"
            saveButton.setVisible(true);       // ✅ Affiche "Enregistrer"
            cancelButton.setText("Annuler");
        });

        saveButton.addActionListener(event -> saveChanges());
        saveButton.setVisible(false);

        cancelButton.addActionListener(event -> {
            if (isEditing) {
                toggleEditMode();
                editButton.setVisible(true);   // ✅ Réaffiche "Modifier"
                saveButton.setVisible(false);  // ✅ Cache "Enregistrer"
                populateFields();              // ✅ Réinitialise les champs
            }
            dispose();
        });

        buttonPanel.add(editButton);
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        return buttonPanel;
    }

    private void toggleEditMode()
    {
        isEditing = !isEditing;
        nomField.setEditable(isEditing);
        prenomField.setEditable(isEditing);
        mailField.setEditable(isEditing);
    }


    private void populateFields()
    {
        if (root != null)
        {
            nomField.setText(root.getNom());
            prenomField.setText(root.getPrenom());
            mailField.setText(root.getMail());
            passwordField.setText(root.getPassword());
        }
    }

    private void setEditingMode(boolean editing)
    {
        isEditing = editing;
        nomField.setEditable(editing);
        prenomField.setEditable(editing);
        mailField.setEditable(editing);
    }

    private void saveChanges()
    {
        if (root == null)
            return;

        try
        {
            root.setNom(nomField.getText());
            root.setPrenom(prenomField.getText());
            root.setMail(mailField.getText());

            String newPassword = new String(newPasswordField.getPassword());
            if (!newPassword.isEmpty())
                root.setPassword(newPassword);

            // ✅ IMPORTANT : Appelle update() AVANT sauvegarder()
            gestionPersonnel.update(root);
            gestionPersonnel.sauvegarder();

            JOptionPane.showMessageDialog(
                    this,
                    "Modifications enregistrées avec succès.",
                    "Succès",
                    JOptionPane.INFORMATION_MESSAGE);

            populateFields();
            setEditingMode(false);
            newPasswordField.setText("");
        }
        catch (SauvegardeImpossible exception)
        {
            JOptionPane.showMessageDialog(
                    this,
                    "Impossible d'enregistrer les modifications.",
                    "Erreur de sauvegarde",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

}

