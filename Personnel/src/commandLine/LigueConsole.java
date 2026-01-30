package commandLine;

import static commandLineMenus.rendering.examples.util.InOut.getString;

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
		//menu.add(changerAdministrateur(ligue));
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
					ligue.addEmploye(getString("nom : "), 
						getString("prenom : "), getString("mail : "), 
						getString("password : "));
				}
		);
	}
	
	private Menu gererEmployes(Ligue ligue)
	{
		Menu menu = new Menu("Gérer les employés de " + ligue.getNom(), "e");
		menu.add(afficherEmployes(ligue));
		menu.add(ajouterEmploye(ligue));
		menu.add(gererUnEmploye(ligue));
		// j'ai remplacé les 3 options d'avant par "gererUnEmploye".
		menu.addBack("q");
		return menu;
	}

	private List<Employe> supprimerEmploye(final Ligue ligue)
	{
		return new List<>("Supprimer un employé", "s", 
				() -> new ArrayList<>(ligue.getEmployes()),
				(index, element) -> {element.remove();}
				);
	}
	
	private List<Employe> changerAdministrateur(final Ligue ligue)
	{
		return null;
	}

	private List<Employe> modifierEmploye(final Ligue ligue)
	{
		return new List<>("Modifier un employé", "e", 
				() -> new ArrayList<>(ligue.getEmployes()),
				employeConsole.editerEmploye()
				);
	}
	
	private Option supprimer(Ligue ligue)
	{
		return new Option("Supprimer", "d", () -> {ligue.remove();});
	}
	
	// Afficher une liste d'employés, puis pour l'employé sélectionné, afficher un menu permettant de modifier ou de supprimer l'employé.
	private List<Employe> gererUnEmploye(final Ligue ligue)
	{
		return new List<>("Gérer un employé", "g",
				() -> new ArrayList<>(ligue.getEmployes()),
				(employe) -> {
					Menu menu = new Menu("Gérer " + employe.getNom());
					// option to edit (delegates to EmployeConsole)
					menu.add(employeConsole.editerEmploye(employe));
					// option to delete
					menu.add(new Option("Supprimer l'employé", "s", () -> { employe.remove(); }));
					menu.addBack("q");
					return menu;
				}
				);
	}

}