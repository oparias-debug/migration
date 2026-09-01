package sv.gob.mh.siip.config.devseed;

/**
 * Un bloque de datos de prueba para el perfil "dev". Cada dominio implementa el suyo como
 * {@code @Component @Profile("dev")}; {@link sv.gob.mh.siip.config.DevDataSeeder} los descubre
 * todos vía inyección de {@code List<DevSeeder>} y los ejecuta en orden (ver {@code @Order}),
 * así que agregar un catálogo nuevo es agregar una clase, no hacer crecer una existente.
 */
public interface DevSeeder {

    void seed();
}
