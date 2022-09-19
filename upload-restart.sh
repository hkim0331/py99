#!/bin/sh
lein uberjar && \
scp target/uberjar/py99.jar app.melt:py99/ && \
ssh app.melt 'sudo systemctl restart py99'
