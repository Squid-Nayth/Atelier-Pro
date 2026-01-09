package personnel;

import java.sql.Date;

public class LocalDate {
	private Date dateArrivee ;
	private Date dateDepart ;
	
	LocalDate(Date dateArrivee, Date dateDepart){
		this.dateArrivee = dateArrivee;
		this.dateDepart = dateDepart;
	}
	
	public void setDateArrivee(Date dateArrivee) {
		this.dateArrivee = dateArrivee;
	}
	public void setDateDepart(Date dateDepart) {
		this.dateDepart = dateDepart;
	}
	public Date getDateArrivee() {
		return dateArrivee;
	}
	public Date setDateDepart() {
		return dateDepart;
	}
	
	
	
	
}
