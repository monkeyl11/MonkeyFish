import java.util.*;
import java.io.File;
import java.io.FileNotFoundException; 


class Main {
    private static Stopwatch stopwatch = new Stopwatch();
    
    public static void main(String[] args) {
        //runPlayerGames("./testcases_games/Paehtz.pgn", false, -1);
        testAll();
        System.out.println(stopwatch);
        //testUndo(-1);
        //testAllUndo();
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
            //Test FEN generation
            // try {
            //     ChessPiece.resetIDCounter();
            //     Position p2 = new Position(p.toFEN());
            //     if (!p2.FENEquals(p)) {
            //         System.out.println("FEN Fail");
            //         System.out.println(p);
            //         System.out.println(p.toFEN());
            //         throw new IllegalArgumentException();
            //     }
            // }
            // catch (Exception e) {
            //     System.out.println("FEN Fail");
            //     System.out.println(p);
            //     System.out.println(p.toFEN());
            //     throw e;
            // }
            i++;
        }
        return i;
    }

    //test
    public static void runPlayerGames(String s, boolean debug, int numGames) {
        numGames = numGames < 0 ? Integer.MAX_VALUE : numGames;
        Position p;
        PGNParser parser = new PGNParser(s);
        List<String> game;
        int testcase = 0;
        s = s.replace(".\\testcases_games\\", "");
        s = s.replace(".pgn", "");
        System.out.println("PLAYING OUT " + s);
        while(true && testcase < numGames) {
            p = new Position(true);
            testcase++;
            //System.out.println("TESTCASE " + testcase);
            game = parser.nextGame();
            if (game == null) {
                break;
            }
            //System.out.println(game);
            stopwatch.start();
            playOutGame(p, game, -1, debug);
            stopwatch.stop();
        }
    }

    //Runs through ~400k games
    public static void testAll() {
        File[] files = new File("./testcases_games").listFiles();
        for (File f: files) {
            runPlayerGames(f.toString(), false, -1);
        }
    }

    public static void testAllUndo() {
        File[] files = new File("./testcases_games").listFiles();
        for (File f: files) {
            testUndo(f.toString(),  -1);
        }
    }

    public static void testUndo(String s, int numTests) {
        Position p = null;
        Position pComp = null;
        PGNParser parser = new PGNParser(s);
        s = s.replace(".\\testcases_games\\", "");
        s = s.replace(".pgn", "");
        System.out.println("PLAYING OUT " + s);
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