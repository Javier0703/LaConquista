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
                    System.out.print("[Jugador " + (juego.jugadorActual().getNumJugador()) + "] Proximo palito: ");
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
}