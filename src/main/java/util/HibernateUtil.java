package util;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateUtil {

    // Variable estática que contendrá la unica instancia
    private static final SessionFactory sf;

    // Bloque estático, se ejecuta una sola vez cuando se carga la clase

    static {
        try {
            sf = new Configuration()
                    .configure()
                    .buildSessionFactory();
            System.out.println("Session factory creado correctamnete");
        } catch (Throwable ex){
            System.err.println("Error al crear SessionFactory: " + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    // Función para obtener la unica instancia del sessionFactory

    public static SessionFactory getSessionFactory(){
        return sf;
    }

    // Metodo para cerrar el session factory y liberar recursos al cerrar la app

    public static void shutdown(){
        if (sf != null && !sf.isClosed()){
            sf.close();
            System.out.println("Session factory cerrado");
        }
    }
}
