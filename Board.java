

public class Board {
	// Board Class
	public void setCell(int row, int col, int value) { grid[row][col] = value; }
	public void setPegsRemaining(int count) { pegsRemaining = count; }
    public enum BoardType { ENGLISH, HEXAGON, DIAMOND }

    private int[][] grid;
    private int pegsRemaining;
    private int rows, cols;

    public Board(BoardType type) {
        switch (type) {
            case ENGLISH:  initEnglish();  break;
            case HEXAGON:  initHexagon();  break;
            case DIAMOND:  initDiamond();  break;
        }
    }

    // default 7x7
    private void initEnglish() {
        rows = 7; cols = 7;
        grid = new int[rows][cols];
        int[][] invalid = {
            {0,0},{0,1},{1,0},{1,1},
            {0,5},{0,6},{1,5},{1,6},
            {5,0},{5,1},{6,0},{6,1},
            {5,5},{5,6},{6,5},{6,6}
        };
        for (int[] p : invalid) grid[p[0]][p[1]] = -1;
        for (int r = 0; r < rows; r++)
            for (int c = 0; c < cols; c++)
                if (grid[r][c] == 0) grid[r][c] = 1;
        grid[3][3] = 0;
        pegsRemaining = 32;
    }

    // Hexagon: diamond-ish shape
    // Row lengths: 3, 4, 5, 4, 3 - padded into a 5x5 grid with -1 margins
    private void initHexagon() {
        rows = 5; cols = 9;
        grid = new int[rows][cols];
        // Mark all as invalid, then enable valid cells
        for (int r = 0; r < rows; r++)
            for (int c = 0; c < cols; c++)
                grid[r][c] = -1;

        // Row 0: 3 pegs starting at col 3
        int[] starts = {3, 2, 1, 2, 3};
        int[] lengths = {3, 5, 7, 5, 3};
        for (int r = 0; r < rows; r++)
            for (int c = starts[r]; c < starts[r] + lengths[r]; c++)
                grid[r][c] = 1;

        // Center empty
        grid[2][4] = 0;
        pegsRemaining = 22;
    }

    // Diamond: rotated square, 9x9 grid with diamond-shaped valid cells
    private void initDiamond() {
        rows = 9; cols = 9;
        grid = new int[rows][cols];
        for (int r = 0; r < rows; r++)
            for (int c = 0; c < cols; c++)
                grid[r][c] = -1;

        // Valid if |r - 4| + |c - 4| <= 4
        int center = 4;
        pegsRemaining = 0;
        for (int r = 0; r < rows; r++)
            for (int c = 0; c < cols; c++)
                if (Math.abs(r - center) + Math.abs(c - center) <= center) {
                    grid[r][c] = 1;
                    pegsRemaining++;
                }

        // Remove center peg
        grid[center][center] = 0;
        pegsRemaining--;
    }

    public int getRows() { return rows; }
    public int getCols() { return cols; }
    public int getCell(int row, int col) { return grid[row][col]; }

    public boolean isValidMove(int fromR, int fromC, int toR, int toC) {
        if (!inBounds(fromR, fromC) || !inBounds(toR, toC)) return false;
        if (grid[fromR][fromC] != 1 || grid[toR][toC] != 0) return false;
        int dr = toR - fromR, dc = toC - fromC;
        if (!((Math.abs(dr) == 2 && dc == 0) || (dr == 0 && Math.abs(dc) == 2))) return false;
        int midR = fromR + dr / 2, midC = fromC + dc / 2;
        return grid[midR][midC] == 1;
    }

    public void makeMove(int fromR, int fromC, int toR, int toC) {
        int midR = (fromR + toR) / 2, midC = (fromC + toC) / 2;
        grid[fromR][fromC] = 0;
        grid[midR][midC] = 0;
        grid[toR][toC] = 1;
        pegsRemaining--;
    }

    public boolean hasValidMoves() {
        int[][] dirs = {{-2,0},{2,0},{0,-2},{0,2}};
        for (int r = 0; r < rows; r++)
            for (int c = 0; c < cols; c++)
                if (grid[r][c] == 1)
                    for (int[] d : dirs)
                        if (isValidMove(r, c, r+d[0], c+d[1])) return true;
        return false;
    }

    public int getPegsRemaining() { return pegsRemaining; }

    private boolean inBounds(int r, int c) {
        return r >= 0 && r < rows && c >= 0 && c < cols && grid[r][c] != -1;
    }
}