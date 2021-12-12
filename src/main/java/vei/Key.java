package vei;

// i would like to quickly disclose that nearly all of this code was written
// in about 5 minutes using vim macros

public enum Key {
    // no key
    NONE,

    // lowercase letters
    A,
    B,
    C,
    D,
    E,
    F,
    G,
    H,
    I,
    J,
    K,
    L,
    M,
    N,
    O,
    P,
    Q,
    R,
    S,
    T,
    U,
    V,
    W,
    X,
    Y,
    Z,

    // uppercase letters
    S_A,
    S_B,
    S_C,
    S_D,
    S_E,
    S_F,
    S_G,
    S_H,
    S_I,
    S_J,
    S_K,
    S_L,
    S_M,
    S_N,
    S_O,
    S_P,
    S_Q,
    S_R,
    S_S,
    S_T,
    S_U,
    S_V,
    S_W,
    S_X,
    S_Y,
    S_Z,

    // command letters
    C_A,
    C_B,
    C_C,
    C_D,
    C_E,
    C_F,
    C_G,
    C_H,
    C_I,
    C_J,
    C_K,
    C_L,
    C_M,
    C_N,
    C_O,
    C_P,
    C_Q,
    C_R,
    C_S,
    C_T,
    C_U,
    C_V,
    C_W,
    C_X,
    C_Y,
    C_Z,

    // command letters
    C_S_A,
    C_S_B,
    C_S_C,
    C_S_D,
    C_S_E,
    C_S_F,
    C_S_G,
    C_S_H,
    C_S_I,
    C_S_J,
    C_S_K,
    C_S_L,
    C_S_M,
    C_S_N,
    C_S_O,
    C_S_P,
    C_S_Q,
    C_S_R,
    C_S_S,
    C_S_T,
    C_S_U,
    C_S_V,
    C_S_W,
    C_S_X,
    C_S_Y,
    C_S_Z,

    // regular numbers
    N_1,
    N_2,
    N_3,
    N_4,
    N_5,
    N_6,
    N_7,
    N_8,
    N_9,
    N_0,

    // uppercase numbers / special characters
    EXCLAMATION,       // 1
    AT,                // 2
    HASHTAG,           // 3
    DOLLAR_SIGN,       // 4
    PERCENT_SIGN,      // 5
    CARAT,             // 6
    ASTERISK,          // 8

    // weird characters
    SPACE,
    ENTER,
    TAB,
    BACKSPACE,

    // control + weird characters
    C_SPACE,
    C_ENTER,
    C_TAB,
    C_BACKSPACE,

    // punctuation
    COLON,
    SEMICOLON,

    UNDERSCORE,
    MINUS,
    PLUS,
    EQUALS,
    SLASH,
    BACKSLASH,
    QUESTION_MARK,
    COMMA,
    PERIOD,
    PIPE,
    TILDE,
    BACKTICK,

    SINGLE_QUOTE,
    DOUBLE_QUOTE,

    DIAMOND_OPEN,
    DIAMOND_CLOSE,

    BRACKET_OPEN,
    BRACKET_CLOSE,

    SQUARE_BRACKET_OPEN,
    SQUARE_BRACKET_CLOSE,

    CURLY_BRACKET_OPEN,
    CURLY_BRACKET_CLOSE;

    public static char reverseMatch(Key input) {
        switch (input) {
            case A:
                return 'a';
            case B:
                return 'b';
            case C:
                return 'c';
            case D:
                return 'd';
            case E:
                return 'e';
            case F:
                return 'f';
            case G:
                return 'g';
            case H:
                return 'h';
            case I:
                return 'i';
            case J:
                return 'j';
            case K:
                return 'k';
            case L:
                return 'l';
            case M:
                return 'm';
            case N:
                return 'n';
            case O:
                return 'o';
            case P:
                return 'p';
            case Q:
                return 'q';
            case R:
                return 'r';
            case S:
                return 's';
            case T:
                return 't';
            case U:
                return 'u';
            case V:
                return 'v';
            case W:
                return 'w';
            case X:
                return 'x';
            case Y:
                return 'y';
            case Z:
                return 'z';
            case S_A:
                return 'A';
            case S_B:
                return 'B';
            case S_C:
                return 'C';
            case S_D:
                return 'D';
            case S_E:
                return 'E';
            case S_F:
                return 'F';
            case S_G:
                return 'G';
            case S_H:
                return 'H';
            case S_I:
                return 'I';
            case S_J:
                return 'J';
            case S_K:
                return 'K';
            case S_L:
                return 'L';
            case S_M:
                return 'M';
            case S_N:
                return 'N';
            case S_O:
                return 'O';
            case S_P:
                return 'P';
            case S_Q:
                return 'Q';
            case S_R:
                return 'R';
            case S_S:
                return 'S';
            case S_T:
                return 'T';
            case S_U:
                return 'U';
            case S_V:
                return 'V';
            case S_W:
                return 'W';
            case S_X:
                return 'X';
            case S_Y:
                return 'Y';
            case S_Z:
                return 'Z';
        }

        return '?';
    }

    public static Key match(char input,
                            boolean isControlPressed,
                            boolean isBackspacePressed) {
        if (!isControlPressed) {
            switch (input) {
                case 'a':
                    return Key.A;
                case 'b':
                    return Key.B;
                case 'c':
                    return Key.C;
                case 'd':
                    return Key.D;
                case 'e':
                    return Key.E;
                case 'f':
                    return Key.F;
                case 'g':
                    return Key.G;
                case 'h':
                    return Key.H;
                case 'i':
                    return Key.I;
                case 'j':
                    return Key.J;
                case 'k':
                    return Key.K;
                case 'l':
                    return Key.L;
                case 'm':
                    return Key.M;
                case 'n':
                    return Key.N;
                case 'o':
                    return Key.O;
                case 'p':
                    return Key.P;
                case 'q':
                    return Key.Q;
                case 'r':
                    return Key.R;
                case 's':
                    return Key.S;
                case 't':
                    return Key.T;
                case 'u':
                    return Key.U;
                case 'v':
                    return Key.V;
                case 'w':
                    return Key.W;
                case 'x':
                    return Key.X;
                case 'y':
                    return Key.Y;
                case 'z':
                    return Key.Z;
                case 'A':
                    return Key.S_A;
                case 'B':
                    return Key.S_B;
                case 'C':
                    return Key.S_C;
                case 'D':
                    return Key.S_D;
                case 'E':
                    return Key.S_E;
                case 'F':
                    return Key.S_F;
                case 'G':
                    return Key.S_G;
                case 'H':
                    return Key.S_H;
                case 'I':
                    return Key.S_I;
                case 'J':
                    return Key.S_J;
                case 'K':
                    return Key.S_K;
                case 'L':
                    return Key.S_L;
                case 'M':
                    return Key.S_M;
                case 'N':
                    return Key.S_N;
                case 'O':
                    return Key.S_O;
                case 'P':
                    return Key.S_P;
                case 'Q':
                    return Key.S_Q;
                case 'R':
                    return Key.S_R;
                case 'S':
                    return Key.S_S;
                case 'T':
                    return Key.S_T;
                case 'U':
                    return Key.S_U;
                case 'V':
                    return Key.S_V;
                case 'W':
                    return Key.S_W;
                case 'X':
                    return Key.S_X;
                case 'Y':
                    return Key.S_Y;
                case 'Z':
                    return Key.S_Z;
                case '1':
                    return Key.N_1;
                case '2':
                    return Key.N_2;
                case '3':
                    return Key.N_3;
                case '4':
                    return Key.N_4;
                case '5':
                    return Key.N_5;
                case '6':
                    return Key.N_6;
                case '7':
                    return Key.N_7;
                case '8':
                    return Key.N_8;
                case '9':
                    return Key.N_9;
                case '0':
                    return Key.N_0;
                case '!':
                    return Key.EXCLAMATION;
                case '@':
                    return Key.AT;
                case '#':
                    return Key.HASHTAG;
                case '$':
                    return Key.DOLLAR_SIGN;
                case '%':
                    return Key.PERCENT_SIGN;
                case '^':
                    return Key.CARAT;
                case '*':
                    return Key.ASTERISK;
                case ' ':
                    return Key.SPACE;
                case '\n':
                    return Key.ENTER;
                case '\t':
                    return Key.TAB;
                case ':':
                    return Key.COLON;
                case ';':
                    return Key.SEMICOLON;
                case '_':
                    return Key.UNDERSCORE;
                case '-':
                    return Key.MINUS;
                case '+':
                    return Key.PLUS;
                case '=':
                    return Key.EQUALS;
                case '/':
                    return Key.SLASH;
                case '\\':
                    return Key.BACKSLASH;
                case '?':
                    return Key.QUESTION_MARK;
                case ',':
                    return Key.COMMA;
                case '.':
                    return Key.PERIOD;
                case '|':
                    return Key.PIPE;
                case '~':
                    return Key.TILDE;
                case '`':
                    return Key.BACKTICK;
                case '\'':
                    return Key.SINGLE_QUOTE;
                case '"':
                    return Key.DOUBLE_QUOTE;
                case '<':
                    return Key.DIAMOND_OPEN;
                case '>':
                    return Key.DIAMOND_CLOSE;
                case '(':
                    return Key.BRACKET_OPEN;
                case ')':
                    return Key.BRACKET_CLOSE;
                case '[':
                    return Key.SQUARE_BRACKET_OPEN;
                case ']':
                    return Key.SQUARE_BRACKET_CLOSE;
                case '{':
                    return Key.CURLY_BRACKET_OPEN;
                case '}':
                    return Key.CURLY_BRACKET_CLOSE;
            }
            if (isBackspacePressed) return Key.BACKSPACE;
        } else {
            switch (input) {
                case 'a':
                    return Key.C_A;
                case 'b':
                    return Key.C_B;
                case 'c':
                    return Key.C_C;
                case 'd':
                    return Key.C_D;
                case 'e':
                    return Key.C_E;
                case 'f':
                    return Key.C_F;
                case 'g':
                    return Key.C_G;
                case 'h':
                    return Key.C_H;
                case 'i':
                    return Key.C_I;
                case 'j':
                    return Key.C_J;
                case 'k':
                    return Key.C_K;
                case 'l':
                    return Key.C_L;
                case 'm':
                    return Key.C_M;
                case 'n':
                    return Key.C_N;
                case 'o':
                    return Key.C_O;
                case 'p':
                    return Key.C_P;
                case 'q':
                    return Key.C_Q;
                case 'r':
                    return Key.C_R;
                case 's':
                    return Key.C_S;
                case 't':
                    return Key.C_T;
                case 'u':
                    return Key.C_U;
                case 'v':
                    return Key.C_V;
                case 'w':
                    return Key.C_W;
                case 'x':
                    return Key.C_X;
                case 'y':
                    return Key.C_Y;
                case 'z':
                    return Key.C_Z;
                case 'A':
                    return Key.C_S_A;
                case 'B':
                    return Key.C_S_B;
                case 'C':
                    return Key.C_S_C;
                case 'D':
                    return Key.C_S_D;
                case 'E':
                    return Key.C_S_E;
                case 'F':
                    return Key.C_S_F;
                case 'G':
                    return Key.C_S_G;
                case 'H':
                    return Key.C_S_H;
                case 'I':
                    return Key.C_S_I;
                case 'J':
                    return Key.C_S_J;
                case 'K':
                    return Key.C_S_K;
                case 'L':
                    return Key.C_S_L;
                case 'M':
                    return Key.C_S_M;
                case 'N':
                    return Key.C_S_N;
                case 'O':
                    return Key.C_S_O;
                case 'P':
                    return Key.C_S_P;
                case 'Q':
                    return Key.C_S_Q;
                case 'R':
                    return Key.C_S_R;
                case 'S':
                    return Key.C_S_S;
                case 'T':
                    return Key.C_S_T;
                case 'U':
                    return Key.C_S_U;
                case 'V':
                    return Key.C_S_V;
                case 'W':
                    return Key.C_S_W;
                case 'X':
                    return Key.C_S_X;
                case 'Y':
                    return Key.C_S_Y;
                case 'Z':
                    return Key.C_S_Z;
                case ' ':
                    return Key.C_SPACE;
                case '\n':
                    return Key.C_ENTER;
                case '\t':
                    return Key.C_TAB;
            }
            if (isBackspacePressed) return Key.C_BACKSPACE;
        }
        return Key.NONE;
    }

    public static Key[] translateString(String string,
                                        Key... extras) {
        Key[] keys = new Key[string.length() + extras.length];
        char[] chars = string.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            keys[i] = match(chars[i], false, false);
        }
        System.arraycopy(extras, 0, keys, chars.length, extras.length);
        return keys;
    }
}
