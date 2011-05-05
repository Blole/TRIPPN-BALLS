print(class.path.." loading")

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
end

function class:onGroundCollide()
	if self.fertile then
		self.speed = lib.vector.new(0,0,0)
		
		new = lib.entity.new("Sphere")
		new.pos = self.pos + lib.vector.new(0,2,-2)
		new.speed = lib.vector.new(0,0.2,-0.2)
	end
end
