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
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

import personnel.Employe;
import personnel.GestionPersonnel;
import personnel.Ligue;
import personnel.SauvegardeImpossible;

public class LigueDetailFrame extends JDialog
{
    private static final long serialVersionUID = 1L;

    private final GestionPersonnel gestionPersonnel;
    private final Ligue ligue;
    private final JTable employesTable;
    private final DefaultTableModel tableModel;
    private final JButton ajouterButton;
    private final JButton modifierButton;
    private final JButton supprimerButton;
    private final JButton changerAdminButton;

    public LigueDetailFrame(GestionPersonnel gestionPersonnel, Ligue ligue)
    {
        super();
        this.gestionPersonnel = gestionPersonnel;
        this.ligue = ligue;
        this.tableModel = new DefaultTableModel();
        this.employesTable = new JTable(tableModel);
        this.ajouterButton = new JButton("Ajouter un employé");
        this.modifierButton = new JButton("Modifier");
        this.supprimerButton = new JButton("Supprimer");
        this.changerAdminButton = new JButton("Changer l'administrateur");

        setTitle("M2L — " + ligue.getNom());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(true);
        setContentPane(buildContent());
        setMinimumSize(new Dimension(700, 450));
        setPreferredSize(new Dimension(800, 500));
        refreshEmployes();
        updateSelectionState();
        pack();
        setLocationRelativeTo(null);
        setModal(true);
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

        JLabel ligueLabel = new JLabel("Ligue : " + ligue.getNom());
        Employe admin = ligue.getAdministrateur();
        String adminText = admin != null ? " (admin : " + admin.getNom() + " " + admin.getPrenom() + ")" : "";
        ligueLabel.setText(ligueLabel.getText() + adminText);
        ligueLabel.setFont(ligueLabel.getFont().deriveFont(Font.BOLD, 16f));

        changerAdminButton.addActionListener(event -> changeAdministrateur());

        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        titlePanel.add(ligueLabel, BorderLayout.WEST);
        titlePanel.add(changerAdminButton, BorderLayout.EAST);

        JLabel subtitle = new JLabel("Employés de la ligue");
        subtitle.setForeground(new Color(90, 102, 120));

        header.add(titlePanel, BorderLayout.NORTH);
        header.add(subtitle, BorderLayout.SOUTH);
        return header;
    }

    private JPanel buildCenterPanel()
    {
        JPanel center = new JPanel(new BorderLayout(0, 10));
        center.setOpaque(false);

        initializeTable();

        employesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        employesTable.getSelectionModel().addListSelectionListener(event -> updateSelectionState());
        employesTable.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent event)
            {
                if (event.getClickCount() == 2 && employesTable.getSelectedRow() >= 0)
                    editSelectedEmploye();
            }
        });

        JScrollPane scrollPane = new JScrollPane(employesTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(188, 194, 204)));

        center.add(scrollPane, BorderLayout.CENTER);
        center.add(buildButtonsPanel(), BorderLayout.SOUTH);
        return center;
    }

    private void initializeTable()
    {
        tableModel.setColumnIdentifiers(new String[]{"Nom", "Prénom", "Mail", "Arrivée", "Départ"});
        refreshEmployes();
    }

    private JPanel buildButtonsPanel()
    {
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        buttonsPanel.setOpaque(false);

        ajouterButton.addActionListener(event -> addEmploye());
        modifierButton.addActionListener(event -> editSelectedEmploye());
        supprimerButton.addActionListener(event -> deleteSelectedEmploye());

        buttonsPanel.add(ajouterButton);
        buttonsPanel.add(modifierButton);
        buttonsPanel.add(supprimerButton);

        JButton renommerButton = new JButton("Renommer la ligue");
        renommerButton.addActionListener(event -> renameLigue());
        JButton supprimerLigueButton = new JButton("Supprimer la ligue");
        supprimerLigueButton.setForeground(new Color(200, 50, 50));
        supprimerLigueButton.addActionListener(event -> deleteLigue());

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        rightPanel.setOpaque(false);
        rightPanel.add(renommerButton);
        rightPanel.add(supprimerLigueButton);

        JPanel container = new JPanel(new BorderLayout());
        container.setOpaque(false);
        container.add(buttonsPanel, BorderLayout.WEST);
        container.add(rightPanel, BorderLayout.EAST);

        return container;
    }

    private JPanel buildFooter()
    {
        JPanel footer = new JPanel(new BorderLayout());
        footer.setOpaque(false);
        footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(214, 220, 228)));

        JButton retourButton = new JButton("Retour");
        retourButton.addActionListener(event -> dispose());

        JPanel footerContent = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footerContent.setOpaque(false);
        footerContent.add(retourButton);

        footer.add(footerContent, BorderLayout.EAST);
        return footer;
    }

    private void refreshEmployes()
    {
        tableModel.setRowCount(0);
        for (Employe employe : ligue.getEmployes())
        {
            tableModel.addRow(new Object[]{
                employe.getNom(),
                employe.getPrenom(),
                employe.getMail(),
                employe.getDateArrivee(),
                employe.getDateDepart()
            });
        }
    }

    private void updateSelectionState()
    {
        boolean hasSelection = employesTable.getSelectedRow() >= 0;
        modifierButton.setEnabled(hasSelection);
        supprimerButton.setEnabled(hasSelection);
    }

    private void addEmploye()
    {
        EmployeEditDialog dialog = new EmployeEditDialog(this, gestionPersonnel, ligue, null);
        dialog.setVisible(true);

        if (dialog.isValidated())
        {
            try
            {
                gestionPersonnel.sauvegarder();
                refreshEmployes();
            }
            catch (SauvegardeImpossible exception)
            {
                showSaveError();
            }
        }
    }

    private void editSelectedEmploye()
    {
        int row = employesTable.getSelectedRow();
        if (row < 0)
            return;

        Employe employe = getEmployeAtRow(row);
        if (employe == null)
            return;

        EmployeEditDialog dialog = new EmployeEditDialog(this, gestionPersonnel, ligue, employe);
        dialog.setVisible(true);

        if (dialog.isValidated())
        {
            try
            {
                gestionPersonnel.sauvegarder();
                refreshEmployes();
            }
            catch (SauvegardeImpossible exception)
            {
                showSaveError();
            }
        }
    }

    private void deleteSelectedEmploye()
    {
        int row = employesTable.getSelectedRow();
        if (row < 0)
            return;

        Employe employe = getEmployeAtRow(row);
        if (employe == null)
            return;

        int confirmation = JOptionPane.showConfirmDialog(
                this,
                "Supprimer l'employé \"" + employe.getNom() + " " + employe.getPrenom() + "\" ?",
                "Confirmation",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirmation != JOptionPane.YES_OPTION)
            return;

        try
        {
            employe.remove();
            gestionPersonnel.sauvegarder();
            refreshEmployes();
        }
        catch (SauvegardeImpossible exception)
        {
            showSaveError();
        }
    }

    private void changeAdministrateur()
    {
        AdministrateurSelectionDialog dialog = new AdministrateurSelectionDialog(this, ligue);
        dialog.setVisible(true);

        if (dialog.isValidated())
        {
            try
            {
                gestionPersonnel.sauvegarder();
                dispose();
                // Reopen pour mettre à jour l'affichage
                LigueDetailFrame newFrame = new LigueDetailFrame(gestionPersonnel, ligue);
                newFrame.setVisible(true);
            }
            catch (SauvegardeImpossible exception)
            {
                showSaveError();
            }
        }
    }

    private void renameLigue()
    {
        String newName = JOptionPane.showInputDialog(
                this,
                "Nouveau nom de la ligue :",
                ligue.getNom());

        if (newName == null || newName.trim().isEmpty())
            return;

        try
        {
            ligue.setNom(newName);
            gestionPersonnel.sauvegarder();
            setTitle("M2L — " + ligue.getNom());
        }
        catch (SauvegardeImpossible exception)
        {
            showSaveError();
        }
    }

    private void deleteLigue()
    {
        int confirmation = JOptionPane.showConfirmDialog(
                this,
                "Supprimer la ligue \"" + ligue.getNom() + "\" et tous ses employés ?",
                "Confirmation",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirmation != JOptionPane.YES_OPTION)
            return;

        try
        {
            ligue.remove();
            gestionPersonnel.sauvegarder();
            dispose();
        }
        catch (SauvegardeImpossible exception)
        {
            showSaveError();
        }
    }

    private Employe getEmployeAtRow(int row)
    {
        String nom = (String) tableModel.getValueAt(row, 0);
        String prenom = (String) tableModel.getValueAt(row, 1);

        for (Employe employe : ligue.getEmployes())
        {
            if (employe.getNom().equals(nom) && employe.getPrenom().equals(prenom))
                return employe;
        }
        return null;
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
