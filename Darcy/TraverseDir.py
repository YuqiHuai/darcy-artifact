import multiprocessing as mp
import os
import subprocess
import sys

SORA_SERVERS = ["sora2.ics.uci.edu", "sora3.ics.uci.edu"]
NUM_WORKERS = 96 if os.uname()[1] in SORA_SERVERS else os.cpu_count() // 2


def run_classycle(class_file: str, xml_output: str):
    subprocess.call(
        [
            "java",
            "-jar",
            "Classycle1.4.2/classycle.jar",
            "-xmlFile=" + xml_output,
            class_file,
        ],
        stdout=subprocess.DEVNULL,
        stderr=subprocess.DEVNULL,
    )


if __name__ == "__main__":
    if len(sys.argv) != 2:
        print("Usage: python3 TraverseDir.py <directory>")
        sys.exit(1)
    if not os.path.exists("XMLreports"):
        os.mkdir("XMLreports")
    count = 0
    tasks = []
    for root, dirs, files in os.walk(sys.argv[1]):
        for file in files:
            if file.endswith(".class") and not file.startswith("module-info"):
                i = str(count) + "-" + file.split(".")[0]
                count += 1
                tasks.append(
                    (os.path.join(root, file), "XMLreports/" + str(i) + ".xml")
                )

    with mp.Pool(NUM_WORKERS) as pool:
        pool.starmap(run_classycle, tasks)
