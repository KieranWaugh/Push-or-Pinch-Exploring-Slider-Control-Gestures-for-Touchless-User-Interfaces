import json
import glob
import csv
from collections import OrderedDict
from tabulate import tabulate


class Log:
    def __init__(self, PID, gesture, task, target, block, startTime, endTime, frames=None):
        self.PID = PID
        self.gesture = gesture
        self.task = task
        self.target = target
        self.block = block
        self.startTime = startTime
        self.endTime = endTime
        if frames is None:
            self.frames = []
        else:
            self.frames = frames

    def __str__(self):
        return str(self.__class__) + ": " + str(self.__dict__)


class Frame:
    def __init__(self, activity, state, cursor_X, cursor_y, slider_position_X, slider_value):
        self.activity = activity
        self.state = state
        self.cursor_X = cursor_X
        self.cursor_y = cursor_y
        self.slider_position_X = slider_position_X
        self.slider_value = slider_value

    def __str__(self):
        return str(self.__class__) + ": " + str(self.__dict__)


def extract_data(jsonData):
    frames = []

    for f in jsonData["frames"]:
        frames.append(Frame(f["activity"], f["state"], f["cursor_X"], f["cursor_y"], f["slider_position_X"], f["slider_value"]))

    return Log(jsonData["PID"], jsonData["gesture"], jsonData["task"], jsonData["target"], jsonData["block"], jsonData["startTime"], jsonData["endTime"], frames)


def block_success(data):
    return data.frames[-1].slider_value == data.target


def time_elapsed_block(data):
    return data.endTime - data.startTime

def overshoots(data: Log):
    overList = []
    for frame in data.frames:
        if frame.slider_value > data.target:
            overList.append(frame.slider_value)
            #print("target: " + str(data.target) + " value: " + str(frame.slider_value))
    return overList

def slider_loc(data: Log):
    # if data.target == 0:
    #     #minExampleSpace = 600
    # else:
    #     #minExampleSpace = 600 + ((data.target - 1) * 132)
    # #maxExampleSpace = 600 + (data.target * 132)
    return ((data.frames[-1].slider_position_X)) #/ exampleSpace)


def main():



    participants = str(input("Enter participant number or * for all\n"))
    task = str(input("enter task identifier:\n1: digitSelector\n2: alphaSelector\n3: shapeMove\n"))
    gesture = str(input("enter gesture identifier\n1: baseline\n2: pinchOnCircle\n3: Dwell\n4: pinchAny\n"))

    if task == "1":
        task = "digitSelector"
    elif task == "2":
        task = "alphaSelector"
    elif task == "3":
        task = "shapeMove"
    else:
        print("wrong input")

    if gesture == "1":
        gesture = "baseline"
    elif gesture == "2":
        gesture = "pinchOnCircle"
    elif gesture == "3":
        gesture = "Dwell"
    elif gesture == "4":
        gesture = "pinchAny"
    else:
        print("wrong input")

    tableRows = [["PID", "Block", "Target", "Slider Value", "Time Elapsed", "Success", "Overshoots", "Overshoots Values", "location"]]
    files = glob.glob("/Users/kieranwaugh/Projects/Touchless_Slider_Experiment/Logs/" + participants + "/" + task + "/" + gesture + "/*.json")
    log: Log
    for file in files:
        with open(file) as json_file:
            log = extract_data(json.load(json_file))
            tableRows.append([log.PID, log.block, log.target, log.frames[-1].slider_value, time_elapsed_block(log), block_success(log), len(set(overshoots(log))), set(overshoots(log)), slider_loc(log)])

    print(tabulate(tableRows, headers="firstrow", numalign="center", stralign="center", tablefmt="grid"))


if __name__ == "__main__":
    main()
