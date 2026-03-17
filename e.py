import copy
import random
import sys
import time


### Boards ###
def make_english():
    """
    English Board
    """
    grid = [[-1]*7 for _ in range(7)]
    for r in range(7):
        for c in range(7):
            if (r in (0,1,5,6) and c in (0,1,5,6)):
                grid[r][c]=-1
            else:
                grid[r][c]=1
    grid[3][3]=0
    return grid

def make_european():
    """
    European board
    """
    grid = make_english()
    for r,c in [(1,1),(1,5),(5,1),(5,5)]:
        grid[r][c]=1
    grid[3][3]=0
    return grid

def make_diamond():
    """
    Diamond board
    """
    grid = [[-1]*9 for _ in range(9)]
    for r in range(9):
        for c in range(9):
            if abs(r-4)+ abs(c-4)<=4:
                grid[r][c]=1
    grid[4][4]=0
    return grid

BOARDS = {
    "1": ("English", make_english),
    "2": ("European", make_european),
    "3": ("Diamond", make_diamond),
}

### Pyhon renders ###
PEG   = "●"
HOLE  = "○"
EMPTY = " "

def render(grid):
    rows = len(grid)
    cols = len(grid[0])
    lines = []
    # Column header
    header = "   " + "  ".join(str(c) for c in range(cols))
    lines.append(header)
    for r in range(rows):
        row_str = f"{r}  "
        for c in range(cols):
            v = grid[r][c]
            if v == 1:
                row_str += PEG + "  "
            elif v == 0:
                row_str += HOLE + "  "
            else:
                row_str += EMPTY + "  "
        lines.append(row_str.rstrip())
    return "\n".join(lines)

def count_pegs(grid):
    return sum(v for row in grid for v in row if v == 1)

def clear_screen():
    print("\033[H\033[J", end="") 

### Move checkers ###
def valid_moves(grid):
    """Return list of (r, c, dr, dc) for every legal jump."""
    moves = []
    rows, cols = len(grid), len(grid[0])
    for r in range(rows):
        for c in range(cols):
            if grid[r][c] != 1:
                continue
            for dr, dc in DIRECTIONS:
                mr, mc = r+dr, c+dc      # middle (jumped) peg
                er, ec = r+2*dr, c+2*dc  # landing hole
                if (0 <= mr < rows and 0 <= mc < cols and
                        0 <= er < rows and 0 <= ec < cols and
                        grid[mr][mc] == 1 and grid[er][ec] == 0):
                    moves.append((r, c, dr, dc))
    return moves

def apply_move(grid, r, c, dr, dc):
    """Return a NEW grid after applying the move (non-destructive)."""
    g = copy.deepcopy(grid)
    g[r][c]          = 0
    g[r+dr][c+dc]    = 0
    g[r+2*dr][c+2*dc]= 1
    return g

### Starts Game ###
class Game:
    def __init__(self, board_key):
        name, factory = BOARDS[board_key]
        self.board_name   = name
        self.board_key    = board_key
        self.initial_grid = factory()
        self.grid         = copy.deepcopy(self.initial_grid)
        self.history      = []       
        self.move_count   = 0
        self.replay_log   = []        



    def display(self, message=""):
        clear_screen()
        pegs = count_pegs(self.grid)
        print(f"╔══ Peg Solitaire ══ Board: {self.board_name} ══ Pegs left: {pegs} ══ Moves: {self.move_count} ╗")
        print()
        print(render(self.grid))
        print()
        if message:
            print(f"  {message}")
        print()
        self._print_menu()

    def _print_menu(self):
        print("  Commands:")
        print("    move <r> <c> <dr> <dc>  — jump a peg  (dr/dc: -1, 0, or 1)")
        print("    undo                    — undo last move")
        print("    replay                  — replay the current game from start")
        print("    new                     — choose a new board and restart")
        print("    random                  — play a random board")
        print("    solve                   — auto-solve from current position")
        print("    quit                    — exit")
        print()



    def do_move(self, args):
        try:
            r, c, dr, dc = int(args[0]), int(args[1]), int(args[2]), int(args[3])
        except (ValueError, IndexError):
            return "Usage: move <r> <c> <dr> <dc>"
        if dr not in (-1,0,1) or dc not in (-1,0,1) or (dr==0 and dc==0):
            return "dr and dc must each be -1, 0, or 1 (and not both 0)."
        rows, cols = len(self.grid), len(self.grid[0])
        mr, mc = r+dr, c+dc
        er, ec = r+2*dr, c+2*dc
        if not (0<=r<rows and 0<=c<cols):
            return "Starting position is out of bounds."
        if self.grid[r][c] != 1:
            return f"No peg at ({r},{c})."
        if not (0<=mr<rows and 0<=mc<cols) or self.grid[mr][mc] != 1:
            return f"No adjacent peg to jump at ({mr},{mc})."
        if not (0<=er<rows and 0<=ec<cols) or self.grid[er][ec] != 0:
            return f"Landing cell ({er},{ec}) is not an empty hole."
        self.history.append(copy.deepcopy(self.grid))
        self.grid = apply_move(self.grid, r, c, dr, dc)
        self.replay_log.append((r, c, dr, dc))
        self.move_count += 1
        return ""

    def do_undo(self):
        if not self.history:
            return "Nothing to undo."
        self.grid = self.history.pop()
        self.move_count -= 1
        if self.replay_log:
            self.replay_log.pop()
        return "Move undone."

    def do_replay(self):
        """Re-play all moves from the initial board with a short delay."""
        clear_screen()
        print("  ── Replay ──")
        time.sleep(0.4)
        tmp = copy.deepcopy(self.initial_grid)
        for i, mv in enumerate(self.replay_log, 1):
            clear_screen()
            pegs = count_pegs(tmp)
            print(f"  Replay — step {i}/{len(self.replay_log)}  |  pegs: {pegs}")
            print()
            print(render(tmp))
            time.sleep(0.6)
            tmp = apply_move(tmp, *mv)
        clear_screen()
        print("  Replay finished — final position:")
        print()
        print(render(tmp))
        time.sleep(1.2)


    def is_over(self):
        return len(valid_moves(self.grid)) == 0

    def win(self):
        return count_pegs(self.grid) == 1


### Board Select ###

def choose_board(prompt="Choose a board"):
    clear_screen()
    print("╔══════════════════════════════╗")
    print("      PEG SOLITAIRE           ")
    print("╚══════════════════════════════╝")
    print()
    print(f"  {prompt}:")
    print()
    for key, (name, _) in BOARDS.items():
        print(f"    [{key}]  {name}")
    print()
    while True:
        choice = input("  Enter 1, 2, or 3: ").strip()
        if choice in BOARDS:
            return choice
        print("  Please enter 1, 2, or 3.")

def random_board():
    return random.choice(list(BOARDS.keys()))



### MAIN PAGE ###
def main():
    print("Peg Solitaire!")
    time.sleep(0.5)
    board_key = choose_board()
    game = Game(board_key)
    msg = ""

    while True:
        game.display(msg)
        msg = ""

        if game.is_over():
            if game.win():
                print("    Congratulations  you solved it!  ")
            else:
                print("  No more moves. Game over!")
            print()
            print("  [r] Replay    [n] New game    [x] Random board    [q] Quit")
            while True:
                cmd = input("  > ").strip().lower()
                if cmd == "r":
                    game.do_replay()
                    break
                elif cmd == "n":
                    board_key = choose_board("New game. choose a board")
                    game = Game(board_key)
                    break
                elif cmd == "x":
                    game = Game(random_board())
                    break
                elif cmd == "q":
                    print("  Goodbye!")
                    sys.exit(0)
                else:
                    print("  Enter r, n, x, or q.")
            continue

        raw = input("  > ").strip()
        if not raw:
            continue
        parts = raw.lower().split()
        cmd = parts[0]

        if cmd in ("quit", "q", "exit"):
            print("  Goodbye!")
            sys.exit(0)

        elif cmd == "move" or (len(parts) == 4 and all(p.lstrip('-').isdigit() for p in parts)):
            args = parts[1:] if cmd == "move" else parts
            msg = game.do_move(args)

        elif cmd == "undo":
            msg = game.do_undo()

        elif cmd == "replay":
            game.do_replay()

        elif cmd == "new":
            board_key = choose_board("Choose a board")
            game = Game(board_key)

        elif cmd == "random":
            game = Game(random_board())
            msg = f"Random board selected: {game.board_name}"

        else:
            msg = f"Unknown command: '{cmd}'. See menu above."


if __name__ == "__main__":
    main()
