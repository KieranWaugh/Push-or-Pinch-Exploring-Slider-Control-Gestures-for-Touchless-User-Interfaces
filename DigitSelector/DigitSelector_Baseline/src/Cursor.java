import com.leapmotion.leap.InteractionBox;
import com.leapmotion.leap.Leap;
import com.leapmotion.leap.Vector;
import de.voidplus.leapmotion.*;
//import processing.core.PVector;
import processing.core.PApplet;
import processing.core.PVector;

import java.util.ArrayList;

import static processing.core.PApplet.println;

class Cursor{
    private final PApplet sketch;
    float x,y;
    public boolean isPinchingOver = false;
    float prevX, prevY;
    public float avgZ;
    public ArrayList<Float> zvalues = new ArrayList<>();
    Hand frontHand;
    boolean isGesture = false;


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
        //InteractionBox interactionBox = new InteractionBox();
        //Hand hand = leap.getFrontHand(); //Why isnt this working!
        //for (Hand hand : leap.getHands()) {

        Hand right = leap.getRightHand();
        Hand left = leap.getLeftHand();
        frontHand = leap.getRightHand(); // initialise with right hand


        if(leap.countHands() >=1) {

            //println(leap.getController().frame().);
            if (right.getStabilizedPosition().z >= left.getStabilizedPosition().z) {
                frontHand = right;
            } else {
                frontHand = left;
            }


            if(!isGesture){
                zvalues.add(frontHand.getFrontFinger().getPosition().z);
            }

            //println(leap.getDevices().get(0).getRaw().distanceToBoundary(frontHand.getFrontFinger().getRaw().tipPosition()));
            //println(interactionBox.center());
            //println("Z axis: " + frontHand.getFrontFinger().getPosition().z);

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

        for (Hand hand : leap.getHands()){

            if (isPinching){
                if (hand.getPinchStrength() < 0.8){
                    pinchStrength = hand.getPinchStrength();
                    isPinching = false;

                }
            }else{
                if (hand.getPinchStrength() > 0.8){
                    pinchStrength = hand.getPinchStrength();
                    isPinching = true;

                }

            }
        }
        return isPinching;
    }

    boolean isTapTest(LeapMotion leap){
        if(leap.hasHands()){
            println("z: " + frontHand.getFrontFinger().getPosition().z);
            if( frontHand.getFrontFinger().getPosition().z > (avgZ + 5)){
                isGesture = true;
                isPinching = true;
                println("GESTURE");
                return true;
            }else{
                isPinching = false;
                return false;
            }
        }else{
            isPinching = false;
            isGesture = false;
            return false;
        }

    }

}
