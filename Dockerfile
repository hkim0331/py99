FROM clojure:lein

ENV DEBIAN_FRONTEND=noninteractive

# Add python3
RUN apt-get update \
    && apt-get -y install --no-install-recommends apt-utils python3 python3-pip >&1

# and numpy, pytest
RUN pip install numpy pytest

# Clean up
RUN apt-get autoremove -y \
    && apt-get clean -y \
    && rm -rf /var/lib/apt/lists/*

ENV DEBIAN_FRONTEND=dialog

ARG USERNAME=vscode
ARG USER_ID=1000
RUN useradd -m -U -u ${USER_ID} ${USERNAME}
