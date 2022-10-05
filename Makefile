DEST="ubuntu@app.melt.kyutech.ac.jp"

build:
	docker build -t hkim0331/py99 .

userbar:
	lein uberjar

deploy: uberjar
	scp target/default+uberjar/py99.jar ${DEST}:py99/py99.jar && \
	ssh ${DEST} 'sudo systemctl restart py99' && \
	ssh ${DEST} 'systemctl status py99'

