import java.util.*;

class Main {
    public static void main(String[] args) {
        //testBasic();
        testUndo(-1);
        // Position p = new Position(true);
        // PGNParser parser = new PGNParser("./testcases_games/Adams.pgn");
        // List<String> game;
        // game = parser.nextGame();
        // playOutGame(p, game, 45, false);
        // System.out.println(p);
        // System.out.println(((Pawn)(p.b.getPieceFromSquare("b7"))).canEnPassant());
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

    //plays out a game from start, returns number of moves
    public static int playOutGame(Position p, List<String> game, int numMoves, boolean debug) {
        numMoves = numMoves < 0 ? Integer.MAX_VALUE : numMoves;
        int i = 0;
        for (String move: game) {
            if (i >= numMoves) {
                return numMoves;
            }
            try {
                if (!p.makeMove(move)) {
                    if (debug) {
                        System.out.println("GAME: " + game);
                        System.out.println(move + " FAILED\n" + p);
                        System.out.println("LEGAL MOVES GIVEN: " + p.legalMoves());
                        //throw new IllegalArgumentException("whatever");
                    }
                    break;

                }
            }
            catch (Exception e) {
                System.out.println("EXCEPTION!!!");
                System.out.println("GAME: " + game);
                System.out.println(move + " FAILED\n" + p);
                System.out.println("LEGAL MOVES GIVEN: " + p.legalMoves());
                throw e;
            }
            i++;
        }
        return i;
    }

    //test to make sure no errors occur when playing a game
    public static void testBasic() {
        Position p;
        PGNParser parser = new PGNParser("./testcases_games/Adams.pgn");
        List<String> game;
        int testcase = 0;
        while(true) {
            p = new Position(true);
            testcase++;
            System.out.println("TESTCASE " + testcase);
            game = parser.nextGame();
            if (game == null) {
                break;
            }
            //System.out.println(game);
            playOutGame(p, game, -1, true);
        }
    }

    public static void testUndo(int numTests) {
        Position p = null;
        Position pComp = null;
        PGNParser parser = new PGNParser("./testcases_games/Adams.pgn");
        List<String> game;
        int testcase = 0;
        int i = 0;
        numTests = numTests < 0 ? Integer.MAX_VALUE : numTests;
        while(true && i < numTests) {
            ChessPiece.resetIDCounter();
            p = new Position(true);
            testcase++;
            game = parser.nextGame();
            if (game == null) {
                break;
            }
            //System.out.println(game);

            int moves = playOutGame(p, game, -1, false);
            //System.out.println(p.b);
            //System.out.println(pComp.b);
            for (int j = 1; j <= moves; j++) {
                p.undoMove();
                ChessPiece.resetIDCounter();
                pComp = new Position(true);
                playOutGame(pComp, game, moves - j, false);
                if (!p.exactlyEquals(pComp)) {
                    System.out.println("FAILED");
                    System.out.println(moves - j);
                    throw new IllegalArgumentException();
                }
            }
            i++;
        }
    }
}