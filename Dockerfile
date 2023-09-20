FROM clojure:temurin-20-lein

ENV DEBIAN_FRONTEND=noninteractive

RUN apt-get update \
    && apt-get -y upgrade \
    && apt-get -y install --no-install-recommends \
      sudo git postgresql-client-14 python3 python3-pip 2>&1

RUN apt-get autoremove -y \
    && apt-get clean -y \
    && rm -rf /var/lib/apt/lists/*

ARG USERNAME=vscode
ARG USER_UID=1000
ARG USER_GID=$USER_UID

RUN groupadd --gid $USER_GID $USERNAME \
    && useradd --uid $USER_UID --gid $USER_GID -m $USERNAME \
    && echo ${USERNAME} ALL=\(ALL\) NOPASSWD:ALL > /etc/sudoers.d/$USERNAME \
    && chmod 0440 /etc/sudoers.d/$USERNAME

USER $USERNAME

RUN pip install numpy matplotlib pytest wheel==0.41.2
