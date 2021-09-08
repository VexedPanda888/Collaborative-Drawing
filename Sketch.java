import java.awt.*;
import java.util.*;
import java.util.List;

public class Sketch {
    private List<Shape> shapes;

    public Sketch() {
        shapes = new ArrayList<>();
    }

    public void draw(Graphics g) {
        if(shapes != null) {
            for(Shape shape : shapes) {
                shape.draw(g);
            }
        }
    }

    public Integer size() {
        return shapes.size();
    }

    public Shape getShape(int id) { return shapes.get(id); }

    public void addShape(Shape shape) {
        shapes.add(shape);
    }

    public void addShape(int id, Shape shape) { shapes.add(id, shape); }

    public void removeShape(int id) {
        shapes.remove(id);
    }

    /**
     * Helper method
     * Iterates through the parts of the message.
     * Depending on the message, executes the proper method(s) for the editor (client).
     */
    public synchronized void  handleMessage(String msg) {
        String[] parsedMsg = msg.split(" ");
        switch (parsedMsg[0]) { // index 0 of message holds mode information
            case "DRAW" -> {
                addShape(constructShape(1, parsedMsg));
            }
            case "MOVE" -> {
                if(!parsedMsg[2].equals("start")) {
                    int id = Integer.parseInt(parsedMsg[1]);
                    int dx = Integer.parseInt(parsedMsg[2]);
                    int dy = Integer.parseInt(parsedMsg[3]);
                    getShape(id).moveBy(dx, dy); // move the shape
                }
            }
            case "RECOLOR" -> {
                int id = Integer.parseInt(parsedMsg[1]);
                Color color = new Color(Integer.parseInt(parsedMsg[2]));
                getShape(id).setColor(color);
            }
            case "DELETE" -> {
                int id = Integer.parseInt(parsedMsg[1]);
                removeShape(id);
            }
        }
    }

    /**
     * Helper method
     * Iterates through the parts of the message.
     * When it reaches a shape, it constructs that shape and then jumps the iteration past that shape's information.
     *
     * @param worldMessage has all the information about the sketch
     */
    public synchronized void handleWorldMessage(String worldMessage) {
        String[] parsedWorld = worldMessage.split(" "); // parse message
        for(int i = 1; i < parsedWorld.length; i++) { // default iterate through every part of the message
            System.out.println("handling world: " + parsedWorld[i]);
            int id = Integer.parseInt(parsedWorld[i]); // id of shape

            Shape shape = constructShape(i+1, parsedWorld); // construct the shape
            System.out.println("created: " + shape.toString());

            // jump the index over the shape
            if(shape instanceof Polyline) {
                int numSegments = ((Polyline)shape).size();
                i += numSegments * 7 + 1; // jump depending on the polyline size
            }
            else {
                i += 6; // jump by a constant amount for other shapes
            }

            addShape(id, shape); // add the shape to the sketch
        }
    }

    /**
     * Helper method
     * Iterates through a message beginning at a specified index to construct a shape.
     *
     * @param start the index of parsedMsg where the name of the shape is
     * @param parsedMsg the entire parsed message from which the shape should be constructed
     * @return a shape
     */
    private static Shape constructShape(int start, String[] parsedMsg) {
        if(parsedMsg[start].equals("polyline")) {
            List<Segment> segments = new ArrayList<>(); // list of segments in polyline
            Color color = Color.getColor(parsedMsg[start + 6]); // color of polyline from first segment

            int i = start + 1; // begin immediately after the shape type
            while(i < parsedMsg.length && parsedMsg[i].equals("segment")) { // stop when no more segments to add
                segments.add((Segment)constructShape(i, parsedMsg)); // create and add a segment
                i += 6; // jump over the current segment
            }

            return new Polyline(segments, color); // create and return the polyline
        }
        else {
            int x1 = Integer.parseInt(parsedMsg[start+1]);
            int y1 = Integer.parseInt(parsedMsg[start+2]);
            int x2 = Integer.parseInt(parsedMsg[start+3]);
            int y2 = Integer.parseInt(parsedMsg[start+4]);
            Color color = new Color(Integer.parseInt(parsedMsg[start+5]));
            // create the shape
            return switch (parsedMsg[start]) {
                case "ellipse" -> new Ellipse(x1, y1, x2, y2, color);
                case "rectangle" -> new Rectangle(x1, y1, x2, y2, color);
                case "segment" -> new Segment(x1, y1, x2, y2, color);
                default -> null;
            };
        }
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("sketch ");
        for(int id = 0; id < shapes.size(); id++) {
            result.append(id).append(" ").append(shapes.get(id)).append(" ");
        }
        return result.toString();
    }
}
