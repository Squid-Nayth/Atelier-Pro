package personnel;

import java.io.Serializable;
import java.util.Collections;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Représente une ligue. Chaque ligue est reliée à une liste
 * d'employés dont un administrateur. Comme il n'est pas possible
 * de créer un employé sans l'affecter à une ligue, le root est 
 * l'administrateur de la ligue jusqu'à ce qu'un administrateur 
 * lui ait été affecté avec la fonction {@link #setAdministrateur}.
 */

public class Ligue implements Serializable, Comparable<Ligue>
{
	private static final long serialVersionUID = 1L;
	private int id = -1;
	private String nom;
	private SortedSet<Employe> employes;
	private Employe administrateur;
	private GestionPersonnel gestionPersonnel;
	
	/**
	 * Crée une ligue.
	 * @param nom le nom de la ligue.
	 */
	
	Ligue(GestionPersonnel gestionPersonnel, String nom) throws SauvegardeImpossible
	{
		this(gestionPersonnel, -1, nom);
		this.id = gestionPersonnel.insert(this); 
	}

	Ligue(GestionPersonnel gestionPersonnel, int id, String nom)
	{
		this.nom = nom;
		employes = new TreeSet<>();
		this.gestionPersonnel = gestionPersonnel;
		administrateur = gestionPersonnel.getRoot();
		this.id = id;
	}
	
	/**
	 * Retourne l'identifiant de la ligue en base de données.
	 * @return l'identifiant de la ligue.
	 */
	public int getId()
	{
		return id;
	}

	/**
	 * Retourne le nom de la ligue.
	 * @return le nom de la ligue.
	 */

	public String getNom()
	{
		return nom;
	}

	/**
	 * Change le nom.
	 * @param nom le nouveau nom de la ligue.
	 * @throws SauvegardeImpossible 
	 */

	public void setNom(String nom) throws SauvegardeImpossible
	{
		this.nom = nom;
		gestionPersonnel.update(this);
	}

	/**
	 * Retourne l'administrateur de la ligue.
	 * @return l'administrateur de la ligue.
	 */
	
	public Employe getAdministrateur()
	{
		return administrateur;
	}

	/**
	 * Fait de administrateur l'administrateur de la ligue.
	 * Lève DroitsInsuffisants si l'administrateur n'est pas 
	 * un employé de la ligue ou le root. Révoque les droits de l'ancien 
	 * administrateur.
	 * @param administrateur le nouvel administrateur de la ligue.
	 */
	
	public void setAdministrateur(Employe administrateur) throws SauvegardeImpossible
	{
		Employe root = gestionPersonnel.getRoot();
		if (administrateur != root && administrateur.getLigue() != this)
			throw new DroitsInsuffisants();
		Employe ancienAdmin = this.administrateur;
		this.administrateur = administrateur;
		if (!ancienAdmin.estRoot())
			ancienAdmin.setLigueAdministree(null);
		if (!administrateur.estRoot())
			administrateur.setLigueAdministree(this);
	}

	/**
	 * Affecte l'administrateur sans déclencher de sauvegarde.
	 * Réservé au chargement depuis la base de données.
	 * @param administrateur l'administrateur à affecter.
	 */
	public void setAdministrateurSansSauvegarde(Employe administrateur)
	{
		this.administrateur = administrateur;
	}

	/**
	 * Cherche parmi les employés de la ligue celui dont l'identifiant
	 * correspond et le désigne comme administrateur.
	 * Réservé au chargement depuis la base de données, après que les
	 * employés de la ligue ont été chargés.
	 * @param idAdministrateur l'identifiant en base de l'administrateur.
	 */
	public void setAdministrateurDepuisBase(int idAdministrateur)
	{
		for (Employe emp : employes)
			if (emp.getId() == idAdministrateur)
			{
				this.administrateur = emp;
				emp.setLigueAdminSansSauvegarde(this);
				return;
			}
	}

	/**
	 * Retourne les employés de la ligue.
	 * @return les employés de la ligue dans l'ordre alphabétique.
	 */
	
	public SortedSet<Employe> getEmployes()
	{
		return Collections.unmodifiableSortedSet(employes);
	}

	/**
	 * Ajoute un employé dans la ligue. Cette méthode 
	 * est le seul moyen de créer un employé.
	 * @param nom le nom de l'employé.
	 * @param prenom le prénom de l'employé.
	 * @param mail l'adresse mail de l'employé.
	 * @param password le password de l'employé.
	 * @param dateArrivee la date d'arrivée de l'employé.
	 * @param dateDepart la date de départ de l'employé.
	 * @return l'employé créé. 
	 */

	public Employe addEmploye(String nom, String prenom, String mail, String password, java.time.LocalDate dateArrivee, java.time.LocalDate dateDepart)throws SauvegardeImpossible
	{
		Employe employe = new Employe(this.gestionPersonnel, this, nom, prenom, mail, password, dateArrivee, dateDepart);
		employes.add(employe);
		return employe;
	}
	
	/**
	 * Instancie un employé à partir de données lues dans la base de données
	 * et l'ajoute à la liste des employés de la ligue.
	 * une ligue existante sans insertion en base.
	 * @param id l'identifiant de l'employé lu en base.
	 * @param nom le nom de l'employé lu en base.
	 * @param prenom le prénom de l'employé lu en base.
	 * @param mail le mail de l'employé lu en base.
	 * @param password le password de l'employé lu en base.
	 * @param dateArrivee la date d'arrivée lue en base.
	 * @param dateDepart la date de départ lue en base.
	 * @return l'employé instancié.
	 */
	public Employe addEmploye(int id, String nom, String prenom, String mail, String password, java.time.LocalDate dateArrivee, java.time.LocalDate dateDepart)
	{
	    // Utilise le constructeur sans insertion car l'employé existe déjà en base
	    Employe employe = new Employe(this.gestionPersonnel, this, id, nom, prenom, mail, password, dateArrivee, dateDepart);
	    employes.add(employe);
	    return employe;
	}

	/**
	 * Surcharge utile pour l'interface console : crée un employé sans dates
	 * (utilise des dates par défaut : aujourd'hui et aujourd'hui).
	 */
	public Employe addEmploye(String nom, String prenom, String mail, String password)throws SauvegardeImpossible
	{
		java.time.LocalDate today = java.time.LocalDate.now();
		return addEmploye(nom, prenom, mail, password, today, today);
	}
	
	void remove(Employe employe)
	{
		employes.remove(employe);
	}
	
	/**
	 * Supprime la ligue, entraîne la suppression de tous les employés
	 * de la ligue.
	 */
	
	public void remove() throws SauvegardeImpossible
	{
		gestionPersonnel.delete(this);
	}
	

	@Override
	public int compareTo(Ligue autre)
	{
		return getNom().compareTo(autre.getNom());
	}
	
	@Override
	public String toString()
	{
		return nom;
	}
}