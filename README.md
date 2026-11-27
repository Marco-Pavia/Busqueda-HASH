# Búsqueda por Funciones Hash en Java: Tabla Hash con Exploración Lineal

Implementación en Java de una **tabla hash** sobre un arreglo (`Producto[]`) con función hash por división y resolución de colisiones mediante **exploración lineal** (*linear probing*). El método `TablaHash.insertar` calcula el índice de cada producto y, si la casilla está ocupada, avanza de forma circular hasta encontrar un espacio libre; el método `TablaHash.buscar` repite el mismo recorrido hasta hallar el código o toparse con un `null`. El programa principal compara el tiempo de búsqueda contra la **búsqueda secuencial** y la **búsqueda binaria** usando `System.nanoTime()`.

**Autor:** [Tu nombre completo]
**Materia:** AED-1026 Estructura de Datos — Unidad 6: Búsqueda por Funciones Hash
**Docente:** Víctor Hugo Vásquez Herrera
**Institución:** Instituto Tecnológico Superior de Xalapa

## Lenguaje de programación

**Java** (JDK 8 o superior; se recomienda JDK 17 o superior). No requiere librerías externas: solo clases nativas del lenguaje.

## Estructura del proyecto

```text
Busqueda-Hash/
├── src/
│   └── Busqueda_hash/
│       ├── Producto.java
│       ├── TablaHash.java
│       └── Main.java
├── .gitignore
└── README.md
```

| Archivo | Descripción |
|---|---|
| `Producto.java` | Modelo de datos: `codigo` (la clave), `nombre` y `precio`, con sus getters y `toString`. |
| `TablaHash.java` | Arreglo `Producto[]` de tamaño `TAM`; contiene la función hash privada `hash`, `insertar` y `buscar` con exploración lineal, contadores de colisiones y de pasos, e `imprimir`. |
| `Main.java` | Programa de prueba: resuelve la prueba de escritorio (TAM = 7), genera 10,000 productos, implementa la búsqueda secuencial y la binaria, y mide los tres métodos con `System.nanoTime()`. También compara TAM = 15013 contra TAM = 10007. |

Todas las clases pertenecen al paquete `Busqueda_hash`, por lo que la carpeta del paquete debe conservar ese nombre y el programa se ejecuta con el nombre completo (`Busqueda_hash.Main`). La carpeta `out/` y las carpetas del IDE (`.idea/`) son locales y no es necesario subirlas al repositorio.

## Fundamento teórico

### Tabla hash y función hash por división

Una tabla hash guarda cada elemento en la posición que indica una **función hash** aplicada a su **clave**. Como leer `tabla[i]` en un arreglo cuesta lo mismo para cualquier `i`, la búsqueda no recorre el arreglo: calcula el índice y va directo a esa casilla (O(1) en promedio).

```text
h(k) = |k| mod TAM
```

| Elemento | Explicación |
|---|---|
| **TAM primo** (p. ej. 15013) | Reparte las claves de manera uniforme y evita que patrones en los códigos (múltiplos de 10, por ejemplo) se concentren en pocas casillas. |
| **TAM > cantidad de datos** | Con n datos, el factor de carga es α = n / TAM. Debe ser menor que 1 (idealmente ≤ 0.7) para que existan casillas `null` que detengan las búsquedas. |
| **Valor absoluto** | El operador `%` de Java conserva el signo del dividendo (`-7 % 5 = -2`). Sin el valor absoluto, un código negativo daría un índice negativo. |

### Colisiones y exploración lineal

Una **colisión** ocurre cuando dos claves distintas producen el mismo índice (por ejemplo, 15013 y 30026 con TAM = 15013 dan 0). Con exploración lineal se avanza a la siguiente casilla hasta encontrar una libre:

| Paso | Acción |
|---|---|
| 1 | Calcular el índice inicial con la función hash. |
| 2 | Mientras `tabla[indice] != null`, hay colisión: avanzar con `indice = (indice + 1) % TAM`. |
| 3 | **Insertar:** colocar el producto en la primera casilla libre. **Buscar:** devolver el producto si el código coincide, o `null` si se topa con una casilla vacía. |

### Medición de tiempo

`System.nanoTime()` devuelve un `long` en nanosegundos de un reloj de alta resolución. Su valor absoluto no tiene relación con la fecha, por lo que solo sirve para medir diferencias: `fin - inicio`.

### Comparativa

| Criterio | Búsqueda Secuencial | Búsqueda Binaria | Búsqueda por Hash |
|---|---|---|---|
| ¿Requiere datos ordenados? | No | Sí | No |
| Complejidad (promedio) | O(N) | O(log N) | O(1) |
| Velocidad con 10,000 datos | Lenta (hasta 10,000 comparaciones) | Rápida (~14 comparaciones) | Muy rápida (1 a 3 casillas en promedio) |
| Ventaja principal | Simple, funciona con cualquier arreglo | Rápida sin memoria adicional | Acceso casi directo |
| Desventaja principal | Lenta con muchos datos | Exige mantener el orden | Memoria extra, colisiones, peor caso O(N) |

### Código clave de la exploración lineal

```java
// Insertar
int indice = hash(p.getCodigo());
while (tabla[indice] != null) {        // casilla ocupada: colisión
    colisiones++;
    indice = (indice + 1) % TAM;       // avance circular
}
tabla[indice] = p;
```

```java
// Buscar
int indice = hash(codigo);
while (tabla[indice] != null) {
    if (tabla[indice].getCodigo() == codigo) {
        return tabla[indice];          // encontrado
    }
    indice = (indice + 1) % TAM;
}
return null;                           // llegó a un null: no existe
```

### Detalles importantes de la lógica

1. **Avance circular:** se usa `(indice + 1) % TAM` y no `indice++`. Con `indice++`, al llegar a la última casilla se lanzaría `ArrayIndexOutOfBoundsException` y nunca se revisarían las casillas libres del inicio del arreglo.
2. **Cálculo del índice:** se usa `Math.abs(codigo % TAM)` en lugar de `Math.abs(codigo) % TAM`, porque `Math.abs(Integer.MIN_VALUE)` sigue siendo negativo. Ambas expresiones son equivalentes matemáticamente.
3. **Condición de paro:** el `while` termina solo al encontrar el código o un `null`. Si la tabla se llenara por completo, la búsqueda de un dato inexistente nunca terminaría; por eso TAM debe ser mayor que la cantidad de datos.
4. **Factor de carga:** con 10,000 datos, TAM = 10007 deja solo 7 casillas libres (α ≈ 0.9993) y las colisiones se multiplican; con TAM = 15013 (α ≈ 0.67) se mantienen bajas.
5. **Eliminación:** no basta con asignar `null` a una casilla, porque se rompería la cadena de exploración de los elementos que colisionaron después. Esta implementación no incluye eliminación.

## Consideraciones sobre los datos de entrada

- Las claves son enteros (`int`) y pueden ser negativas; la función hash las maneja con valor absoluto.
- La tabla **no admite más elementos que `TAM`**; el programa de prueba usa 10,000 productos, menor que 15013 y que 10007.
- Los códigos de los productos de prueba son únicos (se generan con `HashSet`) y la semilla del generador aleatorio es fija (`2026`), por lo que el número de colisiones es siempre el mismo.
- Los tiempos en nanosegundos **cambian** en cada ejecución y en cada equipo; los conteos de colisiones y de pasos, no.
- Para cambiar los datos de prueba, edita `Main.java` (valor de `N`, semilla o tamaños de tabla) y vuelve a compilar.

## Datos de prueba

| Prueba | Datos | Función hash |
|---|---|---|
| 1 (prueba de escritorio) | `15, 22, 18, 25, 32` en un arreglo de TAM = 7 | `h(k) = k mod 7` |
| 2 (comparativa) | 10,000 productos con códigos aleatorios únicos, TAM = 15013 | `h(k) = \|k\| mod 15013` |
| 3 (comparativa) | Los mismos 10,000 productos, TAM = 10007 | `h(k) = \|k\| mod 10007` |

## Cómo clonar, compilar y ejecutar

### Desde la terminal

1. Clona el repositorio y entra a la carpeta (o descomprime el `.zip` y entra a `Busqueda-Hash/`):
   ```bash
   git clone https://github.com/<tu-usuario>/Busqueda-Hash.git
   cd Busqueda-Hash
   ```
2. Compila desde la carpeta raíz del proyecto (la que contiene `src/`):
   ```bash
   javac -d out src/Busqueda_hash/*.java
   ```
3. Ejecuta el programa:
   ```bash
   java -cp out Busqueda_hash.Main
   ```

En Windows (CMD o PowerShell) los mismos comandos funcionan sin cambios.

### Desde IntelliJ IDEA

1. Abre la carpeta del proyecto con *File → Open*.
2. Marca la carpeta `src` como *Sources Root* (clic derecho → *Mark Directory as → Sources Root*) y verifica el SDK en *File → Project Structure → Project SDK*.
3. Abre `Main.java` y presiona el botón verde ▶ junto al método `main`.
4. La salida aparece en la ventana *Run* de la parte inferior.

## Ejemplo de ejecución

**Salida de `java -cp out Busqueda_hash.Main`** (los tiempos varían en cada equipo):

```text
=== PRUEBA DE ESCRITORIO (TAM = 7) ===
[0] -> vacio
[1] -> 15
[2] -> 22
[3] -> vacio
[4] -> 18
[5] -> 25
[6] -> 32
Colisiones totales: 4

=== BUSQUEDA DEL CODIGO 490306 (N = 10000) ===
Secuencial      : 8478 ns
Binaria         : 1263 ns
Hash (TAM=15013): 980 ns
Hash (TAM=10007): 109255 ns

=== COLISIONES AL INSERTAR 10000 PRODUCTOS ===
TAM = 15013: 10325 colisiones
TAM = 10007: 657075 colisiones
Pasos promedio por busqueda (TAM=15013): 2.03
Pasos promedio por busqueda (TAM=10007): 66.71
```

### Traza de la prueba de escritorio (TAM = 7, h(k) = k mod 7)

| Orden | k | h(k) | Casillas revisadas | Índice final | Colisiones |
|---|---|---|---|---|---|
| 1 | 15 | 1 | [1] libre | 1 | 0 |
| 2 | 22 | 1 | [1] ocupada → [2] libre | 2 | 1 |
| 3 | 18 | 4 | [4] libre | 4 | 0 |
| 4 | 25 | 4 | [4] ocupada → [5] libre | 5 | 1 |
| 5 | 32 | 4 | [4] ocupada → [5] ocupada → [6] libre | 6 | 2 |

Arreglo final: `[ — , 15 , 22 , — , 18 , 25 , 32 ]` (4 colisiones en total; 3 elementos colisionaron).

**Si se ejecuta fuera de la carpeta raíz** (sin el paquete en el classpath):

```text
Error: Could not find or load main class Busqueda_hash.Main
```

El texto exacto de este mensaje depende de la versión de Java.

## Resultados esperados

La columna «Colisiones» es determinista (semilla fija); la columna de tiempo es solo orientativa.

| Prueba | Resultado esperado | Colisiones al insertar | Pasos promedio por búsqueda |
|---|---|---|---|
| 1 (TAM = 7) | `[—, 15, 22, —, 18, 25, 32]` | 4 | No aplica |
| 2 (TAM = 15013) | Hash más rápido que binaria y secuencial | 10,325 | 2.03 |
| 3 (TAM = 10007) | Hash degradado (tabla casi llena) | 657,075 | 66.71 |
