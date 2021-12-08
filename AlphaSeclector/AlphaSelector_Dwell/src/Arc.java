import processing.core.PApplet;

import static processing.core.PApplet.radians;

public class Arc {

    private final PApplet sketch;
    public float height, width;

    public Arc(PApplet sketch, float height, float width){
        this.sketch = sketch;
        this.height = height;
        this.width = width;
    }

    public void display(Cursor c, float start, float stop){
        sketch.noFill();
        sketch.stroke(0,0,0);
        sketch.strokeWeight(6);
        sketch.arc(c.x, c.y, height, width, radians(start), radians(stop));
    }


}
