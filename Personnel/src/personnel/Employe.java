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
	private Ligue ligueAdministree = null;
	private GestionPersonnel gestionPersonnel;
	// dates de l'employé (remplacent la classe personnel.LocalDate)
	private LocalDate dateArrivee;
	private LocalDate dateDepart;
	private int id = -1;
	
	
	
	/**
	 * Constructeur pour créer un nouvel employé et l'insérer en base.
	 * @throws SauvegardeImpossible si l'insertion en base échoue.
	 */
	Employe(GestionPersonnel gestionPersonnel, Ligue ligue, String nom, String prenom, String mail, String password, java.time.LocalDate dateArrivee, java.time.LocalDate dateDepart )throws SauvegardeImpossible
	{
		this.gestionPersonnel = gestionPersonnel;
		this.nom = nom;
		this.prenom = prenom;
		this.password = PasswordHasher.hashPassword(password);
		this.mail = mail;
		this.ligue = ligue;
		// validation des dates
		setDateArrivee(dateArrivee);
		setDateDepart(dateDepart);
		this.id = gestionPersonnel.insert(this);
	}
	
	/**
	 * Constructeur dédié à la création du root.
	 * Insère automatiquement le root dans la base de données lors de sa création.
	 * @param gestionPersonnel le gestionnaire du personnel.
	 * @param nom le nom du root.
	 * @param password le password du root.
	 * @throws SauvegardeImpossible si l'insertion en base échoue.
	 */
	Employe(GestionPersonnel gestionPersonnel, String nom, String password) throws SauvegardeImpossible
	{
		// Appel de la surcharge sans insertion pour initialiser les champs,
		this(gestionPersonnel, -1, nom, PasswordHasher.hashPassword(password)); 
		// Insertion en base et récupération de l'id généré après initialisation
	    this.id = gestionPersonnel.insert(this);  
	}
	
	
	
	Employe(GestionPersonnel gestionPersonnel, int id, String nom, String password)
	{
		// Relais vers le constructeur complet sans insertion
	    // Le root n'a pas de ligue, prénom, ni mail.
	    this(gestionPersonnel, null, id, nom, "", "", password,
	         java.time.LocalDate.now(), java.time.LocalDate.now());
	}
	
	Employe(GestionPersonnel gestionPersonnel, Ligue ligue, int id, String nom, String prenom, String mail, String password, java.time.LocalDate dateArrivee, java.time.LocalDate dateDepart)
	{
	    this.gestionPersonnel = gestionPersonnel;
	    this.ligue = ligue;
	    this.id = id;
	    this.nom = nom;
	    this.prenom = prenom;
	    this.mail = mail;
	    this.password = password;
	    // Dates lues directement depuis la base, pas de validation nécessaire
	    this.dateArrivee = dateArrivee;
	    this.dateDepart = dateDepart;
	    // Pas d'insertion en base : l'employé existe déjà
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
	 * @throws SauvegardeImpossible 
	 */
	
	public void setNom(String nom) throws SauvegardeImpossible
	{
		this.nom = nom;
		gestionPersonnel.update(this);
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
	 * @throws SauvegardeImpossible 
	 */

	public void setPrenom(String prenom) throws SauvegardeImpossible
	{
		this.prenom = prenom;
		gestionPersonnel.update(this);
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
	 * @throws SauvegardeImpossible 
	 */

	public void setMail(String mail) throws SauvegardeImpossible
	{
		this.mail = mail;
		gestionPersonnel.update(this);
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
		return PasswordHasher.checkPassword(password, this.password);
	}

	/**
	 * Change le password de l'employé.
	 * @param password le nouveau password de l'employé. 
	 * @throws SauvegardeImpossible 
	 */
	
	public void setPassword(String password) throws SauvegardeImpossible
	{
		this.password = PasswordHasher.hashPassword(password);
		gestionPersonnel.update(this);
	}

	/**
	 * Retourne la ligue à laquelle l'employé est affecté.
	 * @return la ligue à laquelle l'employé est affecté.
	 */
	
	public Ligue getLigue()
	{
		return ligue;
	}

	public Ligue getLigueAdministree()
	{
		return ligueAdministree;
	}

	void setLigueAdministree(Ligue ligue) throws SauvegardeImpossible
	{
		this.ligueAdministree = ligue;
		gestionPersonnel.update(this);
	}

	void setLigueAdminSansSauvegarde(Ligue ligue)
	{
		this.ligueAdministree = ligue;
	}

	
	

	/**
	 * Supprime l'employé. Si celui-ci est un administrateur, le root
	 * récupère les droits d'administration sur sa ligue.
	 */
	
	public void remove() throws SauvegardeImpossible
	{
	    Employe root = gestionPersonnel.getRoot();
	    if (this != root)
	    {
	        if (estAdmin(getLigue()))
	            try { 
	                getLigue().setAdministrateur(root); 
	            }
	            catch (SauvegardeImpossible e) { 
	                throw new RuntimeException(e); 
	            }
	        
	        getLigue().remove(this);           // Supprime de la ligue en mémoire
	        gestionPersonnel.delete(this);     // Supprime de la BD
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

		public void setDateArrivee(LocalDate dateArrivee) throws DateIncoherenteException, SauvegardeImpossible {
			if(dateArrivee == null) {
				throw new DateIncoherenteException("La date d'arrivée ne peut être null");
			}
			if (this.dateDepart != null && dateArrivee.isAfter(this.dateDepart)) {
				throw new DateIncoherenteException("La nouvelle date d'arrivée (" + dateArrivee + ") ne peut pas être postérieure à la date de départ (" + this.dateDepart + ")");
			}
			this.dateArrivee = dateArrivee;
			gestionPersonnel.update(this);
		}
		
		public void setDateDepart(LocalDate dateDepart) throws DateIncoherenteException, SauvegardeImpossible {
			if(dateDepart == null) {
				throw new DateIncoherenteException("La date de départ ne peut être null");
			}
			if (this.dateArrivee != null && dateDepart.isBefore(this.dateArrivee)) {
				throw new DateIncoherenteException("La nouvelle date de départ (" + dateDepart + ") ne peut pas être antérieure à la date d'arrivée (" + this.dateArrivee + ")");
			}
			this.dateDepart = dateDepart;
			gestionPersonnel.update(this);
		}
		
		
	}