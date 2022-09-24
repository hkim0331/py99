FROM clojure:lein

ENV DEBIAN_FRONTEND=noninteractive

# Add sudo, git, python3, python3-pup
RUN apt-get update \
    && apt-get -y install --no-install-recommends \
    apt-utils sudo git python3 python3-pip >&1

# python packages
RUN pip install numpy pytest matplotlib

# Clean up
RUN apt-get autoremove -y \
    && apt-get clean -y \
    && rm -rf /var/lib/apt/lists/*

ENV DEBIAN_FRONTEND=dialog

ARG USERNAME=vscode
ARG USER_ID=1000
RUN useradd -m -U -u ${USER_ID} ${USERNAME}
RUN echo "${USERNAME} ALL=(ALL) NOPASSWD:ALL" > /etc/sudoers.d/10-vscode
