package engine;
import java.io.File;
import java.io.FilenameFilter;

import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaUserdata;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.ThreeArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;
import org.luaj.vm2.lib.jse.JsePlatform;

import structures.Entity;
import structures.Events;
import structures.Vector;


public class LuaLoader {
	private static final LuaValue _G = JsePlatform.standardGlobals();
	private static final LuaTable emt = new LuaTable();
	private static final LuaValue vmt = new LuaTable();
	

	public static void main(String[] args) {
//		metatableEntity = (LuaUserdata)_G.get("luajava").get("newInstance").call(LuaValue.valueOf(Entity.class.getName()));
//		System.out.println(metatableEntity.metatag(LuaValue.INDEX));
//		System.out.println(metatableEntity.get("render"));
		setupMetatables();
		String luaFile = Settings.doodadDir+"sphere"+Settings.luaEventFile;
		
		LuaTable fenv = getFileEnvironment(luaFile).checktable();
		LuaValue onCollide = getVariable(fenv, "onCollide").checkfunction();
		
		Entity e1 = new Entity("LUA_DEBUG", false);
		Entity e2 = new Entity("LUA_DEBUG", false);
		e1.pos.set(1,1,1);
		e2.pos.set(2,2,2);
		LuaUserdata u1 = LuaValue.userdataOf(e1, emt);
		LuaUserdata u2 = LuaValue.userdataOf(e2, emt);
		
		System.out.println("------------------------------------------------");
		System.out.println("\t"+luaFile);
		System.out.println("------------------------------------------------");
		
		onCollide.call(u1, u2);
		
		System.out.println();
		System.out.println();
		System.out.println();
	}
	
	public static void init() {
		setupMetatables();
	}
	
	public static Events load(String doodadName) {
		LuaValue fenv = getFileEnvironment(Settings.doodadDir+doodadName+Settings.luaEventFile);
		
		Events events = Events.getNew(doodadName);
		events.onCollide= getVariable(fenv, "onCollide").checkfunction();
		events.onStop 	= getVariable(fenv, "onStop").checkfunction();
		events.onPause 	= getVariable(fenv, "onPause").checkfunction();
		events.onPlay	= getVariable(fenv, "onPlay").checkfunction();
		return events;
	}
	
	/**
	 * Get a function from an environment.
	 * @param fenv Environment to get function from
	 * @param functionName name of function
	 * @return the LuaFunction
	 */
	private static LuaValue getVariable(LuaValue fenv, String functionName) {
		return fenv.get("onCollide");
	}
	
	/**
	 * Takes a file name and loads tries to load it from
	 * the doodad directory.
	 * @param filePath e.g. <code>doodas/sphere/events.lua</code>
	 * @return file's environment
	 */
	private static LuaTable getFileEnvironment(String filePath) {
		LuaValue f = _G.get("loadfile").call(LuaValue.valueOf(filePath));
		f.call();
		return f.getfenv().checktable();
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
