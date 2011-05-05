package engine;

import java.io.File;
import java.io.FilenameFilter;

import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;
import org.luaj.vm2.lib.ThreeArgFunction;
import org.luaj.vm2.lib.jse.JsePlatform;

import structures.Entity;
import structures.Vector;


public class LuaLoader {
	private static final LuaTable classTables = new LuaTable();
	private static LuaValue defaultClassTable = LuaValue.NIL;
	
	private static final LuaTable emt = new LuaTable();
	private static final LuaTable vmt = new LuaTable();
	private static final LuaValue classTableMT = new LuaTable();;
	private static final LuaValue fieldMT = new LuaTable();
	
	private static final LuaTable lib = new LuaTable();
	private static final LuaTable global = new LuaTable();
	private static final LuaTable fields = new LuaTable();
	private static final LuaTable _G = JsePlatform.standardGlobals();
	
	public static void init() {
		setupMetatables();
		setupLib();
		setupDefaultClassTable();
	}
	
	public static LuaTable getClassTable(String doodadClassName) {
		return classTables.get(doodadClassName).checktable();
	}

	/**
	 * Takes a class environment and tries to load the file specified in 
	 * the <code>class.path</code> variable from the doodad directory.
	 * @param cenv class environment
	 * @return file's environment after running it
	 */
	private static LuaTable getEnvAfterRunningFileInIt(LuaTable cenv) {
		LuaValue filePath = cenv.get("class").get("path");
		cenv.load(_G.get("loadfile").call(filePath));
		return cenv;
	}
	
	/**
	 * @return an array with all the names of the files
	 * which end in .lua in the doodad directory.
	 */
	private static String[] getAvailableDoodads() {
		FilenameFilter filter = new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
		        return name.endsWith(".lua");
			}
		};
		return new File(Settings.doodadDir).list(filter);
	}
	
	public static void printTable(String header, LuaValue table) {
		System.out.printf("\n--  Table: %s\n", header);
		System.out.println( "-----------------------");
		printTable(table, 1);
		System.out.println( "=======================\n");
	}
	
	private static void printTable(LuaValue table, int indent) {
		if (table.istable())
			for (LuaValue key : table.checktable().keys())
				printTable(key, indent+1);
		else {
			for(; indent>=0; indent--)
				System.out.print("  ");
			System.out.println(table);
		}
	}
	
	/**
	 * @param doodadClassName specify to set variable <code>doodad</code>
	 * to that string in the new environment.
	 */
	private static LuaTable newClassEnv(String doodadClassName) {
		LuaTable cenv = JsePlatform.standardGlobals();
		cenv.set("lib", lib);
		cenv.set("global", global);
		LuaTable classTable = new LuaTable();
		classTable.set("doodad", LuaValue.valueOf(doodadClassName));
		classTable.set("path", LuaValue.valueOf(Settings.doodadDir+doodadClassName+".lua"));
		classTable.set(LuaValue.INDEX, classTable); //if used as a metatable
		classTable.set("super", defaultClassTable);
		cenv.set("class", classTable);
		return cenv;
	}
	
	/**
	 * Catches all LuaErrors and prints some info to Engine.err
	 * 
	 * @param doodadClassName
	 * @return the class table, or null if there was any error.
	 */
	private static LuaTable loadClassTable(String doodadClassName) {
		try {
			LuaTable cenv = newClassEnv(doodadClassName);
			return getClassTableFromClassEnv(getEnvAfterRunningFileInIt(cenv));
		} catch (LuaError e) {
			Engine.err.printf("Syntax error in file '%s%s.lua':\n\t%s\n",
					Settings.doodadDir, doodadClassName, e.getMessage());
			return null;
		}
	}
	
	private static LuaTable cleanClassTable(String doodadClassName) {
		return getClassTableFromClassEnv(newClassEnv(doodadClassName));
	}	
	
	private static LuaTable getClassTableFromClassEnv(LuaValue env) {
		LuaValue classTable = env.get("class");
		if (!classTable.istable())
			throw new LuaError(String.format("Variable 'class' is %s, must be a table.",
					classTable.typename()));
		classTable.setmetatable(classTableMT);
		return classTable.checktable();
	}
	
	/**
	 * @param entity
	 * @param doodadClass 
	 * @return userdata of e with emt as metatable
	 */
	public static LuaValue toUserdata(Entity entity, LuaTable classTable) {
		LuaValue entityLua = LuaValue.userdataOf(entity, emt);
		LuaTable field = new LuaTable();
		field.set("class", classTable);
		field.setmetatable(fieldMT);
		fields.set(entityLua, field);
		return entityLua;
	}
	/**
	 * @param v
	 * @return userdata of v with vmt as metatable
	 */
	public static LuaValue toUserdata(Vector v) {
		return LuaValue.userdataOf(v, vmt);
	}
	/**
	 * @param e
	 * @return entity from userdata e
	 */
	private static Entity getEntity(LuaValue e) {
		return (Entity) e.checkuserdata(Entity.class);
	}
	/**
	 * @param v
	 * @return vector from userdata v
	 */
	private static Vector getVector(LuaValue v) {
		return (Vector) v.checkuserdata(Vector.class);
	}
	
	private static void setupDefaultClassTable() {
		defaultClassTable = loadClassTable("default");
		if (defaultClassTable == null)
			throw new RuntimeException("A working 'default.lua' file is required.");
		defaultClassTable.setmetatable(new LuaTable());
	}
	
	private static void setupLib() {
		LuaTable elib = new LuaTable();
		LuaTable vlib = new LuaTable();
		lib.set("entity", elib);
		lib.set("vector", vlib);
		
		lib.set("printtable", new TwoArgFunction() {
			@Override
			public LuaValue call(LuaValue table, LuaValue header) {
				printTable(header.tojstring(), table);
				return LuaValue.NIL;
			}
		});
		
		elib.set("new", new TwoArgFunction() {
			@Override
			public LuaValue call(LuaValue doodadClassName, LuaValue isAnon) {
				Entity entity = new Entity(doodadClassName.checkjstring());
				return entity.doodad.getUserdata();
			}
		});
		
		
		
		vlib.set("new", new ThreeArgFunction() {
			@Override
			public LuaValue call(LuaValue xl, LuaValue yl, LuaValue zl) {
				float x = (float) xl.todouble();
				float y = (float) yl.todouble();
				float z = (float) zl.todouble();
				return toUserdata(new Vector(x, y, z));
			}
		});
	}

	private static void setupMetatables() {
//		classTableMT = new LuaTable();
//		classVarMT.set(LuaValue.INDEX, new TwoArgFunction() {
//			@Override
//			public LuaValue call(LuaValue classVar, LuaValue arg2) {
//				
//				return null;
//			}
//		});
		
		
		
		LuaTable classTablesMT = new LuaTable();
		classTablesMT.set(LuaValue.INDEX, new TwoArgFunction() {
			@Override
			public LuaValue call(LuaValue table, LuaValue doodadClassName) {
				LuaTable classTable = loadClassTable(doodadClassName.tojstring());
				if (classTable == null)
					classTable = cleanClassTable(doodadClassName.tojstring());
				classTables.set(doodadClassName, classTable);
				classTable.setmetatable(classTableMT);
				return classTable;
			}
		});
		classTables.setmetatable(classTablesMT);
		
		
		
		fieldMT.set(LuaValue.INDEX, new TwoArgFunction() {
			@Override
			public LuaValue call(LuaValue fieldTable, LuaValue key) {
				return fieldTable.get("class").get(key);
			}
		});
		
		classTableMT.set(LuaValue.INDEX, new TwoArgFunction() {
			@Override
			public LuaValue call(LuaValue classTable, LuaValue key) {
				return classTable.get("super").get(key);
			}
		});
		
		
		LuaTable fieldsMT = new LuaTable();
		fieldsMT.set(LuaValue.MODE, LuaValue.valueOf("k"));
		fields.setmetatable(fieldsMT);
		
		
		
		emt.set(LuaValue.INDEX, new TwoArgFunction() {
			@Override
			public LuaValue call(LuaValue entityLua, LuaValue luaKey) {
				Entity entity = getEntity(entityLua);
				String key = luaKey.toString();
				if (key.equals("pos"))
					return toUserdata(entity.pos);
				if (key.equals("speed"))
					return toUserdata(entity.speed);
				if (key.equals("model"))
					return LuaValue.valueOf(entity.getModel());
				if (key.equals("sphere"))
					return LuaValue.valueOf(entity.isSphere());
				if (key.equals("frozen"))
					return LuaValue.valueOf(entity.isFrozen());
				if (key.equals("anon"))
					return LuaValue.valueOf(entity.isAnon());
				if (key.equals("ownGravity"))
					return LuaValue.valueOf(entity.hasOwnGravity());
				if (key.equals("boundingRadius"))
					return LuaValue.valueOf(entity.getBoundingRadius());
				if (key.equals("mass"))
					return LuaValue.valueOf(entity.getMass());
				
				return fields.get(entityLua).get(luaKey);
			}
		});
		emt.set(LuaValue.NEWINDEX, new ThreeArgFunction() {
			@Override
			public LuaValue call(LuaValue entityLua, LuaValue luaKey, LuaValue value) {
				Entity entity = getEntity(entityLua);
				String key = luaKey.toString();
				if (key.equals("pos"))
					entity.pos.set(getVector(value));
				if (key.equals("speed"))
					entity.speed.set(getVector(value));
				if (key.equals("model"))
					entity.setModel(value.checkjstring());
				if (key.equals("sphere"))
					entity.setSphere(value.checkboolean());
				if (key.equals("frozen"))
					entity.setFrozen(value.checkboolean());
				if (key.equals("anon"))
					throw new LuaError("Tried to write directly to self.anon");
				if (key.equals("ownGravity"))
					entity.setOwnGravity(value.checkboolean());
				if (key.equals("boundingRadius"))
					entity.setBoundingRadius((float)value.checkdouble());
				if (key.equals("mass"))
					entity.setMass((float)value.checkdouble());
				if (key.equals("father")) {
					System.out.println("\nattach was called:");
					Entity father = getEntity(value);
					father.attach(entity);
				}
				
				fields.get(entityLua).set(luaKey, value);
				return value;
			}
		});
		
		
		
		vmt.set(LuaValue.INDEX, new TwoArgFunction() {
			@Override
			public LuaValue call(LuaValue vectorLua, LuaValue luaKey) {
				Vector vector = getVector(vectorLua);
				String key = luaKey.toString();
				if (key.equals("x"))
					return LuaValue.valueOf(vector.x);
				else if (key.equals("y"))
					return LuaValue.valueOf(vector.y);
				else if (key.equals("z"))
					return LuaValue.valueOf(vector.z);
				return LuaValue.NIL;
			}
		});
		vmt.set(LuaValue.NEWINDEX, new ThreeArgFunction() {
			@Override
			public LuaValue call(LuaValue vectorLua, LuaValue luaKey, LuaValue value) {
				Vector vector = getVector(vectorLua);
				String key = luaKey.toString();
				if (key.equals("x"))
					vector.x = (float) value.checkdouble();
				else if (key.equals("y"))
					vector.y = (float) value.checkdouble();
				else if (key.equals("z"))
					vector.z = (float) value.checkdouble();
				else if (key.equals("z"))
					vector.z = (float) value.checkdouble();
				return value;
			}
		});
		vmt.set(LuaValue.ADD, new TwoArgFunction() {
			@Override
			public LuaValue call(LuaValue vl, LuaValue ul) {
				Vector v = getVector(vl);
				Vector u = getVector(ul);
				return toUserdata(v.add(u));
			}
		});
		vmt.set(LuaValue.SUB, new TwoArgFunction() {
			@Override
			public LuaValue call(LuaValue luaV, LuaValue luaU) {
				Vector v = (Vector) luaV.checkuserdata(Vector.class);
				Vector u = (Vector) luaU.checkuserdata(Vector.class);
				return toUserdata(v.subtract(u));
			}
		});
		vmt.set(LuaValue.MUL, new TwoArgFunction() {
			@Override
			public LuaValue call(LuaValue luaV, LuaValue luaF) {
				Vector v = (Vector) luaV.checkuserdata(Vector.class);
				float f = (float) luaF.checkdouble();
				return toUserdata(v.multiply(f));
			}
		});
		vmt.set(LuaValue.DIV, new TwoArgFunction() {
			@Override
			public LuaValue call(LuaValue luaV, LuaValue luaF) {
				Vector v = (Vector) luaV.checkuserdata(Vector.class);
				float f = (float) luaF.checkdouble();
				return toUserdata(v.multiply(1/f));
			}
		});
	}
}
