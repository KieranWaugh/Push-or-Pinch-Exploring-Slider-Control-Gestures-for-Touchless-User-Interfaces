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
    State state;
    LogData logData;
    boolean isTraining = true;
    static String[] clArgs;
    public static ArrayList<Integer> digits = new ArrayList<>(); //{1,2,3,4,5,6,7,8,9,10,3,7,1,4,8};
    int digitIndex = 0;
    public static int block = 0;

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
        fullScreen(1  );
    }

    @Override
    public void setup(){
        frameRate(60);
        leap = new LeapMotion(this);

        digits.add(1);
        digits.add(2);
        digits.add(3);
        digits.add(4);
        digits.add(5);
        digits.add(7);
        digits.add(7);
        digits.add(8);
        digits.add(6);
        digits.add(8);
        digits.add(5);
        digits.add(4);

        cursor = new Cursor(this);
        rectangle = new Rectangle(this, 300, 300, 100, 100);
        slider = new Slider(this,600, 1960, 720, 720, 9);
        //slider = new Slider(this,300, 1600, 720, 720, 10);


        Collections.shuffle(digits, new Random(Integer.parseInt(clArgs[0]+1))); //+ 1 for gesture type 1
        println(digits);

        // FOR LOGGING /////////////////////
        logData = new LogData(this);
        if(!isTraining){
            loggingData();
        };
        ////////////////////////////////

        state = State.NoHands;
        ellipseMode(CENTER);
        rectMode(CENTER);
        textAlign(CENTER);


    }

    void loggingData(){

        if (digits.size() == 0){
            exit();
        }else{
            logData.PID = Integer.parseInt(clArgs[0]);
            logData.selectionMethod = clArgs[1];
            logData.setting = clArgs[2];
            logData.target = digits.get(digitIndex);
            logData.block = block;
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
        if(digits.size() >0){
            if(!isTraining){
                text("Use the slider to select the number " + digits.get(digitIndex), displayWidth/(float)2, 250);
            }else{
                text("PRACTICE: Use the slider to select a number", displayWidth/(float)2, 250);
            }
        }

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
                    //addLogAction(state, "Pinch not detected", new Data(Data.dataTypes.PinchDetection, cursor.pinchStrength));
                    logData.addFrame(new Frame(FrameCategory.StateTransition, state, "Transitioning from states PinchDetected to NoPinchDetected", cursor.x, cursor.y, slider.circle.xCoor,slider.sliderValue));
                    logData.addFrame(new Frame(FrameCategory.gestureNoLongerDetected, state, "Gesture no longer detected", cursor.x, cursor.y, slider.circle.xCoor,slider.sliderValue));
                    //addLogAction(state, "Transitioning from states PinchDetected to NoPinchDetected", new Data(Data.dataTypes.PinchDetection, cursor.pinchStrength));
                    state = State.NoPinchDetected;
                }


                if (slider.overCircle(cursor, slider.circle.xCoor, slider.circle.yCoor, 75) ||  cursor.isPinchingOver){
                    cursor.isPinchingOver = true;
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
        if(key == ' '){
            try {
                //addLogAction(state, "Participant instructed that the task is complete", null);


                if (isTraining){
                    cursor.isPinchingOver = false;
                    state = State.NoHands;
                    slider.circle.xCoor = slider.startX;
                    isTraining = false;
                    loggingData();
                }else{
                    if (digits.size() >= 1){
                        logData.addFrame(new Frame(FrameCategory.BlockCompleted, state, "Block " + block + " complete", cursor.x, cursor.y, slider.circle.xCoor,slider.sliderValue));
                        logData.export();
                        digits.remove(digitIndex);
                        cursor.isPinchingOver = false;
                        state = State.NoHands;
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

