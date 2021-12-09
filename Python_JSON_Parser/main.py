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


def main():
    tableRows = [["PID", "Block", "Target", "Time Elapsed", "Success"]]
    files = glob.glob("/Users/kieranwaugh/Projects/Touchless_Slider_Experiment/Logs/**/alphaSelector/Dwell/*.json", recursive=True)
    log: Log
    for file in files:
        with open(file) as json_file:
            log = extract_data(json.load(json_file))
            #print(block_success(log))
            #print(time_elapsed_block(log))
            tableRows.append([log.PID, log.block, log.target, time_elapsed_block(log), block_success(log)])

    print(tabulate(tableRows, headers="firstrow", numalign="center", stralign="center", tablefmt="grid"))


if __name__ == "__main__":
    main()
