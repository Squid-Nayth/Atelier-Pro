package testsUnitaires;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import personnel.*;

import java.time.LocalDate;

/**
 * Tests pour différents scénarios d'incohérence de dates
 */
class TestDatesincoherentes {
	GestionPersonnel gp = GestionPersonnel.getGestionPersonnel();

	@Test
	void addEmployeWithNullDepartThrows() throws SauvegardeImpossible {
		Ligue l = gp.addLigue("tdi-1");
		try {
			LocalDate arrival = LocalDate.of(2022, 1, 1);
			LocalDate depart = null;
			assertThrows(DateIncoherenteException.class, () -> l.addEmploye("N", "P", "m@mail", "pwd", arrival, depart));
		} finally {
			l.remove();
		}
	}

	@Test
	void settersRaiseWhenIncoherent() throws SauvegardeImpossible, DateIncoherenteException {
		Ligue l = gp.addLigue("tdi-2");
		try {
			LocalDate arrival = LocalDate.of(2020, 1, 1);
			LocalDate depart = LocalDate.of(2021, 1, 1);
			Employe e = l.addEmploye("N2", "P2", "m2@mail", "pwd2", arrival, depart);

			LocalDate badArrival = LocalDate.of(2022, 1, 1);
			assertThrows(DateIncoherenteException.class, () -> e.setDateArrivee(badArrival));

			LocalDate badDepart = LocalDate.of(2019, 1, 1);
			assertThrows(DateIncoherenteException.class, () -> e.setDateDepart(badDepart));

		} finally {
			l.remove();
		}
	}
}
