import java.util.*;

class Main {
    public static void main(String[] args) {
        Position p = new Position(true);
        PGNParser parser = new PGNParser("./testcases_games/Adams.pgn");
        List<String> game;
        int testcase = 0;
        while(true) {
            testcase++;
            System.out.println("TESTCASE " + testcase);
            game = parser.nextGame();
            //System.out.println(game);
            playOutGame(game);
            if (game == null) {
                break;
            }
        }
        // Scanner s = new Scanner(System.in);
        // while (true) {
        //     System.out.println(p);
        //     while (true) {
        //         System.out.println("Enter move: ");
        //         String response = s.nextLine();
        //         if (p.makeMove(response)) {
        //             break;
        //         }
        //     }
        // }
    }

    public static void playOutGame(List<String> game) {
        Position p = new Position(true);
        for (String move: game) {
            try {
                if (!p.makeMove(move)) {
                System.out.println("GAME: " + game);
                System.out.println(move + " FAILED\n" + p);
                System.out.println("LEGAL MOVES GIVEN: " + p.legalMoves());
                break;
                //throw new IllegalArgumentException("whatever");
                }
            }
            catch (Exception e) {
                System.out.println("EXCEPTION!!!");
                System.out.println("GAME: " + game);
                System.out.println(move + " FAILED\n" + p);
                System.out.println("LEGAL MOVES GIVEN: " + p.legalMoves());
                throw e;
            }
        }
    }
}