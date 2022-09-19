#!/bin/sh

# やめとこ。エラーの場合もある。
# lein uberjar

if [ -z "$1" ]; then
	echo usage: $0 target/file.jar
	exit 1
fi

scp target/uberjar/py99.jar app.melt:py99/
ssh app.melt 'sudo systemctl restart py99'
ssh app.melt 'systemctl status py99'
