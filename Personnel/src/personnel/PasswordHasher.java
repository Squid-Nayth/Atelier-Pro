package personnel;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Classe utilitaire pour gérer le hachage et la vérification des mots de passe.
 * Utilise BCrypt pour une sécurité renforcée.
 */
public class PasswordHasher
{
	private static final int LOG_ROUNDS = 12; // Nombre de rounds pour le hachage

	/**
	 * Hache un mot de passe en clair.
	 * @param plainPassword le mot de passe en clair
	 * @return le mot de passe haché
	 */
	public static String hashPassword(String plainPassword)
	{
		if (plainPassword == null || plainPassword.isEmpty())
			return "";
		return BCrypt.hashpw(plainPassword, BCrypt.gensalt(LOG_ROUNDS));
	}

	/**
	 * Vérifie qu'un mot de passe en clair correspond au mot de passe haché.
	 * @param plainPassword le mot de passe en clair
	 * @param hashedPassword le mot de passe haché
	 * @return true si les mots de passe correspondent
	 */
	public static boolean checkPassword(String plainPassword, String hashedPassword)
	{
		if (plainPassword == null || hashedPassword == null)
			return false;
		return BCrypt.checkpw(plainPassword, hashedPassword);
	}
}
