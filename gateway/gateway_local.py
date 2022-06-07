import serial.tools.list_ports
import time
import sys
from Adafruit_IO import MQTTClient

AIO_FEED_IDS = ["bk-iot-led", "bk-iot-tv", "bk-iot-ac", "bk-iot-pump"]


AIO_USERNAME = "iotg06"
AIO_KEY = "aio_Vnwm12ZcLwR8Bsflg0qhex0T9YYX"


def connected(client):
    print("Ket noi thanh cong...")
    for feed in AIO_FEED_IDS:
        client.subscribe(feed)


def subscribe(client, userdata, mid, granted_qos):
    print("Subcribe thanh cong...")


def disconnected(client):
    print("Ngat ket noi...")
    sys.exit(1)


def message(client, feed_id, payload):
    print("Nhan du lieu tu " + feed_id + ": " + payload)
    if isMicrobitConnected:
        ser.write(("feed: " + feed_id + " send: " +
                  str(payload) + "#\n").encode())
        # ser.write((str(payload) + "#\n").encode())


client = MQTTClient(AIO_USERNAME, AIO_KEY)
client.on_connect = connected
client.on_disconnect = disconnected
client.on_message = message
client.on_subscribe = subscribe
client.connect()
client.loop_background()


def getPort():
    ports = serial.tools.list_ports.comports()
    N = len(ports)
    commPort = "None"
    for i in range(0, N):
        port = ports[i]
        strPort = str(port)
        # if "USB Serial Device" in strPort:
        if "ELTIMA Virtual Serial Port" in strPort:
            splitPort = strPort.split(" ")
            commPort = (splitPort[0])
    return commPort


isMicrobitConnected = False
if getPort() != "None":
    print("Connect with " + getPort())
    ser = serial.Serial(port=getPort(), baudrate=115200)
    isMicrobitConnected = True


def processData(data):
    data = data.replace("!", "")
    data = data.replace("#", "")
    splitData = data.split(":")
    print(splitData)
    try:
        if splitData[0] == "TEMP":
            client.publish("bk-iot-temp", splitData[1])
        elif splitData[0] == "HUMI":
            client.publish("bk-iot-humi", splitData[1])
        elif splitData[0] == "GAS":
            client.publish("bk-iot-gas", splitData[1])
    except:
        pass


mess = ""


def readSerial():
    bytesToRead = ser.inWaiting()
    if (bytesToRead > 0):
        global mess
        mess = mess + ser.read(bytesToRead).decode("UTF-8")
        while ("#" in mess) and ("!" in mess):
            start = mess.find("!")
            end = mess.find("#")
            processData(mess[start:end + 1])
            if (end == len(mess)):
                mess = ""
            else:
                mess = mess[end+1:]


while True:
    if isMicrobitConnected:
        readSerial()

    time.sleep(1)
