package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
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

public class MotDePasseOublieDialog extends JDialog
{
    private static final long serialVersionUID = 1L;

    private final GestionPersonnel gestionPersonnel;

    // Étape 1 : saisie du mail
    private final JTextField mailField;

    // Étape 2 : saisie du nouveau mot de passe
    private final JPasswordField newPasswordField;
    private final JPasswordField confirmPasswordField;

    //  L'employé trouvé par mail (null tant qu'on n'a pas vérifié)
    private Employe employeTrouve = null;

    public MotDePasseOublieDialog(java.awt.Frame parent, GestionPersonnel gestionPersonnel)
    {
        super(parent);
        this.gestionPersonnel = gestionPersonnel;
        this.mailField = new JTextField(20);
        this.newPasswordField = new JPasswordField(20);
        this.confirmPasswordField = new JPasswordField(20);

        setTitle("M2L — Mot de passe oublié");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);
        setContentPane(buildContent());
        pack();
        setMinimumSize(new Dimension(420, 300));
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

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Titre
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.LINE_START;
        JLabel titleLabel = new JLabel("Réinitialisation du mot de passe");
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 14f));
        card.add(titleLabel, gbc);

        // Explication
        gbc.gridy++;
        JLabel infoLabel = new JLabel("Entrez votre mail pour réinitialiser votre mot de passe.");
        infoLabel.setForeground(new Color(90, 102, 120));
        infoLabel.setFont(infoLabel.getFont().deriveFont(11f));
        card.add(infoLabel, gbc);

        // Champ mail
        gbc.gridy++;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.LINE_END;
        gbc.weightx = 0;
        card.add(new JLabel("Mail :"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        card.add(mailField, gbc);

        //  Champs nouveau mot de passe : cachés au départ
        // Ils s'affichent uniquement après vérification du mail

        gbc.gridy++;
        gbc.gridx = 0;
        gbc.weightx = 0;
        JLabel newPwdLabel = new JLabel("Nouveau mot de passe :");
        newPwdLabel.setVisible(false);
        card.add(newPwdLabel, gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        newPasswordField.setVisible(false);
        card.add(newPasswordField, gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        gbc.weightx = 0;
        JLabel confirmLabel = new JLabel("Confirmer :");
        confirmLabel.setVisible(false);
        card.add(confirmLabel, gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        confirmPasswordField.setVisible(false);
        card.add(confirmPasswordField, gbc);

        //  On stocke les labels pour pouvoir les afficher plus tard
        newPwdLabel.setName("newPwdLabel");
        confirmLabel.setName("confirmLabel");

        return card;
    }

    private JPanel buildButtonPanel()
    {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        panel.setOpaque(false);

        JButton verifierButton = new JButton("Vérifier le mail");
        JButton reinitButton = new JButton("Réinitialiser");
        JButton annulerButton = new JButton("Annuler");

        //  Étape 1 : vérifier que le mail existe en base
        verifierButton.addActionListener(e -> {
            String mail = mailField.getText().trim();

            if (mail.isEmpty())
            {
                JOptionPane.showMessageDialog(this,
                        "Veuillez entrer votre adresse mail.",
                        "Champ vide",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Recherche de l'employé par mail
            employeTrouve = gestionPersonnel.findByMail(mail);

            if (employeTrouve == null)
            {
                JOptionPane.showMessageDialog(this,
                        "Aucun compte trouvé avec ce mail.",
                        "Mail introuvable",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            //  Mail trouvé : on affiche les champs de nouveau mot de passe
            mailField.setEditable(false);
            verifierButton.setVisible(false);
            reinitButton.setVisible(true);
            afficherChampsMdp();

            JOptionPane.showMessageDialog(this,
                    "Compte trouvé pour " + employeTrouve.getPrenom() + " " + employeTrouve.getNom() + ".\nEntrez votre nouveau mot de passe.",
                    "Compte trouvé",
                    JOptionPane.INFORMATION_MESSAGE);
        });

        //  Étape 2 : réinitialiser le mot de passe
        reinitButton.setVisible(false);
        reinitButton.addActionListener(e -> {
            String newPwd = new String(newPasswordField.getPassword());
            String confirmPwd = new String(confirmPasswordField.getPassword());

            if (newPwd.isEmpty())
            {
                JOptionPane.showMessageDialog(this,
                        "Veuillez entrer un nouveau mot de passe.",
                        "Champ vide",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Vérification que les deux mots de passe correspondent
            if (!newPwd.equals(confirmPwd))
            {
                JOptionPane.showMessageDialog(this,
                        "Les mots de passe ne correspondent pas.",
                        "Erreur",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            try
            {
                //  Mise à jour du mot de passe en base
                employeTrouve.setPassword(newPwd);
                gestionPersonnel.sauvegarder();

                JOptionPane.showMessageDialog(this,
                        "Mot de passe réinitialisé avec succès !",
                        "Succès",
                        JOptionPane.INFORMATION_MESSAGE);
                dispose();
            }
            catch (SauvegardeImpossible ex)
            {
                JOptionPane.showMessageDialog(this,
                        "Impossible d'enregistrer le nouveau mot de passe.",
                        "Erreur de sauvegarde",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        annulerButton.addActionListener(e -> dispose());

        panel.add(verifierButton);
        panel.add(reinitButton);
        panel.add(annulerButton);
        return panel;
    }

    /**
     * Rend visibles les champs de saisie du nouveau mot de passe.
     * Appelée après vérification réussie du mail.
     */
    private void afficherChampsMdp()
    {
        newPasswordField.setVisible(true);
        confirmPasswordField.setVisible(true);

        // Rendre visibles les labels associés
        for (java.awt.Component comp : newPasswordField.getParent().getComponents())
            if (comp instanceof JLabel)
            {
                String name = comp.getName();
                if ("newPwdLabel".equals(name) || "confirmLabel".equals(name))
                    comp.setVisible(true);
            }

        pack(); //  Redimensionne la fenêtre pour afficher les nouveaux champs
    }
}