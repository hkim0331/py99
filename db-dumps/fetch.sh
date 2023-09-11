#!/bin/sh

SERV=app.melt
APP=py99
DUMP=${APP}/db-dumps
TODAY=`date +%F`

ssh ${SERV} "cd ${DUMP} && ./dump.sh"
scp ${SERV}:${DUMP}/${APP}-${TODAY}.sql .

