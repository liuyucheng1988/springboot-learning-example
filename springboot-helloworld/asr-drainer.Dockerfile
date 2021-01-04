FROM iregistry.baidu-int.com/acg-digital-human/brtc-jdk11:20200311

MAINTAINER caizhensheng@baidu.com

USER root

WORKDIR /home/work

ADD output/asr-drainer /home/work/asr-drainer

RUN chmod +x /home/work/asr-drainer/bin/start.sh

STOPSIGNAL SIGQUIT

ENTRYPOINT bash /home/work/asr-drainer/bin/start.sh
