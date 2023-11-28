FROM clojure:temurin-20-lein

ENV DEBIAN_FRONTEND=noninteractive

RUN set -ex; \
    apt-get -y update; \
    apt-get -y upgrade; \
    apt-get -y install --no-install-recommends \
      sudo openssh-client git git-flow postgresql-client-14 python3 python3-pip

RUN set -ex; apt-get autoremove -y; apt-get clean -y; rm -rf /var/lib/apt/lists/*

RUN pip3 install pytest

ARG USERNAME=vscode
ARG USER_UID=1000
ARG USER_GID=$USER_UID

RUN set -eux; \
    groupadd --gid $USER_GID $USERNAME; \
    useradd --uid $USER_UID --gid $USER_GID -m $USERNAME; \
    echo ${USERNAME} ALL=\(ALL\) NOPASSWD:ALL > /etc/sudoers.d/$USERNAME; \
    chmod 0440 /etc/sudoers.d/$USERNAME; \

echo PATH=/home/$USERNAME/.local/bin:$PATH >> /home/$USERNAME/.bashrc

RUN curl -s https://raw.githubusercontent.com/babashka/babashka/master/install | bash

USER $USERNAME
