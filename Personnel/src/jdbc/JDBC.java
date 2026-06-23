package jdbc;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import personnel.*;

public class JDBC implements Passerelle 
{
    private Connection connection;

    public JDBC()
    {
        try
        {
            Class.forName(Credentials.getDriverClassName());
            connection = DriverManager.getConnection(Credentials.getUrl(), Credentials.getUser(), Credentials.getPassword());
        }
        catch (ClassNotFoundException e)
        {
            System.out.println("Pilote JDBC non installé.");
        }
        catch (SQLException e)
        {
            System.out.println(e);
        }
    }

    // ✅ Méthode pour obtenir une connexion active (reconnexion auto)
    private Connection getConnection() throws SQLException
    {
        if (connection == null || connection.isClosed())
        {
            connection = DriverManager.getConnection(Credentials.getUrl(), Credentials.getUser(), Credentials.getPassword());
        }
        return connection;
    }

    @Override
    public GestionPersonnel getGestionPersonnel()
    {
        GestionPersonnel gestionPersonnel = new GestionPersonnel();
        try
        {
            String requete =
                "SELECT l.num_ligue, l.nom, e.Num_employe AS num_admin " +
                "FROM ligue l " +
                "LEFT JOIN employe e ON e.num_ligue_administrer = l.num_ligue";
            Statement instruction = getConnection().createStatement();
            ResultSet ligues = instruction.executeQuery(requete);
            Map<Ligue, Integer> adminsACharger = new HashMap<>();
            while (ligues.next())
            {
                Ligue ligue = gestionPersonnel.addLigue(ligues.getInt("num_ligue"), ligues.getString("nom"));
                int numAdmin = ligues.getInt("num_admin");
                if (!ligues.wasNull())
                    adminsACharger.put(ligue, numAdmin);
            }

            String requeteEmployes =
                "SELECT e.Num_employe, e.Nom, e.Prenom, e.Mail, e.Password, " +
                "e.Date_arrivee, e.Date_depart, e.num_ligue_appartenir " +
                "FROM employe e " +
                "WHERE e.num_ligue_appartenir IS NOT NULL";

            ResultSet employes = instruction.executeQuery(requeteEmployes);
            while (employes.next())
            {
                Ligue ligue = gestionPersonnel.getLigue(employes.getInt("num_ligue_appartenir"));
                ligue.addEmploye(
                    employes.getInt("Num_employe"),
                    employes.getString("Nom"),
                    employes.getString("Prenom"),
                    employes.getString("Mail"),
                    employes.getString("Password"),
                    employes.getDate("Date_arrivee").toLocalDate(),
                    employes.getDate("Date_depart").toLocalDate()
                );
            }

            for (Map.Entry<Ligue, Integer> entry : adminsACharger.entrySet())
                entry.getKey().setAdministrateurDepuisBase(entry.getValue());

            String requeteRoot =
                "SELECT Num_employe, Nom, Password FROM employe " +
                "WHERE num_ligue_appartenir IS NULL";
            ResultSet rootResult = instruction.executeQuery(requeteRoot);
            if (rootResult.next())
                gestionPersonnel.addRoot(
                    rootResult.getInt("Num_employe"),
                    rootResult.getString("Nom"),
                    rootResult.getString("Password")
                );
        }
        catch (SQLException e)
        {
            System.out.println(e);
        }
        return gestionPersonnel;
    }

    @Override
    public void sauvegarderGestionPersonnel(GestionPersonnel gestionPersonnel) throws SauvegardeImpossible 
    {
        close();
    }

    public void close() throws SauvegardeImpossible
    {
        try
        {
            if (connection != null && !connection.isClosed())
                connection.close();
        }
        catch (SQLException e)
        {
            throw new SauvegardeImpossible(e);
        }
    }

    @Override
    public int insert(Ligue ligue) throws SauvegardeImpossible 
    {
        try 
        {
            PreparedStatement instruction = getConnection().prepareStatement("insert into ligue (nom) values(?)", Statement.RETURN_GENERATED_KEYS);
            instruction.setString(1, ligue.getNom());		
            instruction.executeUpdate();
            ResultSet id = instruction.getGeneratedKeys();
            id.next();
            return id.getInt(1);
        } 
        catch (SQLException exception) 
        {
            exception.printStackTrace();
            throw new SauvegardeImpossible(exception);
        }		
    }

    @Override
    public void update(Ligue ligue) throws SauvegardeImpossible
    {
        try
        {
            PreparedStatement instruction = getConnection().prepareStatement(
                "UPDATE ligue SET nom = ? WHERE num_ligue = ?");
            instruction.setString(1, ligue.getNom());
            instruction.setInt(2, ligue.getId());
            instruction.executeUpdate();
        }
        catch (SQLException exception)
        {
            exception.printStackTrace();
            throw new SauvegardeImpossible(exception);
        }
    }

    @Override
    public void delete(Ligue ligue) throws SauvegardeImpossible
    {
        try
        {
            PreparedStatement supprimerEmployes = getConnection().prepareStatement(
                "DELETE FROM employe WHERE num_ligue_appartenir = ?");
            supprimerEmployes.setInt(1, ligue.getId());
            supprimerEmployes.executeUpdate();

            PreparedStatement supprimerLigue = getConnection().prepareStatement(
                "DELETE FROM ligue WHERE num_ligue = ?");
            supprimerLigue.setInt(1, ligue.getId());
            supprimerLigue.executeUpdate();
        }
        catch (SQLException exception)
        {
            exception.printStackTrace();
            throw new SauvegardeImpossible(exception);
        }
    }

    @Override
    public void delete(Employe employe) throws SauvegardeImpossible
    {
        try
        {
            PreparedStatement supprimerLigue = getConnection().prepareStatement(
                "DELETE FROM employe WHERE Num_employe = ?");
            supprimerLigue.setInt(1, employe.getId());
            supprimerLigue.executeUpdate();
        }
        catch (SQLException exception)
        {
            exception.printStackTrace();
            throw new SauvegardeImpossible(exception);
        }
    }

    @Override
    public int insert(Employe employe) throws SauvegardeImpossible
    {
        try
        {
            PreparedStatement instruction = getConnection().prepareStatement(
                "INSERT INTO employe (Mail, Nom, Prenom, Password, Date_arrivee, Date_depart, num_ligue_appartenir) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS
            );
            instruction.setString(1, employe.getMail());
            instruction.setString(2, employe.getNom());
            instruction.setString(3, employe.getPrenom());
            instruction.setString(4, employe.getPassword());
            instruction.setDate(5, java.sql.Date.valueOf(employe.getDateArrivee()));
            instruction.setDate(6, java.sql.Date.valueOf(employe.getDateDepart()));
            if (employe.getLigue() != null)
                instruction.setInt(7, employe.getLigue().getId());
            else
                instruction.setNull(7, java.sql.Types.INTEGER);
            instruction.executeUpdate();
            ResultSet id = instruction.getGeneratedKeys();
            id.next();
            return id.getInt(1);
        }
        catch (SQLException exception)
        {
            exception.printStackTrace();
            throw new SauvegardeImpossible(exception);
        }
    }

    @Override
    public void update(Employe employe) throws SauvegardeImpossible
    {
        if (employe.estRoot()) {
            try
            {
                PreparedStatement instruction = getConnection().prepareStatement(
                    "UPDATE employe SET Nom = ? , Prenom = ? , Password = ? WHERE Num_employe = ?"
                );
                instruction.setString(1, employe.getNom());
                instruction.setString(2, employe.getPrenom());
                instruction.setString(3, employe.getPassword());
                instruction.setInt(4, employe.getId());
                instruction.executeUpdate();
            }
            catch (SQLException exception)
            {
                exception.printStackTrace();
                throw new SauvegardeImpossible(exception);
            }
        } else {
            try
            {
                PreparedStatement instruction = getConnection().prepareStatement(
                        "UPDATE employe SET Mail = ? , Nom = ? , Prenom = ? , Password = ? , Date_arrivee = ? , Date_depart = ? , num_ligue_appartenir = ? , num_ligue_administrer = ? WHERE Num_employe = ?"
                    );
                    instruction.setString(1, employe.getMail());
                    instruction.setString(2, employe.getNom());
                    instruction.setString(3, employe.getPrenom());
                    instruction.setString(4, employe.getPassword());

                    if (employe.getDateArrivee() != null)
                        instruction.setDate(5, java.sql.Date.valueOf(employe.getDateArrivee()));
                    else
                        instruction.setNull(5, java.sql.Types.DATE);

                    if (employe.getDateDepart() != null)
                        instruction.setDate(6, java.sql.Date.valueOf(employe.getDateDepart()));
                    else
                        instruction.setNull(6, java.sql.Types.DATE);

                    if (employe.getLigue() != null)
                        instruction.setInt(7, employe.getLigue().getId());
                    else
                        instruction.setNull(7, java.sql.Types.INTEGER);

                    if (employe.getLigueAdministree() != null)
                        instruction.setInt(8, employe.getLigueAdministree().getId());
                    else
                        instruction.setNull(8, java.sql.Types.INTEGER);

                    instruction.setInt(9, employe.getId());
                    instruction.executeUpdate();
            }
            catch (SQLException exception)
            {
                exception.printStackTrace();
                throw new SauvegardeImpossible(exception);
            }
        }
    }
}
