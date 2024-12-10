/*
 * Copyright Universidad Valladolid
 */

/*
 * Implementacion de la clase Juego
 * @author javcalv
 */
import java.util.HashSet;
import java.util.Set;

public class Juego {

    private Tablero tablero;
    //Segun agregues un simbolo, agregas un nuevo jugador
    private final char[] simbolos = {'#', '*'};
    private final Jugador[] jugadores;

    // Clase privada Tablero para gestionar el propio tablero
    private class Tablero{
        private final String[] caracteresIniciales = {"AZ", "az", "09"};
        private final char esquina = '•';
        private final String[] paredes = {"|", "---"};
        private String[][] tablero;

        /**
         * Constructor de la clase privada Tablero
         * @param a numero de filas
         * @param b numero de columnas
         */
        public Tablero(int a, int b){
            this.tablero = new String[a*2 +1][b*2+1];
            this.generarTablero();
        }

        /**
         *  Metodo para colocar las esquinas 
         */
        private void generarTablero(){
            for (int i = 0; i<this.tablero.length; i+=2)
                for (int j = 0; j<this.tablero[0].length; j+=2)
                    this.tablero[i][j] = String.valueOf(esquina);
        }

        /**
         * Metodo que sirve para imprimir el tablero
         */
        public void imprimirTablero(){
            for (int i = 0; i<tablero.length; i++)
                for (int j = 0; j<tablero[0].length; j++){
                    String celda = (tablero[i][j] == null) ? "" : tablero[i][j];
                    System.out.print(celda + "\t");
                }
                System.out.println();
        }

        /**
         * Metodo que comprueba si el tablero esta completo o no
         * @return True si el tablero esta incompleto o false si esta completo
         */
        public boolean tableroInompletado() {
            boolean centinela = false;
            for (int i = 0; i < this.tablero.length && !centinela; i++) {
                for (int j = 0; j < this.tablero[i].length && !centinela; j++) {
                    if (this.tablero[i][j] == null) {
                        centinela = true; // Se encontró una celda vacía
                    }
                }
            }
            return centinela;
        }
        
    }

    //Clase privada Jugador, para gestionar sus propiedades:
    private class Jugador{
        private int numJugador;
        private int casillasGanadas = 0;
        private char simbolo;
        private boolean turno = false;

        public Jugador(int numJugador, char simbolo){
            this.numJugador = numJugador;
            this.simbolo = simbolo;
        }

        /**
         * Metodo que devuelve el simbolo del jugador
         * @return Simbolo del jugador
         */
        public char getSimbolo(){
            return this.simbolo;
        }

        /**
         * Metodo que devuelve las casilas ganadas del jugador
         * @return Casillas ganadas del Jugador
         */
        public int getCasillasGanadas(){
            return this.casillasGanadas;
        }

        /**
         * Metodo que devuelve el numero del jugador
         * @return Numero del jugador (ID)
         */
        public int getNumJugador(){
            return numJugador;
        }

        /**
         * Metodo que sirve para alternar el turno a un jugador
         */
        public void alternarTurno(){
            this.turno = !this.turno;
        }

        /**
         * Metodo para incrementar las casillas ganadas
         * @param a Numero de casillas ganadas
         */
        public void incrementarCasillas(int a){
            this.casillasGanadas+=a;
        }

    }

    /**
     * Constructor de la clase Juego
     * @param a dimension de las filas
     * @param b dimension de las columnas
     * @throws IllegalArgumentException si hay menos de 2 jugadores
     * @throws IllegalArgumentException si los jugadores tienen simbolos iguales
     */
    public Juego(int a, int b){
        //Comprobamos que haya al menos 2 jugadores
        if(this.simbolos.length < 2)
            throw new IllegalArgumentException("El numero de jugadores debe ser minimo 2");

        if(tieneDuplicados(this.simbolos))
            throw new IllegalArgumentException("Los jugadores tienen simbolos iguales");

        this.tablero = new Tablero(a,b);
        this.jugadores = new Jugador[this.simbolos.length];

        //Inicializamos los jugadores
        for (int i = 0; i<this.simbolos.length; i++)
            this.jugadores[i] = new Jugador(i + 1, this.simbolos[i]);
    }

    /**
     * Metodo que comprueba si hay simbolos repetidos (Jugadores)
     * @param array Array de caracteres a comprobar
     * @return true si tiene caracteres duplicados o false si no los tiene
     */
    private boolean tieneDuplicados(char[] array) {
        Set<Character> conjunto = new HashSet<>();
        for (char c : array) {
            if (!conjunto.add(c)) {
                return true;
            }
        }
        return false;
    }
}