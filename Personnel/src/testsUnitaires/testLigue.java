package testsUnitaires;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import personnel.*;

class testLigue 
{
	GestionPersonnel gestionPersonnel = GestionPersonnel.getGestionPersonnel();

	@Test
	void gettersAndSettersLigue() throws SauvegardeImpossible
	{
		Ligue l = gestionPersonnel.addLigue("LigueTest");
		try {
			assertEquals("LigueTest", l.getNom());
			l.setNom("LigueRenommee");
			assertEquals("LigueRenommee", l.getNom());
			assertEquals(gestionPersonnel.getRoot(), l.getAdministrateur());
			java.time.LocalDate datesArr = java.time.LocalDate.of(2022,1,1);
			java.time.LocalDate datesDep = java.time.LocalDate.of(2023,1,1);
			Employe e = l.addEmploye("NomE", "PrenomE", "e@mail", "pwd", datesArr, datesDep);
			l.setAdministrateur(e);
			assertEquals(e, l.getAdministrateur());
		} finally {
			l.remove();
		}
	}

	@Test
	void gettersAndSettersEmploye() throws SauvegardeImpossible
	{
		Ligue l = gestionPersonnel.addLigue("LigueEmploye");
		try {
			java.time.LocalDate datesArr = java.time.LocalDate.of(2020,5,1);
			java.time.LocalDate datesDep = java.time.LocalDate.of(2022,5,1);
			Employe e = l.addEmploye("Dupont", "Alice", "a.dupont@mail.com", "initpass", datesArr, datesDep);
			// noms, prénoms, mail, password
			assertEquals("Dupont", e.getNom());
			assertEquals("Alice", e.getPrenom());
			assertEquals("a.dupont@mail.com", e.getMail());
			assertEquals(l, e.getLigue());
			e.setNom("Martin");
			e.setPrenom("Aline");
			e.setMail("aline.martin@mail.com");
			e.setPassword("newpass");
			assertEquals("Martin", e.getNom());
			assertEquals("Aline", e.getPrenom());
			assertEquals("aline.martin@mail.com", e.getMail());
			assertTrue(e.checkPassword("newpass"));
			// suppression d'un employé
			e.remove();
			assertFalse(l.getEmployes().contains(e));
		} finally {
			l.remove();
		}
	}

	@Test
	void testSetDateArriveeDepartAndConstructorValidation() throws SauvegardeImpossible
	{
		Ligue l = gestionPersonnel.addLigue("LigueDatesTest");
		try {
			// création valide
			java.time.LocalDate arr = java.time.LocalDate.of(2020,1,1);
			java.time.LocalDate dep = java.time.LocalDate.of(2022,1,1);
			Employe e = l.addEmploye("N", "P", "m@mail", "pwd", arr, dep);
			assertEquals(arr, e.getDateArrivee());
			assertEquals(dep, e.getDateDepart());

			// setDateArrivee postérieure à depart -> exception
			java.time.LocalDate badArr = java.time.LocalDate.of(2023,1,1);
			assertThrows(DateIncoherenteException.class, () -> e.setDateArrivee(badArr));

			// setDateDepart antérieure à arrivée -> exception
			java.time.LocalDate badDep = java.time.LocalDate.of(2019,1,1);
			assertThrows(DateIncoherenteException.class, () -> e.setDateDepart(badDep));

			// construction avec arrival après depart doit lever
			java.time.LocalDate arr2 = java.time.LocalDate.of(2025,1,1);
			java.time.LocalDate dep2 = java.time.LocalDate.of(2024,1,1);
			assertThrows(DateIncoherenteException.class, () -> l.addEmploye("X", "Y", "x@y.com", "p", arr2, dep2));

			// construction avec null dates doit lever
			assertThrows(DateIncoherenteException.class, () -> l.addEmploye("Null1", "N", "n1@mail", "p", null, dep));
			assertThrows(DateIncoherenteException.class, () -> l.addEmploye("Null2", "N", "n2@mail", "p", arr, null));
		} finally {
			l.remove();
		}
	}

	@Test
	void suppressionEmployeEtComportementAdmin() throws SauvegardeImpossible
	{
		Ligue l = gestionPersonnel.addLigue("LigueSuppression");
		try {
			java.time.LocalDate datesArr = java.time.LocalDate.of(2019,3,1);
			java.time.LocalDate datesDep = java.time.LocalDate.of(2022,3,1);
			Employe admin = l.addEmploye("Admin", "Un", "admin@mail", "pwd", datesArr, datesDep);
			Employe user = l.addEmploye("User", "Deux", "user@mail", "pwd2", datesArr, datesDep);
			l.setAdministrateur(admin);
			assertTrue(admin.estAdmin(l));
			admin.remove();
			assertEquals(gestionPersonnel.getRoot(), l.getAdministrateur());
			assertFalse(l.getEmployes().contains(admin));
			user.remove();
			assertFalse(l.getEmployes().contains(user));
		} finally {
			l.remove();
		}
	}

	@Test
	void setAdministrateurInvalidThrows() throws SauvegardeImpossible
	{
		Ligue l1 = gestionPersonnel.addLigue("L1");
		Ligue l2 = gestionPersonnel.addLigue("L2");
		try {
			java.time.LocalDate datesArr = java.time.LocalDate.of(2021,7,1);
			java.time.LocalDate datesDep = java.time.LocalDate.of(2022,7,1);
			Employe e1 = l1.addEmploye("E1", "P1", "e1@mail", "p1", datesArr, datesDep);
			Employe e2 = l2.addEmploye("E2", "P2", "e2@mail", "p2", datesArr, datesDep);
			assertThrows(DroitsInsuffisants.class, () -> l1.setAdministrateur(e2));
		} finally {
			l1.remove();
			l2.remove();
		}
	}

	@Test
	void changementAdministrateurRevocation() throws SauvegardeImpossible
	{
		Ligue l = gestionPersonnel.addLigue("LigueAdminChange");
		try {
			java.time.LocalDate datesArr = java.time.LocalDate.of(2021,2,1);
			java.time.LocalDate datesDep = java.time.LocalDate.of(2023,2,1);
			Employe e1 = l.addEmploye("E1", "P1", "e1@mail", "p1", datesArr, datesDep);
			Employe e2 = l.addEmploye("E2", "P2", "e2@mail", "p2", datesArr, datesDep);
			l.setAdministrateur(e1);
			assertTrue(e1.estAdmin(l));
			l.setAdministrateur(e2);
			assertTrue(e2.estAdmin(l));
			assertFalse(e1.estAdmin(l));

			l.setAdministrateur(gestionPersonnel.getRoot());
			assertEquals(gestionPersonnel.getRoot(), l.getAdministrateur());
			assertFalse(e2.estAdmin(l));
		} finally {
			l.remove();
		}
	}

	@Test
	void removeRootThrowsException()
	{
		assertThrows(ImpossibleDeSupprimerRoot.class, () -> gestionPersonnel.getRoot().remove());
	}
}
