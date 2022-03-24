import mysql.connector

mydb = mysql.connector.connect(
    host="localhost",
    user="quoctrong",
    password=""
)

print(mydb)
