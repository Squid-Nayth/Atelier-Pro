package testsUnitaires;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Test;

import personnel.*;

import java.time.LocalDate;

/**
 * Tests sur la levée et la propagation de DateIncoherenteException.
 */
class TestDateIncoherenteException {
	GestionPersonnel gp = GestionPersonnel.getGestionPersonnel();

	@Test
	void addEmployeWithNullArrivalThrows() throws SauvegardeImpossible {
		Ligue l = gp.addLigue("tu-exc-1");
		try {
			LocalDate arrival = null;
			LocalDate depart = LocalDate.of(2023, 1, 1);
			assertThrows(DateIncoherenteException.class, () -> l.addEmploye("Nom", "Prenom", "m@mail", "pwd", arrival, depart));
		} finally {
			l.remove();
		}
	}

	@Test
	void addEmployeWithArrivalAfterDepartThrowsAndNotSwallowed() throws SauvegardeImpossible {
		Ligue l = gp.addLigue("tu-exc-2");
		try {
			LocalDate arrival = LocalDate.of(2024, 5, 1);
			LocalDate depart = LocalDate.of(2023, 5, 1);
			assertThrows(DateIncoherenteException.class, () -> l.addEmploye("Nom2", "Prenom2", "m2@mail", "pwd2", arrival, depart));
		} finally {
			l.remove();
		}
	}

	@Test
	void validCreationDoesNotThrow() throws SauvegardeImpossible {
		Ligue l = gp.addLigue("tu-exc-3");
		try {
			LocalDate arrival = LocalDate.of(2020, 1, 1);
			LocalDate depart = LocalDate.of(2022, 1, 1);
			assertDoesNotThrow(() -> l.addEmploye("Valid", "User", "v@mail", "pwd", arrival, depart));
		} finally {
			l.remove();
		}
	}
}
