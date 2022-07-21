import glob
import json
import os


if __name__ == "__main__":
    os.chdir("store")
    for file_name in glob.glob("*.json"):
        split_name = file_name.split(".")
        if len(split_name) != 2:
            continue
        uuid = split_name[0]
        data = None
        with open(file_name, "r") as json_file:
            data = json.load(json_file)
        if data is None:
            continue
        data["uuid"] = uuid
        json_data = json.dumps(data, separators=(',', ':'))
        with open("vampire_data/" + file_name, "w") as json_file:
            json_file.write(json_data)
