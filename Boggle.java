import java.util.ArrayList;
import java.util.HashMap;
import java.awt.Point;
import java.util.Random;

import java.util.Scanner;
import java.io.File;
import java.io.IOException;


public class Boggle {

    private String currWord;
    private int points, gridSize;
    final private Random rand = new Random();

    /* --- DATA STRUCTURES --- */

    // 2d array
    private char[][] letterGrid; 
    // key: letter, value: all occurrences (indices) of the letter on the board
    private HashMap<Character,ArrayList<Point>> letters;

    /* --- CONSTRUCTOR --- */

    public Boggle(int size) {
        resetPoints();
        gridSize = size;
        letterGrid = new char[size][size];
        letters = new HashMap<Character,ArrayList<Point>>();

        // populating the data structures based on randomly generated letters
        for (int i = 0; i<gridSize; i++) {
            for (int j = 0; j<gridSize; j++) {
                char letter = (char) ((rand.nextInt(26))+97); // ASCII to char representation
                letterGrid[i][j] = letter; 

                if (letters.containsKey(letter)) {
                    letters.get(letter).add(new Point(i,j));
                }
                else {
                    ArrayList<Point> tempL = new ArrayList<Point>();
                    tempL.add(new Point(i,j));
                    letters.put(letter,tempL);
                }
            }
        }
    }

    /* --- SETTERS --- */

    private void setCurrWord(String word) {
        currWord = word;
    }
    private void resetPoints() {
        points = 0;
    }

    /* --- LETTER GRID METHODS --- */

    public void printLetterGrid() {
        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
               System.out.print("[" + letterGrid[i][j] + "] ");
            }
            System.out.println();
        }
    }

    /* --- LETTERS HASH MAP METHODS --- */

    public void printLetters() {
        for (char letter : letters.keySet()) {
            System.out.print(letter + ": ");
            for (Point p : letters.get(letter)) {
                System.out.print(p + " ");
            }
            System.out.println();
        }
    }

    /* --- VISITED GRID METHODS --- */

    private boolean[][] createVisitedGrid() {
        boolean[][] visitedGrid = new boolean[gridSize][gridSize];
        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
               visitedGrid[i][j] = false;
            }
        }
        return visitedGrid;
    }

    private boolean[][] copyOfVisitedGrid(boolean[][] visitedGrid) {
        boolean[][] newVisitedGrid = new boolean[gridSize][gridSize];
        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
               newVisitedGrid[i][j] = visitedGrid[i][j];
            }
        }
        return newVisitedGrid;
    }

    /* --- PATH METHODS --- */

    private ArrayList<Point> createPath() {
        return new ArrayList<Point>();
    }

    private ArrayList<Point> copyOfPath(ArrayList<Point> path) {
        ArrayList<Point> newPath = new ArrayList<Point>();
        for (Point p : path) {
               newPath.add(p);
        }
        return newPath;
    }

    private void printPath(ArrayList<Point> path, String word) {
        System.out.print(word + ": ");
        for (Point p : path) {
            int row = (int) p.getX();
            int col = (int) p.getY();
            System.out.print("(" + row + "," + col + ") ");
        }
        System.out.println();
    }

    /* --- CONDITIONS/CONSTRAINTS --- */

    // to avoid array indexing that's out of bounds
    private boolean isWithinBounds(int x, int y) {
        return (x >= 0 && x < gridSize) && (y >= 0 && y < gridSize);
    }

    // to avoid using the same letter tile (at an exact index) more than once
    private boolean notVisited(int x, int y, boolean[][] visitedGrid) {
        return visitedGrid[x][y] == false;
    }

    private boolean isValidTile(int x, int y, boolean[][] visitedGrid) {
        return isWithinBounds(x,y) && notVisited(x,y,visitedGrid); 
    }

    /* ------------------ */

    public void searchForWords() throws IOException {

        Scanner sc = new Scanner(new File("4000-most-common-english-words.csv"));  
        String word = "";

        // reading file
        while (sc.hasNext()) { 
            word = sc.nextLine();
            char firstChar = word.charAt(0);
            // The longest possible word is n*n (using all of the tiles on the letter grid)
            // Only consider words that start with one of the letters on the letter grid -
            // otherwise what tile are you starting on ?
            if ( (word.length() <= (gridSize*gridSize)) && (letters.containsKey(firstChar)) ) {
                for (Point p : letters.get(firstChar)) {
                    setCurrWord(word);
                    findPath(currWord,p,createVisitedGrid(),createPath());
                }
            }
        }
        System.out.println("\n" + points + " points :D");
        sc.close();
    }

    private void findPath(String substr, Point point, boolean[][] visitedGrid, ArrayList<Point> path) {
        
        // Letter at the curr coordinate is valid 
        // > First letter is valid; findPath() wouldn't have been reached in searchForWords() otherwise
        // > Following letters are valid; findPath() recursive call wouldn't have been reached otherwise

        // Since the letter at the curr coordinate is valid
        // 1. Its position in visitedGrid[][] must be marked as true (since it's been used/traversed)
        // 2. It must be added to the path of the curr word trying to formed

        // updating data structures
        int currRow = (int) point.getX();
        int currCol = (int) point.getY();
        visitedGrid[currRow][currCol] = true;
        path.add(point);

        // base case
        if (substr.substring(1).equals("")) {
            calculatePoints(currWord);
            printPath(path,currWord);
            return;
        }

        // 8 recursive cases

        // Copies of visitedGrid & path are passed in the recursive calls
        // This takes care of cases where the path of a word diverges (at a certain letter)
    
        // ie.
        // [a][t]
        // [t][n]
    
        // There are 2 possible unique paths for the word ant, diverging after (0,0),(1,1)
        // If both recursive calls (going up from n or going left from n) reference the *same*
        // array objects, the record of the second path will, in turn, be messed up
        //  > Going up from n: (0,0),(1,1),(0,1); path is updated 
        //  > Going left from n: (0,0),(1,1),(1,0); but since it's referencing the same path
        //    as the previous recursive call, path is updated to contain (0,0),(1,1),(0,1),(1,0)

        // top-left
        if (isValidTile(currRow-1,currCol-1,visitedGrid) &&
            (substr.charAt(1) == letterGrid[currRow-1][currCol-1])) {
            findPath(substr.substring(1),new Point(currRow-1,currCol-1),copyOfVisitedGrid(visitedGrid),copyOfPath(path));
        }

        // top
        if (isValidTile(currRow-1,currCol,visitedGrid) &&
            (substr.charAt(1) == letterGrid[currRow-1][currCol])) {
            findPath(substr.substring(1),new Point(currRow-1,currCol),copyOfVisitedGrid(visitedGrid),copyOfPath(path));
        }

        // top-right
        if (isValidTile(currRow-1,currCol+1,visitedGrid) &&
            (substr.charAt(1) == letterGrid[currRow-1][currCol+1])) {
            findPath(substr.substring(1),new Point(currRow-1,currCol+1),copyOfVisitedGrid(visitedGrid),copyOfPath(path));
        }

        // left
        if (isValidTile(currRow,currCol-1,visitedGrid) &&
            (substr.charAt(1) == letterGrid[currRow][currCol-1])) {
            findPath(substr.substring(1),new Point(currRow,currCol-1),copyOfVisitedGrid(visitedGrid),copyOfPath(path));
        }
        
        // right
        if (isValidTile(currRow,currCol+1,visitedGrid) &&
            (substr.charAt(1) == letterGrid[currRow][currCol+1])) {
            findPath(substr.substring(1),new Point(currRow,currCol+1),copyOfVisitedGrid(visitedGrid), copyOfPath(path));
        }

        // bottom left
        if (isValidTile(currRow+1,currCol-1,visitedGrid) &&
            (substr.charAt(1) == letterGrid[currRow+1][currCol-1])) {
            findPath(substr.substring(1),new Point(currRow+1,currCol-1),copyOfVisitedGrid(visitedGrid), copyOfPath(path));
        }

        // bottom
        if (isValidTile(currRow+1,currCol,visitedGrid) &&
            (substr.charAt(1) == letterGrid[currRow+1][currCol])) {
            findPath(substr.substring(1),new Point(currRow+1,currCol),copyOfVisitedGrid(visitedGrid), copyOfPath(path));
        }

        // bottom-right
        if (isValidTile(currRow+1,currCol+1,visitedGrid) &&
            (substr.charAt(1) == letterGrid[currRow+1][currCol+1])) {
            findPath(substr.substring(1),new Point(currRow+1,currCol+1),copyOfVisitedGrid(visitedGrid), copyOfPath(path));
        }
    }

    private void calculatePoints(String word) {
        switch (word.length()) {
            case 0: break;
            case 1: points += 1; break;
            case 2: points += 2; break;
            case 3: points += 4; break;
            case 4: points += 6; break;
            case 5: points += 9; break;
            case 6: points += 12; break;
            case 7: points += 16; break;
            default: points += 20;
        }
    }
}
