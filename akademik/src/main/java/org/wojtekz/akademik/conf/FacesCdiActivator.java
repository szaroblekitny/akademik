package org.wojtekz.akademik.conf;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.faces.annotation.FacesConfig;

/**
 * Włącznik Jakarta Faces 4 w trybie CDI (Contexts and Dependency Injection).
 * <p>
 * Faces 4.x wymaga CDI. Na Tomcacie dostarcza je biblioteka Weld, ale Weld startuje
 * tylko wtedy, gdy znajdzie w aplikacji choć jeden bean CDI. Nasze beany są beanami
 * Springa (strony korzystają z nich przez {@code SpringBeanFacesELResolver} z
 * {@code faces-config.xml}), więc ta pusta klasa jest jedynym beanem CDI.
 * {@link FacesConfig} to standardowy sposób włączenia Faces 4 przez CDI.
 * <p>
 * Klasa nie jest beanem Springa: {@code @ComponentScan} jej nie obejmuje,
 * a i tak nie ma adnotacji Springa.
 */
@FacesConfig
@ApplicationScoped
public class FacesCdiActivator {
}
