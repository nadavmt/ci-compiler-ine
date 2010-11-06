package IC.Parser;

public class sym {
  
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
  public static final int COMMA = 23 ;
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
  
  
  public static String tokenName(int tokenValue)
  {
	  
	  switch (tokenValue) {
		  case 0: return "EOF";
		  case 1: return "LP";
		  case 2: return "RP";
		  case 3: return "LB";
		  case 4: return "RB";
		  case 5: return "LCBR";
		  case 6: return "RCBR";
		  case 7: return "ASSIGN";  
		  case 8: return "DIVIDE";
		  case 9: return "MOD";
		  case 10: return "MULTIPLY";
		  case 11: return "PLUS";
		  case 12: return "MINUS";
		  case 13: return "EQUAL";
		  case 14: return "NEQUAL";
		  case 15: return "GT";
		  case 16: return "GTE";
		  case 17: return "LT";
		  case 18: return "LTE";
		  case 19: return "LAND";
		  case 20: return "LNEG";
		  case 21: return "LOR";
		  case 22: return "DOT";
		  case 23: return "COMMA";
		  case 24: return "SEMI";
		  case 25: return "VOID";
		  case 26: return "BOOLEAN";
		  case 27: return "INT";
		  case 28: return "INTEGER";
		  case 29: return "STRING";
		  case 30: return "QUOTA";
		  case 31: return "STATIC";
		  case 32: return "IF";
		  case 33: return "ELSE";
		  case 34: return "WHILE";
		  case 35: return "CONTINUE";
		  case 36: return "BREAK";
		  case 37: return "RETURN";
		  case 38: return "CLASS";
		  case 39: return "CLASS_ID";
		  case 40: return "ID";
		  case 41: return "EXTENDS";
		  case 42: return "THIS";
		  case 43: return "NEW";
		  case 44: return "FALSE";
		  case 45: return "TRUE";
		  case 46: return "NULL";
		  case 47: return "LENGTH";
		  default: return null;
	  }
		    
	  
  }
   
}
