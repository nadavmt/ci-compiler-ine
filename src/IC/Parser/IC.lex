package IC.Parser;

%%

%class Lexer
%public
%function next_token
%type Token
%line
%scanerror LexicalError
%state IN_QUOTA
%state LONG_COMMENT
%state LONG_COMMENT_AFTER_STAR
%state ONE_LINE_COMMENT
%{
	private int commentStart=0;//will hold the line in which the LONG_COMMENT started. for error description
	private String returnString = NULL;
	private String errorString = NULL;   
}%
 
 %eofval {
 			<IN_QUOTA> { throw new LexicalError(yyline,"must close String quota before end of file");}/*
 			<LONG_COMMENT> {throw new LexicalError(commentStart, "cannot end file without closing comment");}
 			<LONG_COMMENT_AFTER_STAR> {throw new LexicalError(commentStart, "cannot end file without closing comment");}
 			<YYINITIAL> { return new Token(yyline,sym.EOF);}
 %eofval}						
 
DIGIT = [0-9]
CAPITAL = [A-Z]
SMALL = [a-z]
LETTER = {CAPITAL}|{SMALL}|[_]
ALPHA = {DIGIT}|{LETTER}
NEWLINE = \n
WHITESPACE = ([ \n\t\r])+ //add space \b is from the PPT
NUMBER = ({DIGIT})+	
IDENTIFIER = ({SMALL})+({LETTER})*
CLASS_IDENTIFIER = ({CAPITAL})+({LETTER})*
QUOTA = ["]

%%

<YYINITIAL> {WHITESPACE} {}
/****one line comments*****/
<YYINITIAL> "//" {yybegin(ONE_LINE_COMMENT);} 
<ONE_LINE_COMMENT> . {}
<ONE_LINE_COMMENT> "\n" {yybegin(YYINITIAL);}

 /****long comments****/
<YYINITIAL> "/*" {
					commentStart = yyline;
					yybegin(LONG_COMMENT};
				 }
<LONG_COMMENT> "*" {yybegin(LONG_COMMENT_AFTER_STAR);}
<LONG_COMMENT> [^*] {}
<LONG_COMMENT_AFTER_STAR> "/" {
								commentStart = 0;
								yybegin(YYINITIAL);
								}
<LONG_COMMENT_AFTER_STAR> "*" {}
<LONG_COMMENT_AFTER_STAR> [^/*] {yybegin(LONG_COMMENT);}

/****brackets,parenthesis,curly brackets****/
<YYINITIAL> "(" { return new Token(sym.LP,yyline);}
<YYINITIAL> ")" { return new Token(sym.RP,yyline);}
<YYINITIAL> "[" { return new Token(sym.LB,yyline);}
<YYINITIAL> "]" { return new Token(sym.RB,yyline);}
<YYINITIAL> "{" { return new Token(sym.LCBR,yyline);}
<YYINITIAL> "}" { return new Token(sym.RCBR,yyline);} 

/****mathematics operations****/
<YYINITIAL> "/" {return new Token(sym.DIVIDE,yyline);	}
<YYINITIAL> "%" {return new Token(sym.MOD,yyline);} 
<YYINITIAL> "*" {return new Token(sym.MULTIPLY,yyline);	}
<YYINITIAL> "+" {return new Token(sym.PLUS,yyline); }
<YYINITIAL> "=" { return new Token(sym.ASSIGN,yyline); }

/****unary minus****/
<YYINITIAL> "-"[0]+({NUMBER})+ { throw new LexicalError(yyline,"no trailing zeroes");}
<YYINITIAL> "-"({NUMBER})+ { 	try
								{return new Token (sym.INTEGER,yyline,Integer.parseInt(yytext()));}
								catch (NumberFormatException e) 
									{throw new LexicalError(yyline,"Integer out of range");}
							}
							
/****logical operations****/
<YYINITIAL> "-" {return new Token(sym.MINUS,yyline); } 
<YYINITIAL> "!=" {return new Token(sym.NEQUAL, yyline);}
<YYINITIAL> "==" {return new Token(sym.EQUAL, yyline);}
<YYINITIAL> ">=" {return new Token(sym.GTE, yyline);}
<YYINITIAL> ">" {return new Token(sym.GT, yyline);}
<YYINITIAL> "<=" {return new Token(sym.LTE, yyline);}
<YYINITIAL> "<" {return new Token(sym.LT, yyline);}
<YYINITIAL> "!" {return new Token(sym.LNEG, yyline);}
<YYINITIAL> "&&" {return new Token(sym.LAND, yyline);}
<YYINITIAL> "||" {return new Token(sym.LOR, yyline);}
 
 /*positive numbers*/
<YYINITIAL> [0]+({NUMBER})+ { throw new LexicalError(yyline,"no trailing zeroes");}
<YYINITIAL> ({NUMBER})+ { 	try
								{return new Token (sym.INTEGER,yyline,Integer.parseInt(yytext()));}
								catch
									{throw new LexicalError(yyline,"Integer out of range");}
						}

/****quoted string****/						

<YYINITIAL> {QUOTA} {	returnString = "\"";
						yybegin(IN_QUOTA);
					}
<IN_QUOTA>	"\n" { returnString.concat("\n");}
<IN_QUOTA>	"\t" { returnString.concat("\t");}
<IN_QUOTA>	"\"" { returnString.concat("\"");}
<IN_QUOTA>	"" { returnString.concat("\"");}
<IN_QUOTA>	. 	{ 
					if ((yytext().charAt(0)>=32) && (yytext().charAt(0)<=126))
						returnString.concat(yytext());
					else
						throw new LexicalError(yyline,"illegal character '%c'",yytext().charAt(0));
				}
<IN_QUOTA>	{NEWLINE} { throw new LexicalError(yyline,"must close String quota before end of line");}
<IN_QUOTA>	{QUOTA} { 
						yybegin(YYINITIAL);
						returnString.concat("\"");
						return new Token(sym.QUOTA,yyline,returnString);
					}

/****annotations****/
<YYINITIAL> "." {return new Token(yyline,sym.DOT);}
<YYINITIAL> "," {return new Token(yyline,sym.COMMA);}
<YYINITIAL> ";" {return new Token(yyline,sym.SEMI);}

/****keyWords****/
<YYINITIAL> "void" {return new Token(yyline,sym.VOID);}
<YYINITIAL> "boolean" {return new Token(yyline,sym.BOOLEAN);}
<YYINITIAL> "int" {return new Token(yyline,sym.INT);}
<YYINITIAL> "string" {return new Token(yyline,sym.STRING);}
<YYINITIAL> "static" {return new Token(yyline,sym.STATIC);}
<YYINITIAL> "if" {return new Token(yyline,sym.IF);}
<YYINITIAL> "else" {return new Token(yyline,sym.ELSE);}
<YYINITIAL> "while" {return new Token(yyline,sym.WHILE);}
<YYINITIAL> "continue" {return new Token(yyline,sym.CONITUE);}
<YYINITIAL> "break" {return new Token(yyline,sym.BREAK);}
<YYINITIAL> "return" {return new Token(yyline,sym.RETURN);}
<YYINITIAL> "class" {return new Token(yyline,sym.CLASS);}
<YYINITIAL> "extends" {return new Token(yyline,sym.EXTENDS);}
<YYINITIAL> "this" {return new Token(yyline,sym.THIS);}
<YYINITIAL> "new" {return new Token(yyline,sym.NEW);} 
<YYINITIAL> "false" {return new Token(yyline,sym.FALSE);}
<YYINITIAL> "true" {return new Token(yyline,sym.TRUE);}
<YYINITIAL> "null" {return new Token(yyline,sym.NULL);}
<YYINITIAL> "length" {return new Token(yyline,sym.LENGTH);}  
     				 
/****identifier****/
<YYINITIAL> ({SMALL})+({ALPHA})* {return new Token(sym.ID,yyline,yytext());}
<YYINITIAL> ({CAPITAL})+({ALPHA})* {return new Token(sym.CLASS_ID,yyline,yytext());}
<YYINITIAL> ({DIGIT})+({LETTER})+ {throw new LexicalError(yyline,"identifiers cannot begin with digits");}

/****illegal charecters - if we got here it means it didn't fit any prior one****/
<YYINITIAL> [^WHITESPACE] {	
							errorString = "illegal character '" + yytext().charAt(0) + "'";
							throw new LexicalError(yyline,errorString);
						  }


  


