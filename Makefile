DEST="ubuntu@app.melt.kyutech.ac.jp"
TAG=hkim0331/py99:0.2

build:
	docker build -t ${TAG} .

uberjar:
	lein uberjar

deploy: uberjar
	scp target/uberjar/py99.jar ${DEST}:py99/py99.jar && \
	ssh ${DEST} 'sudo systemctl restart py99' && \
	ssh ${DEST} 'systemctl status py99'

clean:
	${RM} -rf target
