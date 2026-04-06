import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class GameUI extends JPanel implements MouseListener {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Board board;
    private int selectedRow = -1, selectedCol = -1;
    private static final int STATUS_HEIGHT = 40;
    private static final int CANVAS_SIZE = 490; // fixed canvas, cells scale to fit

    public GameUI(Board board) {
        this.board = board;
        setPreferredSize(new Dimension(CANVAS_SIZE, CANVAS_SIZE + STATUS_HEIGHT));
        setBackground(new Color(200, 200, 200));
        addMouseListener(this);
    }

    public void setBoard(Board board) {
        this.board = board;
        selectedRow = selectedCol = -1;
        repaint();
    }

    private int cellSize() {
        return Math.min(CANVAS_SIZE / board.getRows(), CANVAS_SIZE / board.getCols());
    }

    // Total pixel width/height of the actual grid
    private int gridPixelWidth()  { return cellSize() * board.getCols(); }
    private int gridPixelHeight() { return cellSize() * board.getRows(); }

    // Offsets to center the grid in the canvas
    private int offsetX() { return (CANVAS_SIZE - gridPixelWidth())  / 2; }
    private int offsetY() { return (CANVAS_SIZE - gridPixelHeight()) / 2; }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);

        int cell = cellSize();
        int ox = offsetX(), oy = offsetY();
        int pad = Math.max(3, cell / 10);

        for (int r = 0; r < board.getRows(); r++) {
            for (int c = 0; c < board.getCols(); c++) {
                int state = board.getCell(r, c);
                if (state == -1) continue;

                int x = ox + c * cell + pad;
                int y = oy + r * cell + pad;
                int size = cell - pad * 2;

                // Hole
                g2.setColor(new Color(200, 100, 100));
                g2.fillOval(x, y, size, size);

                if (state == 1) {
                    boolean sel = (r == selectedRow && c == selectedCol);
                    int pegPad = Math.max(3, size / 8);
                    g2.setColor(sel ? new Color(100, 100, 100) : new Color(100, 100, 100));
                    g2.fillOval(x + pegPad, y + pegPad, size - pegPad * 2, size - pegPad * 2);

                    g2.setColor(sel ? new Color(100, 100, 100) : new Color(100, 100, 100));
                    int shine = Math.max(4, size / 6);
                    g2.fillOval(x + pegPad + 3, y + pegPad + 3, shine, shine);
                }

                // Valid move highlight
                if (selectedRow >= 0 && board.isValidMove(selectedRow, selectedCol, r, c)) {
                    g2.setColor(new Color(100, 220, 100, 160));
                    int hlPad = Math.max(4, size / 6);
                    g2.fillOval(x + hlPad, y + hlPad, size - hlPad * 2, size - hlPad * 2);
                }
            }
        }

        // Status bar below the canvas area
        g2.setColor(Color.DARK_GRAY);
        g2.setFont(new Font("SansSerif", Font.BOLD, 16));
        String status = "Pegs remaining: " + board.getPegsRemaining();
        if (!board.hasValidMoves())
            status += board.getPegsRemaining() == 1 ? "  YOU WIN!" : "  No moves left!";
        FontMetrics fm = g2.getFontMetrics();
        int textX = (CANVAS_SIZE - fm.stringWidth(status)) / 2;
        g2.drawString(status, textX, CANVAS_SIZE + STATUS_HEIGHT / 2 + fm.getAscent() / 2);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        int cell = cellSize();
        int ox = offsetX(), oy = offsetY();

        int col = (e.getX() - ox) / cell;
        int row = (e.getY() - oy) / cell;

        // Ignore clicks outside the grid area
        if (row < 0 || row >= board.getRows() || col < 0 || col >= board.getCols()) return;
        if (board.getCell(row, col) == -1) return;

        if (selectedRow == -1) {
            if (board.getCell(row, col) == 1) {
                selectedRow = row;
                selectedCol = col;
            }
        } else {
            if (board.isValidMove(selectedRow, selectedCol, row, col))
                board.makeMove(selectedRow, selectedCol, row, col);
            selectedRow = selectedCol = -1;
        }
        repaint();
    }

    public void mousePressed(MouseEvent e) {}
    public void mouseReleased(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
}

    public void mousePressed(MouseEvent e) {}
    public void mouseReleased(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
}
