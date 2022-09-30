FROM hkim0331/clojure

ENV DEBIAN_FRONTEND=noninteractive

# Add python3
RUN apt-get update \
    && apt-get -y install --no-install-recommends \
       python3 python3-pip >&1

# and numpy, pytest
RUN pip install numpy matplotlib pytest

# Clean up
RUN apt-get autoremove -y \
    && apt-get clean -y \
    && rm -rf /var/lib/apt/lists/*

ENV DEBIAN_FRONTEND=dialog
