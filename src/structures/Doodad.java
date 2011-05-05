package structures;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

public class Doodad {
	private static final HashMap<String, LuaTable> doodadClasses = new HashMap<String, LuaTable>();
	private static final Queue<Call> callQueue = new LinkedList<Call>();
	public static LuaTable globals;
	
	private LuaTable userVars;
	private HashMap<String, LuaValue> mandatoryFuncs = new HashMap<String, LuaValue>();
	
	public Doodad(String doodadClass) {
		LuaTable mt = doodadClasses.get(doodadClass);
		if (mt == null)
			throw new RuntimeException("Error instantiating "+doodadClass+", class not yet created.");
		userVars = new LuaTable();
		userVars.setmetatable(mt);
	}
	
	public static boolean doodadClassExists(String doodadName) {
		return doodadClasses.containsKey(doodadName);
	}
	
	public static void createDoodadClass(String doodadName, LuaTable classTable) {
		classTable.set(LuaValue.INDEX, classTable);
		doodadClasses.put(doodadName, classTable);
	}
	
	public static void executeQueue() {
//		System.out.println(callQueue.size());
		while (!callQueue.isEmpty())
			callQueue.poll().execute();
	}
	
	public void setVar(String funcName, LuaValue func) {
		mandatoryFuncs.put(funcName, func);
	}

	public LuaValue getVar(String key) {
		return mandatoryFuncs.get(key);
	}

	public LuaTable getCopyOfUserVars() {
		//TODO
		return userVars; // should be a copy!!!!!!!!!!!!!!
	}

	public void call(String funcName) {
		LuaValue func = getVar(funcName);
		if (func != null)
			func.call();
	}
	public void call(String funcName, LuaValue arg0) {
		LuaValue func = getVar(funcName);
		if (func != null)
			func.call(arg0);
	}
	public void call(String funcName, LuaValue arg0, LuaValue arg1) {
		LuaValue func = getVar(funcName);
		if (func != null)
			func.call(arg0, arg1);
	}
	public void call(String funcName, LuaValue arg0, LuaValue arg1, LuaValue arg2) {
		LuaValue func = getVar(funcName);
		if (func != null)
			func.call(arg0, arg1, arg2);
	}
	public void pushCall(String funcName) {
		LuaValue func = getVar(funcName);
		if (func != null)
			new Call(func, null);
	}
	public void pushCall(String funcName, LuaValue arg0) {
		LuaValue func = getVar(funcName);
		if (func != null)
			new Call(func, arg0);
	}
	public void pushCall(String funcName, LuaValue arg0, LuaValue arg1) {
		LuaValue func = getVar(funcName);
		if (func != null)
			new Call(func, LuaValue.varargsOf(arg0, arg1));
	}
	public void pushCall(String funcName, LuaValue arg0, LuaValue arg1, LuaValue arg2) {
		LuaValue func = getVar(funcName);
		if (func != null)
			new Call(func, LuaValue.varargsOf(arg0, arg1, arg2));
	}
	
	private final class Call {
		private LuaValue func;
		private Varargs args;
		
		public Call (LuaValue func, Varargs args) {
			this.func = func;
			this.args = args;
			callQueue.add(this);
		}
		
		public void execute() {
			func.invoke(args);
		}
	}
}