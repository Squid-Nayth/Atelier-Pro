package personnel;

import java.sql.Date;

public class LocalDate {
	private LocalDate dateArrivee ;
	private LocalDate dateDepart ;
	
	LocalDate(LocalDate dateArrivee, LocalDate dateDepart){
		this.dateArrivee = dateArrivee;
		this.dateDepart = dateDepart;
	}
	
	public void setDateArrivee(LocalDate dateArrivee) {
		this.dateArrivee = dateArrivee;
	}
	public void setDateDepart(LocalDate dateDepart) {
		this.dateDepart = dateDepart;
	}
	public LocalDate getDateArrivee() {
		return dateArrivee;
	}
	public LocalDate setDateDepart() {
		return dateDepart;
	}
	
	
	
	
}
