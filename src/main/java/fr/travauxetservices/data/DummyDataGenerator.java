package fr.travauxetservices.data;

import fr.travauxetservices.MyVaadinUI;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Phobos on 13/12/14.
 */
public class DummyDataGenerator {
    public synchronized static void create() {
        EntityManager em = Persistence.createEntityManagerFactory(MyVaadinUI.PERSISTENCE_UNIT).createEntityManager();
        em.getTransaction().begin();

        List<Category> categories = getCategories();
        for (Category category : categories) {
            em.persist(category);
        }

        List<Division> divisions = getDivisions();
        for (Division division : divisions) {
            em.persist(division);
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

        User c1 = new User(Role.ADMIN, Gender.MR, "Thierry", "Linxe", "tlinxe@email.fr", "tlinxe", image);
        em.persist(c1);
        User c2 = new User(Role.CUSTOMER, Gender.MR, "Prénom2", "Nom2", "pn2@email.fr", "mdp", null);
        em.persist(c2);
        User c3 = new User(Role.CUSTOMER, Gender.MR, "Prénom3", "Nom3", "pn3@email.fr", "mdp", null);
        em.persist(c3);

        Request r1 = new Request(new Date(System.currentTimeMillis()), c1, "Jardinage", "Recherche un jardinier pour tondre ma pelouse", categories.get(0), divisions.get(0), 0, Rate.EXCHANGE);
        em.persist(r1);
        Request r2 = new Request(new Date(System.currentTimeMillis()), c2, "Peinture", "Recherche un peintre", categories.get(1), divisions.get(3), (double) 30, Rate.TASK);
        em.persist(r2);
        Request r3 = new Request(new Date(System.currentTimeMillis()), c3, "Garde", "Garde d'enfant pour un petit con agé de 11 ans qui me pourri la vie", categories.get(0), divisions.get(1), (double) 10, Rate.TIME);
        em.persist(r3);

        Offer o1 = new Offer(new Date(System.currentTimeMillis()), c1, "Jardinage", "Jardinier disponible tous les WE pour la tonte de votre pelouse de merde", categories.get(0), divisions.get(5), 0, Rate.TASK);
        em.persist(o1);
        Offer o2 = new Offer(new Date(System.currentTimeMillis()), c2, "Peinture", "Offre tout travail de peinture et j'aime particulièrement repeindre les chiotes", categories.get(1), divisions.get(6), (double) 30, Rate.PART);
        em.persist(o2);
        Offer o3 = new Offer(new Date(System.currentTimeMillis()), c3, "Garde", "Je garde vos enfants, même les petits cons", categories.get(0), divisions.get(2), (double) 10, Rate.TIME);
        em.persist(o3);
        Offer o4 = new Offer(new Date(System.currentTimeMillis()), c3, "Electricité", "Tout travail d'électricité", categories.get(0), divisions.get(9), (double) 10, Rate.TIME);
        em.persist(o4);

        em.getTransaction().commit();
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

    private static List<Division> getDivisions() {
        JSONParser parser = new JSONParser();
        try {
            Integer count = 0;
            JSONObject jsonObject = (JSONObject) parser.parse(IOToolkit.getResourceAsText("/model/divisions.json"));
            return getDivisions(jsonObject, count, null);
        } catch (ParseException pe) {
            System.out.println(pe);
        }
        return new ArrayList<Division>();
    }

    private static List<Division> getDivisions(JSONObject o, int count, Division parent) throws ParseException {
        List<Division> values = new ArrayList<Division>();
        JSONArray jsonArray = (JSONArray) o.get("divisions");
        if (jsonArray != null) {
            Iterator i = jsonArray.iterator();
            while (i.hasNext()) {
                JSONObject object = (JSONObject) i.next();
                Object id = object.get("id");
                Object name = object.get("name");
                if (name != null) {
                    Division newDivision = new Division(id.toString(), name.toString(), parent);
                    values.add(newDivision);
                    values.addAll(getDivisions(object, count, newDivision));
                }
            }
        }
        return values;
    }
}
