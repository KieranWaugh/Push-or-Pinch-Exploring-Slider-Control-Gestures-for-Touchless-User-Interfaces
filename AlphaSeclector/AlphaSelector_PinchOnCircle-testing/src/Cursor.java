import com.leapmotion.leap.Leap;
import com.leapmotion.leap.Vector;
import de.voidplus.leapmotion.*;
//import processing.core.PVector;
import processing.core.PApplet;
import processing.core.PVector;

import static processing.core.PApplet.println;

class Cursor{
    private final PApplet sketch;
    float x,y;
    public boolean isPinchingOver = false;
    float prevX, prevY;
    MovingAverageFilter pinchFilter = new MovingAverageFilter(0.9, 10);

    boolean isPinching = false;
    float pinchStrength = 0;

    Cursor(PApplet sketch){
        this.sketch = sketch;

    }

    public void display(boolean isIndex, float x, float y, int r, int g, int b){

        if (isIndex){
            sketch.fill(r,g,b);
            sketch.noStroke();

        }else{
            sketch.fill(105,105,105);
            sketch.noStroke();
        }
        sketch.ellipse(x,y,30,30);
    }

    public void update(LeapMotion leap){
        //Hand hand = leap.getFrontHand(); //Why isnt this working!
        //for (Hand hand : leap.getHands()) {

        Hand right = leap.getRightHand();
        Hand left = leap.getLeftHand();
        Hand frontHand = leap.getRightHand(); // initialise with right hand

        if(leap.countHands() >=1) {


            if (right.getStabilizedPosition().z >= left.getStabilizedPosition().z) {
                frontHand = right;
            } else {
                frontHand = left;
            }


            for (Finger finger : frontHand.getFingers()) {
                if (finger.getType() == 1) {
                    PVector temp = frontHand.getStabilizedPosition().normalize();
                    x = frontHand.getStabilizedPosition().x;
                    y = frontHand.getStabilizedPosition().y;
                    //x = hand.getFinger(4).getStabilizedPosition().x;
                    //y = hand.getFinger(4).getStabilizedPosition().y;
                    display(true, x, y, 255, 200, 200);


                    if (isPinching) {
                        display(true, x, y, 255, 121, 121);

                        if (isPinchingOver) {
                            display(true, prevX, prevY, 255, 121, 121);
                        }
                    }

                    prevX = frontHand.getStabilizedPosition().x;
                    prevY = frontHand.getStabilizedPosition().y;

                    //display(true, hand.getFinger(4).getStabilizedPosition().x, hand.getFinger(4).getStabilizedPosition().y);
                } else {
                    prevX = frontHand.getStabilizedPosition().x;
                    prevY = frontHand.getStabilizedPosition().y;
                    //display(false, finger.getStabilizedPosition().x, finger.getStabilizedPosition().y);
                }

            }
            if (frontHand.isRight()) {
                sketch.stroke(255, 0, 0);
            } else {
                sketch.stroke(0, 255, 0);
            }
            isPinchingTest(leap);

//            display();
        }
        //}
    }

    void changeColour(int r, int b, int g){

    }

    boolean isPinchingTest(LeapMotion leap){
        float pinchDistance = getPinchDistance(leap);

        // Keeping naming for consistency, but this measures distance between fingertips
        pinchStrength = (float) pinchFilter.update(pinchDistance);
        //println(pinchStrength);

        isPinching = pinchStrength <= 40;

        return isPinching;
    }

    float getPinchDistance(LeapMotion leap) {
        if (leap.getHands().isEmpty()) {
            return Float.MAX_VALUE;
        }

        // Get thumb and index finger
        Finger thumb = null;
        Finger index = null;


        for (Hand hand : leap.getHands()) {
            for (Finger finger : hand.getFingers()) {
                //Finger.Type type = finger.type;
                com.leapmotion.leap.Finger.Type type = finger.getRaw().type();

                if (type == com.leapmotion.leap.Finger.Type.TYPE_THUMB) {
                    thumb = finger;
                } else if (type == com.leapmotion.leap.Finger.Type.TYPE_INDEX) {
                    index = finger;
                }
            }
        }

        if (thumb == null || index == null) {
            return Float.MAX_VALUE;
        }

        // Get Euclidean distance between fingertips
        Vector thumbTip = thumb.getRaw().stabilizedTipPosition();
        Vector indexTip = index.getRaw().stabilizedTipPosition();

        return thumbTip.distanceTo(indexTip);
    }

}
