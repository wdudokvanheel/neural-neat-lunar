package nl.wdudokvanheel.neat.lunar.game.model;

public class Rectangle{
	public double x;
	public double y;
	public double width;
	public double height;

	public Rectangle(double x, double y, double width, double height){
		setRect(x, y, width, height);
	}

	public double getX(){
		return x;
	}

	public double getY(){
		return y;
	}

	public double getWidth(){
		return width;
	}

	public double getHeight(){
		return height;
	}

	public double getMinX(){
		return getX();
	}

	public double getMinY(){
		return getY();
	}

	public double getMaxX(){
		return getX() + getWidth();
	}

	public double getMaxY(){
		return getY() + getHeight();
	}

	public boolean contains(double x, double y){
		double w = getWidth();
		double h = getHeight();
		if(w < 0 || h < 0){
			return false;
		}

		double x0 = getX();
		double y0 = getY();
		return (x >= x0) && (y >= y0) && (x - x0 < w) && (y - y0 < h);
	}

	public boolean intersects(double x, double y, double w, double h){
		if(w <= 0 || h <= 0){
			return false;
		}

		double x0 = getX();
		double y0 = getY();
		return (x + w > x0) && (y + h > y0) && (x < x0 + getWidth()) && (y < y0 + getHeight());
	}

	public void setRect(double x, double y, double width, double height){
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}
}
