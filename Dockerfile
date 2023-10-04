FROM clojure:temurin-20-lein

ENV DEBIAN_FRONTEND=noninteractive

RUN set -eux; \
    apt-get update; \
    apt-get -y install --no-install-recommends \
      sudo openssh-client git postgresql-client-14 python3 python3-pip

RUN set -eux; \
    apt-get autoremove -y; \
    apt-get clean -y; \
    rm -rf /var/lib/apt/lists/*

ARG USERNAME=vscode
ARG USER_UID=1000
ARG USER_GID=$USER_UID

RUN set -eux; \
    groupadd --gid $USER_GID $USERNAME; \
    useradd --uid $USER_UID --gid $USER_GID -m $USERNAME; \
    echo ${USERNAME} ALL=\(ALL\) NOPASSWD:ALL > /etc/sudoers.d/$USERNAME; \
    chmod 0440 /etc/sudoers.d/$USERNAME


USER $USERNAME

# numpy は matplitlib が連れてくる 1.25.2 を使おう。
# setuptools は matplitlib 入れると 68.2.2 で上書きされる。
RUN set -eux; \
    pip3 install pytest matplotlib wheel==0.41.2 \
