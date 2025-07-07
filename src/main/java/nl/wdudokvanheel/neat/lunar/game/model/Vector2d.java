package nl.wdudokvanheel.neat.lunar.game.model;

public class Vector2d{
	public double x;
	public double y;

	public Vector2d(){
		this(0, 0);
	}

	public Vector2d(int x, int y){
		this.x = x;
		this.y = y;
	}

	public Vector2d(double x, double y){
		this.x = x;
		this.y = y;
	}

	public Vector2d scale(double scalar){
		return new Vector2d(this.x * scalar, this.y * scalar);
	}

	public Vector2d add(int x, int y){
		return new Vector2d(this.x + x, this.y + y);
	}

	public Vector2d add(double x, double y){
		return new Vector2d(this.x + x, this.y + y);
	}

	public Vector2d add(Vector2d other){
		return add(other.x, other.y);
	}

	public Vector2d subtract(Vector2d other){
		return new Vector2d(this.x - other.x, this.y - other.y);
	}

	public double dot(Vector2d other){
		return this.x * other.x + this.y * other.y;
	}

	public double length(){
		return Math.sqrt(this.x * this.x + this.y * this.y);
	}

	public Vector2d normalize(){
		double length = this.length();
		if(length == 0){
			return new Vector2d(0, 0);
		}
		return new Vector2d(this.x / length, this.y / length);
	}

	public double distance(Vector2d other){
		double dx = this.x - other.x;
		double dy = this.y - other.y;
		return Math.sqrt(dx * dx + dy * dy);
	}

	public Vector2d rotate(Vector2d center, double angle){
		double rad = Math.toRadians(angle);
		double cos = Math.cos(rad);
		double sin = Math.sin(rad);

		double newX = center.x + (x - center.x) * cos - (y - center.y) * sin;
		double newY = center.y + (x - center.x) * sin + (y - center.y) * cos;

		return new Vector2d(newX, newY);
	}

	public Vector2d floor(){
		return new Vector2d(Math.floor(x), Math.floor(y));
	}

	public double magnitude(){
		return Math.sqrt(this.x * this.x + this.y * this.y);
	}

	public double angle(Vector2d other){
		// Calculate the difference between the two vectors
		double dx = other.x - this.x;
		double dy = other.y - this.y;

		// Calculate the angle in radians
		double angleRadians = Math.atan2(dy, dx);

		// Convert radians to degrees and adjust the reference point to be upward (0 degrees)
		double angleDegrees = Math.toDegrees(angleRadians) - 90;

		// Normalize the angle to the range [0, 360)
		if(angleDegrees < 0){
			angleDegrees += 360;
		}

		return angleDegrees;
	}

	public Vector2d set(Vector2d vector2d){
		this.x = vector2d.x;
		this.y = vector2d.y;
		return this;
	}

	public Vector2d setX(double x){
		this.x = x;
		return this;
	}

	public Vector2d setY(double y){
		this.y = y;
		return this;
	}

	public Vector2d set(double x, double y){
		this.x = x;
		this.y = y;
		return this;
	}

	public boolean equals(Vector2d other){
		return x == other.x && y == other.y;
	}

	public Vector2d clone(){
		return new Vector2d(this.x, this.y);
	}

	public Vector2d subtract(int x, int y){
		return new Vector2d(this.x - x, this.y - y);
	}

	public Vector2d add(int addition){
		return new Vector2d(x + addition, y + addition);
	}
}
