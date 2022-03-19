import sys
from aio_config import *
import globals as g
import requests
from readSerial import *


def connected(client):
    print("Listening for all feed changes...")
    client.subscribe(FEED_DOOR)
    client.subscribe(FEED_FAN)
    client.subscribe(FEED_HUMIDITY)
    client.subscribe(FEED_LIGHT)
    client.subscribe(FEED_REFRESHER)
    client.subscribe(FEED_TEMP)
    client.subscribe(FEED_WARNING)
    client.subscribe(FEED_AUTODOOR)


def subscribe(client, userdata, mid, granted_qos):
    print("Subscribed successful!")


def disconnected(client):
    print("Disconnected from Adafruit IO!")
    sys.exit(1)


def message(client, feed_id, payload):

    if feed_id == FEED_REFRESHER or feed_id == FEED_LIGHT or feed_id == FEED_FAN or feed_id == FEED_AUTODOOR:
        print(feed_id + " received new request: " + payload)
        ser.write((str(feed_id) + " received new request:" +
                  str(payload) + "#").encode())

    if feed_id == FEED_TEMP or feed_id == FEED_HUMIDITY or feed_id == FEED_DOOR:
        g.lastSentOK = True
        print(feed_id + " received ACK: " + payload)
        # pass


TOKEN = "BBFF-vUFnhxoMZL1o----------------------BRFKKpjdP7WnTDvrcS"
url = "https://things.ubidots.com"
url = "{}/api/v1.6/devices/{}".format(url, "iot")
headers = {"X-Auth-Token": TOKEN, "Content-Type": "application/json"}


def publishData(data, flagSendAgain):
    data = data.replace("!", "")
    data = data.replace("#", "")
    splitData = data.split(":")
    if not flagSendAgain:
        print("Publish new " + str(data))
        payload = {
            FEED_TEMP: splitData[1], FEED_HUMIDITY: splitData[2], FEED_DOOR: splitData[3]}
        requests.post(url=url, headers=headers, json=payload)
    client.publish(FEED_TEMP, splitData[1])
    client.publish(FEED_HUMIDITY, splitData[2])
    client.publish(FEED_DOOR, splitData[3])
