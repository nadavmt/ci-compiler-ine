//package IC.SemanticAnalysis;
//
//import java.util.Map;
//
//import IC.AST.ICClass;
//
//public class TypeTable {
//	// Maps element types to array types
//	private static Map<Type, ArrayType> uniqueArrayTypes;
//	private static Map<String, ClassType> uniqueClassTypes;
//	public static Type boolType = new BoolType();
//	public static Type intType = new IntType();
//	public static Type stringType = new StringType();
//	public static Type voidType = new VoidType();
//
//	// Returns unique array type object
//	public static ArrayType arrayType(Type elemType) {
//		if (uniqueArrayTypes.containsKey(elemType)) {
//			// array type object already created – return it
//			return uniqueArrayTypes.get(elemType);
//		} else {
//			// object doesn’t exist – create and return it
//			ArrayType arrt = new ArrayType(elemType);
//			uniqueArrayTypes.put(elemType, arrt);
//			return arrt;
//		}
//	}
//
//	public static ClassType classType(ICClass c) {
//		if (uniqueClassTypes.containsKey(c.getName())) {
//			// array type object already created – return it
//			return uniqueClassTypes.get(c.getName());
//		} else {
//			// object doesn’t exist – create and return it
//			ClassType cls = new ClassType(c);
//			uniqueClassTypes.put(c.getName(), cls);
//			return cls;
//		}
//	}
//	
//	public static ICClass getClassByName(String name)
//	{
//		return uniqueClassTypes.get(name).getClassAST();
//	}
//	
//}
