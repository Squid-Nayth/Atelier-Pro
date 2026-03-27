package jdbc;


import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

//Bonjour

import personnel.*;

public class JDBC implements Passerelle 
{
	Connection connection;

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
	
	@Override
	public GestionPersonnel getGestionPersonnel()
	{
	    GestionPersonnel gestionPersonnel = new GestionPersonnel();
	    try
	    {
	        // Chargement de toutes les ligues existantes en base
	        String requete = "select * from ligue";
	        Statement instruction = connection.createStatement();
	        ResultSet ligues = instruction.executeQuery(requete);
	        while (ligues.next())
	            gestionPersonnel.addLigue(ligues.getInt("num_ligue"), ligues.getString("nom"));

	        // Requête avec jointure pour charger tous les employés
	        // et leur ligue associée en une seule requête
	        String requeteEmployes =
	            "SELECT e.Num_employe, e.Nom, e.Prenom, e.Mail, e.Password, " +
	            "e.Date_arrivee, e.Date_depart, e.num_ligue_appartenir " +
	            "FROM employe e " +
	            "JOIN ligue l ON e.num_ligue_appartenir = l.num_ligue " +
	            "WHERE e.num_ligue_appartenir IS NOT NULL";

	        ResultSet employes = instruction.executeQuery(requeteEmployes);
	        while (employes.next())
	        {
	            // Récupération de la ligue correspondante déjà chargée en mémoire
	            Ligue ligue = gestionPersonnel.getLigue(employes.getInt("num_ligue_appartenir"));

	            // Création de l'objet Employé à partir des données lues en base
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

	        // Chargement des administrateurs de ligue
        String requeteAdmins =
            "SELECT Num_employe, num_ligue_administrer FROM employe " +
            "WHERE num_ligue_administrer IS NOT NULL";
        ResultSet admins = instruction.executeQuery(requeteAdmins);
        while (admins.next())
        {
            int numLigue = admins.getInt("num_ligue_administrer");
            int numEmploye = admins.getInt("Num_employe");
            Ligue ligue = gestionPersonnel.getLigue(numLigue);
            if (ligue != null)
                for (Employe emp : ligue.getEmployes())
                    if (emp.getId() == numEmploye)
                    {
                        ligue.setAdministrateurSansSauvegarde(emp);
                        break;
                    }
        }

        // Chargement du root depuis la base
	        // Le root est identifié par l'absence de ligue (num_ligue_appartenir IS NULL)
	        String requeteRoot =
	            "SELECT Num_employe, Nom, Password FROM employe " +
	            "WHERE num_ligue_appartenir IS NULL";
	        ResultSet rootResult = instruction.executeQuery(requeteRoot);
	        if (rootResult.next())
	            // Le root existe déjà en base : on l'instancie sans le réinsérer
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
			if (connection != null)
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
			PreparedStatement instruction;
			instruction = connection.prepareStatement("insert into ligue (nom) values(?)", Statement.RETURN_GENERATED_KEYS);
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
			// Mise à jour du nom de la ligue
			PreparedStatement instruction = connection.prepareStatement(
				"UPDATE ligue SET nom = ? WHERE num_ligue = ?");
			instruction.setString(1, ligue.getNom());
			instruction.setInt(2, ligue.getId());
			instruction.executeUpdate();

			// Réinitialisation de l'ancien administrateur de cette ligue
			PreparedStatement clearAdmin = connection.prepareStatement(
				"UPDATE employe SET num_ligue_administrer = NULL WHERE num_ligue_administrer = ?");
			clearAdmin.setInt(1, ligue.getId());
			clearAdmin.executeUpdate();

			// Assignation du nouvel administrateur (sauf si c'est le root)
			Employe admin = ligue.getAdministrateur();
			if (!admin.estRoot())
			{
				PreparedStatement setAdmin = connection.prepareStatement(
					"UPDATE employe SET num_ligue_administrer = ? WHERE Num_employe = ?");
				setAdmin.setInt(1, ligue.getId());
				setAdmin.setInt(2, admin.getId());
				setAdmin.executeUpdate();
			}
		}
		catch (SQLException exception)
		{
			exception.printStackTrace();
			throw new SauvegardeImpossible(exception);
		}
	}
	/**
	 * Insère un employé en base de données.
	 * Utilise un SELECT avec jointure sur la table ligue pour récupérer
	 * num_ligue_appartenir directement depuis la table ligue via son id.
	 * @param employe l'employé à insérer.
	 * @return l'identifiant généré par la base de données.
	 * @throws SauvegardeImpossible si l'insertion échoue.
	 */
	@Override
	public int insert(Employe employe) throws SauvegardeImpossible
	{
	    try
	    {
	       
	        // on remplace VALUES (?, ?, ?, ?, ?, ?, ?) par un SELECT avec JOIN
	        // sur la table ligue pour récupérer num_ligue directement en base
	        PreparedStatement instruction = connection.prepareStatement(
	            "INSERT INTO employe (Mail, Nom, Prenom, Password, Date_arrivee, Date_depart, num_ligue_appartenir) " +
	            "SELECT ?, ?, ?, ?, ?, ?, num_ligue FROM ligue WHERE num_ligue = ?",
	            Statement.RETURN_GENERATED_KEYS
	        );
	        instruction.setString(1, employe.getMail());
	        instruction.setString(2, employe.getNom());
	        instruction.setString(3, employe.getPrenom());
	        instruction.setString(4, employe.getPassword());
	        instruction.setDate(5, java.sql.Date.valueOf(employe.getDateArrivee()));
	        instruction.setDate(6, java.sql.Date.valueOf(employe.getDateDepart()));
	        // Jointure avec la ligue : récupération de num_ligue depuis la table ligue
	        // Si root (pas de ligue), on insère NULL
	        if (employe.getLigue() != null)
	            instruction.setInt(7, employe.getLigue().getId());
	        else
	            instruction.setNull(7, java.sql.Types.INTEGER);
	        instruction.executeUpdate();
	        // Récupération de l'identifiant auto-généré par la base de données
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
	    try
	    {
	       
	        // on remplace VALUES (?, ?, ?, ?, ?, ?, ?) par un SELECT avec JOIN
	        // sur la table ligue pour récupérer num_ligue directement en base
	        PreparedStatement instruction = connection.prepareStatement(
	            "UPDATE employe set Mail = ? , Nom = ? , Prenom = ? , Password = ? , Date_arrivee = ? , Date_depart = ? , num_ligue_appartenir = ? where id = ? "
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
	    }
	    catch (SQLException exception)
	    {
	        exception.printStackTrace();
	        throw new SauvegardeImpossible(exception);
	    }
	}
}
