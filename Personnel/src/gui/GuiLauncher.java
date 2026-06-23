package gui;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import personnel.GestionPersonnel;

public final class GuiLauncher
{
    private GuiLauncher()
    {
    }

    public static void main(String[] args)
    {
        SwingUtilities.invokeLater(() ->
        {
            applyLookAndFeel();
            GestionPersonnel gestionPersonnel = GestionPersonnel.getGestionPersonnel();
            new ConnexionFrame(gestionPersonnel).setVisible(true);
        });
    }

    private static void applyLookAndFeel()
    {
        try
        {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch (Exception exception)
        {
            
        }
    }
}