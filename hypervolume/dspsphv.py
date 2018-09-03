import sys
import os
import csv
from glob import glob

from hypervolume import hypervolume as hv


BASE_DIR = "results"
#precisa http://lopez-ibanez.eu/hypervolume para funcionar

def list_result_files(result_type="NOB", base_dir=BASE_DIR):
    path_pattern = "../{}/**/{}/*.csv".format(BASE_DIR, result_type)
    return glob(path_pattern, recursive=True)


def dim(fname):
    with open(fname, "r") as f:
        return len(f.readline().split(" "))


def extract_instance_data(fname):
    filepath, filename = os.path.split(fname)
    filename, ext = os.path.splitext(filename)
    print(filepath)
    alg_name = filepath.split(os.sep)[2]
    exec_num = filepath.split(os.sep)[-2]
    tmp = filename.split("-")
    return [alg_name, tmp[2] + "-" + tmp[3], exec_num, tmp[5]]


def get_reference_string(fname):
    return " ".join(["1.1" for _ in range(dim(fname))])


def main():
    output_file = "metrics.csv"
    if len(sys.argv) > 1:
        output_file = sys.argv[1]
    result_files = list_result_files()
    if result_files:
        with open(output_file, "w") as out:
            writer = csv.writer(out, delimiter=" ")
            s = len(result_files)
            for i, fname in enumerate(result_files):
                data = extract_instance_data(fname)
                data.append(hv(fname, get_reference_string(fname)))
                writer.writerow(data)
                print("Calculating hypervolume. Please wait... ({:5.1f}%)".format((i * 100) / s), end="\r")
            print("\nDone.")
    else:
        print("Result file folder is empty.")


if __name__ == '__main__':
    main()
