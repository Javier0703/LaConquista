/*
 * Copyright Universidad Valladolid
 */
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
/*
 * Implementacion de la clase Juego
 * @author javcalv
*/
public class Juego {

    private Tablero tablero;
    private final int NUMERO_JUGADORES = 2;
    private final char[] SIMBOLOS = {'#', '*', '@' , '$'};
    private final String GUARDAR_JUEGO = "**";
    private final Jugador[] JUGADORES;

    // Clase privada Tablero para gestionar el propio tablero
    private class Tablero{
        private final String[] PAREDES = {"|", "---"};
        private final String[] CARACTERES_INICIALES = {"AZ", "az", "09"};
        private final char ESQUINA = 'º';
        private String caracteresConcatenados;
        private String[][] tablero;

        /**
         * Constructor de la clase privada Tablero
         * @param a numero de filas
         * @param b numero de columnas
         */
        public Tablero(int a, int b){
            this.tablero = new String[(a*2) + 1][(b*2) + 1];
            this.caracteresConcatenados = this.concatenarCaraceres();
            this.generarTablero();
        }

        /**
         * Metodo para concatenar los caracteres seleccionados
         * @return caracteres concatenados para posicionarlos en la matriz
         */
        private String concatenarCaraceres(){
            String str = "";
            for (int i = 0; i < this.CARACTERES_INICIALES.length; i++) {
                // Comprobamos como debemos recorrer si adelante o atras
                char primerC = this.CARACTERES_INICIALES[i].charAt(0);
                char segundoC = this.CARACTERES_INICIALES[i].charAt(1);
                if (primerC <= segundoC) {
                    for (char c = primerC; c <= segundoC; c++) {
                        str += c;
                    }
                } else {
                    for (char c = primerC; c >= segundoC; c--) {
                        str += c;
                    }
                }
            }
            return str;
        }

        /**
         *  Metodo para generar el tablero con las esquinas y las celdas disponibles.
         */
        private void generarTablero() {
            int contadorLetras = 0; 
            for (int i = 0; i < this.tablero.length; i++) {
                for (int j = 0; j < this.tablero[0].length; j++) { 
                    //Colocamos las esquinas
                    if (i % 2 == 0 && j % 2 == 0) { 
                        this.tablero[i][j] = String.valueOf(this.ESQUINA);
                    } 
                    //Colocamos las letras
                    else if ((i%2==0 && j%2==1) || (i%2==1 && j%2==0)){
                        this.tablero[i][j] = String.valueOf(this.caracteresConcatenados.charAt(
                            contadorLetras % this.caracteresConcatenados.length()));
                        contadorLetras++;
                    }  
                }
            }
        }

        /*************************
         * METODOS CLASE TABLERO *
         *************************/

        /**
         * Metodo que sirve para imprimir el tablero
         */
        public void imprimirTablero(){
            for (int i = 0; i<tablero.length; i++){
                for (int j = 0; j<tablero[0].length; j++){
                    String celda = (tablero[i][j] == null) ? " " : tablero[i][j];
                    System.out.print(celda + " ");
                }
                System.out.println("");
            }
        }

        /**
         * Metodo que comprueba si el tablero esta completo o no
         * @return True si el tablero esta incompleto o false si esta completo
         */
        public boolean tableroIncompleto() {
            boolean centinela = false;
            for (int i = 0; i < this.tablero.length && !centinela; i++) {
                for (int j = 0; j < this.tablero[i].length && !centinela; j++) {
                    if (this.tablero[i][j] == null) {
                        centinela = true;
                    }
                }
            }
            return centinela;
        }

        /**
         * Metodo que comprueba si la seleccion del jugador se encuentra en el tablero 
         * @param s Casilla que ha seleccionado el jugador
         * @return true si la ha encontrado o false si no;
         */
        public boolean comprobarCasillaSeleccionada(String s){
            if (s.length()!=1) return false;
            if (this.caracteresConcatenados.indexOf(s) == -1) return false;
            boolean encontrado = false;
            for(int i = 0; i<this.tablero.length && !encontrado; i++){
                for(int j = 0; j<this.tablero[0].length && !encontrado; j++){
                    if(this.tablero[i][j] != null && this.tablero[i][j].equals(s)){
                        anyadirPared(i,j);
                        encontrado = true;
                    }
                }
            }
            return encontrado;
        }

        /**
         * Metodo que agrega la pared en la casilla
         * @param i la fila donde se coloca la pared
         * @param j la columna donde se coloca la pared
         */
        private void anyadirPared(int i, int j){
            this.tablero[i][j] = this.PAREDES[j % this.PAREDES.length];
        }

        /**
         * Metodo para comprobar si se han creado cajas y agregarlas
         */
        // TODO 
        public int comprobarCuadradosCompletos(char simbolo){
            return 0;
        }
        
    }

    //Clase privada Jugador, para gestionar sus propiedades:
    class Jugador{

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
         * public int getCasillasGanadas(){
            return this.casillasGanadas;
        }
         */
        public int getCasillasGanadas(){
            return this.casillasGanadas;
        }

        /**
         * Metodo que devuelve el numero del jugador
         * @return Numero del jugador (ID)
         */
        public int getNumJugador(){
            return this.numJugador;
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

        /**
         * Metodo que devuelve el turno del jugador
         * @return Turno jugador
         */
        public boolean turnoJugador(){
            return this.turno;
        }

    }

    /**
     * Constructor de la clase Juego
     * @param a dimension de las filas
     * @param b dimension de las columnas
     * @throws IllegalArgumentException si hay menos de 2 JUGADORES
     * @throws IllegalArgumentException si los JUGADORES tienen SIMBOLOS iguales
     */
    public Juego(int a, int b){
        //Comprobamos que haya al menos 2 JUGADORES y que no tengan simbolos repetidos
        if(this.NUMERO_JUGADORES< 2 || this.NUMERO_JUGADORES > 4) 
            throw new IllegalArgumentException("El numero de JUGADORES debe ser entre 2 - 4");
        if(tieneDuplicados(this.SIMBOLOS))
            throw new IllegalArgumentException("Los JUGADORES tienen SIMBOLOS iguales");

        this.tablero = new Tablero(a,b);
        this.JUGADORES = new Jugador[this.NUMERO_JUGADORES];

        //Inicializamos los JUGADORES
        for (int i = 0; i<this.NUMERO_JUGADORES; i++)
            this.JUGADORES[i] = new Jugador(i + 1, this.SIMBOLOS[i]);
        
        //Seleccionamos un jugador aleatorio para que comienze el juego:
        Random random = new Random();
        this.JUGADORES[random.nextInt(this.JUGADORES.length)].alternarTurno();
    }

    /**
     * Metodo que comprueba si hay SIMBOLOS repetidos (Jugadores)
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

    /*********************
     * METODOS DEL JUEGO *
     *********************/

    /**
     * Metodo para imprime el tablero del juego
     */
    public void imprimirTablero(){
        this.tablero.imprimirTablero();;
    }

    /**
     * Metodo que indica si la casilla seleccionada por el usuario es correcto
     * @param s Casilla que ha seleccionado
     * @return true si es correcta falsa si no (no esta disponible)
     */
    public boolean comprobarCasillaSeleccionada(String s){
        return this.tablero.comprobarCasillaSeleccionada(s);
    }

    /**
     * Metodo que actualiza los Datos del juego con cada jugada, donde comprueba los
     * cudrados que se han ganado y se añade al jugador que esta jugando
     */
    public void actualizarDatosJuego(){
        Jugador jugador = jugadorActual();
        char simbolo = jugador.getSimbolo();
        int casillasGanadas = this.tablero.comprobarCuadradosCompletos(simbolo);
        if(casillasGanadas !=0){
            this.anyadirCasillasGanadas(casillasGanadas);
        }
        else{
            this.siguienteJugador();
        }
    }

    /**
     * Metodo que sirve para añadir casillas ganadas a un jugador
     * @param a Numero de casillas que ha ganado
     */
    public void anyadirCasillasGanadas(int a){
        Jugador jugador = jugadorActual();
        jugador.incrementarCasillas(a);
    }

    /**
     * Metodo que devuelve el jugador actual
     * @return Jugador que tiene el turno actual;
     */
    public Jugador jugadorActual(){
        for (Jugador j : this.JUGADORES){
            if (j.turnoJugador())
                return j;
        }
        return null; 
    }

    /**
     * Pasamos a que el siguinte jugador sea el activo.
     */
    //TODO REMODELAR
    public void siguienteJugador(){
        boolean turnoCambiado = false;
        for (int i = 0; i<this.JUGADORES.length && !turnoCambiado; i++){
            if (this.JUGADORES[i].turnoJugador()){
                this.JUGADORES[i].alternarTurno();
                if (i+1 == this.JUGADORES.length){
                    this.JUGADORES[0].alternarTurno();
                    turnoCambiado = true;
                }
            }
        }
    }

    /**
     * Metodo que inica si el tablero esta completo o no
     */
    public boolean juegoCompletado(){
        return (this.tablero.tableroIncompleto())? false : true;
    }

    
}