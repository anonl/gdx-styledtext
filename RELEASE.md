
# Release procedure

- Set version number in `build.gradle`
- Update the [changelog](CHANGELOG.md)
- Merge changes to upstream master branch.
- Wait for the [CI build](https://github.com/anonl/gdx-styledtext/actions) to finish successfully.
- Tag the commit with the version number (i.e. `v1.2.3`)
- Upload the release to OSSRH (staging repo): `./gradlew publish`
- Publish the release, see https://central.sonatype.org/pages/releasing-the-deployment.html
