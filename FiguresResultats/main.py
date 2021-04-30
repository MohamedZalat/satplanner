# This is a sample Python script.

# Press Maj+F10 to execute it or replace it with your code.
# Press Double Shift to search everywhere for classes, files, tool windows, actions, and settings.
import sys
import pandas as pd
import matplotlib.pyplot as plt
from matplotlib.ticker import MultipleLocator, FormatStrFormatter



def readCsv(filename):

    # reading csv files
    return pd.read_csv(filename, sep=";")

def rassembleData(data1, data2):

    #data3 = pd.concat([data1, data2], axis=1, ignore_index=True)
    print(data1)
    print(data2)
    data3 = pd.merge(data1, data2, on="problems")
    print(data3)
    return data3

# Press the green button in the gutter to run the script.
if __name__ == '__main__':

    # arg 1 : data name
    if len(sys.argv) > 2:
        dataname1 = sys.argv[1]
        dataname2 = sys.argv[2]
    else:
        dataname1 = "simple"
        dataname2 = "simple"


    data = readCsv(dataname1)
    data2 = readCsv(dataname2)

    data3 = rassembleData(data, data2)
    data3 = data3.sort_values(by='valeurs ASP', ascending=True, axis=0)
    ax = plt.gca()
    ax.xaxis.set_major_locator(MultipleLocator(1))
    ax.xaxis.set_minor_locator(MultipleLocator(1))
    ax.yaxis.set_major_locator(MultipleLocator(1))
    ax.yaxis.set_major_locator(MultipleLocator(0.5))
    plt.ylim([0, 13])
    plt.ylabel("time")
    plt.title("Time for Depots problems")

    data3.plot(x='problems', y='valeurs Planner', kind='line', ax=ax)
    data3.plot(x='problems', y='valeurs ASP', kind='line', color='red', ax=ax)

    plt.savefig("resultat/timeBlocksworld.png")
    plt.show()

# See PyCharm help at https://www.jetbrains.com/help/pycharm/
