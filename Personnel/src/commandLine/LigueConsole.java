package commandLine;

import static commandLineMenus.rendering.examples.util.InOut.getString;

import java.text.DateFormat;
import java.time.LocalDate;
import java.util.ArrayList;

import commandLineMenus.List;
import commandLineMenus.Menu;
import commandLineMenus.Option;

import personnel.*;

public class LigueConsole 
{
	private GestionPersonnel gestionPersonnel;
	private EmployeConsole employeConsole;

	public LigueConsole(GestionPersonnel gestionPersonnel, EmployeConsole employeConsole)
	{
		this.gestionPersonnel = gestionPersonnel;
		this.employeConsole = employeConsole;
	}

	Menu menuLigues()
	{
		Menu menu = new Menu("Gérer les ligues", "l");
		menu.add(afficherLigues());
		menu.add(ajouterLigue());
		menu.add(selectionnerLigue());
		menu.addBack("q");
		return menu;
	}

	private Option afficherLigues()
	{
		return new Option("Afficher les ligues", "l", () -> {System.out.println(gestionPersonnel.getLigues());});
	}

	private Option afficher(final Ligue ligue)
	{
		return new Option("Afficher la ligue", "l", 
				() -> 
				{
					System.out.println(ligue);
					System.out.println("administrée par " + ligue.getAdministrateur());
				}
		);
	}
	private Option afficherEmployes(final Ligue ligue)
	{
		return new Option("Afficher les employes", "l", () -> {System.out.println(ligue.getEmployes());});
	}

	private Option ajouterLigue()
	{
		return new Option("Ajouter une ligue", "a", () -> 
		{
			try
			{
				gestionPersonnel.addLigue(getString("nom : "));
			}
			catch(SauvegardeImpossible exception)
			{
				System.err.println("Impossible de sauvegarder cette ligue");
			}
		});
	}
	
	private Menu editerLigue(Ligue ligue)
	{
		Menu menu = new Menu("Editer " + ligue.getNom());
		menu.add(afficher(ligue));
		menu.add(gererEmployes(ligue));
		menu.add(changerAdministrateur(ligue));
		menu.add(changerNom(ligue));
		menu.add(supprimer(ligue));
		menu.addBack("q");
		return menu;
	}

	private Option changerNom(final Ligue ligue)
	{
		return new Option("Renommer", "r", 
				() -> {ligue.setNom(getString("Nouveau nom : "));});
	}

	private List<Ligue> selectionnerLigue()
	{
		return new List<Ligue>("Sélectionner une ligue", "e", 
				() -> new ArrayList<>(gestionPersonnel.getLigues()),
				(element) -> editerLigue(element)
				);
	}
	
	private Option ajouterEmploye(final Ligue ligue)
	{
		return new Option("ajouter un employé", "a",
				() -> 
				{
					try {
						ligue.addEmploye(getString("nom : "), 
								getString("prenom : "), getString("mail : "), 
								getString("password : "),LocalDate.parse(getString("date d'arrivée (yyyy-mm-dd) : ")),
								LocalDate.parse(getString("date de départ (yyyy-mm-dd) : ")));
						
					} catch (DateIncoherenteException e) {
						System.out.println("\n Les dates d'arrivée et/ou de départ sont incohérentes ,reommencer s'il vous plait :");
						
					}catch (java.time.format.DateTimeParseException e) {
						System.out.println("\n Le format de la date est incohérente ,veuillez la tapez dans le format suivant : yyyy-mm-dd ");
					}
					
				}
		);
	}
	
	private Menu gererEmployes(Ligue ligue)
	{
		Menu menu = new Menu("Gérer les employés de " + ligue.getNom(), "e");
		menu.add(afficherEmployes(ligue));
		menu.add(ajouterEmploye(ligue));
		menu.add(gererUnEmploye(ligue));
		menu.addBack("q");
		return menu;
	}

	private List<Employe> gererUnEmploye(final Ligue ligue)
	{
		return new List<Employe>("Gérer un employé", "g",
				() -> new ArrayList<>(ligue.getEmployes()),
				(employe) -> {
					Menu menu = new Menu("Gérer " + employe.getNom());
					// reuse existing EmployeConsole menu for editing
					menu.add(employeConsole.editerEmploye(employe));
					// add delete option
					menu.add(new Option("Supprimer l'employé", "s", () -> { employe.remove(); }));
					menu.addBack("q");
					return menu;
				}
				);
	}
	
	private List<Employe> changerAdministrateur(final Ligue ligue)
	{
		return new List <>("Changer l'administrateur" , "c",
				() -> new ArrayList <>(ligue.getEmployes()),
				(index, element) -> {
					ligue.setAdministrateur(element);
					System.out.println(element.getPrenom() + " " + element.getNom() +
							"est le nouvel administratuer de " + ligue.getNom());
				});
	}		

	private Option supprimer(Ligue ligue)
	{
		return new Option("Supprimer", "d", () -> {ligue.remove();});
	}
	
}