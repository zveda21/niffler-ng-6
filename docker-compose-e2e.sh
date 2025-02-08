#!/bin/bash
source ./docker.properties
export COMPOSE_PROFILES=test
export PROFILE=docker
export PREFIX="${IMAGE_PREFIX}"
export ALLURE_DOCKER_API=http://allure:5050/
export HEAD_COMMIT_MESSAGE="local build"
export ARCH=$(uname -m)

# Default values
BROWSER="chrome"      # Default browser is Chrome
SKIP_BUILD=false      # By default, do not skip the build

# Parsing command-line arguments
while [[ $# -gt 0 ]]; do
  case "$1" in
    chrome|firefox)
      BROWSER="$1"
      ;;
    --skip-build)
      SKIP_BUILD=true
      ;;
    *)
      echo "Unknown argument: $1"
      exit 1
      ;;
  esac
  shift
done

export BROWSER_TYPE=$BROWSER

echo '### Java version ###'
java --version

docker compose down
docker_containers=$(docker ps -a -q)
docker_images=$(docker images --format '{{.Repository}}:{{.Tag}}' | grep 'niffler')

if [ ! -z "$docker_containers" ]; then
  echo "### Stop containers: $docker_containers ###"
  docker stop $docker_containers
  docker rm $docker_containers
fi

if [ ! -z "$docker_images" ]; then
  echo "### Remove images: $docker_images ###"
  docker rmi $docker_images
fi

echo '### Java version ###'
java --version
bash ./gradlew clean
bash ./gradlew jibDockerBuild -x :niffler-e-2-e-tests:test

docker pull selenoid/vnc_chrome:127.0
docker compose up -d
docker ps -a