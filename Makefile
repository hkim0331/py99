all: deploy

uberjar:
	lein uberjar

deploy: uberjar
	scp target/default+uberjar/py99.jar app.melt:py99/ && \
	ssh app.melt 'sudo systemctl restart py99' && \
	ssh app.melt 'systemctl status py99'

hkim0331/py99:
	docker build -t $@ .

clean:
	${RM} -r target/
