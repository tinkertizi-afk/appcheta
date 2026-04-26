#!/bin/sh
# Gradle wrapper script for SelfFocus project

# Determine the Java command to use to start the JVM
if [ -n "$JAVA_HOME" ] ; then
    JAVACMD="$JAVA_HOME/bin/java"
else
    JAVACMD="java"
fi

# Check if ANDROID_HOME is set
if [ -z "$ANDROID_HOME" ]; then
    echo "Error: ANDROID_HOME environment variable is not set"
    echo "Please set ANDROID_HOME to your Android SDK location"
    exit 1
fi

# Set up Gradle home
GRADLE_USER_HOME="${GRADLE_USER_HOME:-$HOME/.gradle}"
GRADLE_VERSION="8.2"
GRADLE_DIST_DIR="$GRADLE_USER_HOME/wrapper/dists/gradle-${GRADLE_VERSION}-bin"

# Download and run Gradle if not present
if [ ! -d "$GRADLE_DIST_DIR" ]; then
    echo "Downloading Gradle ${GRADLE_VERSION}..."
    mkdir -p "$GRADLE_DIST_DIR"
    curl -L "https://services.gradle.org/distributions/gradle-${GRADLE_VERSION}-bin.zip" -o "/tmp/gradle-${GRADLE_VERSION}.zip"
    unzip -q "/tmp/gradle-${GRADLE_VERSION}.zip" -d "$GRADLE_DIST_DIR"
    rm "/tmp/gradle-${GRADLE_VERSION}.zip"
fi

GRADLE_HOME=$(find "$GRADLE_DIST_DIR" -maxdepth 1 -type d -name "gradle-*" | head -1)

exec "$GRADLE_HOME/bin/gradle" "$@"
