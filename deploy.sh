if [ ! -z "$TRAVIS_TAG" ] && [ "$TRAVIS_PULL_REQUEST" == "false" ] && [ "$TRAVIS_SECURE_ENV_VARS" == "true" ]; then
  gradlew uploadArchives
else
  echo TRAVIS_TAG=$TRAVIS_TAG
  echo TRAVIS_PULL_REQUEST=$TRAVIS_PULL_REQUEST
  echo TRAVIS_SECURE_ENV_VARS=$TRAVIS_SECURE_ENV_VARS
fi
