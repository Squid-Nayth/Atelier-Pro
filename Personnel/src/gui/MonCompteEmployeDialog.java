package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.filechooser.FileNameExtensionFilter;

import personnel.Employe;
import personnel.GestionPersonnel;
import personnel.SauvegardeImpossible;

public class MonCompteEmployeDialog extends JDialog
{
    private static final long serialVersionUID = 1L;

    private final GestionPersonnel gestionPersonnel;
    private final Employe employe;

    private final JTextField nomField;
    private final JTextField prenomField;
    private final JTextField mailField;
    private final JPasswordField newPasswordField;
    private final JPasswordField confirmPasswordField;
    private JLabel photoLabel; //  Affiche la photo de profil
    private boolean isEditing = false;

    public MonCompteEmployeDialog(java.awt.Frame parent, GestionPersonnel gestionPersonnel, Employe employe)
    {
        super(parent);
        this.gestionPersonnel = gestionPersonnel;
        this.employe = employe;
        this.nomField = new JTextField(20);
        this.prenomField = new JTextField(20);
        this.mailField = new JTextField(20);
        this.newPasswordField = new JPasswordField(20);
        this.confirmPasswordField = new JPasswordField(20);

        setTitle("M2L — Mon profil");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);
        setContentPane(buildContent());
        populateFields();       //  Rempli les champs avec les données actuelles
        setEditingMode(false);  //  Lecture seule par défaut
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

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        //  Photo de profil centrée en haut du formulaire
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;

        JPanel photoPanel = new JPanel(new BorderLayout(0, 6));
        photoPanel.setOpaque(false);

        photoLabel = new JLabel();
        photoLabel.setPreferredSize(new Dimension(90, 90));
        photoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        photoLabel.setBorder(BorderFactory.createLineBorder(new Color(200, 210, 220), 2));
        refreshPhoto(); //  Charge la photo au démarrage

        // Bouton pour changer la photo
        JButton changerPhotoButton = new JButton("Changer la photo");
        changerPhotoButton.setFont(changerPhotoButton.getFont().deriveFont(11f));
        changerPhotoButton.addActionListener(e -> choisirPhoto());

        photoPanel.add(photoLabel, BorderLayout.CENTER);
        photoPanel.add(changerPhotoButton, BorderLayout.SOUTH);
        card.add(photoPanel, gbc);

        // Champs du formulaire
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.LINE_END;

        // Nom
        gbc.gridy++;
        gbc.gridx = 0;
        gbc.weightx = 0;
        card.add(new JLabel("Nom :"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        card.add(nomField, gbc);

        // Prénom
        gbc.gridy++;
        gbc.gridx = 0;
        gbc.weightx = 0;
        card.add(new JLabel("Prénom :"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        card.add(prenomField, gbc);

        // Mail
        gbc.gridy++;
        gbc.gridx = 0;
        gbc.weightx = 0;
        card.add(new JLabel("Mail :"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        card.add(mailField, gbc);

        // Nouveau mot de passe
        gbc.gridy++;
        gbc.gridx = 0;
        gbc.weightx = 0;
        card.add(new JLabel("Nouveau mot de passe :"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        card.add(newPasswordField, gbc);

        // Confirmation du mot de passe
        gbc.gridy++;
        gbc.gridx = 0;
        gbc.weightx = 0;
        card.add(new JLabel("Confirmer le mot de passe :"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        card.add(confirmPasswordField, gbc);

        return card;
    }

    private JPanel buildButtonPanel()
    {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        panel.setOpaque(false);

        JButton editButton = new JButton("Modifier");
        JButton saveButton = new JButton("Enregistrer");
        JButton cancelButton = new JButton("Fermer");

        editButton.addActionListener(e -> {
            setEditingMode(true);
            editButton.setVisible(false);
            saveButton.setVisible(true);
            cancelButton.setText("Annuler");
        });

        //  Enregistrer caché par défaut, visible uniquement en mode édition
        saveButton.setVisible(false);
        saveButton.addActionListener(e -> saveChanges(editButton, saveButton, cancelButton));

        cancelButton.addActionListener(e -> {
            if (isEditing) {
                // Annuler les modifications : on remet les valeurs d'origine
                setEditingMode(false);
                populateFields();
                editButton.setVisible(true);
                saveButton.setVisible(false);
                cancelButton.setText("Fermer");
            } else {
                dispose();
            }
        });

        panel.add(editButton);
        panel.add(saveButton);
        panel.add(cancelButton);
        return panel;
    }

    //  Remplit les champs avec les données actuelles de l'employé
    private void populateFields()
    {
        nomField.setText(employe.getNom());
        prenomField.setText(employe.getPrenom());
        mailField.setText(employe.getMail());
        newPasswordField.setText("");
        confirmPasswordField.setText("");
    }

    //  Active ou désactive l'édition des champs
    private void setEditingMode(boolean editing)
    {
        isEditing = editing;
        nomField.setEditable(editing);
        prenomField.setEditable(editing);
        mailField.setEditable(editing);
        newPasswordField.setEditable(editing);
        confirmPasswordField.setEditable(editing);
    }

    /**
     * Charge et affiche la photo de profil.
     * Priorité : photo personnalisée > photo par défaut > texte "?"
     */
    private void refreshPhoto()
    {
        String path = employe.getPhotoPath();
        ImageIcon icon = null;

        // Photo personnalisée si elle existe
        if (path != null && !path.isBlank())
        {
            try { icon = new ImageIcon(path); } catch (Exception e) { icon = null; }
        }

        // Sinon photo par défaut dans assets/
        if (icon == null)
        {
            URL defaultUrl = getClass().getClassLoader().getResource("default_avatar.png");
            if (defaultUrl != null)
                icon = new ImageIcon(defaultUrl);
        }

        if (icon != null)
        {
            Image img = icon.getImage().getScaledInstance(86, 86, Image.SCALE_SMOOTH);
            photoLabel.setIcon(new ImageIcon(img));
            photoLabel.setText("");
        }
        else
        {
            // Aucune image trouvée : affichage texte
            photoLabel.setIcon(null);
            photoLabel.setText("?");
            photoLabel.setFont(photoLabel.getFont().deriveFont(40f));
        }
    }

    /**
     * Ouvre un sélecteur de fichier pour choisir une photo.
     * Copie l'image dans src/assets/photos/ et met à jour l'employé.
     */
    private void choisirPhoto()
    {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Choisir une photo de profil");
        chooser.setFileFilter(new FileNameExtensionFilter(
                "Images (jpg, png, gif)", "jpg", "jpeg", "png", "gif"));
        chooser.setAcceptAllFileFilterUsed(false);

        int result = chooser.showOpenDialog(this);
        if (result != JFileChooser.APPROVE_OPTION)
            return;

        File selectedFile = chooser.getSelectedFile();

        try
        {
            // Crée le dossier assets/photos/ s'il n'existe pas
            File destDir = new File("src/assets/photos");
            if (!destDir.exists())
                destDir.mkdirs();

            // Nom du fichier basé sur l'id de l'employé pour éviter les doublons
            String ext = selectedFile.getName().substring(selectedFile.getName().lastIndexOf('.'));
            String newFileName = "avatar_" + employe.getId() + ext;
            File destFile = new File(destDir, newFileName);

            // Copie du fichier
            Files.copy(selectedFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

            //  Met à jour le chemin de la photo dans l'objet Employe
            employe.setPhotoPath(destFile.getAbsolutePath());
            refreshPhoto();

            JOptionPane.showMessageDialog(this,
                    "Photo mise à jour avec succès !",
                    "Succès",
                    JOptionPane.INFORMATION_MESSAGE);
        }
        catch (IOException e)
        {
            JOptionPane.showMessageDialog(this,
                    "Impossible de copier la photo : " + e.getMessage(),
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void saveChanges(JButton editButton, JButton saveButton, JButton cancelButton)
    {
        try
        {
            //  Mise à jour des champs de base
            employe.setNom(nomField.getText().trim());
            employe.setPrenom(prenomField.getText().trim());
            employe.setMail(mailField.getText().trim());

            //  Changement de mot de passe uniquement si rempli
            String newPwd = new String(newPasswordField.getPassword());
            String confirmPwd = new String(confirmPasswordField.getPassword());

            if (!newPwd.isEmpty())
            {
                // Vérification que les deux champs correspondent
                if (!newPwd.equals(confirmPwd))
                {
                    JOptionPane.showMessageDialog(this,
                            "Les mots de passe ne correspondent pas.",
                            "Erreur",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }
                employe.setPassword(newPwd);
            }

            gestionPersonnel.sauvegarder();

            JOptionPane.showMessageDialog(this,
                    "Profil mis à jour avec succès.",
                    "Succès",
                    JOptionPane.INFORMATION_MESSAGE);

            // Retour en mode lecture
            setEditingMode(false);
            populateFields();
            editButton.setVisible(true);
            saveButton.setVisible(false);
            cancelButton.setText("Fermer");
        }
        catch (SauvegardeImpossible e)
        {
            JOptionPane.showMessageDialog(this,
                    "Impossible d'enregistrer les modifications.",
                    "Erreur de sauvegarde",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}