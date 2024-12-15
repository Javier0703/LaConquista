/*
 * Copyright Universidad de Valaldolid 2025
 */
import java.util.*;
import java.io.*;
import java.util.regex.Pattern;
import java.text.SimpleDateFormat;

/**
 * Implementacion del Juego de La Conquista
 * @author javcalv
 */
public class LaConquista {
    public static void main(String[] args){
        String ficheroResultados = "ficheros/resultados.txt";
        boolean play = true;
        Scanner in = new Scanner(System.in);
        int i;
        do{
            do {
                String linea = "---------------------------";
                System.out.println("Bienvenido a ¡La Conquista!" + "\n" + linea);
                System.out.println("1. Nuevo Juego\n2. Cargar Partida\n3. Ver Resultados\n4. Salir\n" + linea);
                System.out.print("Elige una opcion: ");
                i = in.nextInt();
                if (i < 1 || i > 4) {
                    System.out.println("Opcion no valida (1 - 4)");
                }
            } while (i < 1 || i > 4);

            boolean newGame = false; boolean loadGame = false;
            switch (i) {
                case 1:
                    newGame = true;
                    break;
                case 2:
                    // Cargar Partida
                    System.out.println("Cargar Partida (Funcionalidad no implementada)");
                    break;
                case 3:
                    verResultados(ficheroResultados);
                    play = false;
                    break;
                case 4:
                    System.out.println("Gracias por Jugar, hasta pronto");
                    play = false;
                    break;
            }
    
            Juego juego;

            if(newGame || loadGame){
                if (newGame)
                    juego = newGame(in);
                //CARGAMOS EL JUEGO
                else{
                    juego = new Juego(2, 5);
                }
        
                boolean continuarJugando = true;
                while(continuarJugando){
    
                    boolean juegoFinalizado = juego.juegoCompletado();
                    System.out.println(" ---------- Cargando Partida ---------- ");
    
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
    
                        //Comprobamos datos del juego
                        if(!palo.equals("**")){
                            juego.actualizarDatosJuego();
                            juegoFinalizado = juego.juegoCompletado();
                        }
                        //GUARDAR PARTIDA
                        else{
                            //VOLVER AL MENU INICIAL
                            //GUARDAR PARTIDA
                            //VOLVER AL MENU
                        }
                    }

                    //ESTO DEBE SALIR SIEMPRE MENOS QUE GUARDEMOS UNA PARTIDA
                    System.out.println("¡Finalizo el juego!");
                    anyadirResultado(juego, ficheroResultados);
                    juego.imprimirResultadosJugadores();
                    String st = "";
                    do{
                        System.out.print("¿Quieres jugar de nuevo?: S/N ");
                        st = in.nextLine();
                    }
                    while(!(st.equals("S") || st.equals("N")));

                    if(st.equals("S")){
                        juego = newGame(in);
                    }
                    continuarJugando = false;
                }
            }
        }
        while(play);
        in.close();
    }

    /**
     * Metod para generar una nueva Partida.
     * @param in Scanner para hacer los inputs
     * @return nuevo Juego
     */
    public static Juego newGame(Scanner in){
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
        return new Juego(filas, columnas);
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
            //ADD ON ficheros/resultados.txt the info
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
        } 
        catch (FileNotFoundException e) {
            System.out.println("No se encontró el fichero");
        }
    }
}
