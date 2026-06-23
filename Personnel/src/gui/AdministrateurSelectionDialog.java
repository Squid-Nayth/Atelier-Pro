package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import personnel.Employe;
import personnel.Ligue;
import personnel.SauvegardeImpossible;

public class AdministrateurSelectionDialog extends JDialog
{
    private static final long serialVersionUID = 1L;

    private final Ligue ligue;
    private final JComboBox<Employe> adminComboBox;
    private boolean validated = false;

    public AdministrateurSelectionDialog(LigueDetailFrame ligueDetailFrame, Ligue ligue)
    {
        super(ligueDetailFrame);
        this.ligue = ligue;
        this.adminComboBox = new JComboBox<>();

        setTitle("M2L — Changer l'administrateur");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);
        setContentPane(buildContent());

        populateComboBox();

        pack();
        setLocationRelativeTo(ligueDetailFrame);
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

        JLabel label = new JLabel("Sélectionner le nouvel administrateur :");
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.anchor = GridBagConstraints.LINE_START;
        card.add(label, constraints);

        constraints.gridy++;
        constraints.weightx = 1.0;
        adminComboBox.setPreferredSize(new java.awt.Dimension(250, 25));
        card.add(adminComboBox, constraints);

        return card;
    }

    private JPanel buildButtonPanel()
    {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setOpaque(false);

        JButton validateButton = new JButton("Valider");
        validateButton.addActionListener(event -> validateAndSave());

        JButton cancelButton = new JButton("Annuler");
        cancelButton.addActionListener(event -> dispose());

        buttonPanel.add(validateButton);
        buttonPanel.add(cancelButton);

        return buttonPanel;
    }

    private void populateComboBox()
    {
        DefaultComboBoxModel<Employe> model = new DefaultComboBoxModel<>();
        for (Employe employe : ligue.getEmployes())
            model.addElement(employe);

        adminComboBox.setModel(model);

        // Sélectionner l'administrateur actuel
        Employe currentAdmin = ligue.getAdministrateur();
        if (currentAdmin != null)
            adminComboBox.setSelectedItem(currentAdmin);
    }

    private void validateAndSave()
    {
        Employe selectedAdmin = (Employe) adminComboBox.getSelectedItem();

        if (selectedAdmin == null)
        {
            JOptionPane.showMessageDialog(
                    this,
                    "Veuillez sélectionner un administrateur.",
                    "Erreur",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try
        {
            ligue.setAdministrateur(selectedAdmin);
            validated = true;
            dispose();
        }
        catch (SauvegardeImpossible exception)
        {
            JOptionPane.showMessageDialog(
                    this,
                    "Impossible de changer l'administrateur.",
                    "Erreur de sauvegarde",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isValidated()
    {
        return validated;
    }
}
