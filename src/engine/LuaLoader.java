package engine;
import java.util.HashMap;

import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.ThreeArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;
import org.luaj.vm2.lib.jse.JsePlatform;

import structures.Entity;
import structures.Doodad;
import structures.Vector;


public class LuaLoader {
	private static final LuaTable emt = new LuaTable();
	private static final LuaTable vmt = new LuaTable();
	private static final LuaTable lib = new LuaTable();
	private static final LuaTable _G = globals();
	
	public static void init() {
		setupMetatables();
		setupLib();
		Doodad.globals = globals();
	}

	public static Doodad getDoodad(String doodadName) {
		if (!Doodad.doodadClassExists(doodadName)) {
			LuaTable classTable = new LuaTable();
			LuaTable fenv = getFileEnvironment(Doodad.globals, Settings.doodadDir+doodadName+Settings.luaEventFile);
//			LuaTable globals = fenv.get("g");
			LuaTable funcs = fenv.get("funcs").checktable();
			Doodad.createDoodadClass(doodadName, funcs);
//			for (String funcName : Events.mandatoryFunctions) {
//			LuaValue func = maybeGetFunction(fenv, funcName);
//			if (func != null)
//				events.setVar(funcName, func);
//		}
			//TODO
			//there shouldn't be no info.lua file,
			//everything should be fixed in the onBirth function
		}
		return new Doodad(doodadName);
	}

	private static LuaFunction maybeGetFunction(LuaTable fenv, String varName) {
		LuaValue var = getVariable(fenv, varName);
		if (!var.isnil()) {
			if (var.isfunction())
				return (LuaFunction) var;
			else
				niceError(fenv, varName+" was defined, but not as a function," +
						"and will therefore not work.", false);
		}
		
		return null;
	}

	public static void loadInfo(Entity entity, String doodadName) {
		LuaTable fenv = getFileEnvironment(Doodad.globals, Settings.doodadDir+doodadName+Settings.luaInfoFile);
		entity.modelName = getVariable(fenv, "model").checkstring().toString();
		entity.setOwnGravity(getVariable(fenv, "hasOwnGravity").checkboolean());
		entity.setFreeze(getVariable(fenv, "isFrozen").checkboolean());
		entity.setSphere(getVariable(fenv, "isSphere").checkboolean());
		entity.setBoundingRadius((float)getVariable(fenv, "boundingRadius").checkdouble());
		entity.setMass((float)getVariable(fenv, "mass").checkdouble());
	}
	
	/**
	 * Get a variable from an environment.
	 * @param fenv Environment to get variable from
	 * @param varName name of variable
	 * @return the LuaFunction
	 */
	private static LuaValue getVariable(LuaValue fenv, String varName) {
		return fenv.get(varName);
	}
	
	/**
	 * Takes a file name and loads tries to load it from
	 * the doodad directory.
	 * @param filePath e.g. <code>doodads/sphere/events.lua</code>
	 * @return file's environment
	 */
	private static LuaTable getFileEnvironment(LuaTable startingEnv, String filePath) {
		LuaValue fileFunction = _G.get("loadfile").call(LuaValue.valueOf(filePath));
		//TODO
		//fileFunction.setfenv(startingEnv);
		fileFunction.call();
		LuaTable fenv = fileFunction.getfenv().checktable();
		fenv.set("filePath", filePath);
		return fenv;
	}
	
//	/**
//	 * @return an array with all the names of the files
//	 * which end in .lua in the doodad directory.
//	 */
//	private static String[] getAvailableDoodads() {
//		FilenameFilter filter = new FilenameFilter() {
//			@Override
//			public boolean accept(File dir, String name) {
//		        return name.endsWith(".lua");
//			}
//		};
//		return new File(Settings.doodadDir).list(filter);
//	}
	
	
	
	
	private static LuaTable globals() {
		LuaTable _G = JsePlatform.standardGlobals();
		_G.set("lib", lib);
		return _G;
	}

	private static void setupLib() {
		LuaTable elib = new LuaTable();
		LuaTable vlib = new LuaTable();
		lib.set("entity", elib);
		lib.set("vector", vlib);
		
		elib.set("new", new TwoArgFunction() {
			@Override
			public LuaValue call(LuaValue doodadName, LuaValue isAnon) {
				Entity entity = new Entity(checkString(doodadName), isAnon.toboolean());
				LuaValue luaEntity = toUserdata(entity);
				return luaEntity;
			}
		});
		elib.set("attach", new TwoArgFunction() {
			@Override
			public LuaValue call(LuaValue e1l, LuaValue e2l) {
				System.out.println("\nattach was called:");
				_G.foreach(new TwoArgFunction() {

					@Override
					public LuaValue call(LuaValue k, LuaValue v) {
						System.out.println(k+"  "+v);
						return null;
					}
					
				});
				Entity e1 = getEntity(e1l);
				Entity e2 = getEntity(e2l);
				if (!e2.isAnon())
					niceError(LuaValue.valueOf("sure would be nice to have fenv here..."), String.format(
							"Tried to attach a non-anon entity, \n%s\n to\n%s",
							e2, e1), false);
				else
					e1.attach(e2);
				return LuaValue.NIL;
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

	protected static String checkString(LuaValue string) {
		if (string.isstring())
			return string.toString();
		else
			niceError(LuaValue.valueOf("sure would fenv"), "not a string", false);
		return null;
	}

	protected static boolean checkBoolean(LuaValue bool) {
		if (bool.isboolean())
			return bool.checkboolean();
		else
			niceError(LuaValue.valueOf("sure would fenv"), "not a boolean", false);
		return false;
	}

	private static void setupMetatables() {
		emt.set(LuaValue.INDEX, new TwoArgFunction() {
			@Override
			public LuaValue call(LuaValue entityLua, LuaValue luaKey) {
				Entity entity = (Entity) entityLua.checkuserdata(Entity.class);
				String key = luaKey.toString();
				if (key.equals("pos"))
					return toUserdata(entity.pos);
				if (key.equals("speed"))
					return toUserdata(entity.speed);
				return LuaValue.NIL;
			}
		});
		emt.set(LuaValue.NEWINDEX, new ThreeArgFunction() {
			@Override
			public LuaValue call(LuaValue entityLua, LuaValue luaKey, LuaValue value) {
				Entity entity = (Entity) entityLua.checkuserdata(Entity.class);
				String key = luaKey.toString();
				if (key.equals("pos")) {
					entity.pos.set(getVector(value));
				}
				return value;
			}
		});
		
		
		vmt.set(LuaValue.INDEX, new TwoArgFunction() {
			@Override
			public LuaValue call(LuaValue vectorLua, LuaValue luaKey) {
				Vector vector = (Vector) vectorLua.checkuserdata(Vector.class);
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
				Vector vector = (Vector) vectorLua.checkuserdata(Vector.class);
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
			public LuaValue call(LuaValue luaV, LuaValue luaU) {
				Vector v = (Vector) luaV.checkuserdata(Vector.class);
				Vector u = (Vector) luaU.checkuserdata(Vector.class);
				return LuaValue.userdataOf(v.add(u));
			}
		});
		vmt.set(LuaValue.SUB, new TwoArgFunction() {
			@Override
			public LuaValue call(LuaValue luaV, LuaValue luaU) {
				Vector v = (Vector) luaV.checkuserdata(Vector.class);
				Vector u = (Vector) luaU.checkuserdata(Vector.class);
				return LuaValue.userdataOf(v.subtract(u));
			}
		});
		vmt.set(LuaValue.MUL, new TwoArgFunction() {
			@Override
			public LuaValue call(LuaValue luaV, LuaValue luaF) {
				Vector v = (Vector) luaV.checkuserdata(Vector.class);
				float f = (float) luaF.checkdouble();
				return userdataOf(v.multiply(f));
			}
		});
		vmt.set(LuaValue.DIV, new TwoArgFunction() {
			@Override
			public LuaValue call(LuaValue luaV, LuaValue luaF) {
				Vector v = (Vector) luaV.checkuserdata(Vector.class);
				float f = (float) luaF.checkdouble();
				return userdataOf(v.multiply(1/f));
			}
		});
	}
	
	private static void niceError(LuaValue fenv, String error, boolean crash) {
		String output = String.format("   Error in %s:\n%s",
				fenv.get("filePath"), error);
		if (crash)
			throw new RuntimeException(output);
		else
			Engine.err.println(output);
	}
	
	/**
	 * @param e
	 * @return userdata of e with emt as metatable
	 */
	public static LuaValue toUserdata(Entity e) {
		return LuaValue.userdataOf(e, emt);
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
}
