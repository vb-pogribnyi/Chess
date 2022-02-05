import os
import re
import subprocess

class Fish:
    def __init__(self, depth=10, nnue=True):
        self.fish_mem = None
        self.depth = depth
        self.fish = subprocess.Popen('stockfish.exe', stdin=subprocess.PIPE, stdout=subprocess.PIPE)
        self.scores = []
        self.score_type = ''

        cmd = 'setoption name Use NNUE value {}'.format('true' if nnue else 'false')
        self.fish.stdin.write((cmd + '\n').encode())
        self.fish.stdin.flush()

        greeting = self.fish.stdout.readline().decode()
        print(greeting)

    def set_state(self, history):
        cmd = 'position startpos moves ' + ' '.join(history)
        self.fish.stdin.write((cmd + '\n').encode())
        self.fish.stdin.flush()

    def get_move(self):
        if self.fish_mem:
            result = self.fish_mem
            self.fish_mem = None
        else:
            self.scores.clear()
            self.fish.stdin.write('go depth {}\n'.format(self.depth).encode())
            self.fish.stdin.flush()
            fish_out = ''
            while not 'bestmove' in fish_out:
                fish_out = self.fish.stdout.readline().decode()
                score = re.search('score (cp|mate) ([-]?[0-9]+)', fish_out)
                if score:
                    self.score_type = score.group(1)
                    score = int(score.group(2)) / 100
                    self.scores.append(score)
                # print(fish_out)
            fish_out = fish_out.split(' ')[1]
            fish_out = fish_out.rstrip()
            if 'none' in fish_out:
                return None
            result = fish_out[:2]
            self.fish_mem = fish_out[2:]
        return result

    def get_possible_moves(self):
        self.fish.stdin.write('go perft 1\n'.encode())
        self.fish.stdin.flush()
        result = {}
        fish_result = []
        while True:
            fish_out = self.fish.stdout.readline().decode()
            if 'Nodes searched' in fish_out:
                break
            fish_out = fish_out.split(':')
            if len(fish_out) > 1:
                fish_result.append(fish_out[0])
        for v in fish_result:
            start_square = v[:2]
            end_square = v[2:]
            if not start_square in result:
                result[start_square] = []
            result[start_square].append(end_square)
        return result

class Human:
    def get_move(self):
        return input()

    def set_state(self, history):
        pass

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

# pieces_codes = {
#     'p': '\u265f', 'n': '\u265e', 'b': '\u265d', 'r': '\u265c', 'q': '\u265b', 'k': '\u265a',
#     'P': '\u2659', 'N': '\u2658', 'B': '\u2657', 'R': '\u2656', 'Q': '\u2655', 'K': '\u2654'
# }

pieces_codes = {
    'p': 'p', 'n': 'n', 'b': 'b', 'r': 'r', 'q': 'q', 'k': 'k',
    'P': 'P', 'N': 'N', 'B': 'B', 'R': 'R', 'Q': 'Q', 'K': 'K'
}

is_white_move = True
possible_moves = None
moves = []
selection = None

c_chess = lambda x, y: '{}{}'.format(chr(x + 96), str(y))
# c_num = lambda c: (ord(c[0]) - 96, int(c[1]))


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
            # char = '\u2005{}\u2006'.format(char)
            char = '{}'.format(char)

            if x > 0 and y < 8:
                if c_chess(xc, yc) in state:
                    piece = state[c_chess(xc, yc)]
                    char = pieces_codes[piece]
                    if selection == c_chess(xc, yc):
                        char = colors['green'] + char + colors['reset']
            if moves is not None:
                for m in moves:
                    if m[:2] == c_chess(xc, yc):
                        char = colors['yellow'] + char + colors['reset']

            result += ' {}'.format(char)

        print(result)

guard_fish = Fish(15, nnue=False)
# player_w = Human()
player_w = Fish(10)
# player_b = Human()
player_b = Fish(5)
history = []
while True:
    guard_fish.set_state(history)
    if not possible_moves:
        possible_moves = guard_fish.get_possible_moves()
        guard_fish.get_move()
        # print('Calc moves', is_white_move)
    if selection and not moves:
        moves = possible_moves[selection]

    os.system('cls')
    print(guard_fish.score_type, ' '.join([str(i) for i in guard_fish.scores]))
    draw()
    print(' '.join(history))

    if not is_white_move:
        player_b.set_state(history)
        cmd = player_b.get_move()
        if len(possible_moves) == 0:
            exit('White win')
    else:
        player_w.set_state(history)
        cmd = player_w.get_move()
        if len(possible_moves) == 0:
            exit('Black win')

    if selection is None and cmd in possible_moves:
        selection = cmd
    elif selection is not None and cmd in possible_moves[selection]:
        if cmd in state:
            print('Taking', state[cmd], cmd)
            state.pop(cmd)
        # Castling
        if selection == 'e1' and state['e1'] == 'K' and cmd == 'g1':
            state['f1'] = state['h1']
            state.pop('h1')
        elif selection == 'e1' and state['e1'] == 'K' and cmd == 'c1':
            state['d1'] = state['a1']
            state.pop('a1')
        elif selection == 'e8' and state['e8'] == 'k' and cmd == 'g8':
            state['f8'] = state['h8']
            state.pop('h8')
        elif selection == 'e8' and state['e8'] == 'k' and cmd == 'c8':
            state['d8'] = state['a8']
            state.pop('a8')

        if len(cmd) == 3:
            state[cmd[:2]] = cmd[2].upper() if is_white_move else cmd[2].lower()
        else:
            state[cmd] = state[selection]
        state.pop(selection)
        history.append(selection + cmd)
        selection = None
        is_white_move = not is_white_move
        possible_moves = None
        moves = None
    else:
        selection = None
