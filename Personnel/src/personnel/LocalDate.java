package personnel;


public class LocalDate {
	private java.time.LocalDate dateArrivee ;
	private java.time.LocalDate dateDepart ;
	
	public LocalDate(java.time.LocalDate dateArrivee, java.time.LocalDate dateDepart){
		this.dateArrivee = dateArrivee;
		this.dateDepart = dateDepart;
	}
	
	public void setDateArrivee(java.time.LocalDate dateArrivee) {
		this.dateArrivee = dateArrivee;
	}
	public void setDateDepart(java.time.LocalDate dateDepart) {
		this.dateDepart = dateDepart;
	}
	public java.time.LocalDate getDateArrivee() {
		return dateArrivee;
	}
	public java.time.LocalDate getDateDepart() {
		return dateDepart;
	}
	
	@Override
	public String toString()
	{
		return getDateArrivee() + " " + getDateDepart() ;
	}
	
	
	
	
}
