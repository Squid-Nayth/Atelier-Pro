package personnel;

public interface Passerelle 
{
	public GestionPersonnel getGestionPersonnel();
	public void sauvegarderGestionPersonnel(GestionPersonnel gestionPersonnel)  throws SauvegardeImpossible;
	public int insert(Ligue ligue) throws SauvegardeImpossible;
	/**
	 * Insère un employé dans le support de persistance.
	 * Retourne l'identifiant généré par la base de données.
	 * @param employe l'employé à insérer.
	 * @return l'identifiant généré.
	 * @throws SauvegardeImpossible si l'insertion échoue.
	 */
	public int insert(Employe employe) throws SauvegardeImpossible;
	
	public void update(Ligue ligue) throws SauvegardeImpossible;
	public void update(Employe employe) throws SauvegardeImpossible;
	public void delete(Ligue ligue) throws SauvegardeImpossible;
	void delete(Employe employe) throws SauvegardeImpossible;
}
