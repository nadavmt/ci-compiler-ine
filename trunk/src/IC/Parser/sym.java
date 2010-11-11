package IC.Parser;

/**
 * Contains the constants used to identify the types of tokens returned
 * by the lexical analyzer. Also contains a method for getting the String
 * representation of a given token.
 */
public class sym
{
	public static final int EOF = 0;
	
	public static final int LP = 1;
	public static final int RP = 2;
	public static final int LB = 3;
	public static final int RB = 4;
	public static final int LCBR = 5;
	public static final int RCBR = 6;
	
	public static final int ASSIGN = 7;
	public static final int DIVIDE = 8;
	public static final int MOD = 9;
	public static final int MULTIPLY = 10;
	public static final int PLUS = 11;
	public static final int MINUS = 12;
	
	public static final int EQUAL = 13;
	public static final int NEQUAL = 14;
	public static final int GT = 15;
	public static final int GTE = 16;
	public static final int LT = 17;
	public static final int LTE = 18;
	
	public static final int LAND = 19;
	public static final int LNEG = 20;
	public static final int LOR = 21;
	
	public static final int DOT = 22;
	public static final int COMMA = 23;
	public static final int SEMI = 24;
	
	public static final int VOID = 25;
	public static final int BOOLEAN = 26;
	public static final int INT = 27;
	public static final int INTEGER = 28;
	public static final int STRING = 29;
	public static final int QUOTE = 30;
	public static final int STATIC = 31;
	
	public static final int IF = 32;
	public static final int ELSE = 33;
	public static final int WHILE = 34;
	public static final int CONTINUE = 35;
	public static final int BREAK = 36;
	public static final int RETURN = 37;
	
	public static final int CLASS = 38;
	public static final int CLASS_ID = 39;
	public static final int ID = 40;
	
	public static final int EXTENDS = 41;
	public static final int THIS = 42;
	public static final int NEW = 43;
	
	public static final int FALSE= 44;
	public static final int TRUE = 45;
	public static final int NULL = 46;
	
	public static final int LENGTH = 47;

	/**
	 * Returns the String representation for a given token ID.
	 * 
	 * @param tokenValue The token ID for which the description is wanted
	 * @return The given token's String value
	 */
	public static String tokenName(int tokenValue)
	{
		switch (tokenValue)
		{
			case EOF: return "EOF";
			case LP: return "LP";
			case RP: return "RP";
			case LB: return "LB";
			case RB: return "RB";
			case LCBR: return "LCBR";
			case RCBR: return "RCBR";
			case ASSIGN: return "ASSIGN";  
			case DIVIDE: return "DIVIDE";
			case MOD: return "MOD";
			case MULTIPLY: return "MULTIPLY";
			case PLUS: return "PLUS";
			case MINUS: return "MINUS";
			case EQUAL: return "EQUAL";
			case NEQUAL: return "NEQUAL";
			case GT: return "GT";
			case GTE: return "GTE";
			case LT: return "LT";
			case LTE: return "LTE";
			case LAND: return "LAND";
			case LNEG: return "LNEG";
			case LOR: return "LOR";
			case DOT: return "DOT";
			case COMMA: return "COMMA";
			case SEMI: return "SEMI";
			case VOID: return "VOID";
			case BOOLEAN: return "BOOLEAN";
			case INT: return "INT";
			case INTEGER: return "INTEGER";
			case STRING: return "STRING";
			case QUOTE: return "QUOTE";
			case STATIC: return "STATIC";
			case IF: return "IF";
			case ELSE: return "ELSE";
			case WHILE: return "WHILE";
			case CONTINUE: return "CONTINUE";
			case BREAK: return "BREAK";
			case RETURN: return "RETURN";
			case CLASS: return "CLASS";
			case CLASS_ID: return "CLASS_ID";
			case ID: return "ID";
			case EXTENDS: return "EXTENDS";
			case THIS: return "THIS";
			case NEW: return "NEW";
			case FALSE: return "FALSE";
			case TRUE: return "TRUE";
			case NULL: return "NULL";
			case LENGTH: return "LENGTH";
			
			default: return null;
		}
	}
}
