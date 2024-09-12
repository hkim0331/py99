TAG=hkim0331/py99:0.6.4
DEST="ubuntu@app.melt.kyutech.ac.jp"

develop:
	poetry run code .

build:
	docker build -t ${TAG} .

clean:
	${RM} py99.zip

hub: clean security manifest

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
