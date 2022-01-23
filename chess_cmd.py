import os

colors = {
    'black': '\u001b[30m',
    'red': '\u001b[31m',
    'green': '\u001b[32m',
    'yellow': '\u001b[33m',
    'blue': '\u001b[34m',
    'magenta': '\u001b[35m',
    'cyan': '\u001b[36m',
    'white': '\u001b[37m',
    'reset': '\u001b[0m'
}

state = {
    'a8': 'r', 'b8': 'n', 'c8': 'b', 'd8': 'q', 'e8': 'k', 'f8': 'b', 'g8': 'n', 'h8': 'r',
    'a7': 'p', 'b7': 'p', 'c7': 'p', 'd7': 'p', 'e7': 'p', 'f7': 'p', 'g7': 'p', 'h7': 'p',
    'a2': 'P', 'b2': 'P', 'c2': 'P', 'd2': 'P', 'e2': 'P', 'f2': 'P', 'g2': 'P', 'h2': 'P',
    'a1': 'R', 'b1': 'N', 'c1': 'B', 'd1': 'Q', 'e1': 'K', 'f1': 'B', 'g1': 'N', 'h1': 'R'
}
pieces_codes = {
    'p': '\u265f', 'n': '\u265e', 'b': '\u265d', 'r': '\u265c', 'q': '\u265b', 'k': '\u265a',
    'P': '\u2659', 'N': '\u2658', 'B': '\u2657', 'R': '\u2656', 'Q': '\u2655', 'K': '\u2654'
}

moves = None
selection = None

c_chess = lambda x, y: '{}{}'.format(chr(x + 96), str(y))
c_num = lambda c: (ord(c[0]) - 96, int(c[1]))

def is_enemy(p1, p2):
    return (p1.lower() == p1) != (p2.lower() == p2)

def get_linear_moves(x, y, piece, dx, dy):
    result = set()
    while True:
        x += dx
        y += dy
        move = (x, y)
        coord = c_chess(x, y)
        if not (9 > x > 0 and 9 > y > 0):
            break
        if coord in state:
            if is_enemy(piece, state[coord]):
                result.add(move)
                break
            else:
                break
        result.add(move)
    return result


def get_moves(piece, coord):
    moves, result = set(), set()
    x, y = c_num(coord)
    if piece.lower() == 'r':
        moves = moves.union(get_linear_moves(x, y, piece, 1, 0))
        moves = moves.union(get_linear_moves(x, y, piece, -1, 0))
        moves = moves.union(get_linear_moves(x, y, piece, 0, 1))
        moves = moves.union(get_linear_moves(x, y, piece, 0, -1))
    if piece.lower() == 'n':
        mv_options = [
            (x + 2, y + 1), (x + 2, y - 1), (x + 1, y + 2), (x + 1, y - 2),
            (x - 2, y + 1), (x - 2, y - 1), (x - 1, y + 2), (x - 1, y - 2)
        ]
        for option in mv_options:
            coord = c_chess(option[0], option[1])
            if coord not in state or is_enemy(piece, state[coord]):
                moves.add(option)
    if piece.lower() == 'k':
        mv_options = [
            (x + 1, y + 1), (x + 1, y), (x + 1, y - 1),
            (x, y + 1), (x, y - 1),
            (x - 1, y + 1), (x - 1, y), (x - 1, y - 1),
        ]
        for option in mv_options:
            coord = c_chess(option[0], option[1])
            if coord not in state or is_enemy(piece, state[coord]):
                moves.add(option)
    if piece.lower() == 'b':
        moves = moves.union(get_linear_moves(x, y, piece, 1, 1))
        moves = moves.union(get_linear_moves(x, y, piece, -1, -1))
        moves = moves.union(get_linear_moves(x, y, piece, -1, 1))
        moves = moves.union(get_linear_moves(x, y, piece, 1, -1))
    if piece.lower() == 'q':
        moves = moves.union(get_linear_moves(x, y, piece, 1, 1))
        moves = moves.union(get_linear_moves(x, y, piece, -1, -1))
        moves = moves.union(get_linear_moves(x, y, piece, -1, 1))
        moves = moves.union(get_linear_moves(x, y, piece, 1, -1))
        moves = moves.union(get_linear_moves(x, y, piece, 1, 0))
        moves = moves.union(get_linear_moves(x, y, piece, -1, 0))
        moves = moves.union(get_linear_moves(x, y, piece, 0, 1))
        moves = moves.union(get_linear_moves(x, y, piece, 0, -1))
    if piece == 'p':
        if c_chess(x, y - 1) not in state:
            moves.add((x, y-1))
        if y == 7 and c_chess(x, y - 2) not in state:
            moves.add((x, y-2))
        if c_chess(x + 1, y - 1) in state and is_enemy(piece, state[c_chess(x + 1, y - 1)]):
            moves.add((x+1, y-1))
        if c_chess(x - 1, y - 1) in state and is_enemy(piece, state[c_chess(x - 1, y - 1)]):
            moves.add((x-1, y-1))
    if piece == 'P':
        if c_chess(x, y + 1) not in state:
            moves.add((x, y+1))
        if y == 2 and c_chess(x, y + 2) not in state:
            moves.add((x, y+2))
        if c_chess(x + 1, y + 1) in state and is_enemy(piece, state[c_chess(x + 1, y + 1)]):
            moves.add((x+1, y+1))
        if c_chess(x - 1, y + 1) in state and is_enemy(piece, state[c_chess(x - 1, y + 1)]):
            moves.add((x-1, y+1))
    for m in moves:
        if not(8 >= m[0] >= 1 and 8 >= m[1] >= 1):
            continue
        result.add((m[0], m[1]))
    return result


def draw():
    for y in range(9):
        result = ''
        for x in range(9):
            xc = x
            yc = 8 - y
            char = '*'
            if x == 0 and y < 8:
                char = yc
            elif x == 0:
                char = ' '
            if y == 8 and x > 0:
                char = chr(xc + 96)
            char = '\u2005{}\u2006'.format(char)

            if x > 0 and y < 8:
                if c_chess(xc, yc) in state:
                    piece = state[c_chess(xc, yc)]
                    char = pieces_codes[piece]
                    if selection == c_chess(xc, yc):
                        char = colors['green'] + char + colors['reset']
            if moves is not None:
                for m in moves:
                    if xc == m[0] and yc == m[1]:
                        char = colors['yellow'] + char + colors['reset']

            result += ' {}'.format(char)

        print(result)

history = []
while True:
    if selection is not None:
        moves = get_moves(state[selection], selection)
    else:
        moves = None
    # os.system('cls')
    draw()
    print(' '.join(history))
    cmd = input()
    if len(cmd) > 2 or len(cmd) < 1:
        continue
    if selection is None and cmd in state:
        selection = cmd
    elif selection is not None and c_num(cmd) in moves:
        if cmd in state:
            print('Taking', state[cmd], cmd)
            state.pop(cmd)
        history.append(selection + cmd)
        state[cmd] = state[selection]
        state.pop(selection)
        selection = None
    elif cmd in state:
        selection = cmd
    else:
        selection = None

