/* The following code was generated by JFlex 1.4.3 on 20:19 06/11/10 */

package IC.Parser;


/**
 * This class is a scanner generated by 
 * <a href="http://www.jflex.de/">JFlex</a> 1.4.3
 * on 20:19 06/11/10 from the specification file
 * <tt>IC.lex</tt>
 */
public class Lexer {

  /** This character denotes the end of file */
  public static final int YYEOF = -1;

  /** initial size of the lookahead buffer */
  private static final int ZZ_BUFFERSIZE = 16384;

  /** lexical states */
  public static final int LONG_COMMENT = 4;
  public static final int IN_QUOTE = 2;
  public static final int ONE_LINE_COMMENT = 8;
  public static final int YYINITIAL = 0;
  public static final int LONG_COMMENT_AFTER_STAR = 6;

  /**
   * ZZ_LEXSTATE[l] is the state in the DFA for the lexical state l
   * ZZ_LEXSTATE[l+1] is the state in the DFA for the lexical state l
   *                  at the beginning of a line
   * l is of the form l = 2*k, k a non negative integer
   */
  private static final int ZZ_LEXSTATE[] = { 
     0,  0,  1,  1,  2,  2,  3,  3,  4, 4
  };

  /** 
   * Translates characters to character classes
   */
  private static final String ZZ_CMAP_PACKED = 
    "\11\0\1\6\1\5\2\0\1\6\22\0\1\6\1\25\1\7\2\0"+
    "\1\20\1\30\1\0\1\12\1\13\1\11\1\21\1\36\1\23\1\35"+
    "\1\10\1\24\11\1\1\0\1\37\1\27\1\22\1\26\2\0\1\2"+
    "\1\2\1\2\1\2\1\2\2\2\2\2\6\2\1\2\2\2\2\2"+
    "\2\2\1\2\3\2\1\14\1\32\1\15\1\0\1\4\1\0\1\47"+
    "\1\44\1\53\1\43\1\46\1\54\1\52\1\56\1\42\1\3\1\60"+
    "\1\45\1\3\1\33\1\41\2\3\1\51\1\50\1\34\1\57\1\40"+
    "\1\55\1\61\2\3\1\16\1\31\1\17\uff82\0";

  /** 
   * Translates characters to character classes
   */
  private static final char [] ZZ_CMAP = zzUnpackCMap(ZZ_CMAP_PACKED);

  /** 
   * Translates DFA states to action switch labels.
   */
  private static final int [] ZZ_ACTION = zzUnpackAction();

  private static final String ZZ_ACTION_PACKED_0 =
    "\5\0\1\1\1\2\1\3\1\4\1\5\1\6\1\7"+
    "\1\10\1\11\1\12\1\13\1\14\1\15\1\16\1\17"+
    "\1\20\1\21\1\22\1\2\1\23\1\24\1\25\2\1"+
    "\2\4\1\26\1\27\1\30\12\4\1\31\1\32\1\33"+
    "\1\31\1\5\1\34\1\35\1\36\1\37\1\40\1\41"+
    "\1\42\1\43\2\44\1\45\1\46\1\47\1\50\1\51"+
    "\1\52\6\4\1\53\13\4\1\54\1\55\1\56\1\45"+
    "\1\57\4\4\1\60\14\4\1\61\1\62\1\63\1\64"+
    "\3\4\1\65\11\4\1\66\6\4\1\67\1\70\1\71"+
    "\1\4\1\72\1\4\1\73\1\74\1\75\1\4\1\76"+
    "\1\77\1\4\1\100";

  private static int [] zzUnpackAction() {
    int [] result = new int[143];
    int offset = 0;
    offset = zzUnpackAction(ZZ_ACTION_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackAction(String packed, int offset, int [] result) {
    int i = 0;       /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int count = packed.charAt(i++);
      int value = packed.charAt(i++);
      do result[j++] = value; while (--count > 0);
    }
    return j;
  }


  /** 
   * Translates a state to a row index in the transition table
   */
  private static final int [] ZZ_ROWMAP = zzUnpackRowMap();

  private static final String ZZ_ROWMAP_PACKED_0 =
    "\0\0\0\62\0\144\0\226\0\310\0\372\0\u012c\0\u015e"+
    "\0\u0190\0\u01c2\0\372\0\u01f4\0\372\0\372\0\372\0\372"+
    "\0\372\0\372\0\372\0\372\0\372\0\u0226\0\u0258\0\u028a"+
    "\0\u02bc\0\u02ee\0\u0320\0\u0352\0\u0384\0\u03b6\0\u03e8\0\372"+
    "\0\372\0\372\0\u041a\0\u044c\0\u047e\0\u04b0\0\u04e2\0\u0514"+
    "\0\u0546\0\u0578\0\u05aa\0\u05dc\0\372\0\372\0\372\0\u060e"+
    "\0\372\0\372\0\372\0\372\0\372\0\u0640\0\372\0\372"+
    "\0\372\0\u0672\0\u06a4\0\u028a\0\372\0\372\0\372\0\372"+
    "\0\372\0\u06d6\0\u0708\0\u073a\0\u076c\0\u079e\0\u07d0\0\u0190"+
    "\0\u0802\0\u0834\0\u0866\0\u0898\0\u08ca\0\u08fc\0\u092e\0\u0960"+
    "\0\u0992\0\u09c4\0\u09f6\0\372\0\372\0\372\0\u06a4\0\u0190"+
    "\0\u0a28\0\u0a5a\0\u0a8c\0\u0abe\0\u0190\0\u0af0\0\u0b22\0\u0b54"+
    "\0\u0b86\0\u0bb8\0\u0bea\0\u0c1c\0\u0c4e\0\u0c80\0\u0cb2\0\u0ce4"+
    "\0\u0d16\0\u0190\0\u0190\0\u0190\0\u0190\0\u0d48\0\u0d7a\0\u0dac"+
    "\0\u0190\0\u0dde\0\u0e10\0\u0e42\0\u0e74\0\u0ea6\0\u0ed8\0\u0f0a"+
    "\0\u0f3c\0\u0f6e\0\u0190\0\u0fa0\0\u0fd2\0\u1004\0\u1036\0\u1068"+
    "\0\u109a\0\u0190\0\u0190\0\u0190\0\u10cc\0\u0190\0\u10fe\0\u0190"+
    "\0\u0190\0\u0190\0\u1130\0\u0190\0\u0190\0\u1162\0\u0190";

  private static int [] zzUnpackRowMap() {
    int [] result = new int[143];
    int offset = 0;
    offset = zzUnpackRowMap(ZZ_ROWMAP_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackRowMap(String packed, int offset, int [] result) {
    int i = 0;  /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int high = packed.charAt(i++) << 16;
      result[j++] = high | packed.charAt(i++);
    }
    return j;
  }

  /** 
   * The transition table of the DFA
   */
  private static final int [] ZZ_TRANS = zzUnpackTrans();

  private static final String ZZ_TRANS_PACKED_0 =
    "\1\6\1\7\1\10\1\11\1\6\2\12\1\13\1\14"+
    "\1\15\1\16\1\17\1\20\1\21\1\22\1\23\1\24"+
    "\1\25\1\26\1\27\1\30\1\31\1\32\1\33\1\34"+
    "\1\35\1\6\1\36\1\37\1\40\1\41\1\42\1\43"+
    "\1\11\1\44\1\11\1\45\1\46\1\47\1\11\1\50"+
    "\1\51\1\11\1\52\1\53\1\54\4\11\5\55\1\56"+
    "\1\55\1\57\22\55\1\60\27\55\11\61\1\62\50\61"+
    "\10\63\1\64\1\61\50\63\5\61\1\65\54\61\63\0"+
    "\1\7\3\66\17\0\1\7\6\0\2\66\3\0\22\66"+
    "\1\0\4\10\17\0\1\10\6\0\2\10\3\0\22\10"+
    "\1\0\4\11\17\0\1\11\6\0\2\11\3\0\22\11"+
    "\5\0\2\12\63\0\1\67\1\70\72\0\1\71\40\0"+
    "\1\72\22\0\1\73\36\0\1\74\3\66\17\0\1\74"+
    "\6\0\2\66\3\0\22\66\22\0\1\75\61\0\1\76"+
    "\61\0\1\77\67\0\1\100\62\0\1\101\31\0\4\11"+
    "\17\0\1\11\6\0\2\11\3\0\6\11\1\102\10\11"+
    "\1\103\2\11\1\0\4\11\17\0\1\11\6\0\2\11"+
    "\3\0\11\11\1\104\4\11\1\105\3\11\1\0\4\11"+
    "\17\0\1\11\6\0\2\11\3\0\1\11\1\106\20\11"+
    "\1\0\4\11\17\0\1\11\6\0\1\107\1\11\3\0"+
    "\14\11\1\110\5\11\1\0\4\11\17\0\1\11\6\0"+
    "\2\11\3\0\1\11\1\111\7\11\1\112\10\11\1\0"+
    "\4\11\17\0\1\11\6\0\2\11\3\0\6\11\1\113"+
    "\13\11\1\0\4\11\17\0\1\11\6\0\2\11\3\0"+
    "\5\11\1\114\13\11\1\115\1\0\4\11\17\0\1\11"+
    "\6\0\1\11\1\116\3\0\22\11\1\0\4\11\17\0"+
    "\1\11\6\0\2\11\3\0\6\11\1\117\13\11\1\0"+
    "\4\11\17\0\1\11\6\0\2\11\3\0\1\11\1\120"+
    "\3\11\1\121\14\11\1\0\4\11\17\0\1\11\6\0"+
    "\2\11\3\0\7\11\1\122\12\11\1\0\4\11\17\0"+
    "\1\11\6\0\2\11\3\0\16\11\1\123\3\11\7\0"+
    "\1\124\23\0\1\125\1\126\27\0\3\66\26\0\2\66"+
    "\3\0\22\66\1\0\1\72\22\0\1\72\36\0\1\127"+
    "\22\0\1\127\36\0\4\11\17\0\1\11\6\0\2\11"+
    "\3\0\15\11\1\130\4\11\1\0\4\11\17\0\1\11"+
    "\6\0\2\11\3\0\5\11\1\131\14\11\1\0\4\11"+
    "\17\0\1\11\6\0\2\11\3\0\17\11\1\132\2\11"+
    "\1\0\4\11\17\0\1\11\6\0\2\11\3\0\2\11"+
    "\1\133\17\11\1\0\4\11\17\0\1\11\6\0\2\11"+
    "\3\0\2\11\1\134\17\11\1\0\4\11\17\0\1\11"+
    "\6\0\1\11\1\135\3\0\22\11\1\0\4\11\17\0"+
    "\1\11\6\0\2\11\3\0\1\11\1\136\20\11\1\0"+
    "\4\11\17\0\1\11\6\0\2\11\3\0\6\11\1\137"+
    "\13\11\1\0\4\11\17\0\1\11\6\0\1\140\1\11"+
    "\3\0\22\11\1\0\4\11\17\0\1\11\6\0\2\11"+
    "\3\0\10\11\1\141\11\11\1\0\4\11\17\0\1\11"+
    "\6\0\1\11\1\142\3\0\22\11\1\0\4\11\17\0"+
    "\1\11\6\0\2\11\3\0\7\11\1\143\1\11\1\144"+
    "\10\11\1\0\4\11\17\0\1\11\6\0\1\11\1\145"+
    "\3\0\22\11\1\0\4\11\17\0\1\11\6\0\1\146"+
    "\1\11\3\0\22\11\1\0\4\11\17\0\1\11\6\0"+
    "\2\11\3\0\7\11\1\147\12\11\1\0\4\11\17\0"+
    "\1\11\6\0\2\11\3\0\5\11\1\150\14\11\1\0"+
    "\4\11\17\0\1\11\6\0\2\11\3\0\2\11\1\151"+
    "\17\11\1\0\4\11\17\0\1\11\6\0\2\11\3\0"+
    "\5\11\1\152\14\11\1\0\4\11\17\0\1\11\6\0"+
    "\2\11\3\0\6\11\1\153\13\11\1\0\4\11\17\0"+
    "\1\11\6\0\2\11\3\0\10\11\1\154\11\11\1\0"+
    "\4\11\17\0\1\11\6\0\2\11\3\0\3\11\1\155"+
    "\16\11\1\0\4\11\17\0\1\11\6\0\2\11\3\0"+
    "\5\11\1\156\14\11\1\0\4\11\17\0\1\11\6\0"+
    "\2\11\3\0\7\11\1\157\12\11\1\0\4\11\17\0"+
    "\1\11\6\0\2\11\3\0\12\11\1\160\7\11\1\0"+
    "\4\11\17\0\1\11\6\0\2\11\3\0\6\11\1\161"+
    "\13\11\1\0\4\11\17\0\1\11\6\0\2\11\3\0"+
    "\6\11\1\162\13\11\1\0\4\11\17\0\1\11\6\0"+
    "\1\11\1\163\3\0\22\11\1\0\4\11\17\0\1\11"+
    "\6\0\2\11\3\0\2\11\1\164\17\11\1\0\4\11"+
    "\17\0\1\11\6\0\2\11\3\0\17\11\1\165\2\11"+
    "\1\0\4\11\17\0\1\11\6\0\1\11\1\166\3\0"+
    "\22\11\1\0\4\11\17\0\1\11\6\0\2\11\3\0"+
    "\10\11\1\167\11\11\1\0\4\11\17\0\1\11\6\0"+
    "\2\11\3\0\10\11\1\170\11\11\1\0\4\11\17\0"+
    "\1\11\6\0\2\11\3\0\5\11\1\171\14\11\1\0"+
    "\4\11\17\0\1\11\6\0\2\11\3\0\6\11\1\172"+
    "\13\11\1\0\4\11\17\0\1\11\6\0\2\11\3\0"+
    "\20\11\1\173\1\11\1\0\4\11\17\0\1\11\6\0"+
    "\1\11\1\174\3\0\22\11\1\0\4\11\17\0\1\11"+
    "\6\0\1\175\1\11\3\0\22\11\1\0\4\11\17\0"+
    "\1\11\6\0\2\11\3\0\2\11\1\176\17\11\1\0"+
    "\4\11\17\0\1\11\6\0\1\177\1\11\3\0\22\11"+
    "\1\0\4\11\17\0\1\11\6\0\2\11\3\0\11\11"+
    "\1\200\10\11\1\0\4\11\17\0\1\11\6\0\2\11"+
    "\3\0\2\11\1\201\17\11\1\0\4\11\17\0\1\11"+
    "\6\0\2\11\3\0\10\11\1\202\11\11\1\0\4\11"+
    "\17\0\1\11\6\0\2\11\3\0\6\11\1\203\13\11"+
    "\1\0\4\11\17\0\1\11\6\0\2\11\3\0\6\11"+
    "\1\204\13\11\1\0\4\11\17\0\1\11\6\0\2\11"+
    "\3\0\7\11\1\205\12\11\1\0\4\11\17\0\1\11"+
    "\6\0\2\11\3\0\16\11\1\206\3\11\1\0\4\11"+
    "\17\0\1\11\6\0\2\11\3\0\3\11\1\207\16\11"+
    "\1\0\4\11\17\0\1\11\6\0\2\11\3\0\13\11"+
    "\1\210\6\11\1\0\4\11\17\0\1\11\6\0\2\11"+
    "\3\0\12\11\1\211\7\11\1\0\4\11\17\0\1\11"+
    "\6\0\1\212\1\11\3\0\22\11\1\0\4\11\17\0"+
    "\1\11\6\0\1\213\1\11\3\0\22\11\1\0\4\11"+
    "\17\0\1\11\6\0\1\214\1\11\3\0\22\11\1\0"+
    "\4\11\17\0\1\11\6\0\2\11\3\0\10\11\1\215"+
    "\11\11\1\0\4\11\17\0\1\11\6\0\2\11\3\0"+
    "\17\11\1\216\2\11\1\0\4\11\17\0\1\11\6\0"+
    "\2\11\3\0\6\11\1\217\13\11";

  private static int [] zzUnpackTrans() {
    int [] result = new int[4500];
    int offset = 0;
    offset = zzUnpackTrans(ZZ_TRANS_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackTrans(String packed, int offset, int [] result) {
    int i = 0;       /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int count = packed.charAt(i++);
      int value = packed.charAt(i++);
      value--;
      do result[j++] = value; while (--count > 0);
    }
    return j;
  }


  /* error codes */
  private static final int ZZ_UNKNOWN_ERROR = 0;
  private static final int ZZ_NO_MATCH = 1;
  private static final int ZZ_PUSHBACK_2BIG = 2;

  /* error messages for the codes above */
  private static final String ZZ_ERROR_MSG[] = {
    "Unkown internal scanner error",
    "Error: could not match input",
    "Error: pushback value was too large"
  };

  /**
   * ZZ_ATTRIBUTE[aState] contains the attributes of state <code>aState</code>
   */
  private static final int [] ZZ_ATTRIBUTE = zzUnpackAttribute();

  private static final String ZZ_ATTRIBUTE_PACKED_0 =
    "\5\0\1\11\4\1\1\11\1\1\11\11\12\1\3\11"+
    "\12\1\3\11\1\1\5\11\1\1\3\11\3\1\5\11"+
    "\22\1\3\11\71\1";

  private static int [] zzUnpackAttribute() {
    int [] result = new int[143];
    int offset = 0;
    offset = zzUnpackAttribute(ZZ_ATTRIBUTE_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackAttribute(String packed, int offset, int [] result) {
    int i = 0;       /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int count = packed.charAt(i++);
      int value = packed.charAt(i++);
      do result[j++] = value; while (--count > 0);
    }
    return j;
  }

  /** the input device */
  private java.io.Reader zzReader;

  /** the current state of the DFA */
  private int zzState;

  /** the current lexical state */
  private int zzLexicalState = YYINITIAL;

  /** this buffer contains the current text to be matched and is
      the source of the yytext() string */
  private char zzBuffer[] = new char[ZZ_BUFFERSIZE];

  /** the textposition at the last accepting state */
  private int zzMarkedPos;

  /** the current text position in the buffer */
  private int zzCurrentPos;

  /** startRead marks the beginning of the yytext() string in the buffer */
  private int zzStartRead;

  /** endRead marks the last character in the buffer, that has been read
      from input */
  private int zzEndRead;

  /** number of newlines encountered up to the start of the matched text */
  private int yyline;

  /** the number of characters up to the start of the matched text */
  private int yychar;

  /**
   * the number of characters from the last newline up to the start of the 
   * matched text
   */
  private int yycolumn;

  /** 
   * zzAtBOL == true <=> the scanner is currently at the beginning of a line
   */
  private boolean zzAtBOL = true;

  /** zzAtEOF == true <=> the scanner is at the EOF */
  private boolean zzAtEOF;

  /** denotes if the user-EOF-code has already been executed */
  private boolean zzEOFDone;

  /* user code: */
	private int commentStart=0;//will hold the line in which the LONG_COMMENT started. for error description
	private String returnString = null;
	private String errorString = null;
	private boolean allowEOF = true; //will be a flag if it's legal to get to the EOF
	private int illegalLine = 0;   


  /**
   * Creates a new scanner
   * There is also a java.io.InputStream version of this constructor.
   *
   * @param   in  the java.io.Reader to read input from.
   */
  public Lexer(java.io.Reader in) {
    this.zzReader = in;
  }

  /**
   * Creates a new scanner.
   * There is also java.io.Reader version of this constructor.
   *
   * @param   in  the java.io.Inputstream to read input from.
   */
  public Lexer(java.io.InputStream in) {
    this(new java.io.InputStreamReader(in));
  }

  /** 
   * Unpacks the compressed character translation table.
   *
   * @param packed   the packed character translation table
   * @return         the unpacked character translation table
   */
  private static char [] zzUnpackCMap(String packed) {
    char [] map = new char[0x10000];
    int i = 0;  /* index in packed string  */
    int j = 0;  /* index in unpacked array */
    while (i < 154) {
      int  count = packed.charAt(i++);
      char value = packed.charAt(i++);
      do map[j++] = value; while (--count > 0);
    }
    return map;
  }


  /**
   * Refills the input buffer.
   *
   * @return      <code>false</code>, iff there was new input.
   * 
   * @exception   java.io.IOException  if any I/O-Error occurs
   */
  private boolean zzRefill() throws java.io.IOException {

    /* first: make room (if you can) */
    if (zzStartRead > 0) {
      System.arraycopy(zzBuffer, zzStartRead,
                       zzBuffer, 0,
                       zzEndRead-zzStartRead);

      /* translate stored positions */
      zzEndRead-= zzStartRead;
      zzCurrentPos-= zzStartRead;
      zzMarkedPos-= zzStartRead;
      zzStartRead = 0;
    }

    /* is the buffer big enough? */
    if (zzCurrentPos >= zzBuffer.length) {
      /* if not: blow it up */
      char newBuffer[] = new char[zzCurrentPos*2];
      System.arraycopy(zzBuffer, 0, newBuffer, 0, zzBuffer.length);
      zzBuffer = newBuffer;
    }

    /* finally: fill the buffer with new input */
    int numRead = zzReader.read(zzBuffer, zzEndRead,
                                            zzBuffer.length-zzEndRead);

    if (numRead > 0) {
      zzEndRead+= numRead;
      return false;
    }
    // unlikely but not impossible: read 0 characters, but not at end of stream    
    if (numRead == 0) {
      int c = zzReader.read();
      if (c == -1) {
        return true;
      } else {
        zzBuffer[zzEndRead++] = (char) c;
        return false;
      }     
    }

	// numRead < 0
    return true;
  }

    
  /**
   * Closes the input stream.
   */
  public final void yyclose() throws java.io.IOException {
    zzAtEOF = true;            /* indicate end of file */
    zzEndRead = zzStartRead;  /* invalidate buffer    */

    if (zzReader != null)
      zzReader.close();
  }


  /**
   * Resets the scanner to read from a new input stream.
   * Does not close the old reader.
   *
   * All internal variables are reset, the old input stream 
   * <b>cannot</b> be reused (internal buffer is discarded and lost).
   * Lexical state is set to <tt>ZZ_INITIAL</tt>.
   *
   * @param reader   the new input stream 
   */
  public final void yyreset(java.io.Reader reader) {
    zzReader = reader;
    zzAtBOL  = true;
    zzAtEOF  = false;
    zzEOFDone = false;
    zzEndRead = zzStartRead = 0;
    zzCurrentPos = zzMarkedPos = 0;
    yyline = yychar = yycolumn = 0;
    zzLexicalState = YYINITIAL;
  }


  /**
   * Returns the current lexical state.
   */
  public final int yystate() {
    return zzLexicalState;
  }


  /**
   * Enters a new lexical state
   *
   * @param newState the new lexical state
   */
  public final void yybegin(int newState) {
    zzLexicalState = newState;
  }


  /**
   * Returns the text matched by the current regular expression.
   */
  public final String yytext() {
    return new String( zzBuffer, zzStartRead, zzMarkedPos-zzStartRead );
  }


  /**
   * Returns the character at position <tt>pos</tt> from the 
   * matched text. 
   * 
   * It is equivalent to yytext().charAt(pos), but faster
   *
   * @param pos the position of the character to fetch. 
   *            A value from 0 to yylength()-1.
   *
   * @return the character at position pos
   */
  public final char yycharat(int pos) {
    return zzBuffer[zzStartRead+pos];
  }


  /**
   * Returns the length of the matched text region.
   */
  public final int yylength() {
    return zzMarkedPos-zzStartRead;
  }


  /**
   * Reports an error that occured while scanning.
   *
   * In a wellformed scanner (no or only correct usage of 
   * yypushback(int) and a match-all fallback rule) this method 
   * will only be called with things that "Can't Possibly Happen".
   * If this method is called, something is seriously wrong
   * (e.g. a JFlex bug producing a faulty scanner etc.).
   *
   * Usual syntax/scanner level error handling should be done
   * in error fallback rules.
   *
   * @param   errorCode  the code of the errormessage to display
   */
  private void zzScanError(int errorCode) throws LexicalError {
    String message;
    try {
      message = ZZ_ERROR_MSG[errorCode];
    }
    catch (ArrayIndexOutOfBoundsException e) {
      message = ZZ_ERROR_MSG[ZZ_UNKNOWN_ERROR];
    }

    throw new LexicalError(message);
  } 


  /**
   * Pushes the specified amount of characters back into the input stream.
   *
   * They will be read again by then next call of the scanning method
   *
   * @param number  the number of characters to be read again.
   *                This number must not be greater than yylength()!
   */
  public void yypushback(int number)  throws LexicalError {
    if ( number > yylength() )
      zzScanError(ZZ_PUSHBACK_2BIG);

    zzMarkedPos -= number;
  }


  /**
   * Resumes scanning until the next regular expression is matched,
   * the end of input is encountered or an I/O-Error occurs.
   *
   * @return      the next token
   * @exception   java.io.IOException  if any I/O-Error occurs
   */
  public Token next_token() throws java.io.IOException, LexicalError {
    int zzInput;
    int zzAction;

    // cached fields:
    int zzCurrentPosL;
    int zzMarkedPosL;
    int zzEndReadL = zzEndRead;
    char [] zzBufferL = zzBuffer;
    char [] zzCMapL = ZZ_CMAP;

    int [] zzTransL = ZZ_TRANS;
    int [] zzRowMapL = ZZ_ROWMAP;
    int [] zzAttrL = ZZ_ATTRIBUTE;

    while (true) {
      zzMarkedPosL = zzMarkedPos;

      boolean zzR = false;
      for (zzCurrentPosL = zzStartRead; zzCurrentPosL < zzMarkedPosL;
                                                             zzCurrentPosL++) {
        switch (zzBufferL[zzCurrentPosL]) {
        case '\u000B':
        case '\u000C':
        case '\u0085':
        case '\u2028':
        case '\u2029':
          yyline++;
          zzR = false;
          break;
        case '\r':
          yyline++;
          zzR = true;
          break;
        case '\n':
          if (zzR)
            zzR = false;
          else {
            yyline++;
          }
          break;
        default:
          zzR = false;
        }
      }

      if (zzR) {
        // peek one character ahead if it is \n (if we have counted one line too much)
        boolean zzPeek;
        if (zzMarkedPosL < zzEndReadL)
          zzPeek = zzBufferL[zzMarkedPosL] == '\n';
        else if (zzAtEOF)
          zzPeek = false;
        else {
          boolean eof = zzRefill();
          zzEndReadL = zzEndRead;
          zzMarkedPosL = zzMarkedPos;
          zzBufferL = zzBuffer;
          if (eof) 
            zzPeek = false;
          else 
            zzPeek = zzBufferL[zzMarkedPosL] == '\n';
        }
        if (zzPeek) yyline--;
      }
      zzAction = -1;

      zzCurrentPosL = zzCurrentPos = zzStartRead = zzMarkedPosL;
  
      zzState = ZZ_LEXSTATE[zzLexicalState];


      zzForAction: {
        while (true) {
    
          if (zzCurrentPosL < zzEndReadL)
            zzInput = zzBufferL[zzCurrentPosL++];
          else if (zzAtEOF) {
            zzInput = YYEOF;
            break zzForAction;
          }
          else {
            // store back cached positions
            zzCurrentPos  = zzCurrentPosL;
            zzMarkedPos   = zzMarkedPosL;
            boolean eof = zzRefill();
            // get translated positions and possibly new buffer
            zzCurrentPosL  = zzCurrentPos;
            zzMarkedPosL   = zzMarkedPos;
            zzBufferL      = zzBuffer;
            zzEndReadL     = zzEndRead;
            if (eof) {
              zzInput = YYEOF;
              break zzForAction;
            }
            else {
              zzInput = zzBufferL[zzCurrentPosL++];
            }
          }
          int zzNext = zzTransL[ zzRowMapL[zzState] + zzCMapL[zzInput] ];
          if (zzNext == -1) break zzForAction;
          zzState = zzNext;

          int zzAttributes = zzAttrL[zzState];
          if ( (zzAttributes & 1) == 1 ) {
            zzAction = zzState;
            zzMarkedPosL = zzCurrentPosL;
            if ( (zzAttributes & 8) == 8 ) break zzForAction;
          }

        }
      }

      // store back cached position
      zzMarkedPos = zzMarkedPosL;

      switch (zzAction < 0 ? zzAction : ZZ_ACTION[zzAction]) {
        case 48: 
          { return new Token(yyline,sym.INT);
          }
        case 65: break;
        case 16: 
          { return new Token(yyline,sym.PLUS);
          }
        case 66: break;
        case 30: 
          { allowEOF = true;
								illegalLine = 0;
								commentStart = 0;
								yybegin(YYINITIAL);
          }
        case 67: break;
        case 34: 
          { illegalLine = yyline;
					allowEOF = false;
					commentStart = yyline;
					yybegin(LONG_COMMENT);
          }
        case 68: break;
        case 7: 
          { return new Token(yyline,sym.DIVIDE);
          }
        case 69: break;
        case 50: 
          { return new Token(yyline,sym.TRUE);
          }
        case 70: break;
        case 23: 
          { return new Token(yyline,sym.COMMA);
          }
        case 71: break;
        case 60: 
          { return new Token(yyline,sym.STRING);
          }
        case 72: break;
        case 52: 
          { return new Token(yyline,sym.VOID);
          }
        case 73: break;
        case 56: 
          { return new Token(yyline,sym.FALSE);
          }
        case 74: break;
        case 53: 
          { return new Token(yyline,sym.ELSE);
          }
        case 75: break;
        case 10: 
          { return new Token(yyline,sym.RP);
          }
        case 76: break;
        case 20: 
          { return new Token(yyline,sym.GT);
          }
        case 77: break;
        case 55: 
          { return new Token(yyline,sym.CLASS);
          }
        case 78: break;
        case 32: 
          { throw new LexicalError(yyline,"identifiers cannot begin with digits");
          }
        case 79: break;
        case 15: 
          { return new Token(yyline,sym.MOD);
          }
        case 80: break;
        case 19: 
          { return new Token(yyline,sym.LNEG);
          }
        case 81: break;
        case 62: 
          { return new Token(yyline,sym.BOOLEAN);
          }
        case 82: break;
        case 21: 
          { return new Token(yyline,sym.LT);
          }
        case 83: break;
        case 51: 
          { return new Token(yyline,sym.THIS);
          }
        case 84: break;
        case 11: 
          { return new Token(yyline,sym.LB);
          }
        case 85: break;
        case 58: 
          { return new Token(yyline,sym.LENGTH);
          }
        case 86: break;
        case 37: 
          { throw new LexicalError(yyline,"no trailing zeroes");
          }
        case 87: break;
        case 3: 
          { return new Token(yyline,sym.CLASS_ID,yytext());
          }
        case 88: break;
        case 24: 
          { return new Token(yyline,sym.SEMI);
          }
        case 89: break;
        case 31: 
          { yybegin(YYINITIAL);
          }
        case 90: break;
        case 40: 
          { return new Token(yyline,sym.LTE);
          }
        case 91: break;
        case 46: 
          { returnString.concat("\t");
          }
        case 92: break;
        case 17: 
          { return new Token(yyline,sym.ASSIGN);
          }
        case 93: break;
        case 2: 
          { try
								{return new Token (yyline,sym.INTEGER,Integer.parseInt(yytext()));}
								catch (NumberFormatException e)
									{throw new LexicalError(yyline,"Integer out of range");}
          }
        case 94: break;
        case 29: 
          { yybegin(LONG_COMMENT);
          }
        case 95: break;
        case 28: 
          { yybegin(LONG_COMMENT_AFTER_STAR);
          }
        case 96: break;
        case 61: 
          { return new Token(yyline,sym.RETURN);
          }
        case 97: break;
        case 47: 
          { return new Token(yyline,sym.NEW);
          }
        case 98: break;
        case 43: 
          { return new Token(yyline,sym.IF);
          }
        case 99: break;
        case 6: 
          { illegalLine = yyline;
						allowEOF = false;
						returnString = "\"";
						yybegin(IN_QUOTE);
          }
        case 100: break;
        case 49: 
          { return new Token(yyline,sym.NULL);
          }
        case 101: break;
        case 36: 
          { try
								{return new Token (yyline,sym.INTEGER,Integer.parseInt(yytext()));}
								catch (NumberFormatException e) 
									{throw new LexicalError(yyline,"Integer out of range");}
          }
        case 102: break;
        case 9: 
          { return new Token(yyline,sym.LP);
          }
        case 103: break;
        case 59: 
          { return new Token(yyline,sym.STATIC);
          }
        case 104: break;
        case 13: 
          { return new Token(yyline,sym.LCBR);
          }
        case 105: break;
        case 57: 
          { return new Token(yyline,sym.WHILE);
          }
        case 106: break;
        case 35: 
          { return new Token(yyline,sym.EQUAL);
          }
        case 107: break;
        case 54: 
          { return new Token(yyline,sym.BREAK);
          }
        case 108: break;
        case 26: 
          { throw new LexicalError(yyline,"must close String QUOTE before end of line");
          }
        case 109: break;
        case 27: 
          { allowEOF = true;
						illegalLine = 0;
						yybegin(YYINITIAL);
						returnString.concat("\"");
						return new Token(yyline,sym.QUOTE,returnString);
          }
        case 110: break;
        case 44: 
          { returnString.concat("\"");
          }
        case 111: break;
        case 39: 
          { return new Token(yyline,sym.GTE);
          }
        case 112: break;
        case 12: 
          { return new Token(yyline,sym.RB);
          }
        case 113: break;
        case 4: 
          { return new Token(yyline,sym.ID,yytext());
          }
        case 114: break;
        case 25: 
          { if ((yytext().charAt(0)>=32) && (yytext().charAt(0)<=126))
						returnString.concat(yytext());
					else
						{	errorString = "illegal character for string '" +yytext().charAt(0) + "'"; 
							throw new LexicalError(yyline,errorString);
						}
          }
        case 115: break;
        case 8: 
          { return new Token(yyline,sym.MULTIPLY);
          }
        case 116: break;
        case 63: 
          { return new Token(yyline,sym.EXTENDS);
          }
        case 117: break;
        case 41: 
          { return new Token(yyline,sym.LAND);
          }
        case 118: break;
        case 22: 
          { return new Token(yyline,sym.DOT);
          }
        case 119: break;
        case 14: 
          { return new Token(yyline,sym.RCBR);
          }
        case 120: break;
        case 1: 
          { errorString = "illegal character '" + yytext().charAt(0) + "'";
							throw new LexicalError(yyline,errorString);
          }
        case 121: break;
        case 42: 
          { return new Token(yyline,sym.LOR);
          }
        case 122: break;
        case 64: 
          { return new Token(yyline,sym.CONTINUE);
          }
        case 123: break;
        case 33: 
          { yybegin(ONE_LINE_COMMENT);
          }
        case 124: break;
        case 18: 
          { return new Token(yyline,sym.MINUS);
          }
        case 125: break;
        case 38: 
          { return new Token(yyline,sym.NEQUAL);
          }
        case 126: break;
        case 45: 
          { returnString.concat("\n");
          }
        case 127: break;
        case 5: 
          { 
          }
        case 128: break;
        default: 
          if (zzInput == YYEOF && zzStartRead == zzCurrentPos) {
            zzAtEOF = true;
              {  		if (allowEOF)
 			return new Token(yyline,sym.EOF);
 		else
 			throw new LexicalError(illegalLine,"unclosed QUOTE/COMMENT");
 		
 							
 }
          } 
          else {
            zzScanError(ZZ_NO_MATCH);
          }
      }
    }
  }


}
