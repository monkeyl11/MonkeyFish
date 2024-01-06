
import java.util.*;
import java.io.File;
import java.io.FileNotFoundException; 

//Does not support comments in-notation

class PGNParser {
    private Scanner scan;
    private boolean validFile;

    public PGNParser(String fileDir){
        try {
            File f = new File(fileDir);
            scan = new Scanner(f);
            validFile = true;

        }
        catch (Exception FileNotFoundException) {
            System.out.println("Invalid file");
            validFile = false;
        }
    }

    public List<String> nextGame() {
        if (!validFile) {
            System.out.println("File not found/File reader has been closed");
            return null;
        }
        ArrayList<String> game = new ArrayList<>();
        String line;
        while(true) {
            if (!scan.hasNextLine()) {
                System.out.println("End of PGN reached");
                validFile = false;
                scan.close();
                return null;
            }
            line = scan.nextLine();
            if (line.contains("1."))
                break;
        }
        while(line != null && line != "") {
            if (line.contains("{")) {
                throw new IllegalArgumentException("PGN file cannot contain comments, this feature is unsupported");
            }
            String[] moves = line.split(" ");
            for (String move: moves) {
                if (move.contains(".")) {
                    move = move.substring(move.indexOf(".") + 1, move.length());
                }
                if (move.length() != 0 && !move.matches("(?s).*[0-2]-[0-2].*")) {
                    game.add(move);
                }
            }
            if (scan.hasNextLine())
                line = scan.nextLine();
            else
                break;
        }
        return game;
    }

    public void close() {
        scan.close();
        validFile = false;
    }

    public boolean validFile() {
        return validFile;
    }
}