import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PushbackReader;

public class JackTokenizer {

	private PushbackReader reader;
	private boolean hasMoreTokens;
	private char ch;
	private TokenType tokenType;
	private KeyWord keyWord;
	private char symbol;
	private String identifier;
	private State start;
	private String token;
	private String symbols = "{}()[].,;+-*/&|<>=~";

	public JackTokenizer(File src) {
		hasMoreTokens = true;

		try {
			reader = new PushbackReader(new FileReader(src));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	public boolean hasMoreTokens() {
		return hasMoreTokens;
	}

	public TokenType tokenType() {
		return tokenType;
	}

	public KeyWord keyWord() {
		return keyWord;
	}

	public char symbol() {
		return symbol;
	}

	public String identifier() {
		return identifier;
	}

	public int intVal() {
		try {
			return Integer.parseInt(identifier);
		} catch (NumberFormatException e) {
			e.printStackTrace();
			System.exit(1);
		}
		return -1;
	}

	// Return string without the enclosing quotes.
	public String stringVal() {
		return identifier;
	}

	private void getNextChar() {
		try {
			int data = reader.read();
			if (data == -1)
				hasMoreTokens = false;
			else
				ch = (char) data;
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	private void unread() {
		try {
			reader.unread(ch);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	public void advance() {
		// Complete to advance over next token, setting values in the fields
		// Need to getNextChar()
		// Need to check hasnextToken
		State s;
		s = State.START;

		this.getNextChar();
		identifier = "";

		while (hasMoreTokens()) {

			switch (s) {
			case START:
				// Letter
				if (Character.isLetter(ch)) {
					identifier += ch;
					s = State.LETTER;
				}

				// Digit
				else if (Character.isDigit(ch)) {
					identifier += ch;
					s = State.NUMBER;
					// should I be returning something here ch = ?
				}
				// Slash
				else if (ch == '/') {
					s = State.SLASH;
				}
				// Symbols
				else if (symbols.contains(new String(new char[] { ch }))) {
					// should I be adding this to the identifier ?
					tokenType = tokenType.SYMBOL;
					symbol = ch;
					return;
				}

				else if (ch == '"') {
					s = State.STRING;
				}
				break;

			case NUMBER:
				if (Character.isDigit(ch)) {
					identifier += ch;
					s = State.NUMBER;

				} else {
					unread();
					tokenType = TokenType.INT_CONST;
					return;
				}
				break;

			case LETTER:
				if (Character.isLetterOrDigit(ch) || ch == '_') {

					// ask is this the correct way to handle other case
					identifier += ch;
					s = State.LETTER;

				} else {
					unread();
					tokenType = TokenType.IDENTIFIER;
					keyWordChecker();
					return;
				}
				break;

			case STRING:
				// Should this be not equal instead check while testing
				if (ch != '"') {

					identifier += ch;
					s = s.STRING;
				} else {
					tokenType = TokenType.STRING_CONST;
					return;
				}
				break;

			case SLASH:

				// If char is a slash, we are in the slash state
				if (ch == '/') {
					s = State.COMMENT_LINE;
				}
				else if (ch == '*') {
					s = State.Star_State;
				}
				else {
					// division symbol
					this.unread();
					tokenType = TokenType.SYMBOL;
					symbol = '/';
					return;
				}
				break;
			case COMMENT_LINE:
				if (ch == '\n') {
					s = State.START;
				}
				break;
			case Star_State:
				if (ch == '*') {
					s = s.FINAL_Star;
				}
				break;

			case FINAL_Star:
				if (ch == '/') {
					s = State.START;
				}
				else {
					s = State.FINAL_Star;
				}
				break;
			}
			this.getNextChar();

		}

	}

	String returnToken() {
		return identifier;
	}

	private void keyWordChecker() {
		tokenType = TokenType.KEYWORD;

		switch (identifier) {
		case "class":
			keyWord = KeyWord.CLASS;
			break;
		case "constructor":
			keyWord = KeyWord.CONSTRUCTOR;
			break;
		case "function":
			keyWord = KeyWord.FUNCTION;
			break;
		case "method":
			keyWord = KeyWord.METHOD;
			break;
		case "field":
			keyWord = KeyWord.FIELD;
			break;
		case "static":
			keyWord = KeyWord.STATIC;
			break;
		case "var":
			keyWord = KeyWord.VAR;
			break;
		case "int":
			keyWord = KeyWord.INT;
			break;
		case "char":
			keyWord = KeyWord.CHAR;
			break;
		case "boolean":
			keyWord = KeyWord.BOOLEAN;
			break;
		case "void":
			keyWord = KeyWord.VOID;
			break;
		case "true":
			keyWord = KeyWord.TRUE;
			break;
		case "false":
			keyWord = KeyWord.FALSE;
			break;
		case "null":
			keyWord = KeyWord.NULL;
			break;
		case "this":
			keyWord = KeyWord.THIS;
			break;
		case "let":
			keyWord = KeyWord.LET;
			break;
		case "do":
			keyWord = KeyWord.DO;
			break;
		case "if":
			keyWord = KeyWord.IF;
			break;
		case "else":
			keyWord = KeyWord.ELSE;
			break;
		case "while":
			keyWord = KeyWord.WHILE;
			break;
		case "return":
			keyWord = KeyWord.RETURN;
			break;
		default:
			tokenType = TokenType.IDENTIFIER;
			break;
		}

	}

}
