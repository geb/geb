# Required accounts and credentials

1. Generate a GPG key pair and distribute the public key as per [this blog post](http://blog.sonatype.com/2010/01/how-to-generate-pgp-signatures-with-maven). Add the following entries to `~/.gradle/gradle.properties`:
	* signing.keyId=«key id»
	* signing.password=«key password»
	* signing.secretKeyRingFile=«path to the secure gpg keyring (not public)»
1. [Sign up](https://issues.sonatype.org/secure/Signup!default.jspa) for a Jira account @ Sonatype. Send your Jira username to someone who is already allowed to publish Geb to Sonatype so that they add a comment [this ticket](https://issues.sonatype.org/browse/OSSRH-3108) to request access rights for you. Add your Sonatype credentials to `~/.gradle/gradle.properties`:
	* sonatypeOssUsername=«Jira@Sontype username»
	* sonatypeOssPassword=«Jira@Sontype password»
1. [Register at grails.org](https://grails.org/register) and at [Grails dev mailing list](https://groups.google.com/forum/#!forum/grails-dev-discuss). Send an email to the dev list requesting rights to publish to Grails Geb plugin quoting your grails.org username in the email. Add the following entries to `~/.grails/settings.groovy`:
	* grails.project.repos.grailsCentral.username = "«username (not the email!)»"
	* grails.project.repos.grailsCentral.password = "«password»"
1. Ensure that you have an account at [Jira@Codehaus](http://jira.codehaus.org/) if not then it can be created via [Xircles](http://xircles.codehaus.org/signup). 

# Releasing

1. Ensure that the revision you're about to promote has been successfully built on [CI](https://snap-ci.com/geb/geb/branch/master).
1. Update the version to the required one (usually just dropping -SNAPSHOT) in `geb.gradle` file.
1. Commit with message "Version «number»" (don't push yet)
1. Tag commit with name "v«number»" (still don't push yet)
1. Run `./gradlew clean release`
1. Log into [Sonatype OSS repository](https://oss.sonatype.org), go to "Staging Repositories", find the one for Geb, release and then promote it.
1. Wait for the new version to [appear in Maven Central](http://search.maven.org/#search%7Cgav%7C1%7Cg%3A%22org.gebish%22%20AND%20a%3A%22geb-core%22), this might take several hours.

# Post-release actions
1. Bump the version to a snapshot of the next planned version.
1. Remove the oldest version from `oldManualVersions` list in `site.gradle` and append the newly released one.
1. Commit with message 'Begin version «version»', and push (make sure you push the tag as well). 
1. Bump Geb versions in example projects: 
	* [geb-example-gradle](https://github.com/geb/geb-example-gradle)
	* [geb-example-cucumber-jvm](https://github.com/geb/geb-example-cucumber-jvm)
	* [geb-example-maven](https://github.com/geb/geb-example-maven)
	* [geb-example-grails](https://github.com/geb/geb-example-grails)
1. Update Jira issues and versions:
	* Find all unresolved issues in Jira that have the fix version set to the first unreleased version and bulk edit them (using the "Tools" button in the right upper corner of issue list) to have the fix version set ot the next version.
	* Find all resolved issues in Jira for the first unreleased version and bulk close them.
	* Go to Administrations -> Projects -> Geb -> Versions, find the first unresolved version, change the version number if it's different from the one that was released, release it and set the release date.
1. Wait for the build of the next version to pass and the site including manual for the released version to be published.
1. Send an email to the mailing list, you can use [this one](http://markmail.org/message/j35koyww35lh4mxk) as a template. Please mention significant breaking changes if there are any.
