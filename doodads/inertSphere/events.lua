
function onBirth()
	--print("born")
end

function onCollide(e1, e2)
	e1.pos.x = e1.pos.x + 4
	e1.speed.x = 0.1
	e1.speed.y = 0.1
	e1.speed.z = 1
end