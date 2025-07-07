package nl.wdudokvanheel.neat.lunar.game.logic;


import nl.wdudokvanheel.neat.lunar.game.model.Lander;

public class Physics{
	static void updateLanderPhysics(Lander lander){
		double deltaTime = 1 / 60f;
		double angleInRadians = Math.toRadians(lander.angle);

		// Calculate forces acting on the lander
		double thrustX = 0;
		double thrustY = LunarGame.GRAVITY * LunarGame.LANDER_MASS;

		double thrust = lander.thrust;

		if(lander.fuel < thrust){
			// Apply thrust with remaining fuel
			thrust = lander.fuel;
		}
		// Burn fuel
		lander.fuel -= thrust;

		// Apply engine thrust
		if(thrust > 0){
			thrustX = thrust * LunarGame.LANDER_THRUST_POWER * Math.sin(angleInRadians);
			thrustY = -thrust * LunarGame.LANDER_THRUST_POWER * Math.cos(angleInRadians) + LunarGame.GRAVITY * LunarGame.LANDER_MASS;
		}

		double dragX = -LunarGame.DRAG_COEFFICIENT_X * lander.speed.x * Math.abs(lander.speed.x);
		double dragY = -LunarGame.DRAG_COEFFICIENT_Y * lander.speed.y * Math.abs(lander.speed.y);

		// Update acceleration
		lander.acceleration.x = thrustX + dragX;
		lander.acceleration.y = thrustY + dragY;

		// Update speed
		lander.speed.x += lander.acceleration.x * deltaTime;
		lander.speed.y += lander.acceleration.y * deltaTime;

		// Calculate displacement
		double dx = lander.speed.x * deltaTime;
		double dy = lander.speed.y * deltaTime;

		// Update position
		lander.position.x += dx;
		lander.position.y += dy;

		//Update total traveled distance
		lander.distanceTraveled += Math.sqrt(dx * dx + dy * dy);
	}
}
