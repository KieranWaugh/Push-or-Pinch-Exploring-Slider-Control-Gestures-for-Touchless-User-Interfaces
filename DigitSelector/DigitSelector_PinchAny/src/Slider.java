import processing.core.PApplet;

import static java.lang.Math.sqrt;
import static processing.core.PApplet.sq;

public class Slider {

    private final PApplet sketch;
    float startX, endX, startY, endY;
    int sections;
    double sectionsDistance;
    double textDistance;
    float[] sectionsArray;
    Line line;
    Circle circle;
    Line[] sectionMarkers;
    float circleLocX, circleLocY;
    int circleLocation;
    int sliderValue = 0;

    public Slider(PApplet sketch, float startX, float endX, float startY, float endY, int sections){
        this.sketch = sketch;
        this.startX = startX;
        this.endX = endX;
        this.startY = startY;
        this.endY = endY;
        this.sections = sections + 1;

        sectionsArray = getSliderSections(this.sections);
        textDistance = sectionsDistance/2;
        sectionMarkers = new Line[this.sections + 1];
        for (int i = 0; i < sectionMarkers.length; i++){

            if (i == sectionMarkers.length - 1){
                sectionMarkers[sectionMarkers.length - 1] = new Line(sketch, endX, endX, 700, 720);
            }else{
                sectionMarkers[i] = new Line(sketch, sectionsArray[i], sectionsArray[i], 700, 720);
            }

        }


        line = new Line(sketch, startX, endX, startY, endY);
        circle = new Circle(sketch, startX, startY, 75, 75);
        circleLocX = startX;
        circleLocY = startY;


    }

    public void display(){

        circle.xCoor = PApplet.constrain(circle.xCoor, startX, endX);
        circle.yCoor = PApplet.constrain(circle.yCoor, startY, endY);

        circleLocation = Math.round(circle.xCoor);

        line.display(0,0,0,16);


        for (int i = 0; i < sectionMarkers.length; i++){
            sectionMarkers[i].display(0,0,0,8);

            if (i != sectionMarkers.length - 1){
                sketch.text(i, (sectionMarkers[i].endX) + (float)textDistance, 675);
            }

        }



        sketch.textSize(128);
        sketch.fill(0,0,0);


        for (int i = 0; i < sectionsArray.length; i++){

            if (i -1 > -1){
               boolean temp = ((circleLocation > sectionsArray[i-1]) && (circleLocation <= sectionsArray[i]));
                if ((circleLocation > sectionsArray[i-1]) && (circleLocation <= sectionsArray[i])){
                    sliderValue = i-1;
                }else if(!temp && circleLocation >= sectionsArray[i]){ //last one in list??
                    sliderValue = i;
                }
            }else{
                sliderValue = 0;
            }

        }

    }

    boolean overCircle(Cursor c, float x, float y, float diameter) {
        float disX = x - c.x;
        float disY = y - c.y;
        return sqrt(sq(disX) + sq(disY)) < diameter / 2;
    }

    float[] getSliderSections(int noSections){
        sectionsDistance =(sqrt(sq(endX-startX) - sq(endY-startY)))/noSections;

        float[] places = new float[noSections];

        for (int i = 0; i < noSections; i++){
            if(i == 0){
                places[0] = startX;
            }else{
                places[i] = places[i-1] + (float)sectionsDistance;
            }
        }

        return places;
    }

}
