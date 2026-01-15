package testsUnitaires;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import personnel.*;

class testLigue 
{
	GestionPersonnel gestionPersonnel = GestionPersonnel.getGestionPersonnel();
	
	@Test
	void createLigue() throws SauvegardeImpossible
	{
		Ligue ligue = gestionPersonnel.addLigue("Fléchettes");
		assertEquals("Fléchettes", ligue.getNom());
	}

	@Test
	void addEmploye() throws SauvegardeImpossible
	{
		LocalDate lc = new LocalDate(java.time.LocalDate.of(2022, 4, 15), java.time.LocalDate.of(2025, 4, 15)); 
		Ligue ligue = gestionPersonnel.addLigue("Fléchettes");
		Employe employe = ligue.addEmploye("Bouchard", "Gérard", "g.bouchard@gmail.com", "azerty", lc); 
		assertEquals(employe, ligue.getEmployes().first());
	}

	@Test
	void employeSettersAndPassword() throws SauvegardeImpossible
	{
		LocalDate lc = new LocalDate(java.time.LocalDate.of(2022, 4, 15), java.time.LocalDate.of(2025, 4, 15));
		Ligue ligue = gestionPersonnel.addLigue("Tennis");
		Employe e = ligue.addEmploye("Dupont", "Alice", "a.dupont@mail.com", "initpass", lc);
		// Setters
		e.setNom("Martin");
		e.setPrenom("Aline");
		e.setMail("aline.martin@mail.com");
		e.setPassword("newpass");
		// Vérifications
		assertEquals("Martin", e.getNom());
		assertEquals("Aline", e.getPrenom());
		assertEquals("aline.martin@mail.com", e.getMail());
		assertTrue(e.checkPassword("newpass"));
		assertFalse(e.checkPassword("initpass"));
	}

	@Test
	void localDateSettersAndValidation()
	{
		LocalDate lc = new LocalDate(java.time.LocalDate.of(2022, 4, 15), java.time.LocalDate.of(2025, 4, 15));
		// Changer date d'arrivée vers une date valide
		lc.setDateArrivee(java.time.LocalDate.of(2023, 1, 1));
		assertEquals(java.time.LocalDate.of(2023, 1, 1), lc.getDateArrivee());
		// Changer date de départ vers une date valide
		lc.setDateDepart(java.time.LocalDate.of(2026, 1, 1));
		assertEquals(java.time.LocalDate.of(2026, 1, 1), lc.getDateDepart());
		// Tenter de fixer une date d'arrivée postérieure à la date de départ -> exception
		assertThrows(IllegalArgumentException.class, () -> lc.setDateArrivee(java.time.LocalDate.of(2027, 1, 1)));
		// Tenter de fixer une date de départ antérieure à la date d'arrivée -> exception
		assertThrows(IllegalArgumentException.class, () -> lc.setDateDepart(java.time.LocalDate.of(2020, 1, 1)));
	}

	@Test
	void administrateurSettingAndRights() throws SauvegardeImpossible
	{
		Ligue l1 = gestionPersonnel.addLigue("Basket");
		Ligue l2 = gestionPersonnel.addLigue("Handball");
		LocalDate lc = new LocalDate(java.time.LocalDate.of(2021, 1, 1), java.time.LocalDate.of(2024, 1, 1));
		Employe e1 = l1.addEmploye("Nom1", "Prenom1", "e1@mail", "p1", lc);
		Employe e2 = l2.addEmploye("Nom2", "Prenom2", "e2@mail", "p2", lc);
		// Affecter administrateur valide
		l1.setAdministrateur(e1);
		assertEquals(e1, l1.getAdministrateur());
		// Essayer d'affecter un administrateur venant d'une autre ligue -> DroitsInsuffisants
		assertThrows(DroitsInsuffisants.class, () -> l1.setAdministrateur(e2));
		// Affecter le root comme administrateur (autorisé)
		l1.setAdministrateur(gestionPersonnel.getRoot());
		assertEquals(gestionPersonnel.getRoot(), l1.getAdministrateur());
	}

	@Test
	void removeEmployeAdminResetsAdmin() throws SauvegardeImpossible
	{
		Ligue l = gestionPersonnel.addLigue("Rugby");
		LocalDate lc = new LocalDate(java.time.LocalDate.of(2020, 6, 1), java.time.LocalDate.of(2023, 6, 1));
		Employe admin = l.addEmploye("Admin", "Un", "admin@mail", "pwd", lc);
		Employe other = l.addEmploye("User", "Deux", "user@mail", "pwd2", lc);
		l.setAdministrateur(admin);
		// Supprimer l'administrateur -> doit revenir au root
		admin.remove();
		assertEquals(gestionPersonnel.getRoot(), l.getAdministrateur());
		// L'employé supprimé n'est plus dans la liste
		assertFalse(l.getEmployes().contains(admin));
	}

	@Test
	void removeRootThrowsException()
	{
		assertThrows(ImpossibleDeSupprimerRoot.class, () -> gestionPersonnel.getRoot().remove());
	}

	@Test
	void removeLigueRemovesFromGestionPersonnel() throws SauvegardeImpossible
	{
		Ligue l = gestionPersonnel.addLigue("Volley");
		LocalDate lc = new LocalDate(java.time.LocalDate.of(2019, 3, 1), java.time.LocalDate.of(2022, 3, 1));
		l.addEmploye("A", "B", "a@mail", "p", lc);
		l.addEmploye("C", "D", "c@mail", "p2", lc);
		// Supprimer la ligue
		l.remove();
		assertFalse(gestionPersonnel.getLigues().contains(l));
	}

	@Test
	void ligueSetNom() throws SauvegardeImpossible
	{
		Ligue l = gestionPersonnel.addLigue("Initial");
		assertEquals("Initial", l.getNom());
		l.setNom("MisAJour");
		assertEquals("MisAJour", l.getNom());
	}
}