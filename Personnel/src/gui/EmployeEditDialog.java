package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import personnel.DateIncoherenteException;
import personnel.Employe;
import personnel.GestionPersonnel;
import personnel.Ligue;
import personnel.SauvegardeImpossible;

public class EmployeEditDialog extends JDialog
{
    private static final long serialVersionUID = 1L;

    private final GestionPersonnel gestionPersonnel;
    private final Ligue ligue;
    private final Employe employe;
    private final JTextField nomField;
    private final JTextField prenomField;
    private final JTextField mailField;
    private final JPasswordField passwordField;
    private final JTextField dateArriveeField;
    private final JTextField dateDepartField;
    private boolean validated = false;

    public EmployeEditDialog(EmployesListFrame employesListFrame, GestionPersonnel gestionPersonnel, Ligue ligue, Employe employe)
    {
        super(employesListFrame);
        this.gestionPersonnel = gestionPersonnel;
        this.ligue = ligue;
        this.employe = employe;
        this.nomField = new JTextField(20);
        this.prenomField = new JTextField(20);
        this.mailField = new JTextField(20);
        this.passwordField = new JPasswordField(20);
        this.dateArriveeField = new JTextField(20);
        this.dateDepartField = new JTextField(20);

        setTitle("M2L — " + (employe == null ? "Ajouter un employé" : "Modifier un employé"));
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);
        setContentPane(buildContent());

        if (employe != null)
            populateFields();

        pack();
        setLocationRelativeTo(employesListFrame);
        setModal(true);
    }
    
    public EmployeEditDialog(LigueDetailFrame ligueDetailFrame, GestionPersonnel gestionPersonnel, Ligue ligue, Employe employe)
    {
        super(ligueDetailFrame);
        this.gestionPersonnel = gestionPersonnel;
        this.ligue = ligue;
        this.employe = employe;
        this.nomField = new JTextField(20);
        this.prenomField = new JTextField(20);
        this.mailField = new JTextField(20);
        this.passwordField = new JPasswordField(20);
        this.dateArriveeField = new JTextField(20);
        this.dateDepartField = new JTextField(20);

        setTitle("M2L — " + (employe == null ? "Ajouter un employé" : "Modifier un employé"));
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);
        setContentPane(buildContent());

        if (employe != null)
            populateFields();

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

        JLabel titleLabel = new JLabel("Informations de l'employé");
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
        card.add(nomField, constraints);

        // Prénom
        constraints.gridy++;
        constraints.gridx = 0;
        constraints.weightx = 0;
        card.add(new JLabel("Prénom :"), constraints);
        constraints.gridx = 1;
        constraints.weightx = 1.0;
        card.add(prenomField, constraints);

        // Mail
        constraints.gridy++;
        constraints.gridx = 0;
        constraints.weightx = 0;
        card.add(new JLabel("Mail :"), constraints);
        constraints.gridx = 1;
        constraints.weightx = 1.0;
        card.add(mailField, constraints);

        // Password
        constraints.gridy++;
        constraints.gridx = 0;
        constraints.weightx = 0;
        card.add(new JLabel("Mot de passe :"), constraints);
        constraints.gridx = 1;
        constraints.weightx = 1.0;
        card.add(passwordField, constraints);

        // Date d'arrivée
        constraints.gridy++;
        constraints.gridx = 0;
        constraints.weightx = 0;
        card.add(new JLabel("Date d'arrivée :"), constraints);
        constraints.gridx = 1;
        constraints.weightx = 1.0;
        dateArriveeField.setText("dd/MM/yyyy");
        card.add(dateArriveeField, constraints);

        // Date de départ
        constraints.gridy++;
        constraints.gridx = 0;
        constraints.weightx = 0;
        card.add(new JLabel("Date de départ :"), constraints);
        constraints.gridx = 1;
        constraints.weightx = 1.0;
        dateDepartField.setText("dd/MM/yyyy");
        card.add(dateDepartField, constraints);

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

    private void populateFields()
    {
        nomField.setText(employe.getNom());
        prenomField.setText(employe.getPrenom());
        mailField.setText(employe.getMail());
        passwordField.setText(employe.getPassword());
        dateArriveeField.setText(employe.getDateArrivee().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        dateDepartField.setText(employe.getDateDepart().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
    }

    private void validateAndSave()
    {
        String nom = nomField.getText().trim();
        String prenom = prenomField.getText().trim();
        String mail = mailField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (nom.isEmpty() || prenom.isEmpty() || mail.isEmpty() || password.isEmpty())
        {
            JOptionPane.showMessageDialog(
                    this,
                    "Tous les champs doivent être remplis.",
                    "Erreur de saisie",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        LocalDate dateArrivee;
        LocalDate dateDepart;

        try
        {
            dateArrivee = LocalDate.parse(dateArriveeField.getText(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            dateDepart = LocalDate.parse(dateDepartField.getText(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        }
        catch (DateTimeParseException e)
        {
            JOptionPane.showMessageDialog(
                    this,
                    "Format de date invalide. Utilisez dd/MM/yyyy.",
                    "Erreur de saisie",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try
        {
            if (employe == null)
            {
                // Création d'un nouvel employé
                ligue.addEmploye(nom, prenom, mail, password, dateArrivee, dateDepart);
            }
            else
            {
                // Modification d'un employé existant
                employe.setNom(nom);
                employe.setPrenom(prenom);
                employe.setMail(mail);
                employe.setPassword(password);
                employe.setDateArrivee(dateArrivee);
                employe.setDateDepart(dateDepart);
            }

            if (gestionPersonnel != null)
                gestionPersonnel.sauvegarder();

            validated = true;
            dispose();
        }
        catch (DateIncoherenteException e)
        {
            JOptionPane.showMessageDialog(
                    this,
                    e.getMessage(),
                    "Erreur de date",
                    JOptionPane.WARNING_MESSAGE);
        }
        catch (SauvegardeImpossible exception)
        {
            JOptionPane.showMessageDialog(
                    this,
                    "Impossible d'enregistrer l'employé.",
                    "Erreur de sauvegarde",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isValidated()
    {
        return validated;
    }
}
