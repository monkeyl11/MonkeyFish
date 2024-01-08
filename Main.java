import java.util.*;
import java.io.File;
import java.io.FileNotFoundException; 

import java.io.FileReader;

import org.json.simple.JSONArray; 
import org.json.simple.JSONObject; 
import org.json.simple.parser.*; 


class Main {
    private static Stopwatch stopwatch = new Stopwatch();
    private static int totalCasesFailed = 0;
    private static int totalCases = 0;
    
    public static void main(String[] args){
        try {
            testSpecialPositions();
        }
        catch (Exception e) {
            System.out.println(e);
        }

        //runPlayerGames("./testcase_games/Paehtz.pgn", false, -1);
        testAll();

        


        //testUndo(-1);
        //testAllUndo();
    }

    public static void testCase(String fen, String move) {
        Position p = new Position(fen);
        //p.makeMove(move);
        p.legalMoves();
        System.out.println(p);

    }

    public static void testFENs() {
        Scanner s = new Scanner(System.in);
        while(true) {
            System.out.println("Enter FEN: ");
            Position p = new Position(s.nextLine());
            System.out.println(p.legalMoves());
            System.out.println(p);
        }
    }

    public static void testSpecialPositions() throws Exception{
        boolean passedAll = true;
        File[] files = new File("./testcase_positions").listFiles();
        for (File f: files) {
            Object obj = new JSONParser().parse(new FileReader(f));
            JSONObject jo = (JSONObject) obj;
            ArrayList<JSONObject> testCases = (ArrayList)jo.get("testCases");
            for (JSONObject testcase: testCases) {
                //System.out.println(JSONObj.toString());
                HashMap startingInfo = (HashMap)testcase.get("start");
                Position p = new Position((String)(startingInfo.get("fen")));
                List<Move> generatedMoves = p.legalMoves();
                List<Move> expectedMoves = new ArrayList<>(64);
                ArrayList<JSONObject> moves = (ArrayList<JSONObject>)testcase.get("expected");
                for (JSONObject m: moves) {
                    expectedMoves.add(p.algebraicNotationToMove((String)(m.get("move"))));
                }
                boolean testcasePassed = true;
                for (Move m: generatedMoves) {
                    if (!expectedMoves.contains(m)) {
                        System.out.println("ERROR: Extra move generated- " + m);
                        testcasePassed = false;
                        passedAll = false;
                    }
                }
                for (Move m: expectedMoves) {
                    if (!generatedMoves.contains(m)) {
                        System.out.println("ERROR: Move missing- " + m);
                        testcasePassed = false;
                        passedAll = false;
                    }
                }
                if (!testcasePassed) {
                    System.out.println("TESTCASE FAILED- " + startingInfo.get("fen"));
                }
            }

        }
        if (passedAll) {
            System.out.println("PASSED ALL SPECIAL TEST CASES");
        }
    }

    //plays out a game from start, returns number of moves
    public static int playOutGame(Position p, List<String> game, int numMoves, boolean debug) {
        totalCases++;
        numMoves = numMoves < 0 ? Integer.MAX_VALUE : numMoves;
        int i = 0;
        for (String move: game) {
            if (i >= numMoves) {
                return numMoves;
            }
            try {
                if (!p.makeMove(move)) {
                    totalCasesFailed++;
                    if (debug) {
                        System.out.println("GAME: " + game);
                        System.out.println(move + " FAILED\n" + p);
                        System.out.println("LEGAL MOVES GIVEN: " + p.legalMoves());
                        System.out.println("FEN: " + p.toFEN());
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

    //test
    public static void runPlayerGames(String s, boolean debug, int numGames) {
        numGames = numGames < 0 ? Integer.MAX_VALUE : numGames;
        Position p;
        PGNParser parser = new PGNParser(s);
        List<String> game;
        int testcase = 0;
        s = s.replace(".\\testcase_games\\", "");
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
        File[] files = new File("./testcase_games").listFiles();
        for (File f: files) {
            runPlayerGames(f.toString(), false, -1);
        }
        System.out.println(stopwatch);
        System.out.println("Cases failed: " + totalCasesFailed + " / " + totalCases);
        resetCases();
    }

    public static void testAllUndo() {
        File[] files = new File("./testcase_games").listFiles();
        for (File f: files) {
            testUndo(f.toString(),  -1);
        }
    }

    public static void testUndo(String s, int numTests) {
        Position p = null;
        Position pComp = null;
        PGNParser parser = new PGNParser(s);
        s = s.replace(".\\testcase_games\\", "");
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

    public static void resetCases() {
        totalCasesFailed = 0;
        totalCases = 0;
    }
}