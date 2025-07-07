package nl.wdudokvanheel.neat.lunar.game.model;

public class Line{
	public Vector2d start;
	public Vector2d end;

	public Line(){
		this(0, 0, 0, 0);
	}

	public Line(int sourceX, int sourceY, int targetX, int targetY){
		this(new Vector2d(sourceX, sourceY), new Vector2d(targetX, targetY));
	}

	public Line(double sourceX, double sourceY, double targetX, double targetY){
		this(new Vector2d(sourceX, sourceY), new Vector2d(targetX, targetY));
	}

	public Line(Vector2d start, Vector2d end){
		this.start = start;
		this.end = end;
	}

	public boolean intersects(Rectangle rect){
		// Check if any of the line endpoints are inside the rectangle
		if(rect.contains(start.x, start.y) || rect.contains(end.x, end.y)){
			return true;
		}

		// Check for intersections between the line and each of the rectangle edges
		Line[] rectEdges = {
				new Line(rect.getMinX(), rect.getMinY(), rect.getMaxX(), rect.getMinY()),
				new Line(rect.getMaxX(), rect.getMinY(), rect.getMaxX(), rect.getMaxY()),
				new Line(rect.getMaxX(), rect.getMaxY(), rect.getMinX(), rect.getMaxY()),
				new Line(rect.getMinX(), rect.getMaxY(), rect.getMinX(), rect.getMinY())
		};

		for(Line rectEdge : rectEdges){
			if(this.intersectsLine(rectEdge)){
				return true;
			}
		}

		return false;
	}


	public boolean intersectsLine(Line other){
		double denom = (other.end.y - other.start.y) * (this.end.x - this.start.x) - (other.end.x - other.start.x) * (this.end.y - this.start.y);

		if(denom == 0){
			return false; // Lines are parallel
		}

		double ua = ((other.end.x - other.start.x) * (this.start.y - other.start.y) - (other.end.y - other.start.y) * (this.start.x - other.start.x)) / denom;
		double ub = ((this.end.x - this.start.x) * (this.start.y - other.start.y) - (this.end.y - this.start.y) * (this.start.x - other.start.x)) / denom;

		return ua >= 0 && ua <= 1 && ub >= 0 && ub <= 1;
	}

	public boolean intersectsLine(double leftX, double topY, double rightX, double topY1){
		return intersectsLine(new Line(leftX, topY, rightX, topY1));
	}
}
