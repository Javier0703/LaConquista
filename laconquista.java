import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * Prueba del juego de La Conquista
 * @author javcalv
 */
public class laconquista {
    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);
        int i;
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

        boolean newGame = false;
        boolean loadGame = false;
        switch (i) {
            case 1:
                newGame = true;
                break;
            case 2:
                // Cargar Partida
                System.out.println("Cargar Partida (Funcionalidad no implementada)");
                break;
            case 3:
                // Ver Resultados
                System.out.println("Ver Resultados (Funcionalidad no implementada)");
                break;
            case 4:
                // Salir
                System.out.println("Gracias por Jugar, hasta pronto");
                break;
        }

        Juego juego;

        if(newGame || loadGame){
            if (newGame) {
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
                juego = new Juego(filas, columnas);
            }
    
            //CARGAMOS EL JUEGO
            //TODO AFTER
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
                        //TODO AFTER
                    }
                }
                juego.imprimirResultadosJugadores();
                System.out.println("¡Finalizo el juego!");
                juego.imprimirResultadosJugadores();
                System.out.print("¿Quieres jugar de nuevo?: S/N ");
                in.nextLine();
            }
        }
    }    
}