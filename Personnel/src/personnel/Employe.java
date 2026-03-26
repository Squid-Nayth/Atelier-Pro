package personnel;

import java.io.Serializable;
import java.sql.Date;
import java.time.LocalDate;

/**
 * Employé d'une ligue hébergée par la M2L. Certains peuvent 
 * être administrateurs des employés de leur ligue.
 * Un seul employé, rattaché à aucune ligue, est le root.
 * Il est impossible d'instancier directement un employé, 
 * il faut passer la méthode {@link Ligue#addEmploye addEmploye}.
 */

public class Employe implements Serializable, Comparable<Employe>
{
	private static final long serialVersionUID = 4795721718037994734L;
	private String nom, prenom, password, mail;
	private Ligue ligue;
	private GestionPersonnel gestionPersonnel;
	// dates de l'employé (remplacent la classe personnel.LocalDate)
	private LocalDate dateArrivee;
	private LocalDate dateDepart;
	private int id = -1;
	
	
	Employe(GestionPersonnel gestionPersonnel, Ligue ligue, String nom, String prenom, String mail, String password, java.time.LocalDate dateArrivee, java.time.LocalDate dateDepart )
	{
		this.gestionPersonnel = gestionPersonnel;
		this.nom = nom;
		this.prenom = prenom;
		this.password = password;
		this.mail = mail;
		this.ligue = ligue;
		// validation des dates
		setDateArrivee(dateArrivee);
		setDateDepart(dateDepart);
	}
	
	/**
	 * Constructeur dédié à la création du root.
	 * Modèle : surcharge du constructeur de Ligue effectuant une insertion en base.
	 * Insère automatiquement le root dans la base de données lors de sa création.
	 * @param gestionPersonnel le gestionnaire du personnel.
	 * @param nom le nom du root.
	 * @param password le password du root.
	 * @throws SauvegardeImpossible si l'insertion en base échoue.
	 */
	Employe(GestionPersonnel gestionPersonnel, String nom, String password) throws SauvegardeImpossible
	{
	    this.gestionPersonnel = gestionPersonnel;
	    this.nom = nom;
	    // Le root n'a pas de prénom, mail, ni de ligue
	    this.prenom = "";
	    this.mail = "";
	    this.password = password;
	    this.ligue = null;
	    // Dates par défaut : aujourd'hui
	    this.dateArrivee = java.time.LocalDate.now();
	    this.dateDepart = java.time.LocalDate.now();
	    // Insertion en base et récupération de l'id généré, comme pour Ligue
	    this.id = gestionPersonnel.insert(this);
	}
	
	
	
	Employe(GestionPersonnel gestionPersonnel, int id, String nom, String password)
	{
	    this.gestionPersonnel = gestionPersonnel;
	    this.id = id;
	    this.nom = nom;
	    // Le root n'a pas de prénom, de mail, ni de ligue
	    this.prenom = "";
	    this.mail = "";
	    this.password = password;
	    this.ligue = null;
	    // Dates initialisées à aujourd'hui par défaut
	    this.dateArrivee = java.time.LocalDate.now();
	    this.dateDepart = java.time.LocalDate.now();
	    // Pas d'insertion en base : le root existe déjà
	}
	
	
	/**
	 * Retourne vrai ssi l'employé est administrateur de la ligue 
	 * passée en paramètre.
	 * @return vrai ssi l'employé est administrateur de la ligue 
	 * passée en paramètre.
	 * @param ligue la ligue pour laquelle on souhaite vérifier si this 
	 * est l'admininstrateur.
	 */
	
	public boolean estAdmin(Ligue ligue)
	{
		return ligue.getAdministrateur() == this;
	}
	
	/**
	 * Retourne vrai ssi l'employé est le root.
	 * @return vrai ssi l'employé est le root.
	 */
	
	public boolean estRoot()
	{
		return gestionPersonnel.getRoot() == this;
	}
	
	/**
	 * Retourne le nom de l'employé.
	 * @return le nom de l'employé. 
	 */
	
	public String getNom()
	{
		return nom;
	}

	/**
	 * Change le nom de l'employé.
	 * @param nom le nouveau nom.
	 */
	
	public void setNom(String nom)
	{
		this.nom = nom;
	}

	/**
	 * Retourne le prénom de l'employé.
	 * @return le prénom de l'employé.
	 */
	
	public String getPrenom()
	{
		return prenom;
	}
	/**
	 * Retourne l'identifiant de l'employé en base de données.
	 * @return l'identifiant de l'employé.
	 */
	public int getId() {
		return id;
	}
	/**
	 * Retourne le password de l'employé.
	 * Nécessaire pour l'insertion en base de données via JDBC.
	 * @return le password de l'employé.
	 */
	public String getPassword() {
		return password;
	}
	
	/**
	 * Change le prénom de l'employé.
	 * @param prenom le nouveau prénom de l'employé. 
	 */

	public void setPrenom(String prenom)
	{
		this.prenom = prenom;
	}

	/**
	 * Retourne le mail de l'employé.
	 * @return le mail de l'employé.
	 */
	
	public String getMail()
	{
		return mail;
	}
	
	/**
	 * Change le mail de l'employé.
	 * @param mail le nouveau mail de l'employé.
	 */

	public void setMail(String mail)
	{
		this.mail = mail;
	}

	/**
	 * Retourne vrai ssi le password passé en paramètre est bien celui
	 * de l'employé.
	 * @return vrai ssi le password passé en paramètre est bien celui
	 * de l'employé.
	 * @param password le password auquel comparer celui de l'employé.
	 */
	
	public boolean checkPassword(String password)
	{
		return this.password.equals(password);
	}

	/**
	 * Change le password de l'employé.
	 * @param password le nouveau password de l'employé. 
	 */
	
	public void setPassword(String password)
	{
		this.password= password;
	}

	/**
	 * Retourne la ligue à laquelle l'employé est affecté.
	 * @return la ligue à laquelle l'employé est affecté.
	 */
	
	public Ligue getLigue()
	{ 
		return ligue;
	}

	
	

	/**
	 * Supprime l'employé. Si celui-ci est un administrateur, le root
	 * récupère les droits d'administration sur sa ligue.
	 */
	
	public void remove()
	{
		Employe root = gestionPersonnel.getRoot();
		if (this != root)
		{
			if (estAdmin(getLigue()))
				getLigue().setAdministrateur(root);
			getLigue().remove(this);
		}
		else
			throw new ImpossibleDeSupprimerRoot();
	}

	@Override
	public int compareTo(Employe autre)
	{
		int cmp = getNom().compareTo(autre.getNom());
		if (cmp != 0)
			return cmp;
		return getPrenom().compareTo(autre.getPrenom());
	}
	
	@Override
	public String toString()
	{
		String res = nom + " " + prenom + " " + mail + " " + dateArrivee + " - " + dateDepart + " (";
		if (estRoot())
			res += "super-utilisateur";
		else
			res += ligue.toString();
		return res + ")";
	}
	
	// getters et setters pour les dates
	
		public LocalDate getDateArrivee() {
			return dateArrivee;
		}

		public LocalDate getDateDepart() {
			return dateDepart;
		}

		public void setDateArrivee(LocalDate dateArrivee) throws DateIncoherenteException {
			if(dateArrivee == null) {
				throw new DateIncoherenteException("La date d'arrivée ne peut être null");
			}
			if (this.dateDepart != null && dateArrivee.isAfter(this.dateDepart)) {
				throw new DateIncoherenteException("La nouvelle date d'arrivée (" + dateArrivee + ") ne peut pas être postérieure à la date de départ (" + this.dateDepart + ")");
			}
			this.dateArrivee = dateArrivee;
		}
		
		public void setDateDepart(LocalDate dateDepart) throws DateIncoherenteException {
			if(dateDepart == null) {
				throw new DateIncoherenteException("La date de départ ne peut être null");
			}
			if (this.dateArrivee != null && dateDepart.isBefore(this.dateArrivee)) {
				throw new DateIncoherenteException("La nouvelle date de départ (" + dateDepart + ") ne peut pas être antérieure à la date d'arrivée (" + this.dateArrivee + ")");
			}
			this.dateDepart = dateDepart;
		}
		
		
	}