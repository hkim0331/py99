#!/bin/sh

# やめとこ。エラーの場合もある。
# lein uberjar

scp target/default+uberjar/py99.jar app.melt:py99/
ssh app.melt 'sudo systemctl restart py99'
ssh app.melt 'systemctl status py99'
