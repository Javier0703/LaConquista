/**
 * Copyright Universidad de Valldolid
 */

 import java.util.Scanner;

/**
 * Prueba del juego de La Conquista
 * @author javcalv
 */
public class laconquista{
    public static void main(String args[]){
        String linea = "---------------------------";
        System.out.println("Bienvendio a ¡La Conquista!" + "\n" + linea);
        System.out.println("1. Nuevo Juego\n2. Cargar Partida\n3. Ver Resultados\n4. Salir\n" + linea);
        Scanner in = new Scanner(System.in); int i;
        do{
            System.out.print("Elige una opcion: ");
            i = in.nextInt();
        }
        while(i<0 || i>4);
        
        switch (i) {
            case 1:
                nuevoJuego();
                break;
            case 2:
            //Emplearemos mas adelante
                System.exit(0);
            case 3:
            //Emplearemos mas adelante
                System.exit(0);
            case 4:
                finJuego();
            default:
                System.out.println("Opcion no valida");
                break;
        }
    }

    private static void finJuego(){
        System.out.println("Gracias por Jugar, hasta pronto");
    }
}