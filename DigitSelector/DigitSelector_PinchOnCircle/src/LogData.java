import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import processing.core.PApplet;



import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;



public class LogData {


    private final PApplet sketch;
    public int PID;
    public String selectionMethod;
    public String setting;
    public int target;
    public double sliderSectionLength;
    public int block;
    public long startTime;
    //public ArrayList<Action> actions = new ArrayList<>();
    public ArrayList<Frame> frames = new ArrayList<>();

    LogData(PApplet sketch){
        this.sketch = sketch;
    }

    void export() throws IOException {

        Log log = new Log(PID, selectionMethod,setting,target,sliderSectionLength,block,startTime,sketch.millis(), frames);
        log.export(sketch, log);


    }

    void addFrame(Frame frame){
        frames.add(frame);
    }

}

class Frame{

    Main.FrameCategory activity;
    Main.State state;
    String message;
    float cursor_X;
    float cursor_y;
    float slider_position_X;
    int slider_value;

    Frame(Main.FrameCategory activity, Main.State state, String message, float cursor_X, float cursor_y, float slider_position_X, int slider_value){
        this.activity = activity;
        this.state = state;
        this.message = message;
        this.cursor_X = cursor_X;
        this.cursor_y = cursor_y;
        this.slider_position_X = slider_position_X;
        this.slider_value = slider_value;
    }

    Frame(Main.FrameCategory activity, Main.State state, float cursor_X, float cursor_y, float slider_position_X, int slider_value){
        this.activity = activity;
        this.state = state;
        this.cursor_X = cursor_X;
        this.cursor_y = cursor_y;
        this.slider_position_X = slider_position_X;
        this.slider_value = slider_value;
    }
}


class Action{

    Main.State state;
    String message;
    long time;
    Data data;

    Action(Main.State state, String message, long time, Data data){
        this.message = message;
        this.state = state;
        this.time = time;
        this.data = data;
    }

}

class Data{

    dataTypes type;
    Object data;

    enum dataTypes{
        SliderMoved,
        CursorOverSlider,
        PinchDetection,
        CursorPosition,

    }


    Data(dataTypes type, Object... data){
        this.type = type;
        this.data = data;
    }
}

class Log{

    int PID;
    String gesture;
    String task;
    int target;
    double sliderSectionLength;
    int block;
    long startTime;
    long endTime;
    ArrayList<Frame> frames;

    Log(int PID, String gesture, String task, int target, double sliderSectionLength, int block, long startTime, long endTime, ArrayList<Frame> frames){
        this.PID = PID;
        this.gesture = gesture;
        this.task = task;
        this.target = target;
        this.sliderSectionLength = sliderSectionLength;
        this.block = block;
        this.startTime = startTime;
        this.endTime = endTime;
        this.frames = frames;
    }

    void export(PApplet sketch, Log logData) throws IOException {
        Gson log = new GsonBuilder().setPrettyPrinting().create();
        String dir = "/Users/kieranwaugh/Projects/Touchless_Slider_Experiment/Logs/" + PID + "/" + task + "/" + gesture;
        Files.createDirectories(Paths.get(dir));
        FileWriter fw = new FileWriter(dir + "/" + PID+"_"+Main.block+".json");
        fw.write(log.toJson(logData));
        fw.close();
        //log.toJson(logData, new FileWriter("Logs/" + task + "/" + gesture + "/" + PID +".json"));
        if(Main.digits.size() == 0){
            sketch.exit();
        }


    }

}