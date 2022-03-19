import serial.tools.list_ports
import globals as g


def getPort():
    ports = serial.tools.list_ports.comports()
    N = len(ports)
    commPort = "None"
    for i in range(0, N):
        port = ports[i]
        strPort = str(port)
        if "ELTIMA Virtual Serial Port" in strPort:
            splitPort = strPort.split(" ")
            commPort = (splitPort[0])
            break
    return commPort


if getPort() != "None":
    ser = serial.Serial(port=getPort(), baudrate=115200)
    print("Connected with " + getPort())
    g.isComConnect = True


def readSerial():
    bytesToRead = ser.inWaiting()
    if (bytesToRead > 0):
        mess = ser.read(bytesToRead).decode("UTF-8")
        start = mess.find("!")
        end = mess.find("#")
        g.data = mess[start:end+1]
        mess = ""
    if g.data != "":
        return True
    else:
        return False
