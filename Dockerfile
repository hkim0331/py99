FROM clojure:lein

ENV DEBIAN_FRONTEND=noninteractive

RUN apt-get update \
   && apt-get -y upgrade \
   && apt-get -y install --no-install-recommends \
      apt-utils sudo git python3 python3-pip 2>&1

RUN apt-get -y install --no-install-recommends \
               apt-utils sudo git 2>&1

RUN apt-get autoremove -y \
    && apt-get clean -y \
    && rm -rf /var/lib/apt/lists/*

ENV DEBIAN_FRONTEND=dialog

RUN pip install numpy matplotlib pytest

ARG USERNAME=vscode
ARG USER_ID=1000
RUN useradd -m -U -u ${USER_ID} ${USERNAME}
RUN echo ${USERNAME} ALL=\(ALL\) NOPASSWD:ALL > /etc/sudoers.d/10-vscode
