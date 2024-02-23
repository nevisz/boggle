import java.util.Scanner;
import java.io.IOException;

public class Main_ {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int gridSize = 0;

        while(true) {
            try {
                System.out.print("Enter grid size: ");
                gridSize = Integer.valueOf(sc.nextLine());
            }
            catch (NumberFormatException nfe) {
                System.err.println("Exception: " + nfe + "\n");
            }
            if (gridSize >= 1) { break; }
        }
        
        sc.close();
        System.out.println("----------------\n");

        Boggle game = new Boggle(gridSize);
        game.printLetterGrid();
        System.out.println();
        
        try {
            game.searchForWords();
        }
        catch (IOException io) {
            System.out.println(io.getMessage());
        }
    }
}
