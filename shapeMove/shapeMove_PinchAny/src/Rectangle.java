import processing.core.PApplet;
import de.voidplus.leapmotion.*;

class Rectangle{

    float x, y , Rheight, Rwidth;
    private PApplet sketch;

    Rectangle(PApplet sketch, int x, int y, int Rheight, int Rwidth){
        this.sketch = sketch;
        this.x = x;
        this.y = y;
        this.Rheight = Rheight;
        this.Rwidth = Rwidth;
    }

    void display(int r, int g, int b, int a){
        sketch.fill(r,b,g, a);
        sketch.noStroke();
        sketch.rect(x, y, Rheight, Rwidth);

    }

//    void colour(int a, int b, int c){
//        display(a, b, c);
//    }

    void size(float rheight, float rwidth, int r, int g, int b, int a){
        Rheight = rheight;
        Rwidth = rwidth;
        display(r,g,b,a);
    }

    boolean overRect(Cursor c) {
        if (c.x >= x-Rwidth/2 && c.x <= x+Rwidth/2 &&
                c.y >= y-Rheight/2 && c.y <= y+Rheight/2) {
            return true;
        }
        else {
            return false;
        }
    }

}