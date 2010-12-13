package IC.Parser;

%%

%class Lexer
%public
%function next_token
%type Token
%line
%scanerror LexicalError
%cup

%{
	public int getLineNumber()
	{
		return yyline + 1;
	}
%}

%state IN_QUOTE
%state LONG_COMMENT
%state LONG_COMMENT_AFTER_STAR
%state ONE_LINE_COMMENT
%{
	private int commentStart = 0; // will hold the line in which the LONG_COMMENT started. for error description
	private String returnString = null;
	private String errorString = null;
	private boolean allowEOF = true; // will be a flag if it's legal to get to the EOF
	private int illegalLine = 0; // saves the line in which the comment/quote started for error reporting 
%}
 
%eofval{	
 	if (allowEOF)
 	{
 		return new Token(yyline, sym.EOF, "end of file");
 	}
 	else
 	{
 		throw new LexicalError(illegalLine, "unclosed QUOTE/COMMENT");
 	}
%eofval}						
 
DIGIT = [0-9]
CAPITAL = [A-Z]
SMALL = [a-z]
LETTER = {CAPITAL}|{SMALL}|_
ALPHA_NUMERIC = {DIGIT}|{LETTER}
NEWLINE = [\n\r]
WHITESPACE = ([ \n\t\r])+
NUMBER = ({DIGIT})+
IDENTIFIER = ({SMALL})+({ALPHA_NUMERIC})*
CLASS_IDENTIFIER = ({CAPITAL})+({ALPHA_NUMERIC})*
QUOTE = [\"]

%%

<YYINITIAL> {WHITESPACE} { }

/**** one line comments *****/
<YYINITIAL> "//" { yybegin(ONE_LINE_COMMENT); } 
<ONE_LINE_COMMENT> . {}
<ONE_LINE_COMMENT> {NEWLINE} { yybegin(YYINITIAL); }

/**** long comments ****/
<YYINITIAL> "/*" { 
					illegalLine = yyline;
					allowEOF = false;
					commentStart = yyline;
					yybegin(LONG_COMMENT);
				 }
<LONG_COMMENT> "*" { yybegin(LONG_COMMENT_AFTER_STAR); }
<LONG_COMMENT> [^*] {}
<LONG_COMMENT_AFTER_STAR> "/" {
								allowEOF = true;
								illegalLine = 0;
								commentStart = 0;
								yybegin(YYINITIAL);
							  }
<LONG_COMMENT_AFTER_STAR> "*" {}
<LONG_COMMENT_AFTER_STAR> [^/*] { yybegin(LONG_COMMENT); }

/**** brackets, parenthesis, curly brackets ****/
<YYINITIAL> "(" { return new Token(yyline, sym.LP, "("); }
<YYINITIAL> ")" { return new Token(yyline, sym.RP, ")"); }
<YYINITIAL> "[" { return new Token(yyline, sym.LB, "["); }
<YYINITIAL> "]" { return new Token(yyline, sym.RB, "]"); }
<YYINITIAL> "{" { return new Token(yyline, sym.LCBR, "{"); }
<YYINITIAL> "}" { return new Token(yyline, sym.RCBR, "}"); }

/**** mathematics operations ****/
<YYINITIAL> "/" { return new Token(yyline, sym.DIVIDE, "/"); }
<YYINITIAL> "%" { return new Token(yyline, sym.MOD, "%"); } 
<YYINITIAL> "*" { return new Token(yyline, sym.MULTIPLY, "*"); }
<YYINITIAL> "+" { return new Token(yyline, sym.PLUS, "+"); }
<YYINITIAL> "=" { return new Token(yyline, sym.ASSIGN, "="); }
<YYINITIAL> "-" { return new Token(yyline, sym.MINUS, "-");	}

/**** logical operations ****/
<YYINITIAL> "!=" { return new Token(yyline, sym.NEQUAL, "!="); }
<YYINITIAL> "==" { return new Token(yyline, sym.EQUAL, "=="); }
<YYINITIAL> ">=" { return new Token(yyline, sym.GTE, ">="); }
<YYINITIAL> ">" { return new Token(yyline, sym.GT, ">"); }
<YYINITIAL> "<=" { return new Token(yyline, sym.LTE, "<="); }
<YYINITIAL> "<" { return new Token(yyline, sym.LT, "<"); }
<YYINITIAL> "!" { return new Token(yyline, sym.LNEG, "!"); }
<YYINITIAL> "&&" { return new Token(yyline, sym.LAND, "&&"); }
<YYINITIAL> "||" { return new Token(yyline, sym.LOR, "||"); }

/**** positive numbers ****/
<YYINITIAL> [0]+({NUMBER})+ { throw new LexicalError(yyline, "no leading zeros");}
<YYINITIAL> ({NUMBER})+ {
							try
							{								
								Integer.parseInt(yytext());
								return new Token(yyline, sym.INTEGER, "integer value", Integer.parseInt(yytext()));
							}
							catch (NumberFormatException e)
							{
								throw new LexicalError(yyline, "integer out of range");
							}
						}

/**** quoted string ****/

<YYINITIAL> {QUOTE} {	
						illegalLine = yyline;
						allowEOF = false;
						returnString = "\"";
						yybegin(IN_QUOTE);
					}
<IN_QUOTE>	{QUOTE} {
						allowEOF = true;
						illegalLine = 0;
						yybegin(YYINITIAL);
						returnString += "\"";
						return new Token(yyline, sym.QUOTE, "string value", returnString);
					}
<IN_QUOTE>	{NEWLINE} { throw new LexicalError(yyline, "must close String QUOTE before end of line"); }
<IN_QUOTE>	"\\n" { returnString += "\\n"; }
<IN_QUOTE>	"\\t" { returnString += "\\t"; }
<IN_QUOTE>	"\\\"" { returnString += "\""; }
<IN_QUOTE>	. 	{
					if ((yytext().charAt(0) >= 32) && (yytext().charAt(0) <= 126))
					{
						returnString += yytext();
					}
					else
					{
						errorString = "illegal character for string '" + yytext().charAt(0) + "'"; 
						throw new LexicalError(yyline, errorString);
					}
				}

/**** annotations ****/
<YYINITIAL> "." { return new Token(yyline, sym.DOT, "."); }
<YYINITIAL> "," { return new Token(yyline, sym.COMMA, ","); }
<YYINITIAL> ";" { return new Token(yyline, sym.SEMI, ";"); }

/**** keywords ****/
<YYINITIAL> "void" { return new Token(yyline, sym.VOID, "void"); }
<YYINITIAL> "boolean" { return new Token(yyline, sym.BOOLEAN, "boolean"); }
<YYINITIAL> "int" { return new Token(yyline, sym.INT, "int"); }
<YYINITIAL> "string" { return new Token(yyline, sym.STRING, "string"); }
<YYINITIAL> "static" { return new Token(yyline, sym.STATIC, "static"); }
<YYINITIAL> "if" { return new Token(yyline, sym.IF, "if"); }
<YYINITIAL> "else" { return new Token(yyline, sym.ELSE, "else"); }
<YYINITIAL> "while" { return new Token(yyline, sym.WHILE, "while"); }
<YYINITIAL> "continue" { return new Token(yyline, sym.CONTINUE, "continue"); }
<YYINITIAL> "break" { return new Token(yyline, sym.BREAK, "break"); }
<YYINITIAL> "return" { return new Token(yyline, sym.RETURN, "return"); }
<YYINITIAL> "class" { return new Token(yyline, sym.CLASS, "class"); }
<YYINITIAL> "extends" { return new Token(yyline, sym.EXTENDS, "extends"); }
<YYINITIAL> "this" { return new Token(yyline, sym.THIS, "this"); }
<YYINITIAL> "new" { return new Token(yyline, sym.NEW, "new"); } 
<YYINITIAL> "false" { return new Token(yyline, sym.FALSE, "false"); }
<YYINITIAL> "true" { return new Token(yyline, sym.TRUE, "true"); }
<YYINITIAL> "null" { return new Token(yyline, sym.NULL, "null"); }
<YYINITIAL> "length" { return new Token(yyline, sym.LENGTH, "length"); }  
     				 
/**** identifier ****/
<YYINITIAL> {IDENTIFIER} { return new Token(yyline, sym.ID, "identifier", yytext()); }
<YYINITIAL> {CLASS_IDENTIFIER} { return new Token(yyline, sym.CLASS_ID, "class identifier", yytext()); }
<YYINITIAL> ({DIGIT})+({LETTER})+ { throw new LexicalError(yyline, "identifiers cannot begin with digits"); }

/**** illegal charecters - if we got here it means it didn't fit any prior one ****/
<YYINITIAL> [^WHITESPACE] {
							errorString = "illegal character '" + yytext().charAt(0) + "'";
							throw new LexicalError(yyline, errorString);
						  }
