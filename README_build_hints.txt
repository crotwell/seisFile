
# asciidoc man pages:
generate in build, copied to src and docs
example output from src/doc/man-example with run_examples.sh


./gradlew clean eB test clientTest assemble

# if client code has changed, may need to rerun man templates for picocli
#
./gradlew genManTemplate
# and check git diff on src/doc/man-templates

# check for dep updates
./gradlew dependencyUpdates

# wrapper upgrade
./gradlew wrapper --gradle-version 8.6 --distribution-type bin

# SNAPSHOT publish,
# version must end with -SNAPSHOT
# will go to
# https://oss.sonatype.org/content/repositories/snapshots/edu/sc/seis/seisFile/
./gradlew publish


# Release
move the curr_release branch, github pages builds from this branch
