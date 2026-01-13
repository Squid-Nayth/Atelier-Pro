package personnel;


public class LocalDate {
	private java.time.LocalDate dateArrivee ;
	private java.time.LocalDate dateDepart ;
	
	public LocalDate(java.time.LocalDate dateArrivee, java.time.LocalDate dateDepart){
		if (dateArrivee == null|| dateDepart == null) {
			throw new IllegalArgumentException("Les dates d'arrivée et de départ ne peuvent pas être null");
		}
		if (dateDepart.isBefore(dateArrivee)) {
			throw new IllegalArgumentException("La date de départ ne peut être antérieure à la date d'arrivée");
			
			
		}
		
		this.dateArrivee = dateArrivee;
		this.dateDepart = dateDepart;
	}
	
	public void setDateArrivee(java.time.LocalDate dateArrivee) {
		if(dateDepart == null) {
			throw new IllegalArgumentException("La date d'arrivée ne peut elle null");
		}
		
		if (this.dateDepart != null && dateArrivee.isAfter(this.dateDepart)) {
			throw new IllegalArgumentException("La nouvelle date d'arriver( " + dateArrivee + " ) ne peut pas être postérieure à la date de départ (" + this.dateDepart + ")");
			
		}
		this.dateArrivee = dateArrivee;
	}
	public void setDateDepart(java.time.LocalDate dateDepart) {
		if(dateDepart == null) {
			throw new IllegalArgumentException("La date de départ ne peut être null ");
			
		}
		
		if (this.dateArrivee != null && dateDepart.isBefore(this.dateArrivee)) {
			throw new IllegalArgumentException("La nouvelle date de départ(" + dateDepart + ") ne peut pas être antérieure à la date d'arrivée(" + this.dateArrivee + ")" );
		}
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
