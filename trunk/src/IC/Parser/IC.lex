package IC.Parser;

%%

%class Lexer
%public
%function next_token
%type Token
%line
%scanerror LexicalError
%state IN_QUOTE
%state LONG_COMMENT
%state LONG_COMMENT_AFTER_STAR
%state ONE_LINE_COMMENT
%{
	private int commentStart = 0; // will hold the line in which the LONG_COMMENT started. for error description
	private String returnString = null;
	private String errorString = null;
	private boolean allowEOF = true; // will be a flag if it's legal to get to the EOF
	private int illegalLine = 0;
	private boolean negativeNumber = false; 
%}
 
%eofval{	
 	if (allowEOF)
 	{
 		return new Token(yyline,sym.EOF);
 	}
 	else
 	{
 		throw new LexicalError(illegalLine, "unclosed QUOTE/COMMENT");
 	}
%eofval}						
 
DIGIT = [0-9]
CAPITAL = [A-Z]
SMALL = [a-z]
LETTER = {CAPITAL}|{SMALL}|[_]
ALPHA = {DIGIT}|{LETTER}
NEWLINE = [\n\r]
WHITESPACE = ([ \n\t\r])+
NUMBER = ({DIGIT})+
IDENTIFIER = ({SMALL})+({ALPHA})*
CLASS_IDENTIFIER = ({CAPITAL})+({ALPHA})*
QUOTE = [\"]

%%

<YYINITIAL> {WHITESPACE} { negativeNumber = false; }

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
<YYINITIAL> "(" { return new Token(yyline, sym.LP); }
<YYINITIAL> ")" { return new Token(yyline, sym.RP); }
<YYINITIAL> "[" { return new Token(yyline, sym.LB); }
<YYINITIAL> "]" { return new Token(yyline, sym.RB); }
<YYINITIAL> "{" { return new Token(yyline, sym.LCBR); }
<YYINITIAL> "}" { return new Token(yyline, sym.RCBR); }

/**** mathematics operations ****/
<YYINITIAL> "/" { return new Token(yyline, sym.DIVIDE); }
<YYINITIAL> "%" { return new Token(yyline, sym.MOD); } 
<YYINITIAL> "*" { return new Token(yyline, sym.MULTIPLY); }
<YYINITIAL> "+" { return new Token(yyline, sym.PLUS); }
<YYINITIAL> "=" { return new Token(yyline, sym.ASSIGN); }

/**** unary minus ****/
/*<YYINITIAL> "-"[0]+({NUMBER})+ { throw new LexicalError(yyline, "no leading zeros"); }
<YYINITIAL> "-"({NUMBER})+ {
								try
								{
									return new Token (yyline, sym.INTEGER, Integer.parseInt(yytext()));
								}
								catch (NumberFormatException e)
								{
									throw new LexicalError(yyline, "integer out of range");
								}
						   }
	*/

/**** logical operations ****/
<YYINITIAL> "-" {
					negativeNumber = true;
					return new Token(yyline, sym.MINUS); 
				}
<YYINITIAL> "!=" { return new Token(yyline, sym.NEQUAL); }
<YYINITIAL> "==" { return new Token(yyline, sym.EQUAL); }
<YYINITIAL> ">=" { return new Token(yyline, sym.GTE); }
<YYINITIAL> ">" { return new Token(yyline, sym.GT); }
<YYINITIAL> "<=" { return new Token(yyline, sym.LTE); }
<YYINITIAL> "<" { return new Token(yyline, sym.LT); }
<YYINITIAL> "!" { return new Token(yyline, sym.LNEG); }
<YYINITIAL> "&&" { return new Token(yyline, sym.LAND); }
<YYINITIAL> "||" { return new Token(yyline, sym.LOR); }

/**** positive numbers ****/
<YYINITIAL> [0]+({NUMBER})+ { throw new LexicalError(yyline, "no leading zeros");}
<YYINITIAL> ({NUMBER})+ {
							try
							{
								returnString = ((negativeNumber) ? "-" : "");
								returnString += yytext();
								Integer.parseInt(returnString);
								return new Token(yyline, sym.INTEGER, Integer.parseInt(yytext()));
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
						return new Token(yyline, sym.QUOTE, returnString);
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
<YYINITIAL> "." { return new Token(yyline, sym.DOT); }
<YYINITIAL> "," { return new Token(yyline, sym.COMMA); }
<YYINITIAL> ";" { return new Token(yyline, sym.SEMI); }

/**** keywords ****/
<YYINITIAL> "void" { return new Token(yyline, sym.VOID); }
<YYINITIAL> "boolean" { return new Token(yyline, sym.BOOLEAN); }
<YYINITIAL> "int" { return new Token(yyline, sym.INT); }
<YYINITIAL> "string" { return new Token(yyline, sym.STRING); }
<YYINITIAL> "static" { return new Token(yyline, sym.STATIC); }
<YYINITIAL> "if" { return new Token(yyline, sym.IF); }
<YYINITIAL> "else" { return new Token(yyline, sym.ELSE); }
<YYINITIAL> "while" { return new Token(yyline, sym.WHILE); }
<YYINITIAL> "continue" { return new Token(yyline, sym.CONTINUE); }
<YYINITIAL> "break" { return new Token(yyline, sym.BREAK); }
<YYINITIAL> "return" { return new Token(yyline, sym.RETURN); }
<YYINITIAL> "class" { return new Token(yyline, sym.CLASS); }
<YYINITIAL> "extends" { return new Token(yyline, sym.EXTENDS); }
<YYINITIAL> "this" { return new Token(yyline, sym.THIS); }
<YYINITIAL> "new" { return new Token(yyline, sym.NEW); } 
<YYINITIAL> "false" { return new Token(yyline, sym.FALSE); }
<YYINITIAL> "true" { return new Token(yyline, sym.TRUE); }
<YYINITIAL> "null" { return new Token(yyline, sym.NULL); }
<YYINITIAL> "length" { return new Token(yyline, sym.LENGTH); }  
     				 
/**** identifier ****/
<YYINITIAL> ({SMALL})+({ALPHA})* { return new Token(yyline, sym.ID, yytext()); }
<YYINITIAL> ({CAPITAL})+({ALPHA})* { return new Token(yyline, sym.CLASS_ID, yytext()); }
<YYINITIAL> ({DIGIT})+({LETTER})+ { throw new LexicalError(yyline, "identifiers cannot begin with digits"); }

/**** illegal charecters - if we got here it means it didn't fit any prior one ****/
<YYINITIAL> [^WHITESPACE] {
							errorString = "illegal character '" + yytext().charAt(0) + "'";
							throw new LexicalError(yyline, errorString);
						  }
