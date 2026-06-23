package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

import personnel.Employe;
import personnel.GestionPersonnel;
import personnel.Ligue;
import personnel.SauvegardeImpossible;

public class LiguesFrame extends JFrame
{
    private static final long serialVersionUID = 1L;

    private final GestionPersonnel gestionPersonnel;
    private final DefaultListModel<Ligue> ligueListModel;
    private final JList<Ligue> ligueList;
    private final JButton modifierButton;
    private final JButton supprimerButton;
    private final JLabel statusLabel;

    public LiguesFrame(GestionPersonnel gestionPersonnel)
    {
        super("M2L - Gestion du personnel des ligues");
        this.gestionPersonnel = gestionPersonnel;
        this.ligueListModel = new DefaultListModel<>();
        this.ligueList = new JList<>(ligueListModel);
        this.modifierButton = new JButton("Modifier");
        this.supprimerButton = new JButton("Supprimer");
        this.statusLabel = new JLabel();

        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent event)
            {
                closeApplication();
            }
        });
        setJMenuBar(buildMenuBar());
        setContentPane(buildContent());
        setMinimumSize(new Dimension(700, 430));
        setPreferredSize(new Dimension(760, 470));
        refreshLigues();
        updateSelectionState();
        pack();
        setLocationRelativeTo(null);
    }

    private JMenuBar buildMenuBar()
    {
        JMenuBar menuBar = new JMenuBar();

        JMenu fichierMenu = new JMenu("Fichier");
        JMenuItem quitterItem = new JMenuItem("Quitter");
        quitterItem.addActionListener(event -> closeApplication());
        fichierMenu.add(quitterItem);

        JMenu aideMenu = new JMenu("Aide");
        JMenuItem aproposItem = new JMenuItem("A propos");
        aproposItem.addActionListener(event -> JOptionPane.showMessageDialog(
                this,
                "Gestion du personnel des ligues M2L\nVersion 1.0",
                "A propos",
                JOptionPane.INFORMATION_MESSAGE));
        aideMenu.add(aproposItem);

        menuBar.add(fichierMenu);
        menuBar.add(aideMenu);
        return menuBar;
    }

    private JPanel buildContent()
    {
        JPanel root = new JPanel(new BorderLayout(0, 10));
        root.setBorder(BorderFactory.createEmptyBorder(14, 14, 10, 14));
        root.setBackground(new Color(245, 247, 250));

        root.add(buildHeader(), BorderLayout.NORTH);
        root.add(buildCenterPanel(), BorderLayout.CENTER);
        root.add(buildFooter(), BorderLayout.SOUTH);
        return root;
    }

    private JPanel buildHeader()
    {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JLabel title = new JLabel("Ligues enregistrées");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 22f));

        JLabel subtitle = new JLabel("Accès rapide à la gestion principale des ligues");
        subtitle.setForeground(new Color(90, 102, 120));

        header.add(title, BorderLayout.NORTH);
        header.add(subtitle, BorderLayout.SOUTH);
        return header;
    }

    private JPanel buildCenterPanel()
    {
        JPanel center = new JPanel(new BorderLayout(0, 10));
        center.setOpaque(false);

        ligueList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        ligueList.setVisibleRowCount(10);
        ligueList.setFixedCellHeight(30);
        ligueList.addListSelectionListener(event -> updateSelectionState());
        // ✅ MODIFIÉ : Double-clic ouvre la fenêtre de détail
        ligueList.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent event)
            {
                if (event.getClickCount() == 2 && ligueList.getSelectedValue() != null)
                    showLigueDetailFrame();
            }
        });

        JScrollPane scrollPane = new JScrollPane(ligueList);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(188, 194, 204)));

        center.add(scrollPane, BorderLayout.CENTER);
        center.add(buildButtonsPanel(), BorderLayout.SOUTH);
        return center;
    }

    private JPanel buildButtonsPanel()
    {
        JPanel buttonsPanel = new JPanel(new BorderLayout());
        buttonsPanel.setOpaque(false);

        JPanel leftButtons = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        leftButtons.setOpaque(false);

        JButton ajouterButton = new JButton("Ajouter une ligue");
        ajouterButton.addActionListener(event -> addLigue());
        // ✅ MODIFIÉ : Bouton Modifier ouvre la fenêtre de détail
        modifierButton.addActionListener(event -> showLigueDetailFrame());
        supprimerButton.addActionListener(event -> deleteSelectedLigue());

        leftButtons.add(ajouterButton);
        leftButtons.add(modifierButton);
        leftButtons.add(supprimerButton);

        JPanel rightButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        rightButtons.setOpaque(false);

        JButton monCompteButton = new JButton("Mon compte");
        monCompteButton.addActionListener(event -> showRootAccountDialog());
        rightButtons.add(monCompteButton);

        buttonsPanel.add(leftButtons, BorderLayout.WEST);
        buttonsPanel.add(rightButtons, BorderLayout.EAST);
        return buttonsPanel;
    }

    private JPanel buildFooter()
    {
        JPanel footer = new JPanel(new BorderLayout());
        footer.setOpaque(false);
        footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(214, 220, 228)));

        Employe root = gestionPersonnel.getRoot();
        String nomCompte = root == null ? "inconnu" : root.getNom();
        statusLabel.setHorizontalAlignment(SwingConstants.LEFT);
        statusLabel.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));
        statusLabel.setForeground(new Color(98, 104, 114));
        statusLabel.setText("Connecté en tant qu'administrateur " + nomCompte);

        footer.add(statusLabel, BorderLayout.CENTER);
        return footer;
    }

    private void refreshLigues()
    {
        ligueListModel.clear();
        for (Ligue ligue : gestionPersonnel.getLigues())
            ligueListModel.addElement(ligue);

        if (!ligueListModel.isEmpty())
            ligueList.setSelectedIndex(0);
    }

    private void updateSelectionState()
    {
        boolean hasSelection = ligueList.getSelectedValue() != null;
        modifierButton.setEnabled(hasSelection);
        supprimerButton.setEnabled(hasSelection);
    }

    private void addLigue()
    {
        String nom = JOptionPane.showInputDialog(
                this,
                "Nom de la nouvelle ligue :",
                "Ajouter une ligue",
                JOptionPane.PLAIN_MESSAGE);

        if (nom == null)
            return;

        nom = nom.trim();
        if (nom.isEmpty())
        {
            JOptionPane.showMessageDialog(
                    this,
                    "Le nom de la ligue ne peut pas être vide.",
                    "Saisie invalide",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try
        {
            Ligue ligue = gestionPersonnel.addLigue(nom);
            persistChanges();
            refreshLigues();
            ligueList.setSelectedValue(ligue, true);
        }
        catch (SauvegardeImpossible exception)
        {
            showSaveError();
        }
    }

    private void deleteSelectedLigue()
    {
        Ligue ligue = ligueList.getSelectedValue();
        if (ligue == null)
            return;

        int confirmation = JOptionPane.showConfirmDialog(
                this,
                "Supprimer la ligue \"" + ligue.getNom() + "\" ?",
                "Confirmation",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirmation != JOptionPane.YES_OPTION)
            return;

        try
        {
            ligue.remove();
            persistChanges();
            refreshLigues();
        }
        catch (SauvegardeImpossible exception)
        {
            showSaveError();
        }
    }

    // ✅ NOUVEAU : Ouvre la fenêtre de détail de la ligue
    private void showLigueDetailFrame()
    {
        Ligue ligue = ligueList.getSelectedValue();
        if (ligue == null)
            return;

        LigueDetailFrame detailFrame = new LigueDetailFrame(gestionPersonnel, ligue);
        detailFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        detailFrame.setVisible(true);
        
        refreshLigues();
    }

    // ✅ NOUVEAU : Ouvre le dialog du compte root
    private void showRootAccountDialog()
    {
        RootAccountDialog dialog = new RootAccountDialog(this, gestionPersonnel);
        dialog.setVisible(true);
        // Rafraîchir le statut après modification
        Employe root = gestionPersonnel.getRoot();
        if (root != null)
            statusLabel.setText("Connecté en tant qu'administrateur " + root.getNom());
    }

    private void closeApplication()
    {
        try
        {
            gestionPersonnel.sauvegarder();
            dispose();
        }
        catch (SauvegardeImpossible exception)
        {
            showSaveError();
            int confirmation = JOptionPane.showConfirmDialog(
                    this,
                    "Quitter malgré l'échec de la sauvegarde ?",
                    "Confirmation",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);
            if (confirmation == JOptionPane.YES_OPTION)
                dispose();
        }
    }

    private void persistChanges() throws SauvegardeImpossible
    {
        gestionPersonnel.sauvegarder();
    }

    private void showSaveError()
    {
        JOptionPane.showMessageDialog(
                this,
                "Impossible d'enregistrer les changements.",
                "Erreur de sauvegarde",
                JOptionPane.ERROR_MESSAGE);
    }
}
