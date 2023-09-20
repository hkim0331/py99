TAG=hkim0331/py99:0.3.1
DEST="ubuntu@app.melt.kyutech.ac.jp"

all: clean security manifest

build:
	docker build -t ${TAG} .

security:
	security -v unlock-keychain ~/Library/Keychains/login.keychain-db

manifest: arm64 amd64
	docker manifest create --amend ${TAG} ${TAG}-amd64 ${TAG}-arm64
	docker manifest push ${TAG}

amd64:
	docker buildx build --platform linux/$@ --push -t ${TAG}-$@ .

arm64:
	docker buildx build --platform linux/$@ --push -t ${TAG}-$@ .

zip:
	zip -r py99.zip Dockerfile Makefile docker-compose.yml .devcontainer

uberjar:
	lein uberjar

deploy: uberjar
	scp target/uberjar/py99.jar ${DEST}:py99/py99.jar && \
	ssh ${DEST} 'sudo systemctl restart py99' && \
	ssh ${DEST} 'systemctl status py99'

clean:
	${RM} py99.zip -rf target
