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
    Rectangle stationary;
    Rectangle moving;
    Slider slider;
    State state;
    boolean isTraining = true;
    LogData logData;
    static String[] clArgs;
    public static ArrayList<Integer> digits = new ArrayList<>(); //{1,2,3,4,5,6,7,8,9,10,3,7,1,4,8};
    int digitIndex = 0;
    public static int block = 0;

    float pinchCoordX;
    float pinchCoordY;
    float lastCursorX;
    float sliderState;
    boolean unPinched = false;

    enum State {
        NoHands,
        NoPinchDetected,
        PinchDetected
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
        fullScreen(1);
    }

    @Override
    public void setup(){
        frameRate(60);
        leap = new LeapMotion(this);

        digits.add(300);
        digits.add(450);
        digits.add(250);
        digits.add(500);
        digits.add(550);
        digits.add(600);
        digits.add(450);
        digits.add(325);
        digits.add(500);
        digits.add(300);
        digits.add(450);
        digits.add(600);

        cursor = new Cursor(this);

        Collections.shuffle(digits, new Random(Integer.parseInt(clArgs[0]+2))); //+ 2 for gesture type 2
        println(digits);

//        stationary = new Rectangle(this, 1280, 600, digits.get(block), digits.get(block));
//        moving = new Rectangle(this, 1280, 600, 200, 200);
        slider = new Slider(this,600, 1960, 950, 950, 0);
        //slider = new Slider(this,300, 1600, 720, 720, 0);

        // FOR LOGGING /////////////////////
        logData = new LogData(this);
        if (!isTraining){
            loggingData();
        }else{
            stationary = new Rectangle(this, 1280, 600, 400, 400);
            moving = new Rectangle(this, 1280, 600, 200, 200);
        }
        ////////////////////////////////

        state = State.NoHands;
        ellipseMode(CENTER);
        rectMode(CENTER);
        textAlign(CENTER);
        sliderState = slider.startX;


    }

    void loggingData(){
        if(digits.size() >0){
            //text("Use the slider to select the number " + digits.get(digitIndex), displayWidth/(float)2, 400);

            stationary = new Rectangle(this, 1280, 600, digits.get(0), digits.get(0));
        }

        moving = new Rectangle(this, 1280, 600, 200, 200);

        if (digits.size() == 0){
            exit();
        }else{

            logData.PID = Integer.parseInt(clArgs[0]);
            logData.selectionMethod = clArgs[1];
            logData.setting = clArgs[2];
            logData.block = block;
            logData.target = digits.get(digitIndex) * 3;
            logData.sliderSectionLength = slider.sectionsDistance;
            logData.startTime = millis();

            println("Participant number: " + logData.PID);
            println("Gesture: " + logData.selectionMethod);
            println("Task: " + logData.setting);
            println("Block: " + block);
            println("Target: " + logData.target);
        }

    }

    @Override
    public void draw(){
        background(255);

        textSize(50);
        fill(0,0,0);
        if(!isTraining){
            text("Use the slider to match the size of the red square with the black square", displayWidth/(float)2, 250);
        }else{
            text("PRACTICE: Use the slider to match the size of the red square with the black square", displayWidth/(float)2, 250);
        }
        stationary.display(0,0,0, 255);
        moving.display(255,0,0, 200);
        slider.display();
        cursor.update(leap);

        logData.addFrame(new Frame(FrameCategory.drawCalled, state, cursor.x, cursor.y, slider.circle.xCoor, slider.sliderValue));


        switch (state){
            case NoHands:
                if(leap.countHands() >=1){
                    logData.addFrame(new Frame(FrameCategory.StateTransition, state, "Transitioning from states NoHands to NoPinchDetected", cursor.x, cursor.y, slider.circle.xCoor,slider.sliderValue));
                    //addLogAction(state, "Transitioning from states NoHands to NoPinchDetected", new Data(Data.dataTypes.CursorPosition, leap.countHands()));
                    state = State.NoPinchDetected;


                }
                slider.circle.display(255, 0, 0);
                cursor.update(leap);
                break;
            case NoPinchDetected:

                cursor.isPinchingOver = false;

                if(leap.countHands() <1){
                    logData.addFrame(new Frame(FrameCategory.StateTransition, state, "Transitioning from states NoPinchDetected to NoHands", cursor.x, cursor.y, slider.circle.xCoor,slider.sliderValue));
                    //addLogAction(state, "Transitioning from states NoPinchDetected to NoHands", new Data(Data.dataTypes.CursorPosition, leap.countHands()));
                    state = State.NoHands;
                }

                if (cursor.isPinchingTest(leap)){
                    //addLogAction(state, "Pinch detected", new Data(Data.dataTypes.PinchDetection, cursor.pinchStrength));
                    logData.addFrame(new Frame(FrameCategory.gestureDetected, state, "Gesture detected", cursor.x, cursor.y, slider.circle.xCoor,slider.sliderValue));
                    logData.addFrame(new Frame(FrameCategory.StateTransition, state, "Transitioning from states NoPinchDetected to PinchDetected", cursor.x, cursor.y, slider.circle.xCoor,slider.sliderValue));
                    //addLogAction(state, "Transitioning from states NoPinchDetected to PinchDetected", new Data(Data.dataTypes.PinchDetection, cursor.pinchStrength));
                    pinchCoordX = cursor.x;
//                    println("pinch: " + pinchCoordX);
//                    println("slider: " + slider.circle.xCoor);
                    pinchCoordY = cursor.y;
                    lastCursorX = cursor.x;
                    state = State.PinchDetected;
                }

                if (slider.overCircle(cursor, slider.circle.xCoor, slider.circle.yCoor, 75)){
                    slider.circle.colour(0, 68, 255);
                    cursor.update(leap);
                    logData.addFrame(new Frame(FrameCategory.cursorOverSlider, state, "Cursor is over circle without pinch detection", cursor.x, cursor.y, slider.circle.xCoor,slider.sliderValue));
                    //addLogAction(state, "Cursor is over circle without pinch detection", new Data(Data.dataTypes.CursorOverSlider, cursor.x, cursor.y));
                }else{
                    slider.circle.display(255, 0, 0);
                    cursor.update(leap);
                }

                break;
            case PinchDetected:



                if (!cursor.isPinchingTest(leap)){

                    logData.addFrame(new Frame(FrameCategory.StateTransition, state, "Transitioning from states PinchDetected to NoPinchDetected", cursor.x, cursor.y, slider.circle.xCoor,slider.sliderValue));
                    logData.addFrame(new Frame(FrameCategory.gestureNoLongerDetected, state, "Gesture no longer detected", cursor.x, cursor.y, slider.circle.xCoor,slider.sliderValue));
                    state = State.NoPinchDetected;
                    unPinched = true;
                    sliderState = slider.circle.xCoor;
                    //println("set sliderstate: " + sliderState);


                }


                if(unPinched){
                    //println("re setting slider pos: " + sliderState);
                    //println("loc: " + slider.circle.xCoor);
                    slider.circle.xCoor = sliderState;
                    unPinched = false;
                }else{
                    if (lastCursorX < cursor.x){ //right
                        //println("moving right");
                        slider.circle.xCoor = sliderState + Math.abs((cursor.x - pinchCoordX) );
                        slider.circle.xCoor = constrain(slider.circle.xCoor, slider.startX, slider.endX);
                        var boxHeight = slider.circle.xCoor / 3;

                        moving.size(boxHeight, boxHeight, 255,0,0, 200);

                    }else{//left
                        //println("moving left");
                        slider.circle.xCoor = sliderState - Math.abs((pinchCoordX - cursor.x));
                        slider.circle.xCoor = constrain(slider.circle.xCoor, slider.startX, slider.endX);
                        var boxHeight = slider.circle.xCoor / 3;

                        moving.size(boxHeight, boxHeight, 255,0,0, 200);

                    }
                }

                slider.circle.xCoor = constrain(slider.circle.xCoor, slider.startX, slider.endX);






                slider.circle.colour(0, 68, 255);
                cursor.update(leap);




        }

    }

    @Override
    public void keyPressed(){
        if(key == ' '){
            try {
                //addLogAction(state, "Participant instructed that the task is complete", null);


                if (isTraining){
                    cursor.isPinchingOver = false;
                    state = State.NoHands;
                    slider.circle.xCoor = slider.startX;
                    isTraining = false;
                    sliderState = slider.startX;
                    loggingData();
                }else{
                    if (digits.size() >= 1){
                        logData.addFrame(new Frame(FrameCategory.BlockCompleted, state, "Block " + block + " complete", cursor.x, cursor.y, slider.circle.xCoor,slider.sliderValue));
                        logData.export();
                        digits.remove(digitIndex);
                        cursor.isPinchingOver = false;
                        state = State.NoHands;
                        sliderState = slider.startX;
                        slider.circle.xCoor = slider.startX;
                        if(digits.size() != 0){
                            block++;
                        }

                        println("Position: " + slider.sliderValue + "\n");
                        loggingData();
                    }else{
                        logData.addFrame(new Frame(FrameCategory.TaskCompleted, state, "Task completed", cursor.x, cursor.y, slider.circle.xCoor,slider.sliderValue));

                    }

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

