import processing.core.PApplet;

public class Circle {
    private final PApplet sketch;
    public float xCoor, yCoor, height, width;

    public Circle(PApplet sketch, float xCoor, float yCoor, float height, float width){
        this.sketch = sketch;
        this.xCoor = xCoor;
        this.yCoor = yCoor;
        this.height = height;
        this.width = width;
    }

    public void display(int r, int b, int g){
        sketch.fill(r,b,g);
        sketch.noStroke();
        sketch.ellipse(xCoor, yCoor, height, width);
    }

    public void colour(int a, int b, int c){
        display(a, b, c);
    }

//    public void changeLocation(float x, float y, int r, int b, int g){
//        sketch.fill(r,b,g);
//        sketch.noStroke();
//        sketch.ellipse(x, y, height, width);
//    }
}
