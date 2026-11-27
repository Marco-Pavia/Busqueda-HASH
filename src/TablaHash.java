public class TablaHash {
    private final Producto[] tabla;
    private final int TAM;
    private long colisiones = 0;   // intentos fallidos al insertar
    private long pasos = 0;        // casillas revisadas en la ultima busqueda

    public TablaHash(int tam) {
        this.TAM = tam;
        this.tabla = new Producto[tam];
    }

    // Funcion hash por division: h(k) = |k| mod TAM
    private int hash(int codigo) {
        return Math.abs(codigo % TAM);
    }

    // Insercion con exploracion lineal
    public void insertar(Producto p) {
        int indice = hash(p.getCodigo());
        while (tabla[indice] != null) {        // casilla ocupada = colision
            colisiones++;
            indice = (indice + 1) % TAM;       // avance circular
        }
        tabla[indice] = p;
    }

    // Busqueda con exploracion lineal
    public Producto buscar(int codigo) {
        int indice = hash(codigo);
        pasos = 1;
        while (tabla[indice] != null) {
            if (tabla[indice].getCodigo() == codigo) {
                return tabla[indice];          // encontrado
            }
            indice = (indice + 1) % TAM;
            pasos++;
        }
        return null;                           // topo con null: no existe
    }

    public long getColisiones() { return colisiones; }
    public long getPasos() { return pasos; }

    public void imprimir() {
        for (int i = 0; i < TAM; i++) {
            System.out.println("[" + i + "] -> "
                + (tabla[i] == null ? "vacio" : tabla[i].getCodigo()));
        }
    }
}
