package structures;

import java.util.LinkedList;
import java.util.Queue;

import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

import engine.Engine;
import engine.LuaLoader;

public class Doodad {
	private static final Queue<DelayedCall> callQueue = new LinkedList<DelayedCall>();
	private LuaValue self;

	public Doodad(String doodadClassName, Entity entity) {
		LuaTable classTable = LuaLoader.getClassTable(doodadClassName);
		self = LuaLoader.toUserdata(entity, classTable);
	}
	
	/**
	 * @return A userdata reflection of the entity, with metatable and everything.
	 */
	public LuaValue getUserdata() {
		return self;
	}
	
	public static void executeQueue() {
		while (!callQueue.isEmpty())
			callQueue.poll().execute();
	}
	
	public LuaValue call(String funcName) {
		return safeCall(funcName, LuaValue.NONE);
	}
	public LuaValue call(String funcName, LuaValue arg0) {
		return safeCall(funcName, arg0);
	}
	public LuaValue call(String funcName, LuaValue arg0, LuaValue arg1) {
		return safeCall(funcName, LuaValue.varargsOf(arg0, arg1));
	}
	public void pushCall(String funcName) {
		new DelayedCall(funcName, LuaValue.NONE);
	}
	public void pushCall(String funcName, LuaValue arg0) {
		new DelayedCall(funcName, arg0);
	}
	public void pushCall(String funcName, LuaValue arg0, LuaValue arg1) {
		new DelayedCall(funcName, LuaValue.varargsOf(arg0, arg1));
	}
	public void pushCall(String funcName, LuaValue arg0, LuaValue arg1, LuaValue arg2) {
		new DelayedCall(funcName, LuaValue.varargsOf(arg0, arg1, arg2));
	}
	private LuaValue safeCall(String funcName, Varargs args) {
		Varargs ret = null;
		try {
//			LuaLoader.printTable(self.getmetatable().tojstring(), self.getmetatable());
//			System.out.println(self.get("class").get("onBirth"));
//			LuaLoader.printTable(LuaLoader.emt.tojstring(), LuaLoader.emt);
			ret = self.invokemethod(funcName, args);
		} catch (LuaError e) {
			Engine.err.printf("Syntax error in file '%s' in function %s:\n\t%s\n",
					self.get("class").get("path"), funcName, e.getMessage());
		}
		if (ret == null)
			return LuaValue.NIL;
		return ret.arg1();
	}
	
	private final class DelayedCall {
		private String funcName;
		private Varargs args;
		
		public DelayedCall (String funcName, Varargs args) {
			this.funcName = funcName;
			this.args = args;
			callQueue.add(this);
		}
		
		public void execute() {
			safeCall(funcName, args);
		}
	}
}