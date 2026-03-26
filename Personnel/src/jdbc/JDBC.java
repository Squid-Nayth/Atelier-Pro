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
			String requete = "select * from ligue";
			Statement instruction = connection.createStatement();
			ResultSet ligues = instruction.executeQuery(requete);
			while (ligues.next())
				gestionPersonnel.addLigue(ligues.getInt(1), ligues.getString(2));
			
			 String requeteRoot = "select Num_employe, Nom, Password from employe where num_ligue_appartenir IS NULL";
		        ResultSet rootResult = instruction.executeQuery(requeteRoot);
		        if (rootResult.next())
		        {
		        	 gestionPersonnel.addRoot(
		                     rootResult.getInt("Num_employe"),
		                     rootResult.getString("Nom"),
		                     rootResult.getString("Password")
		                 );
		        }
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
	/**
	 * Insère un employé dans la base de données.
	 * Modèle : méthode insert(Ligue ligue) de cette même classe.
	 * @param employe l'employé à insérer.
	 * @return l'identifiant généré par la base de données.
	 * @throws SauvegardeImpossible si l'insertion échoue.
	 */
	@Override
	public int insert(Employe employe) throws SauvegardeImpossible
	{
	    try
	    {
	        PreparedStatement instruction = connection.prepareStatement(
	            "INSERT INTO employe (Mail, Nom, Prenom, Password, Date_arrivee, Date_depart, num_ligue_appartenir) VALUES (?, ?, ?, ?, ?, ?, ?)",
	            Statement.RETURN_GENERATED_KEYS
	        );
	        instruction.setString(1, employe.getMail());
	        instruction.setString(2, employe.getNom());
	        instruction.setString(3, employe.getPrenom());
	        instruction.setString(4, employe.getPassword());
	        instruction.setDate(5, java.sql.Date.valueOf(employe.getDateArrivee()));
	        instruction.setDate(6, java.sql.Date.valueOf(employe.getDateDepart()));
	        // Si l'employé appartient à une ligue, on insère sa clé étrangère.
	        // Sinon (cas du root), on insère NULL car il n'appartient à aucune ligue.
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
}
