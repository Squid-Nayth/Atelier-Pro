package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
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
import personnel.Ligue;
import personnel.SauvegardeImpossible;

public class EmployesListFrame extends JDialog
{
    private static final long serialVersionUID = 1L;

    private final Ligue ligue;
    private final JTable employesTable;
    private final DefaultTableModel tableModel;
    private final JButton modifierButton;
    private final JButton supprimerButton;

    public EmployesListFrame(java.awt.Frame parent, Ligue ligue)
    {
        super(parent);
        this.ligue = ligue;
        this.tableModel = new DefaultTableModel();
        this.employesTable = new JTable(tableModel);
        this.modifierButton = new JButton("Modifier");
        this.supprimerButton = new JButton("Supprimer");

        setTitle("M2L — Employés de " + ligue.getNom());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(true);
        setContentPane(buildContent());
        setMinimumSize(new Dimension(700, 400));
        setPreferredSize(new Dimension(800, 450));
        refreshEmployes();
        updateSelectionState();
        pack();
        setLocationRelativeTo(parent);
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

        JLabel title = new JLabel("Employés de " + ligue.getNom());
        title.setFont(title.getFont().deriveFont(Font.BOLD, 18f));

        JLabel subtitle = new JLabel("Liste des employés enregistrés");
        subtitle.setForeground(new Color(90, 102, 120));

        header.add(title, BorderLayout.NORTH);
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

        modifierButton.addActionListener(event -> editSelectedEmploye());
        supprimerButton.addActionListener(event -> deleteSelectedEmploye());

        buttonsPanel.add(modifierButton);
        buttonsPanel.add(supprimerButton);

        return buttonsPanel;
    }

    private JPanel buildFooter()
    {
        JPanel footer = new JPanel(new BorderLayout());
        footer.setOpaque(false);
        footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(214, 220, 228)));

        JButton fermerButton = new JButton("Fermer");
        fermerButton.addActionListener(event -> dispose());

        JPanel footerContent = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footerContent.setOpaque(false);
        footerContent.add(fermerButton);

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

    private void editSelectedEmploye()
    {
        int row = employesTable.getSelectedRow();
        if (row < 0)
            return;

        Employe employe = getEmployeAtRow(row);
        if (employe != null)
        {
            EmployeEditDialog dialog = new EmployeEditDialog(this, null, ligue, employe);
            dialog.setVisible(true);

            if (dialog.isValidated())
                refreshEmployes();
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
                "Supprimer \"" + employe.getNom() + " " + employe.getPrenom() + "\" ?",
                "Confirmation",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirmation == JOptionPane.YES_OPTION)
        {
            try {
				employe.remove();
			} catch (SauvegardeImpossible e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            refreshEmployes();
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
}
