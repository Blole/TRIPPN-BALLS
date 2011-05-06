
class.angle = 0

function class:onBirth()
	self.model = "Sphere"
	self.sphere = true
	self.frozen = false
	self.ownGravity = false
	self.boundingRadius = 1
	self.mass = 1
	self.fertile = true
end

function class:onCollide(other)
	self.fertile = false
	other.fertile = false
	self.speed = lib.vector.new(0,0,0)
	other.speed = lib.vector.new(0,0,0)
end

function class:onGroundCollide()
	if self.fertile then
		
		new = lib.entity.new("Sphere")
		new.angle = self.angle + 0.2
		new.pos = self.pos + lib.vector.new(0,2,0)
		new.speed = lib.vector.new(math.sin(new.angle),0.2,math.cos(new.angle))
		new.speed.length = 0.2
		self.speed = lib.vector.new(0,0,0)
	end
end
