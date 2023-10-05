FROM clojure:temurin-20-lein

ENV DEBIAN_FRONTEND=noninteractive

RUN set -ex; \
    apt-get -y update; \
    apt-get -y upgrade; \
    apt-get -y install --no-install-recommends \
      sudo openssh-client git postgresql-client-14 python3 python3-pip

RUN set -ex; apt-get autoremove -y; apt-get clean -y; rm -rf /var/lib/apt/lists/*

ARG USERNAME=vscode
ARG USER_UID=1000
ARG USER_GID=$USER_UID
RUN set -eux; \
    groupadd --gid $USER_GID $USERNAME; \
    useradd --uid $USER_UID --gid $USER_GID -m $USERNAME; \
    echo ${USERNAME} ALL=\(ALL\) NOPASSWD:ALL > /etc/sudoers.d/$USERNAME; \
    chmod 0440 /etc/sudoers.d/$USERNAME
USER $USERNAME

# FIXME:
# setuptools は matplitlib 入れると 68.2.2 で上書きされる。
RUN set -ex; \
    # try
    pip uninstall setuptools wheel \
    pip install pytest matplotlib setuptools wheel

# root@7bda2d7660ad:/tmp# pip list
# Package    Version
# ---------- -------
# pip        22.0.2
# setuptools 59.6.0
# wheel      0.37.1




