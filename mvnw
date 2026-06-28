#!/bin/sh
# ----------------------------------------------------------------------------
# Maven Wrapper 启动脚本 (Git Bash / Linux / Mac)
# ----------------------------------------------------------------------------

# 找到项目根目录
PRG="$0"
while [ -h "$PRG" ]; do
    ls=$(ls -ld "$PRG")
    link=$(expr "$ls" : '.*-> \(.*\)$')
    if expr "$link" : '/.*' > /dev/null; then
        PRG="$link"
    else
        PRG=$(dirname "$PRG")/"$link"
    fi
done
SAVED="$(pwd)"
cd "$(dirname "$PRG")/" >/dev/null
APP_HOME="$(pwd -P)"
cd "$SAVED" >/dev/null

# 构建 classpath
CLASSPATH="$APP_HOME/.mvn/wrapper/maven-wrapper.jar"

# 启动 JVM
MAVEN_OPTS="${MAVEN_OPTS:--Xmx512m}"
export MAVEN_OPTS

# 执行 Maven Wrapper
exec java \
    -classpath "$CLASSPATH" \
    -Dmaven.multiModuleProjectDirectory="$APP_HOME" \
    org.apache.maven.wrapper.MavenWrapperMain \
    "$@"
