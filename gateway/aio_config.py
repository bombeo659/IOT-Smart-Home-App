from Adafruit_IO import MQTTClient
AIO_USERNAME = "iotg06"
AIO_KEY = "aio_Ahdg47azJ796gm-------------------UVqD3LMKJ66cff"

FEED_DOOR = "iot.door"
FEED_FAN = "iot.fan"
FEED_HUMIDITY = "iot.humidity"
FEED_LIGHT = "iot.light"
FEED_REFRESHER = "iot.refresher"
FEED_TEMP = "iot.temp"
FEED_WARNING = "iot.warning"
FEED_AUTODOOR = "iot.autodoor"

client = MQTTClient(AIO_USERNAME, AIO_KEY)
