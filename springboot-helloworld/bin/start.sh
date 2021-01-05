# !/bin/sh
set -x
script=$0
if [[ ${script:0:1} == "/" ]]; then
	bin=$(dirname $script)
else
	bin=$(pwd)/$(dirname $script)
fi
echo ${bin}
root=${bin}/..
echo ${root}
app=springboot-helloworld
APPLICATION=${bin}/${app}.jar
SPRING_CONFIG_FILE=${root}/config/application.yaml
MAX_MEMORY=1024M

# logback config
LOGBACK_FILE_PATH=${root}/config/logback.xml

DIGITAL_HUMAN_CONTAINER_ID=$(hostname)
mkdir -p ${root}/log
#DEBUG_LOG_FILE_PREFIX=${root}/log/${app}-${DIGITAL_HUMAN_CONTAINER_ID}
DEBUG_LOG_FILE_PREFIX=${root}/log/${app}
DEBUG_LOG_FILE_PATH=${DEBUG_LOG_FILE_PREFIX}.debug.log
touch ${DEBUG_LOG_FILE_PATH}

# java gc log config
GC_LOG_TAGS=gc,safepoint
GC_LOG_OUTPUT=file=${DEBUG_LOG_FILE_PREFIX}.gc.log
GC_LOG_DECORATIONS=utc,um,p,ti,l,tg
GC_LOG_OUTPUT_OPTIONS=filecount=5,filesize=20m

#java -Dspring.config.location=${SPRING_CONFIG_FILE} -Dfile.encoding=UTF-8 \
#	-Dlogging.config=$LOGBACK_FILE_PATH -Dlogging.access.config=$LOGBACK_ACCESS_FILE_PATH \
#	-Dlogging.debug_log_file_prefix=$DEBUG_LOG_FILE_PREFIX \
#	-Xmx${MAX_MEMORY} \
#	-Xlog:${GC_LOG_TAGS}:${GC_LOG_OUTPUT}:${GC_LOG_DECORATIONS}:${GC_LOG_OUTPUT_OPTIONS} \
#	-jar ${APPLICATION} >/dev/null 2>&1 &
java -Dspring.config.location=${SPRING_CONFIG_FILE} -Dfile.encoding=UTF-8 \
	-Dlogging.config=$LOGBACK_FILE_PATH \
	-Dlogging.debug_log_file_prefix=$DEBUG_LOG_FILE_PREFIX \
	 -Dlogging.max_log_file_history_in_days=7 \
	-Xmx${MAX_MEMORY} \
  -jar ${APPLICATION}
  # >/dev/null 2>&1 &
tail -F ${DEBUG_LOG_FILE_PATH}