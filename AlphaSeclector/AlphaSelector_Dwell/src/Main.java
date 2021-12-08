/*
    Touchless Slider Experiment
    Digit Slider
    @author kieran Waugh - 2021
 */

import processing.core.PApplet;
import de.voidplus.leapmotion.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class Main extends PApplet {

    LeapMotion leap;
    Cursor cursor;
    Rectangle rectangle;
    Slider slider;
    Arc arc;
    State state;
    LogData logData;
    static String[] clArgs;
    public static ArrayList<String> digits = new ArrayList<>(); //{1,2,3,4,5,6,7,8,9,10,3,7,1,4,8};
    int digitIndex = 0;
    public static int block = 0;
    boolean firstTouch = true; // for dwell
    int startTime; // for dwell
    int timeThreshold = 800;
    int timeElapsed = 0;
    float start = 0;
    float endTime = 0;
    float lastTime = 0;
    float end = (float)0.45;
    boolean dwell = false;

    enum State {
        NoHands,
        NoDwellDetected,
        DwellDetected
    }

    enum FrameCategory{
        drawCalled,
        gestureDetected,
        gestureNoLongerDetected,
        sliderMoved,
        cursorOverSlider,
        StateTransition,
        BlockCompleted,
        TaskCompleted
    }

    public void settings(){
        fullScreen(2);
    }

    @Override
    public void setup(){
        leap = new LeapMotion(this);

        digits.add("Z");
        digits.add("J");
        digits.add("R");
        digits.add("J");
        digits.add("T");
        digits.add("Q");
        digits.add("D");
        digits.add("R");
        digits.add("A");
        digits.add("X");
        digits.add("E");
        digits.add("V");
        digits.add("T");
        digits.add("B");
        digits.add("M");

        cursor = new Cursor(this);
        arc = new Arc(this, 40, 40);
        rectangle = new Rectangle(this, 300, 300, 100, 100);
        slider = new Slider(this,600, 1960, 720, 720, 25);
        //slider = new Slider(this,300, 1600, 720, 720, 10);


        Collections.shuffle(digits, new Random(Integer.parseInt(clArgs[0]) + 2)); // plus 2 for 2nd gesture type
        println(digits);

        // FOR LOGGING /////////////////////
        loggingData();
        ////////////////////////////////

        state = State.NoHands;
        ellipseMode(CENTER);
        rectMode(CENTER);
        textAlign(CENTER);


    }

    void loggingData(){
        logData = new LogData(this);
        logData.PID = Integer.parseInt(clArgs[0]);
        logData.selectionMethod = clArgs[1];
        logData.setting = clArgs[2];
        logData.target = digits.get(digitIndex);
        logData.startTime = millis();

        println("Participant number: " + logData.PID);
        println("Gesture: " + logData.selectionMethod);
        println("Task: " + logData.setting);
        println("Block: " + block);
    }

    @Override
    public void draw(){
        background(255);

        textSize(50);
        fill(0,0,0);
        if(digits.size() >0){
            text("Use the slider to select the number " + digits.get(digitIndex), displayWidth/(float)2, 250);
        }

        slider.display();
        cursor.update(leap);

        logData.addFrame(new Frame(FrameCategory.drawCalled, state, cursor.x, cursor.y, slider.circle.xCoor, slider.sliderValue));


        switch (state){
            case NoHands:
                if(leap.countHands() >=1){
                    logData.addFrame(new Frame(FrameCategory.StateTransition, state, "Transitioning from states NoHands to NoPinchDetected", cursor.x, cursor.y, slider.circle.xCoor,slider.sliderValue));
                    //addLogAction(state, "Transitioning from states NoHands to NoPinchDetected", new Data(Data.dataTypes.CursorPosition, leap.countHands()));
                    state = State.NoDwellDetected;


                }
                slider.circle.display(255, 0, 0);
                cursor.update(leap);
                break;
            case NoDwellDetected:

                cursor.isPinchingOver = false;

                if(leap.countHands() <1){
                    logData.addFrame(new Frame(FrameCategory.StateTransition, state, "Transitioning from states NoDwellDetected to NoHands", cursor.x, cursor.y, slider.circle.xCoor,slider.sliderValue));
                    //addLogAction(state, "Transitioning from states NoPinchDetected to NoHands", new Data(Data.dataTypes.CursorPosition, leap.countHands()));
                    state = State.NoHands;
                }

                if (timeThreshold <= timeElapsed){
                    dwell = true;
                    timeElapsed = 0;
                    cursor.isGestured = true;
                    cursor.update(leap);
                    //println("dwelled");
                    //addLogAction(state, "Pinch detected", new Data(Data.dataTypes.PinchDetection, cursor.pinchStrength));
                    logData.addFrame(new Frame(FrameCategory.gestureDetected, state, "Gesture detected", cursor.x, cursor.y, slider.circle.xCoor,slider.sliderValue));
                    logData.addFrame(new Frame(FrameCategory.StateTransition, state, "Transitioning from states NoDwellDetected to DwellDetected", cursor.x, cursor.y, slider.circle.xCoor,slider.sliderValue));
                    //addLogAction(state, "Transitioning from states NoPinchDetected to PinchDetected", new Data(Data.dataTypes.PinchDetection, cursor.pinchStrength));
                    state = State.DwellDetected;
                }

                if (slider.overCircle(cursor, slider.circle.xCoor, slider.circle.yCoor, 75)){
                    //overCirc = true; // for dwell
                    slider.circle.colour(0, 68, 255);
                    cursor.update(leap);

                    if(firstTouch){
                        startTime = millis();
                        lastTime = millis();
                        endTime = millis() + timeThreshold;
                        firstTouch = false;
                    }else{
                        timeElapsed = millis() - startTime;

                    }

                    if (timeThreshold > timeElapsed){
                        arc.display(cursor, start, end);

                        end += 0.45 * (millis() - lastTime);
                        lastTime = millis();
                        println(end);
                    }


                    logData.addFrame(new Frame(FrameCategory.cursorOverSlider, state, "Cursor is over circle without Dwell detection", cursor.x, cursor.y, slider.circle.xCoor,slider.sliderValue));
                    //addLogAction(state, "Cursor is over circle without pinch detection", new Data(Data.dataTypes.CursorOverSlider, cursor.x, cursor.y));
                }else{
                    dwell = false;
                    firstTouch = true;
                    //end = 7;
                    end = (float)0.45;
                    slider.circle.display(255, 0, 0);
                    cursor.update(leap);
                }

                break;
            case DwellDetected:



                if (!slider.overCircle(cursor, slider.circle.xCoor, slider.circle.yCoor, 75)){
                    //addLogAction(state, "Pinch not detected", new Data(Data.dataTypes.PinchDetection, cursor.pinchStrength));
                    logData.addFrame(new Frame(FrameCategory.StateTransition, state, "Transitioning from states PinchDetected to NoPinchDetected", cursor.x, cursor.y, slider.circle.xCoor,slider.sliderValue));
                    logData.addFrame(new Frame(FrameCategory.gestureNoLongerDetected, state, "Gesture no longer detected", cursor.x, cursor.y, slider.circle.xCoor,slider.sliderValue));
                    //addLogAction(state, "Transitioning from states PinchDetected to NoPinchDetected", new Data(Data.dataTypes.PinchDetection, cursor.pinchStrength));
                    dwell = false;
                    cursor.isGestured = false;
                    cursor.update(leap);
                    state = State.NoDwellDetected;
                }


                if (slider.overCircle(cursor, slider.circle.xCoor, slider.circle.yCoor, 75) ){
                    //cursor.isPinchingOver = true;
                    slider.circle.xCoor = cursor.x;
                    slider.circle.xCoor = constrain(slider.circle.xCoor, slider.startX, slider.endX);

                    slider.circle.colour(0, 68, 255);
                    cursor.update(leap);

                    if((slider.overCircle(cursor, slider.circle.xCoor, slider.circle.yCoor, 75))){
                        //addLogAction(state, "Pinch detected on slider", new Data(Data.dataTypes.SliderMoved, cursor.x, slider.sliderValue, slider.circle.xCoor));
                        logData.addFrame(new Frame(FrameCategory.sliderMoved, state, "Gesture detected on slider", cursor.x, cursor.y, slider.circle.xCoor,slider.sliderValue));
                    }else if(cursor.isPinchingOver){
                        logData.addFrame(new Frame(FrameCategory.sliderMoved, state, "Gesture continued away from slider", cursor.x, cursor.y, slider.circle.xCoor,slider.sliderValue));
                        //addLogAction(state, "Cursor moved from slider but continued pinch", new Data(Data.dataTypes.SliderMoved, cursor.x, cursor.y, slider.sliderValue, slider.circle.xCoor));
                    }


                }else{
                    slider.circle.display(255, 0, 0);
                    logData.addFrame(new Frame(FrameCategory.gestureDetected, state, "Gesture detected not on slider", cursor.x, cursor.y, slider.circle.xCoor,slider.sliderValue));
                    cursor.isPinchingOver = false;
                    cursor.update(leap);
                }


        }

    }

    @Override
    public void keyPressed(){
        if(key == TAB){
            try {
                //addLogAction(state, "Participant instructed that the task is complete", null);


                if (digits.size() > 1){
                    logData.addFrame(new Frame(FrameCategory.BlockCompleted, state, "Block " + block + " complete", cursor.x, cursor.y, slider.circle.xCoor,slider.sliderValue));
                    logData.export();
                    digits.remove(digitIndex);
                    cursor.isPinchingOver = false;
                    state = State.NoHands;
                    slider.circle.xCoor = slider.startX;
                    block++;
                    loggingData();
                }else{
                    logData.addFrame(new Frame(FrameCategory.TaskCompleted, state, "Task completed", cursor.x, cursor.y, slider.circle.xCoor,slider.sliderValue));
                    logData.export();
                }

                //exit();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    public static void main(String[] args) {
        PApplet.main("Main");
        clArgs = args;
    }

}

