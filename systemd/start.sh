#!/bin/sh

# 小文字で true/false
export EXAM_MODE=false
export PORT=20221
export DATABASE_URL='postgresql://db/py99?user=postgres'
java -jar target/uberjar/py99.jar
