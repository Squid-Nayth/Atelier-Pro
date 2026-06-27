package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

import personnel.Employe;
import personnel.GestionPersonnel;
import personnel.SauvegardeImpossible;

public class EmployeAccueilFrame extends JFrame
{
    private static final long serialVersionUID = 1L;

    private final GestionPersonnel gestionPersonnel;
    private final Employe employe;
    private JLabel photoLabel; //  Label qui affiche la photo de profil

    public EmployeAccueilFrame(GestionPersonnel gestionPersonnel, Employe employe)
    {
        super("M2L - Espace de " + employe.getPrenom() + " " + employe.getNom());
        this.gestionPersonnel = gestionPersonnel;
        this.employe = employe;

        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

        // Fermeture propre : sauvegarde avant de quitter
        addWindowListener(new java.awt.event.WindowAdapter()
        {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e)
            {
                closeApplication();
            }
        });

        setContentPane(buildContent());
        setMinimumSize(new Dimension(500, 400));
        setPreferredSize(new Dimension(560, 440));
        pack();
        setLocationRelativeTo(null);
    }

    private JPanel buildContent()
    {
        JPanel root = new JPanel(new BorderLayout(0, 16));
        root.setBackground(new Color(245, 247, 250));
        root.setBorder(BorderFactory.createEmptyBorder(24, 24, 16, 24));

        root.add(buildProfilePanel(), BorderLayout.NORTH);
        root.add(buildActionsPanel(), BorderLayout.CENTER);
        root.add(buildFooter(), BorderLayout.SOUTH);
        return root;
    }

    //  Panneau du haut : photo + infos de l'employé connecté
    private JPanel buildProfilePanel()
    {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(160, 173, 190)),
                BorderFactory.createEmptyBorder(20, 24, 20, 24)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 8, 4, 8);

        // Photo de profil à gauche
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridheight = 4;
        gbc.anchor = GridBagConstraints.CENTER;
        photoLabel = new JLabel();
        photoLabel.setPreferredSize(new Dimension(80, 80));
        photoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        photoLabel.setBorder(BorderFactory.createLineBorder(new Color(200, 210, 220), 2));
        refreshPhoto(); // Charge la photo au démarrage
        panel.add(photoLabel, gbc);

        // Infos à droite
        gbc.gridheight = 1;
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.LINE_START;

        // Nom complet
        JLabel nomLabel = new JLabel(employe.getPrenom() + " " + employe.getNom());
        nomLabel.setFont(nomLabel.getFont().deriveFont(Font.BOLD, 16f));
        panel.add(nomLabel, gbc);

        // Mail
        gbc.gridy++;
        JLabel mailLabel = new JLabel(employe.getMail());
        mailLabel.setForeground(new Color(90, 102, 120));
        panel.add(mailLabel, gbc);

        // Ligue
        gbc.gridy++;
        String ligueInfo = employe.getLigue() != null ? "Ligue : " + employe.getLigue().getNom() : "";
        JLabel ligueLabel = new JLabel(ligueInfo);
        ligueLabel.setForeground(new Color(90, 102, 120));
        panel.add(ligueLabel, gbc);

        // Rôle (admin ou employé simple)
        gbc.gridy++;
        boolean estAdmin = employe.getLigue() != null && employe.estAdmin(employe.getLigue());
        JLabel roleLabel = new JLabel(estAdmin ? "Administrateur de la ligue" : "Employé");
        roleLabel.setForeground(estAdmin ? new Color(40, 120, 60) : new Color(90, 102, 120));
        roleLabel.setFont(roleLabel.getFont().deriveFont(Font.ITALIC));
        panel.add(roleLabel, gbc);

        return panel;
    }

    //  Panneau du milieu : boutons d'actions selon le rôle
    private JPanel buildActionsPanel()
    {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(6, 0, 6, 0);
        gbc.gridx = 0;
        gbc.gridy = 0;

        // Bouton Mon profil (toujours visible)
        JButton profilButton = new JButton("Mon profil");
        profilButton.setFont(profilButton.getFont().deriveFont(13f));
        profilButton.setPreferredSize(new Dimension(300, 40));
        profilButton.addActionListener(e -> ouvrirMonProfil());
        panel.add(profilButton, gbc);

        //  Bouton Gérer ma ligue : visible uniquement si l'employé est admin
        if (employe.getLigue() != null && employe.estAdmin(employe.getLigue()))
        {
            gbc.gridy++;
            JButton ligueButton = new JButton("Gérer ma ligue : " + employe.getLigue().getNom());
            ligueButton.setFont(ligueButton.getFont().deriveFont(13f));
            ligueButton.setPreferredSize(new Dimension(300, 40));
            ligueButton.addActionListener(e -> ouvrirGestionLigue());
            panel.add(ligueButton, gbc);
        }

        // Bouton Se déconnecter
        gbc.gridy++;
        gbc.insets = new Insets(20, 0, 6, 0);
        JButton deconnecterButton = new JButton("Se déconnecter");
        deconnecterButton.setForeground(new Color(180, 50, 50));
        deconnecterButton.addActionListener(e -> seDeconnecter());
        panel.add(deconnecterButton, gbc);

        return panel;
    }

    private JPanel buildFooter()
    {
        JPanel footer = new JPanel(new BorderLayout());
        footer.setOpaque(false);
        footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(214, 220, 228)));

        JLabel statusLabel = new JLabel("Connecté en tant que " + employe.getPrenom() + " " + employe.getNom());
        statusLabel.setForeground(new Color(98, 104, 114));
        statusLabel.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));
        footer.add(statusLabel, BorderLayout.CENTER);
        return footer;
    }

    /**
     * Charge et affiche la photo de profil.
     * Si l'employé a une photo personnalisée, on l'affiche.
     * Sinon on affiche la photo par défaut depuis assets/.
     * Si aucune image n'est trouvée, on affiche un emoji texte.
     */
    private void refreshPhoto()
    {
        String path = employe.getPhotoPath();
        ImageIcon icon = null;

        //  Photo personnalisée si définie
        if (path != null && !path.isBlank())
        {
            try { icon = new ImageIcon(path); } catch (Exception e) { icon = null; }
        }

        // ✅ Sinon photo par défaut dans assets/
        if (icon == null)
        {
            URL defaultUrl = getClass().getClassLoader().getResource("default_avatar.png");
            if (defaultUrl != null)
                icon = new ImageIcon(defaultUrl);
        }

        // ✅ Affichage de l'image redimensionnée
        if (icon != null)
        {
            Image img = icon.getImage().getScaledInstance(76, 76, Image.SCALE_SMOOTH);
            photoLabel.setIcon(new ImageIcon(img));
            photoLabel.setText("");
        }
        else
        {
            // Fallback si aucune image trouvée
            photoLabel.setIcon(null);
            photoLabel.setText("?");
            photoLabel.setFont(photoLabel.getFont().deriveFont(36f));
        }
    }

    private void ouvrirMonProfil()
    {
        // ✅ Ouvre le dialog de modification du profil (à créer ensuite)
        MonCompteEmployeDialog dialog = new MonCompteEmployeDialog(this, gestionPersonnel, employe);
        dialog.setVisible(true);
        // Rafraîchir la photo si elle a été modifiée
        refreshPhoto();
        revalidate();
        repaint();
    }

    private void ouvrirGestionLigue()
    {
        //  Ouvre la fenêtre de gestion de la ligue (déjà existante)
        LigueDetailFrame ligueFrame = new LigueDetailFrame(gestionPersonnel, employe.getLigue());
        ligueFrame.setVisible(true);
    }

    private void seDeconnecter()
    {
        // Sauvegarde et retour à l'écran de connexion
        try { gestionPersonnel.sauvegarder(); } catch (SauvegardeImpossible e) { /* ignore */ }
        new ConnexionFrame(gestionPersonnel).setVisible(true);
        dispose();
    }

    private void closeApplication()
    {
        try
        {
            gestionPersonnel.sauvegarder();
            dispose();
        }
        catch (SauvegardeImpossible e)
        {
            int confirmation = JOptionPane.showConfirmDialog(this,
                    "Quitter malgré l'échec de la sauvegarde ?",
                    "Confirmation",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);
            if (confirmation == JOptionPane.YES_OPTION)
                dispose();
        }
    }
}