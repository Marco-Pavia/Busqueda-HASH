import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;

public class Main {

    // Busqueda secuencial
    static int secuencial(Producto[] a, int codigo) {
        for (int i = 0; i < a.length; i++) {
            if (a[i].getCodigo() == codigo) return i;
        }
        return -1;
    }

    // Busqueda binaria (arreglo ordenado por codigo)
    static int binaria(Producto[] a, int codigo) {
        int bajo = 0, alto = a.length - 1;
        while (bajo <= alto) {
            int medio = (bajo + alto) / 2;
            if (a[medio].getCodigo() == codigo) return medio;
            if (a[medio].getCodigo() < codigo) bajo = medio + 1;
            else alto = medio - 1;
        }
        return -1;
    }

    public static void main(String[] args) {
        // ---------- Prueba de escritorio (TAM = 7) ----------
        System.out.println("=== PRUEBA DE ESCRITORIO (TAM = 7) ===");
        TablaHash t7 = new TablaHash(7);
        int[] claves = {15, 22, 18, 25, 32};
        for (int k : claves) t7.insertar(new Producto(k, "P" + k, 1.0));
        t7.imprimir();
        System.out.println("Colisiones totales: " + t7.getColisiones());

        // ---------- Comparativa con 10,000 productos ----------
        final int N = 10000;
        Random rnd = new Random(2026);
        HashSet<Integer> usados = new HashSet<>();
        Producto[] datos = new Producto[N];
        for (int i = 0; i < N; i++) {
            int cod;
            do { cod = rnd.nextInt(2_000_000) - 1_000_000; }  // incluye negativos
            while (!usados.add(cod));
            datos[i] = new Producto(cod, "Producto" + i, 10 + rnd.nextInt(990));
        }

        TablaHash h15013 = new TablaHash(15013);
        TablaHash h10007 = new TablaHash(10007);
        for (Producto p : datos) { h15013.insertar(p); h10007.insertar(p); }

        Producto[] ordenado = datos.clone();
        Arrays.sort(ordenado, (x, y) -> Integer.compare(x.getCodigo(), y.getCodigo()));

        // ultimo insertado: peor caso para la busqueda secuencial
        int objetivo = datos[N - 1].getCodigo();
        System.out.println("\n=== BUSQUEDA DEL CODIGO " + objetivo
                + " (N = " + N + ") ===");

        // Calentamiento de la JVM
        for (int i = 0; i < 2000; i++) {
            secuencial(datos, objetivo);
            binaria(ordenado, objetivo);
            h15013.buscar(objetivo);
        }

        long inicio = System.nanoTime();
        secuencial(datos, objetivo);
        long fin = System.nanoTime();
        System.out.println("Secuencial      : " + (fin - inicio) + " ns");

        inicio = System.nanoTime();
        binaria(ordenado, objetivo);
        fin = System.nanoTime();
        System.out.println("Binaria         : " + (fin - inicio) + " ns");

        inicio = System.nanoTime();
        h15013.buscar(objetivo);
        fin = System.nanoTime();
        System.out.println("Hash (TAM=15013): " + (fin - inicio) + " ns");

        inicio = System.nanoTime();
        h10007.buscar(objetivo);
        fin = System.nanoTime();
        System.out.println("Hash (TAM=10007): " + (fin - inicio) + " ns");

        System.out.println("\n=== COLISIONES AL INSERTAR " + N + " PRODUCTOS ===");
        System.out.println("TAM = 15013: " + h15013.getColisiones() + " colisiones");
        System.out.println("TAM = 10007: " + h10007.getColisiones() + " colisiones");

        // Promedio de pasos por busqueda exitosa
        long s1 = 0, s2 = 0;
        for (Producto p : datos) {
            h15013.buscar(p.getCodigo()); s1 += h15013.getPasos();
            h10007.buscar(p.getCodigo()); s2 += h10007.getPasos();
        }
        System.out.printf("Pasos promedio por busqueda (TAM=15013): %.2f%n",
                (double) s1 / N);
        System.out.printf("Pasos promedio por busqueda (TAM=10007): %.2f%n",
                (double) s2 / N);
    }
}
