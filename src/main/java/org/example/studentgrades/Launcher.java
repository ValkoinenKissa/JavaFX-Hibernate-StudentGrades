package org.example.studentgrades;

import javafx.application.Application;
import models.User;
import models.UserType;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

public class Launcher {
    public static void main(String[] args) {
        Application.launch(HelloApplication.class, args);

        User user = new User(
                "alberto",
                "1234",
                "Alberto",
                "Bollo", UserType.ESTUDIANTE);

        SessionFactory sf = new Configuration().configure().buildSessionFactory();

        Session session = sf.openSession();

        Transaction transaction = session.beginTransaction();

        session.persist(user);

        transaction.commit();

        session.close();
        sf.close();

    }
}
