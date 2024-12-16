/*
 * Copyright Universidad de Valaldolid 2025
 */
import java.util.*;
import java.io.*;
import java.util.regex.Pattern;
import java.text.SimpleDateFormat;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * Implementacion del Juego de La Conquista
 * @author javcalv
 */
public class LaConquista {
    public static void main(String[] args){
        String ficheroResultados = "ficheros/resultados.txt";
        String ficheroPartidaGuardada = "ficheros/partidaGuardada.txt";
        boolean seguirJugando = true; 
        int i; 
        int numeroJugadores = 3;
        int turnoJuador = new Random().nextInt(numeroJugadores);
        Scanner in = new Scanner(System.in);

        do{
            do {
                imprimirTablon();
                i = in.nextInt();
                if (i < 1 || i > 4)
                    System.out.println("Opcion no valida (1 - 4)");
            } while (i < 1 || i > 4);
            
            switch (i) {
                case 1:
                    Juego juego = newGame(in, numeroJugadores, turnoJuador);
                    jugar(juego, in, ficheroResultados, ficheroPartidaGuardada, numeroJugadores, turnoJuador);
                    break;
                case 2:
                    Juego juegoCargado = loadGame(ficheroPartidaGuardada);
                    jugar(juegoCargado, in, ficheroResultados, ficheroPartidaGuardada, numeroJugadores, turnoJuador);
                    break;
                case 3:
                    verResultados(ficheroResultados);
                    break;
                case 4:
                    System.out.println("Gracias por Jugar, hasta pronto");
                    seguirJugando = false;
                    in.close();
                    break;
            }
        }
        while(seguirJugando);
    }  

    /**
     * Metodo para imprimir el tablon para seleccionar el modo de juego
     */
    public static void imprimirTablon(){
        String linea = "---------------------------";
        System.out.println("Bienvenido a ¡La Conquista!");
        System.out.println(linea);
        System.out.println("1. Nuevo Juego");
        System.out.println("2. Cargar Partida");
        System.out.println("3. Ver Resultados");
        System.out.println("4. Salir");
        System.out.println(linea);
        System.out.print("Elige una opcion: ");
    }

    public static void jugar(Juego juego, Scanner in, String ficheroResultados, String ficheroPartidaGuardada, int numeroJugadores, int turnoJuador){
        boolean continuarJugando = true;
        while(continuarJugando){
            boolean juegoFinalizado = juego.juegoCompletado();
            while(!juegoFinalizado){
                String palo = "";
                boolean casillaCorrecta = false;
                juego.imprimirTablero();

                do{
                    System.out.print("[Jugador " + (juego.jugadorActual().getNumJugador()) + "] Proximo palito (** Guardar ): ");
                    palo = in.nextLine(); 
                    casillaCorrecta = (palo.equals("**") ||juego.comprobarCasillaSeleccionada(palo));
                    if(!casillaCorrecta){
                        System.out.println("Casilla no disponible. Seleccione otra");
                    }
                }
                while(!casillaCorrecta);
                if(palo.equals("**")){
                    guardarPartida(juego, ficheroPartidaGuardada);
                    juegoFinalizado = true;
                }
                else{
                    juego.actualizarDatosJuego();
                    juegoFinalizado = juego.juegoCompletado();
                    if(juegoFinalizado){
                        System.out.println("¡Finalizo el juego!");
                        juego.imprimirTablero();
                        anyadirResultado(juego, ficheroResultados);
                        juego.imprimirResultadosJugadores(); 
                    }
                    
                }
            }
            String st = "";
            do{
                System.out.print("¿Quieres jugar de nuevo?: S/N ");
                st = in.nextLine();
            }
            while(!(st.equals("S") || st.equals("N")));

            if(st.equals("S")){
                juego = newGame(in, numeroJugadores, turnoJuador);
            }
            else continuarJugando = false;
        }
    }

    /**
     * Metod para generar una nueva Partida.
     * @param in Scanner para hacer los inputs
     * @return nuevo Juego
     */
    public static Juego newGame(Scanner in, int numeroJugadores, int turnoJugador){
        int filas = 0;
        int columnas = 0;
        Pattern CODIGO_DUENO = Pattern.compile("\\d+x\\d+");
        in.nextLine();
        while (filas < 2 || columnas < 2) {
            System.out.print("Genera una nueva dimension de tablero AxB (minimo 2x2): ");
            String size = in.nextLine();
            if (CODIGO_DUENO.matcher(size).matches()) {
                filas = Character.getNumericValue(size.charAt(0));
                columnas = Character.getNumericValue(size.charAt(2));
            } 
        }
        return new Juego(filas, columnas, numeroJugadores, turnoJugador);
    }

    /**
     * Metodo
     * @param archivo
     * @return
     */
    public static Juego loadGame(String archivo) {
        try (Scanner datos = new Scanner(new File(archivo), "UTF-8")) {
            if(!datos.hasNextLine()){
                throw new IllegalStateException("No se encuentran partidas guardadas");
            }
            String dimension = datos.nextLine();
            String tablero = datos.nextLine();  
            String jugadores = datos.nextLine();
            String[] dimensiones = dimension.split("x");
            int filas = Integer.parseInt(dimensiones[0].trim());
            int columnas = Integer.parseInt(dimensiones[1].trim());
            String[] datosJugadores = jugadores.split(",");
            int numeroJugadores = Integer.parseInt(datosJugadores[0].trim());
            int turnoJugador = Integer.parseInt(datosJugadores[1].trim());
            Juego j = new Juego(filas, columnas, numeroJugadores, turnoJugador);
            j.cargarPartida(tablero);
            return j;
    
        } 
        catch (FileNotFoundException e) {
            System.out.println("No se encontró el fichero: " + archivo);
        } 
        catch (NumberFormatException e) {
            System.out.println("Error al convertir un dato numérico: " + e.getMessage());
        } 
        catch (IllegalArgumentException e) {
            System.out.println("Formato de archivo inválido: " + e.getMessage());
        }
        return null;
    }

    /**
     * Metodo para añadir un nuevo resultado finalizado al txt.
     */
    public static void anyadirResultado(Juego j, String archivo){
        Date diaHoy = new Date();
        SimpleDateFormat formato = new SimpleDateFormat("dd/mm/yyyy HH:mm:ss z yyyy", new Locale("es", "ES"));
        String fechaFormateada = formato.format(diaHoy);
        String infoPartida = j.guardarResultado();
        String info = fechaFormateada + " " + infoPartida;
        try{
            PrintWriter escritor = new PrintWriter(new FileWriter(archivo, true));
            escritor.println(info);
            escritor.close();
        }
        catch (IOException e){
            System.err.println("Error al escribir en el archivo: " + e.getMessage());
        }
    }

    /**
     * Metodo para ver los resultados
     */
    public static void verResultados(String archivo) {
        try (
            Scanner datos = new Scanner(new File(archivo), "UTF-8")) {
            while (datos.hasNextLine()) {
                System.out.println(datos.nextLine());
            }
            datos.close();
        } 
        catch (FileNotFoundException e) {
            System.out.println("No se encontró el fichero");
        }
    }

    /**
     * Metodo para guardar una partida
     * @param j Juego que se guerda
     * @param archivo El archivo donde se guardara la partida
     */
    public static void guardarPartida(Juego j, String archivo) {
        try {
            String info = j.guardarPartida();
            Path path = Paths.get(archivo);
            Files.write(path, info.getBytes(StandardCharsets.UTF_8),
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } 
        catch (IOException e) {
            System.err.println("Error al escribir en el archivo: " + e.getMessage());
        }
    }

    /*
    * Implementacion de la clase Juego
    * @author javcalv
    */
    public static class Juego {

        private Tablero tablero;
        private int NUMERO_JUGADORES;
        private final char[] SIMBOLOS = {'#', '*', '@' , '$'};
        private final int MIN_JUGADORES = 2;
        private final int MAX_JUGADORES = 4;
        private final Jugador[] JUGADORES;

        // Clase privada Tablero para gestionar el propio tablero
        private class Tablero{
            private final String[] PAREDES = {"|", "---"};
            private final String[] CARACTERES_INICIALES = {"AZ", "az", "09"};
            private final char ESQUINA = '&';
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
            public void imprimirTablero() {
                int tam = 0;
                //Seleccionamos el tamaño maximo de las paredes (centrar columnas)
                for (String s : this.PAREDES) {
                    if (s != null && s.length() > tam) {
                        tam = s.length();
                    }
                }
                for (int i = 0; i < tablero.length; i++) {
                    for (int j = 0; j < tablero[0].length; j++) {
                        String celda = tablero[i][j];
                        if (celda == null) {
                            celda = " ".repeat(tam);
                        } 
                        else if (celda.length() < tam) {
                            celda = String.format(" " + celda + " ");
                        }
                        System.out.print(celda + " ");
                    }
                    System.out.println();
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
             * @param simbolo Simbolo que se coloca en la cuadricula completa
             */
            public int comprobarCuadradosCompletos(char simbolo){
                int casilla = 0;
                int[] empezar = {1,1};
                for(int i=empezar[0]; i<this.tablero.length; i+=2){
                    for (int j = empezar[1]; j<this.tablero[0].length; j+=2){
                        if (tablero[i][j] == null){
                            if (comprobarCasilla(i, j)){
                                this.tablero[i][j] = String.valueOf(simbolo);
                                casilla++;
                            }
                        }
                    }
                }
                return casilla;
            }

            /**
             * Metodo para comprobar si una casilla esta rodeada de palos o no
             * @param i fila de la tabla
             * @param j columna de la tabla
             * @return true si esta completa o false si no lo esta
             */
            private boolean comprobarCasilla(int i, int j) {
                boolean completa = true;
                String paredes = "";
                
                // Construir la cadena de paredes a partir del vector PAREDES
                for (String p : this.PAREDES) {
                    paredes += p;
                }
                // Comprobar la celda de arriba
                if (i > 0) {
                    String arriba = tablero[i - 1][j];
                    if (!paredes.contains(arriba)) {
                        completa = false;
                    }
                }
                // Comprobar la celda de abajo
                if (i < tablero.length - 1) {
                    String abajo = tablero[i + 1][j];
                    if (!paredes.contains(abajo)) {
                        completa = false;
                    }
                }
                // Comprobar la celda de la izquierda
                if (j > 0) {
                    String izquierda = tablero[i][j - 1];
                    if (!paredes.contains(izquierda)) {
                        completa = false;
                    }
                }
                // Comprobar la celda de la derecha
                if (j < tablero[0].length - 1) {
                    String derecha = tablero[i][j + 1];
                    if (!paredes.contains(derecha)) {
                        completa = false;
                    }
                }
                return completa;
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
         * @throws IllegalArgumentException si el turno del jugador es mayor o igual al numero de ellos
         */
        public Juego(int a, int b, int jugadores, int turnoJugador){
            //Comprobamos que haya al menos 2 JUGADORES y que no tengan simbolos repetidos
            if(jugadores< this.MIN_JUGADORES || jugadores > this.MAX_JUGADORES) 
                throw new IllegalArgumentException("El numero de JUGADORES debe ser entre 2 - 4");
            if(tieneDuplicados(this.SIMBOLOS))
                throw new IllegalArgumentException("Los JUGADORES tienen SIMBOLOS iguales");
            if(turnoJugador>=jugadores)
                throw new IllegalArgumentException("El turno de un jugador no puede ser mayor o igual al numero de ellos");

            this.tablero = new Tablero(a,b);
            this.NUMERO_JUGADORES = jugadores;
            this.JUGADORES = new Jugador[jugadores];

            //Inicializamos los JUGADORES
            for (int i = 0; i<this.NUMERO_JUGADORES; i++)
                this.JUGADORES[i] = new Jugador(i + 1, this.SIMBOLOS[i]);

            //Alternamos el turno al jugador para que comience
            this.JUGADORES[turnoJugador].alternarTurno();
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
            if(casillasGanadas !=0 ){
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
                if (j.turnoJugador()){
                    return j; 
                }
            }
            return null; 
        }

        /**
         * Pasamos a que el siguinte jugador sea el activo.
         */
        public void siguienteJugador(){
            boolean turnoCambiado = false;
            for (int i = 0; i<this.JUGADORES.length && !turnoCambiado; i++){
                //Comprobamos si es su turno
                if (this.JUGADORES[i].turnoJugador()){
                    this.JUGADORES[i].alternarTurno();
                    this.JUGADORES[(i+1) % this.NUMERO_JUGADORES].alternarTurno();
                    turnoCambiado = true;
                }
            }
        }

        /**
         * Metodo que inica si el tablero esta completo o no
         */
        public boolean juegoCompletado(){
            return (this.tablero.tableroIncompleto())? false : true;
        }

        /**
         * Metodo para imprimir los resultados de los jugadores
         */
        public void imprimirResultadosJugadores(){
            for (Jugador j : this.JUGADORES){
                System.out.println("Jugador " + j.getNumJugador() + ": " + j.getCasillasGanadas() + " casillas");
            }
        }

        /**
         * Metodo para guardar la informacion en el txt
         * @return String con la informacion de la partida
         */
        public String guardarResultado(){
            int filas = this.tablero.tablero.length - 3;
            int columnas = this.tablero.tablero[0].length - 3;
            String tablero = "Tam "+filas+"x"+columnas+"\t";
            String jugadorInfo = "";
            for (Jugador j : this.JUGADORES){
                jugadorInfo+="Jugador "+j.getNumJugador()+": "+j.getCasillasGanadas()+ "  |  ";
            }
            return tablero+jugadorInfo;
        }

        /**
         * Metodo para guardad la partida en el fichero
         * @return tablero actual de la partida del estilo
         *  - Tablero del juego
         *  - Dimension del tablero (34) --> 3x4
         *  - Numero de jugadores y el jugador activo
         */
        public String guardarPartida(){
            String tablero = "";
            for (int i = 0; i<this.tablero.tablero.length; i++){
                for (int j = 0; j<this.tablero.tablero[0].length; j++){
                    if (this.tablero.tablero[i][j] == null)
                        tablero += " ";
                    else if(this.tablero.tablero[i][j] == "---")
                        tablero += "-";
                    else
                        tablero += this.tablero.tablero[i][j];
                }
            }
            int filas = (this.tablero.tablero.length - 1) / 2;
            int columnas = (this.tablero.tablero[0].length -1 )/ 2;
            return String.valueOf(filas+"x"+columnas)+"\n"+tablero+"\n"+
                this.NUMERO_JUGADORES+","+(this.jugadorActual().getNumJugador()-1);
        }

        /**
         * Metodo para cargarla partica
         * @param tab El tablero de la partida a cargar
         */
        public void cargarPartida(String tab){
            int centinela = 0;
            for(int i = 0; i<this.tablero.tablero.length; i++){
                for (int j = 0; j<this.tablero.tablero[0].length; j++){
                    char casilla = tab.charAt(centinela);
                    if (casilla != ' '){
                        if(casilla == '-')
                            this.tablero.tablero[i][j] = "---";
                        else
                            this.tablero.tablero[i][j] = String.valueOf(casilla);
                            if (casillaAcertada(casilla)){
                                boolean encontrado = false;
                                for(int ju = 0; ju<this.JUGADORES.length && !encontrado; ju++){
                                    if (this.JUGADORES[ju].getSimbolo() == casilla){
                                        this.JUGADORES[ju].incrementarCasillas(1);
                                        encontrado = true;
                                    }
                                }      
                            }
                    }
                    centinela++;    
                }
            }
        }

        /**
         * Metodo para saber si una casilla del tablero guardado es caislla guardada
         * @param casilla Casilla a comprobar
         * @return true si es verdad o false si no lo es;
         */
        private boolean casillaAcertada(char casilla){
            boolean encontrado = false;
            for(char c : this.SIMBOLOS){
                if (c == casilla)
                    encontrado = true;
            }
            return encontrado;
        }
    }
}