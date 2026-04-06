import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.BorderLayout;
import java.util.List;

public class Main {

    private static Timer autoplayTimer;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> buildUI());
    }

    private static void buildUI() {
        Board.BoardType[] selectedType = { Board.BoardType.ENGLISH };

        Board board = new Board(selectedType[0]);
        GameUI gameUI = new GameUI(board);
        Board[] boardRef = { board };

        // --- Sidebar ---
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(new Color(60, 40, 20));
        sidebar.setPreferredSize(new Dimension(160, 0));
        sidebar.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

        // Board type section
        JLabel typeLabel = new JLabel("Board Type");
        typeLabel.setForeground(new Color(230, 200, 140));
        typeLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        typeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebar.add(typeLabel);
        sidebar.add(Box.createVerticalStrut(10));

        ButtonGroup group = new ButtonGroup();
        Board.BoardType[] types = Board.BoardType.values();
        String[] labels = { "English", "Hexagon", "Diamond" };

        for (int i = 0; i < types.length; i++) {
            Board.BoardType type = types[i];
            JRadioButton rb = new JRadioButton(labels[i]);
            rb.setSelected(type == selectedType[0]);
            rb.setForeground(Color.WHITE);
            rb.setBackground(new Color(60, 40, 20));
            rb.setFont(new Font("SansSerif", Font.PLAIN, 13));
            rb.setAlignmentX(Component.CENTER_ALIGNMENT);
            rb.addActionListener(e -> selectedType[0] = type);
            group.add(rb);
            sidebar.add(rb);
            sidebar.add(Box.createVerticalStrut(6));
        }

        sidebar.add(Box.createVerticalStrut(20));

        // Divider
        JSeparator sep = new JSeparator();
        sep.setMaximumSize(new Dimension(140, 1));
        sep.setForeground(new Color(120, 80, 40));
        sidebar.add(sep);
        sidebar.add(Box.createVerticalStrut(16));

        // Autoplay section
        JLabel autoLabel = new JLabel("Autoplay");
        autoLabel.setForeground(new Color(230, 200, 140));
        autoLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        autoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebar.add(autoLabel);
        sidebar.add(Box.createVerticalStrut(10));

        JLabel statusLabel = new JLabel(" ");
        statusLabel.setForeground(new Color(200, 180, 120));
        statusLabel.setFont(new Font("SansSerif", Font.ITALIC, 11));
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebar.add(statusLabel);
        sidebar.add(Box.createVerticalStrut(6));

        JButton autoplayBtn = new JButton("▶  Autoplay");
        autoplayBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        autoplayBtn.setFont(new Font("SansSerif", Font.BOLD, 13));
        autoplayBtn.setBackground(new Color(40, 120, 60));
        autoplayBtn.setForeground(Color.WHITE);
        autoplayBtn.setFocusPainted(false);
        autoplayBtn.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));

        autoplayBtn.addActionListener(e -> {
            // Stop if already running
            if (autoplayTimer != null && autoplayTimer.isRunning()) {
                autoplayTimer.stop();
                autoplayBtn.setText("▶  Autoplay");
                autoplayBtn.setBackground(new Color(40, 120, 60));
                statusLabel.setText(" ");
                return;
            }

            statusLabel.setText("Solving...");
            autoplayBtn.setEnabled(false);
            gameUI.setEnabled(false);

            // Capture current board state to solve from
            final Board currentBoard = boardRef[0];

            SwingWorker<List<Autoplay.Move>, Void> worker = new SwingWorker<>() {
                @Override
                protected List<Autoplay.Move> doInBackground() {
                    return Autoplay.solve(currentBoard);
                }

                @Override
                protected void done() {
                    try {
                        List<Autoplay.Move> solution = get();
                        autoplayBtn.setEnabled(true);

                        if (solution.isEmpty()) {
                            statusLabel.setText("No solution!");
                            gameUI.setEnabled(true);
                            return;
                        }

                        statusLabel.setText("Playing...");
                        autoplayBtn.setText("■  Stop");
                        autoplayBtn.setBackground(new Color(160, 50, 30));

                        int[] step = { 0 };

                        //  between moves - readable but not slow
                        autoplayTimer = new Timer(400, null);
                        autoplayTimer.addActionListener(tick -> {
                            if (step[0] >= solution.size()) {
                                autoplayTimer.stop();
                                autoplayBtn.setText("▶  Autoplay");
                                autoplayBtn.setBackground(new Color(40, 120, 60));
                                statusLabel.setText("Done!");
                                gameUI.setEnabled(true);
                                return;
                            }
                            Autoplay.Move m = solution.get(step[0]++);
                            currentBoard.makeMove(m.fromR, m.fromC, m.toR, m.toC);
                            gameUI.repaint();
                        });
                        autoplayTimer.start();

                    } catch (Exception ex) {
                        ex.printStackTrace();
                        autoplayBtn.setEnabled(true);
                        gameUI.setEnabled(true);
                        statusLabel.setText("Error");
                    }
                }
            };
            worker.execute();
        });

        sidebar.add(autoplayBtn);
        sidebar.add(Box.createVerticalGlue());
        sidebar.add(Box.createVerticalStrut(16));

        // New game button
        JButton newGameBtn = new JButton("New Game");
        newGameBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        newGameBtn.setFont(new Font("SansSerif", Font.BOLD, 13));
        newGameBtn.setBackground(new Color(180, 120, 40));
        newGameBtn.setForeground(Color.WHITE);
        newGameBtn.setFocusPainted(false);
        newGameBtn.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        newGameBtn.addActionListener(e -> {
            if (autoplayTimer != null) autoplayTimer.stop();
            autoplayBtn.setText("▶  Autoplay");
            autoplayBtn.setBackground(new Color(40, 120, 60));
            statusLabel.setText(" ");
            Board newBoard = new Board(selectedType[0]);
            boardRef[0] = newBoard;
            gameUI.setBoard(newBoard);
            gameUI.setEnabled(true);
        });
        sidebar.add(newGameBtn);

        // --- Frame ---
        JFrame frame = new JFrame("Peg Solitaire");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel content = new JPanel(new BorderLayout());
        content.add(sidebar, BorderLayout.WEST);
        content.add(gameUI, BorderLayout.CENTER);

        frame.setContentPane(content);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setVisible(true);
    }
}
}
