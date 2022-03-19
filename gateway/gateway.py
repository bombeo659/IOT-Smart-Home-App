from re import T
import time
from readSerial import *
from mqttCallback import *
from aio_config import *
import globals as g


def main():
    client.on_connect = connected
    client.on_disconnect = disconnected
    client.on_message = message
    client.on_subscribe = subscribe
    client.connect()
    client.loop_background()

    isRead = 5000
    timeResend = 0
    numOfResend = 0
    isSleep = False
    timeSleep = 0
    numOfSleep = 0
    ser.write(("!#").encode())
    while True:
        if g.isComConnect:
            if isRead == 0 and g.lastSentOK:
                readSerial()
                isRead = g.TIME_TO_READ
                ser.write(("!#").encode())
                publishData(g.data, False)
                g.dataSave = g.data
                g.lastSentOK = False
                timeResend = g.TIME_TO_RESEND
                numOfResend = 0
                numOfSleep = 0

            if not g.lastSentOK and isSleep and timeSleep == 0:
                numOfSleep += 1
                isSleep = False
                timeResend = 0

            if numOfSleep >= g.MAX_NUM_OF_SLEEP:
                print("Send data failed. Skip data this time!")
                g.lastSentOK = True
                timeSleep = 0
                timeResend = 0
                isSleep = False
                numOfResend = 0
                numOfSleep = 0

            if not g.lastSentOK and numOfResend >= g.MAX_NUM_OF_RESEND and timeResend == 0 and not isSleep:
                print(str(numOfResend) + " resend failed. Stop sending!")
                numOfResend = 0
                isSleep = True
                timeSleep = g.TIME_SLEEP

            if not g.lastSentOK and timeResend == 0 and not isSleep:
                timeResend = g.TIME_TO_RESEND
                publishData(g.dataSave, True)
                numOfResend += 1
                print("Resend " + str(g.dataSave) +
                      " time " + str(numOfResend) + "!!")

            if isRead > 0:
                isRead -= 1
            if timeResend > 0:
                timeResend -= 1
            if timeSleep > 0:
                timeSleep -= 1
        else:
            print("None serial port !!!")

        time.sleep(0.001)


if __name__ == "__main__":
    main()
