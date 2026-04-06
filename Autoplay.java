import java.util.*;

public class Autoplay {

    public static class Move {
        public final int fromR, fromC, toR, toC;
        public Move(int fromR, int fromC, int toR, int toC) {
            this.fromR = fromR; this.fromC = fromC;
            this.toR = toR;     this.toC = toC;
        }
    }

    private static final int[][] DIRS = {{-2,0},{2,0},{0,-2},{0,2}};

    public static List<Move> solve(Board board) {
        int[][] snapshot = copyGrid(board);
        int pegSnapshot = board.getPegsRemaining();

        List<Move> bestSolution = new ArrayList<>();
        int[] bestPegs = { pegSnapshot }; // worst case: no moves made

        List<Move> current = new ArrayList<>();
        search(board, current, bestSolution, bestPegs);

        restoreGrid(board, snapshot);
        board.setPegsRemaining(pegSnapshot);

        return bestSolution;
    }

    private static void search(Board board, List<Move> current,
                               List<Move> bestSolution, int[] bestPegs) {
        if (!board.hasValidMoves()) {
            if (board.getPegsRemaining() < bestPegs[0]) {
                bestPegs[0] = board.getPegsRemaining();
                bestSolution.clear();
                bestSolution.addAll(current);
            }
            return;
        }

        // Prune: can't possibly beat best even if every remaining move removes a peg
        // Each move removes exactly 1 peg, so minimum reachable = pegs - movesLeft
        // We can't know movesLeft easily, so just prune if already worse than best
        if (board.getPegsRemaining() >= bestPegs[0] && !current.isEmpty()) {
            // Only prune branches that can't improve — keep searching if there's hope
        }

        for (int r = 0; r < board.getRows(); r++) {
            for (int c = 0; c < board.getCols(); c++) {
                if (board.getCell(r, c) != 1) continue;
                for (int[] d : DIRS) {
                    int tr = r + d[0], tc = c + d[1];
                    if (!board.isValidMove(r, c, tr, tc)) continue;

                    int[][] snapshot = copyGrid(board);
                    int pegs = board.getPegsRemaining();

                    board.makeMove(r, c, tr, tc);
                    current.add(new Move(r, c, tr, tc));

                    search(board, current, bestSolution, bestPegs);

                    current.remove(current.size() - 1);
                    restoreGrid(board, snapshot);
                    board.setPegsRemaining(pegs);

                    // Early exit if perfect solution found
                    if (bestPegs[0] == 1) return;
                }
            }
        }
    }

    private static int[][] copyGrid(Board board) {
        int[][] copy = new int[board.getRows()][board.getCols()];
        for (int r = 0; r < board.getRows(); r++)
            for (int c = 0; c < board.getCols(); c++)
                copy[r][c] = board.getCell(r, c);
        return copy;
    }

    private static void restoreGrid(Board board, int[][] snapshot) {
        for (int r = 0; r < board.getRows(); r++)
            for (int c = 0; c < board.getCols(); c++)
                board.setCell(r, c, snapshot[r][c]);
    }
}