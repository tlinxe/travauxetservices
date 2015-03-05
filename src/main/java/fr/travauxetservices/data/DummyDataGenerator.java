package fr.travauxetservices.data;

import fr.travauxetservices.AppUI;
import fr.travauxetservices.model.*;
import fr.travauxetservices.tools.IOToolkit;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.*;

/**
 * Created by Phobos on 13/12/14.
 */
public class DummyDataGenerator {
    public synchronized static void create() {
        try {
            Configuration configuration = AppUI.getConfiguration();
            EntityManager em = Persistence.createEntityManagerFactory(AppUI.PERSISTENCE_UNIT, configuration.getProperties()).createEntityManager();
            em.getTransaction().begin();

            List<Category> categories = getCategories();
            for (Category category : categories) {
                em.persist(category);
            }

            List<Location> locations = getLocations();
            for (Location location : locations) {
                em.persist(location);
            }

            byte[] image = null;
            final ClassLoader ld = em.getClass().getClassLoader();
            InputStream is = ld.getResourceAsStream("/images/dell.png");
            if (is != null) {
                try {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    int bytesRead;
                    byte[] buffer = new byte[0x200];
                    while ((bytesRead = is.read(buffer)) != -1) {
                        baos.write(buffer, 0, bytesRead);
                    }
                    image = baos.toByteArray();
                    baos.close();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            City c1 = new City("Bordeaux", "33000", "97", "33", 44.84044, -0.5805);
            em.persist(c1);

            List<User> users = new ArrayList<User>(3);
            User u1 = new User(UUID.randomUUID(), new Date(System.currentTimeMillis()), Role.ADMIN, Gender.MR, "Thierry", "Linxe", "tlinxe@email.fr", "motdepasse", "0614441385", image, true, true, false, new HashSet<Rating>());
            em.persist(u1);
            users.add(u1);
            User u2 = new User(UUID.randomUUID(), new Date(System.currentTimeMillis()), Role.CUSTOMER, null, null, "Gaz de Bordeaux", "pn2@email.fr", "motdepasse", null, null, true, true, true, new HashSet<Rating>());
            em.persist(u2);
            users.add(u2);
            Rating s1 = new Rating(UUID.randomUUID(), new Date(System.currentTimeMillis()), u1, "Entretien annuel chaudière", "Date de rdv prise rapidement et surtout Ponctuel! Explications de ce qui est fait et doit éventuellement être fait tout au long de l'entretien de la chaudière. Le prix pour un entretien annuel est bien celui qui est indiqué sur le site (77 euros).", 2, 4, 3, 2, 3, 4);
            em.persist(s1);
            Rating s2 = new Rating(UUID.randomUUID(), new Date(System.currentTimeMillis()), u2, null, "Excellent", 5, 0, 0, 0, 0, 0);
            em.persist(s2);
            User u3 = new User(UUID.randomUUID(), new Date(System.currentTimeMillis()), Role.CUSTOMER, null, null, "Saunier Duval AGP Atelier Gaz Professionnel", "pn3@email.fr", "motdepasse", "0612335678", null, true, true, true, new HashSet<Rating>(Arrays.asList(s1, s2)));
            em.persist(u3);
            users.add(u3);
            User u4 = new User(UUID.randomUUID(), new Date(System.currentTimeMillis()), Role.CUSTOMER, null, null, "Linxe", "tomcat33000@hotmail.fr", "motdepasse", "0542123695", null, false, true, true, new HashSet<Rating>());
            em.persist(u4);
            users.add(u4);
            User u5 = new User(UUID.randomUUID(), new Date(System.currentTimeMillis()), Role.CUSTOMER, null, null, "Vilbois", "benjaminvilbois@gmail.com", "motdepasse", "0542123695", null, false, true, true, new HashSet<Rating>());
            em.persist(u5);
            users.add(u5);

            Calendar cal = Calendar.getInstance();
            Request r1 = new Request(UUID.randomUUID(), cal.getTime(), u1, "Jardinage", "Recherche un jardinier pour tondre ma pelouse", categories.get(0), locations.get(0), c1, 0, Remuneration.EXCHANGE, true, 0);
            em.persist(r1);
            cal.add(Calendar.DATE, -1);
            Request r2 = new Request(UUID.randomUUID(), cal.getTime(), u3, "Peinture", "Recherche un peintre", categories.get(1), locations.get(0), c1, (double) 30.50, Remuneration.TASK, true, 0);
            em.persist(r2);
            cal.add(Calendar.DATE, -1);
            Request r3 = new Request(UUID.randomUUID(), cal.getTime(), u4, "Garde", "Garde d'enfant pour un petit con agé de 11 ans qui me pourri la vie", categories.get(0), locations.get(1), null, (double) 10, Remuneration.TIME, false, 0);
            em.persist(r3);

            Random random = new Random();
            String text = "Jarrdinier qualifié,je propose mes services pour l'entretien général des parcs et jardins sur le secteur Nord-Bassin.\n" +
                    "Tonte,rotofil,entretien des massifs,taille et rabattage de haie,petit élagage,débroussaillage,...etc.\n" +
                    "je me déplace avec matériel et assure l'évacuation en déchetterie.Travail soigné.\n" +
                    "Rémunération en CESU acceptée";
            Offer o1 = new Offer(UUID.randomUUID(), new Date(System.currentTimeMillis()), users.get(random.nextInt(users.size())), "Offre " + 0, text, categories.get(random.nextInt(categories.size())), locations.get(3), c1, 10.50, Remuneration.TIME, true, 5);
            em.persist(o1);
            for (int i = 1; i < 100; i++) {
                cal = Calendar.getInstance();
                cal.add(Calendar.DATE, -random.nextInt(365));
                User user = users.get(random.nextInt(users.size()));
                Offer o2 = new Offer(UUID.randomUUID(), cal.getTime(), user, "Offre " + i, text, categories.get(random.nextInt(categories.size())), locations.get(random.nextInt(locations.size())), null, random.nextDouble(), Remuneration.TIME, (!user.equals(u4)), 0);
                em.persist(o2);
            }

            em.getTransaction().commit();
        } catch (Exception e) {
            //Ignored
        }
    }

    private static List<Category> getCategories() {
        JSONParser parser = new JSONParser();
        try {
            JSONObject jsonObject = (JSONObject) parser.parse(IOToolkit.getResourceAsText("/model/categories.json"));
            return getCategories(jsonObject, null);
        } catch (ParseException pe) {
            System.out.println(pe);
        }
        return new ArrayList<Category>();
    }

    private static List<Category> getCategories(JSONObject o, Category parent) throws ParseException {
        List<Category> values = new ArrayList<Category>();
        JSONArray jsonArray = (JSONArray) o.get("categories");
        if (jsonArray != null) {
            Iterator i = jsonArray.iterator();
            while (i.hasNext()) {
                JSONObject object = (JSONObject) i.next();
                Object name = object.get("name");
                if (name != null) {
                    Category newCategory = new Category(name.toString(), parent);
                    values.add(newCategory);
                    values.addAll(getCategories(object, newCategory));
                }
            }
        }
        return values;
    }

    private static List<Location> getLocations() {
        JSONParser parser = new JSONParser();
        try {
            Integer count = 0;
            JSONObject jsonObject = (JSONObject) parser.parse(IOToolkit.getResourceAsText("/model/locations.json"));
            return getLocations(jsonObject, count, null);
        } catch (ParseException pe) {
            System.out.println(pe);
        }
        return new ArrayList<Location>();
    }

    private static List<Location> getLocations(JSONObject o, int count, Location parent) throws ParseException {
        List<Location> values = new ArrayList<Location>();
        JSONArray jsonArray = (JSONArray) o.get("locations");
        if (jsonArray != null) {
            Iterator i = jsonArray.iterator();
            while (i.hasNext()) {
                JSONObject object = (JSONObject) i.next();
                Object id = object.get("id");
                Object name = object.get("name");
                if (name != null) {
                    Location newLocation = new Location(id.toString(), name.toString(), parent);
                    values.add(newLocation);
                    values.addAll(getLocations(object, count, newLocation));
                }
            }
        }
        return values;
    }
}
