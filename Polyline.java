import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

/**
 * A multi-segment Shape, with straight lines connecting "joint" points -- (x1,y1) to (x2,y2) to (x3,y3) ...
 * 
 * @author Chris Bailey-Kellogg, Dartmouth CS 10, Spring 2016
 * @author CBK, updated Fall 2016
 */
public class Polyline implements Shape {
	private List<Segment> segments;
	private Color color;

	/**
	 * An "empty" polyline, with only one point set so far
	 */
	public Polyline(int x, int y, Color color) {
		this.segments = new ArrayList<>();
		segments.add(new Segment(x, y, color));
		this.color = color;
	}

	/**
	 * A polyline defined by segments
	 */
	public Polyline(List<Segment> segments, Color color) {
		this.color = color;
		this.segments = segments;
	}

	public int size() {
		return segments.size();
	}

	/**
	 * add a another point to the segment list
	 */
	public void addJoint(int x, int y) {
		segments.get(segments.size()-1).setEnd(x, y); // extend current last segment
		segments.add(new Segment(x, y, color)); // add new last segment to extend from
	}

	@Override
	public void moveBy(int dx, int dy) {
		for(Segment segment : segments) {
			segment.moveBy(dx, dy);
		}
	}

	@Override
	public Color getColor() {
		return color;
	}

	@Override
	public void setColor(Color color) {
		this.color = color;
		for(Segment segment : segments) {
			segment.setColor(color);
		}
	}

	@Override
	public boolean contains(int x, int y) {
		for(Segment segment : segments) {
			if(segment.contains(x, y)) return true;
		}
		return false;
	}

	@Override
	public void draw(Graphics g) {
		for(Segment segment : segments) {
			segment.draw(g);
		}
	}

	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append("polyline ");
		for(Segment segment : segments) {
			result.append(segment).append(" ");
		}
		return result.toString();
	}
}
