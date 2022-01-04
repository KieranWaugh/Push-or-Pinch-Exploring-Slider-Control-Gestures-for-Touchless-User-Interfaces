import processing.core.PApplet;

public class Line {

    private PApplet sketch;
    float startX, endX, startY, endY;

    Line(PApplet sketch, float startX, float endX, float startY, float endY){
        this.sketch = sketch;
        this.startX = startX;
        this.endX = endX;
        this.startY = startY;
        this.endY = endY;
    }

    void display(int r, int g, int b, int weight){
        sketch.stroke(r,g,b);
        sketch.strokeWeight(weight);  // Thicker
        sketch.line(startX,startY,endX,endY);
    }


}
