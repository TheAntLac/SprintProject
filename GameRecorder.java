import java.io.*;
import java.util.*;

/**
 * Handles recording moves to memory and persisting/loading them from replay.txt.
 *
 * Recording lifecycle:
 *   startRecording(boardType, boardState) → captures the initial board snapshot
 *   recordMove(fromR, fromC, toR, toC)   → appends each legal move
 *   stopAndSave()                         → writes replay.txt, deletes old file first
 *
 * Replay lifecycle:
 *   loadReplay()                          → reads replay.txt, returns a ReplayData
 *   ReplayData holds the board type, initial grid snapshot, and move list
 */
public class GameRecorder {

    public static final String REPLAY_FILE = "replay.txt";

    // -------------------------------------------------------------------------
    // Inner data class returned by loadReplay()
    // -------------------------------------------------------------------------
    public static class ReplayData {
        public final Board.BoardType boardType;
        public final int[][] initialGrid;
        public final int initialPegs;
        public final List<int[]> moves; // each int[4]: fromR, fromC, toR, toC

        public ReplayData(Board.BoardType boardType, int[][] initialGrid,
                          int initialPegs, List<int[]> moves) {
            this.boardType    = boardType;
            this.initialGrid  = initialGrid;
            this.initialPegs  = initialPegs;
            this.moves        = moves;
        }
    }

    // -------------------------------------------------------------------------
    // Instance state (active recording)
    // -------------------------------------------------------------------------
    private Board.BoardType recordedType;
    private int[][] initialSnapshot;
    private int initialPegs;
    private final List<int[]> moves = new ArrayList<>();
    private boolean recording = false;

    // -------------------------------------------------------------------------
    // Recording API
    // -------------------------------------------------------------------------

    /** Begin a new recording. Snapshots the board's current state as the start. */
    public void startRecording(Board board) {
        recordedType     = board.getBoardType();
        initialSnapshot  = snapshotGrid(board);
        initialPegs      = board.getPegsRemaining();
        moves.clear();
        recording = true;
    }

    /** Append one move. Silently ignored if not currently recording. */
    public void recordMove(int fromR, int fromC, int toR, int toC) {
        if (recording) moves.add(new int[]{ fromR, fromC, toR, toC });
    }

    /** Stop recording and persist to REPLAY_FILE. Returns false if nothing was recorded. */
    public boolean stopAndSave() {
        recording = false;
        if (initialSnapshot == null) return false;

        // Delete old replay file before writing a fresh one
        File f = new File(REPLAY_FILE);
        if (f.exists()) f.delete();

        try (PrintWriter pw = new PrintWriter(new FileWriter(REPLAY_FILE))) {
            // Header line: board type
            pw.println("TYPE=" + recordedType.name());

            // Initial grid dimensions then cell values row by row
            int rows = initialSnapshot.length;
            int cols = initialSnapshot[0].length;
            pw.println("GRID=" + rows + "x" + cols);
            pw.println("PEGS=" + initialPegs);
            for (int r = 0; r < rows; r++) {
                StringBuilder sb = new StringBuilder();
                for (int c = 0; c < cols; c++) {
                    if (c > 0) sb.append(',');
                    sb.append(initialSnapshot[r][c]);
                }
                pw.println(sb);
            }

            // Moves section
            pw.println("MOVES=" + moves.size());
            for (int[] m : moves)
                pw.println(m[0] + "," + m[1] + "," + m[2] + "," + m[3]);

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean isRecording() { return recording; }

    // -------------------------------------------------------------------------
    // Replay API
    // -------------------------------------------------------------------------

    /** Load replay.txt and return a ReplayData, or null if the file doesn't exist / is corrupt. */
    public static ReplayData loadReplay() {
        File f = new File(REPLAY_FILE);
        if (!f.exists()) return null;

        try (BufferedReader br = new BufferedReader(new FileReader(REPLAY_FILE))) {
            // TYPE
            Board.BoardType type = Board.BoardType.valueOf(
                    br.readLine().substring("TYPE=".length()).trim());

            // GRID dimensions
            String[] dims = br.readLine().substring("GRID=".length()).split("x");
            int rows = Integer.parseInt(dims[0]);
            int cols = Integer.parseInt(dims[1]);

            // PEGS
            int pegs = Integer.parseInt(br.readLine().substring("PEGS=".length()).trim());

            // Grid rows
            int[][] grid = new int[rows][cols];
            for (int r = 0; r < rows; r++) {
                String[] parts = br.readLine().split(",");
                for (int c = 0; c < cols; c++)
                    grid[r][c] = Integer.parseInt(parts[c].trim());
            }

            // MOVES
            int moveCount = Integer.parseInt(br.readLine().substring("MOVES=".length()).trim());
            List<int[]> moves = new ArrayList<>(moveCount);
            for (int i = 0; i < moveCount; i++) {
                String[] p = br.readLine().split(",");
                moves.add(new int[]{
                    Integer.parseInt(p[0].trim()), Integer.parseInt(p[1].trim()),
                    Integer.parseInt(p[2].trim()), Integer.parseInt(p[3].trim())
                });
            }

            return new ReplayData(type, grid, pegs, moves);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private static int[][] snapshotGrid(Board board) {
        int[][] snap = new int[board.getRows()][board.getCols()];
        for (int r = 0; r < board.getRows(); r++)
            for (int c = 0; c < board.getCols(); c++)
                snap[r][c] = board.getCell(r, c);
        return snap;
    }
}