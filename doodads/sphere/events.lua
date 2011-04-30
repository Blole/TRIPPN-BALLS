
funcs = {}
function funcs.onCollide(e1, e2)
	print("lua coll")
	e1.attach(e2)
	--e3 = lib.entity.new("inertSphere")
	--e3.pos = e1.pos
	--e3.pos.y = e3.pos.y + 3
	--e1.speed = e1.speed
	--e3 = lib.entity.new("sphere")
	--print(e3)
	--e3.pos = lib.vector.new(0,0,0)
	--e3.speed = lib.vector.new(2,2,2)
	
	--e3 = lib.entity.new("sphere")
	--print(e3)
	--e3.pos = e1.pos + lib.vector.new(1,1,1)
end

