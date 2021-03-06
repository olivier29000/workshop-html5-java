package dev.pizzeria.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Contrôleur responsable du traitement de la réquête : POST /clients.
 */
public class ClientController extends HttpServlet {

	/** serialVersionUID : long */
	private static final long serialVersionUID = 1L;

	private static final Logger LOGGER = LoggerFactory.getLogger(ClientController.class);

	/**
	 * Page HTML de la réponse en cas d'insertion effectuée. Fichier présent
	 * dans le répertoire src/main/resources.
	 */
	public static final String TEMPLATE_CLIENT_INSERE = "templates/client_insere.html";

	List<Client> listeDeClients = new ArrayList<Client>();

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		// récupération du paramètre nom
		// <input name="nom">
		String nom = req.getParameter("nom");
		String prenom = req.getParameter("prenom");
		String ville = req.getParameter("ville");
		String age = req.getParameter("age");

		Client client = new Client(nom, prenom, ville, age);
		listeDeClients.add(client);

		LOGGER.info("Paramètre nom reçu " + nom);
		LOGGER.info("Paramètre prenom reçu " + prenom);
		LOGGER.info("Paramètre ville reçu " + ville);
		LOGGER.info("Paramètre age reçu " + age);
		// TODO insérer un nouveau client en base de données

		for (int i = 0; i < listeDeClients.size(); i++) {
			System.out.println(listeDeClients.get(i).getNom() + " " + listeDeClients.get(i).getPrenom() + " "
					+ listeDeClients.get(i).getVille() + " " + listeDeClients.get(i).getAge());
		}

		try {
			// réponse au format UTF-8 pour le support des accents
			resp.setCharacterEncoding("UTF-8");

			if (nom.equals("") || prenom.equals("") || ville.equals("") || age.equals("")
					|| Controle.nestUnEntier(age)) {
				String templateAjout = Files
						.readAllLines(
								Paths.get(this.getClass().getClassLoader().getResource(TEMPLATE_CLIENT_INSERE).toURI()))
						.stream().collect(Collectors.joining());
				resp.setHeader("Content-Type", "text/html");
				String corpsReponseHTML = templateAjout.replaceAll("AREMPLACER",
						"Tous les champs doivent être renseignés et l'âge doit être un chiffre");
				PrintWriter writer = resp.getWriter();
				writer.write(corpsReponseHTML);
			} else {

				// récupération du contenu du fichier template
				String template = Files
						.readAllLines(
								Paths.get(this.getClass().getClassLoader().getResource(TEMPLATE_CLIENT_INSERE).toURI()))
						.stream().collect(Collectors.joining());

				// écriture dans le corps de la réponse
				PrintWriter writer = resp.getWriter();
				writer.write(template);
				resp.sendRedirect("http://localhost:8080/clients");
			}

		} catch (

		URISyntaxException e) {
			LOGGER.error("Fichier HTML non trouvé", e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.
	 * HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		resp.setHeader("Content-Type", "text/html");
		// écriture dans le corps de la réponse
		PrintWriter writer = resp.getWriter();

		try {

			String nom = "";
			String prenom = "";
			String ville = "";
			String age = "";
			int id;

			String template = Files
					.readAllLines(Paths.get(
							this.getClass().getClassLoader().getResource("templates/AfficherClients.html").toURI()))
					.stream().collect(Collectors.joining());

			StringBuilder sb = new StringBuilder();

			for (int i = 0; i < listeDeClients.size(); i++) {
				nom = listeDeClients.get(i).getNom();
				prenom = listeDeClients.get(i).getPrenom();
				ville = listeDeClients.get(i).getVille();
				age = listeDeClients.get(i).getAge();
				id = listeDeClients.get(i).getId();
				sb.append("<tr><td>" + id + "</td><td>" + nom + "</td><td>" + prenom + "</td><td>" + ville + "</td><td>"
						+ age + "</td><td><a>Modifier</a></td><td><a>Supprimer</a></td></tr>");
				// writer.write("<p>" + listeDeClients.get(i).getPrenom() +
				// "</p>");
				// stations.put("nom", nom);
				// stations.put("adresse", prenom);
			}

			String partieDynamique = sb.toString();
			writer.write(template.replace("PARTIE_VARIABLE", partieDynamique));

		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
