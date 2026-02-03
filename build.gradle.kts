import java.util.Date
import org.asciidoctor.gradle.jvm.AsciidoctorTask
import org.asciidoctor.gradle.jvm.pdf.AsciidoctorPdfTask
import org.gradle.crypto.checksum.Checksum
import org.jreleaser.model.Active
import org.jreleaser.model.Distribution

plugins {
  id("edu.sc.seis.version-class") version "1.4.1"
  id("org.gradle.crypto.checksum") version "1.4.0"
  `java-library`
  `project-report`
  `maven-publish`
  signing
  application
  id("org.asciidoctor.jvm.convert") version "3.3.2"
  id("org.asciidoctor.jvm.pdf") version "3.3.2"
  id("com.github.ben-manes.versions") version "0.53.0"
  id("org.jreleaser") version "1.22.0"
}

tasks.withType<JavaCompile>().configureEach { options.compilerArgs.addAll(arrayOf("-Xlint:deprecation")) }

application {
  mainClass.set("edu.sc.seis.seisFile.client.SeisFile")
  applicationName = "seisfiledefaultapp"
}

group = "edu.sc.seis"
version = "2.3.4"


java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(11))
    }
    withJavadocJar()
    withSourcesJar()
}


jreleaser {
  project {
    description.set("SeisFile: Seismic File Formats in Java")
    authors.add("Philip Crotwell")
    license.set("LGPL-3.0")
    links {
        homepage.set("https://github.com/crotwell/seisFile")
    }
    inceptionYear.set("2005")
  }

  release {
      github {
          repoOwner.set("crotwell")
          overwrite.set(true)
      }
  }
  distributions {
      create("seisFile") {
        distributionType.set(Distribution.DistributionType.JAVA_BINARY)
         artifact {
             path.set(file("build/distributions/{{distributionName}}-{{projectVersion}}.zip"))
         }
         artifact {
             path.set(file("build/distributions/{{distributionName}}-{{projectVersion}}.tar"))
         }
      }
  }
  packagers {
    brew {
      active.set(Active.ALWAYS)
    }
  }
  signing {
    setActive("ALWAYS")
  }
  deploy {
    maven {
      mavenCentral {
        create("sonatype") {
          setActive("ALWAYS")
          url= "https://central.sonatype.com/api/v1/publisher"
          stagingRepository("build/staging-deploy")
        }
      }
    }
  }
}

tasks {
  jar {
      manifest {
        attributes(
            mapOf("Automatic-Module-Name" to "edu.sc.seis.seisFile",
                  "Implementation-Title" to project.name,
                  "Implementation-Version" to archiveVersion,
                  "SeisFile-Compile-Date" to Date()))
      }
  }
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            pom {
              name.set("seisFile")
              description.set("A library for reading and writing seismic file formats in java.")
              url.set("http://www.seis.sc.edu/seisFile.html")

              scm {
                connection.set("scm:git:https://github.com/crotwell/seisFile.git")
                developerConnection.set("scm:git:https://github.com/crotwell/seisFile.git")
                url.set("https://github.com/crotwell/seisFile")
              }

              licenses {
                license {
                  name.set("GNU Lesser General Public License, Version 3")
                  url.set("https://www.gnu.org/licenses/lgpl-3.0.txt")
                }
              }

              developers {
                developer {
                  id.set("crotwell")
                  name.set("Philip Crotwell")
                  email.set("crotwell@seis.sc.edu")
                }
              }
            }
        }
    }
    repositories {
      maven {
        name = "TestDeploy"
        url = uri(layout.buildDirectory.dir("repos/test-deploy"))
      }
      maven {
          url = uri(layout.buildDirectory.dir("staging-deploy"))
      }
    }

}

sourceSets {
    create("client") {
        compileClasspath += sourceSets.main.get().output
        runtimeClasspath += sourceSets.main.get().output
    }

    create("clientTest") {
        compileClasspath += sourceSets.main.get().output + sourceSets["client"].output
        runtimeClasspath += sourceSets.main.get().output + sourceSets["client"].output
    }
}

val clientImplementation by configurations.getting {
    extendsFrom(configurations.implementation.get())
}
val clientTestImplementation by configurations.getting {
    extendsFrom(clientImplementation)
}

dependencies {
//    compile project(":seedCodec")
    implementation("edu.sc.seis:seedCodec:1.2.0")
    clientImplementation("info.picocli:picocli:4.7.7")

    annotationProcessor("info.picocli:picocli-codegen:4.7.7")
    implementation( "org.slf4j:slf4j-api:1.7.36")
    clientImplementation( "org.slf4j:slf4j-reload4j:1.7.36")
    implementation( "com.fasterxml.woodstox:woodstox-core:7.1.1")
    implementation( "org.apache.httpcomponents:httpclient:4.5.14")
    implementation("org.json:json:20251224")

    // Use JUnit Jupiter API for testing.
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.14.2")

    // Use JUnit Jupiter Engine for testing.
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.14.2")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    // Use JUnit Jupiter API for testing.
    clientTestImplementation("org.junit.jupiter:junit-jupiter-api:5.14.2")
    clientTestImplementation("org.junit.jupiter:junit-jupiter-engine:5.14.2")
    clientTestImplementation("org.junit.platform:junit-platform-launcher")

}

// for picocli
tasks.withType<JavaCompile> {
	val compilerArgs = options.compilerArgs
	compilerArgs.add("-Aproject=${project.group}/${project.name}")
}
tasks.register("genAutocomplete"){
  outputs.files(fileTree("build/autocomplete"))
}

// for junit
tasks.named<Test>("test") {
    useJUnitPlatform()
}
tasks.register<Test>("clientTest") {
    description = "Runs the separate client jar tests"
    group = "verification"
    testClassesDirs = sourceSets["clientTest"].output.classesDirs
    classpath = sourceSets["clientTest"].runtimeClasspath
    mustRunAfter(tasks["test"])
    useJUnitPlatform()
}
tasks.check { dependsOn("clientTest") }


repositories {
    mavenLocal()
    mavenCentral()
}


tasks.register<Jar>("clientJar") {
  dependsOn("clientClasses" )
  from(sourceSets["client"].output)
  archiveBaseName.set("seisFileclient")
}

tasks.register("versionToVersionFile") {
  inputs.files("build.gradle.kts")
  outputs.files("VERSION")
  File("VERSION").writeText(""+version)
}

val binDistFiles: CopySpec = copySpec {
    from(configurations["clientCompileClasspath"]) {
       into("lib")
    }
    from(tasks.named("jar")) {
      into("lib")
    }
    from(tasks.named("clientJar")) {
      into("lib")
    }
    from("build/scripts") {
        into("bin")
        include("*")
        exclude("seisfiledefaultapp*")
    }
}

val specFiles: CopySpec = copySpec {
  from("src/doc") {
    include("specs/**")
    exclude("man-*")
  }
  from("src/main/resources/edu/sc/seis/seisFile/quakeml/1.2") {
    include("*.xsd")
    into("specs/quakeml")
  }
  from("src/main/resources/edu/sc/seis/seisFile/stationxml") {
    include("*.xsd")
    into("specs/stationxml")
  }
}

tasks.register<Sync>("installBin") {
  dependsOn("clientJar")
  dependsOn("createRunScripts")
  dependsOn("startScripts")
  group = "dist"
  with( binDistFiles)
  into( layout.buildDirectory.dir("install/seisFile"))
}

distributions {
  main {
    distributionBaseName = "seisFile"
    contents {
      from(configurations["clientCompileClasspath"]) {
         into("lib")
         duplicatesStrategy =  DuplicatesStrategy.EXCLUDE
      }

      from(tasks.named("clientJar")) {
        into("lib")
      }

      from("src/doc") {
        include("specs/**")
        exclude("man-*")
        into("docs")
      }
      from("src/main/resources/edu/sc/seis/seisFile/quakeml/1.2") {
        include("*.xsd")
        into("docs/specs/quakeml")
      }
      from("src/main/resources/edu/sc/seis/seisFile/stationxml") {
        include("*.xsd")
        into("docs/specs/stationxml")
      }
      from(tasks.named("javadoc")) {
          into("docs/javadoc")
      }
      from(tasks.named("asciidoctor")) {
        include("**.html")
        into("docs/manhtml")
      }
      from(tasks.named("asciidoctor")) {
        include("manpage/**")
        into("docs")
      }
      // disable as seems to no longer work???
      //from(tasks.named("asciidoctorPdf")) {
      //  into("docs/manpdf")
      //}
      from(tasks.named("versionToVersionFile")) {
        into(".")
      }
      from(tasks.named("genAutocomplete")) {
        into("docs/bash_completion.d")
      }
      from("build/picocli") {
          include("bash_completion/**")
          into("bash_completion.d")
      }
      from(".") {
          include("LICENSE")
          include("README.md")
          include("build.gradle.kts")
          include("settings.gradle.kts")
          include("src/**")
          include("gradle/**")
          include("gradlew")
          include("gradlew.bat")
      }
      from("../seiswww/build/site") {
          include("seisFile.html")
      }
      from("build/generated-src/modVersion") {
          include("java/**")
          into("src/main")
      }
    }
  }
}

tasks {
  "asciidoctor"(AsciidoctorTask::class) {
    setSourceDir( file("src/doc/man-templates"))
    setOutputDir( file("build/manhtml"))
    sources({
        exclude("example_output/*")
      })
    outputOptions {
        backends("manpage", "html5")
    }
    inputs.dir("build/picocli/man")
    inputs.dir("src/doc/man-templates")
  }
}

// disabled as seems to no longer work???
tasks {
  "asciidoctorPdf"(AsciidoctorPdfTask::class) {
      dependsOn("asciidoctor")
    setSourceDir( file("src/doc/man-templates"))
    setOutputDir( file("build/manpdf"))
    inputs.dir("build/picocli/man")
    inputs.dir("src/doc/man-templates")
  }
}



tasks.register("genManTemplate"){}
tasks.register("createRunScripts"){}
tasks.named("startScripts") {
    dependsOn("createRunScripts")
}

/*
genManTemplate generates documentation from picocli into src/doc/man-templates, but
these files usually need hand modification to fix paths and add other links and info,
so this should be run independently of the actual assemble for release.
 */

val scriptNames = mapOf(
  "seisfile" to "edu.sc.seis.seisFile.client.SeisFile",
  "fdsnevent" to "edu.sc.seis.seisFile.client.FDSNEventClient",
  "fdsnstation" to "edu.sc.seis.seisFile.client.FDSNStationClient",
  "fdsndataselect" to "edu.sc.seis.seisFile.client.FDSNDataSelectClient",
  "irisvirtnet" to "edu.sc.seis.seisFile.client.IRISVirtualNetClient",
  "saclh" to "edu.sc.seis.seisFile.client.SacListHeader",
  "mseedlh" to "edu.sc.seis.seisFile.client.MSeedListHeader",
  "mseed3" to "edu.sc.seis.seisFile.client.MSeed3Client",
  "seedlinkclient" to "edu.sc.seis.seisFile.client.SeedLinkClient",
  "datalinkclient" to "edu.sc.seis.seisFile.client.DataLinkClient",
  "earthwormExportServer" to "edu.sc.seis.seisFile.client.EarthwormExportServer",
  "earthwormImportClient" to "edu.sc.seis.seisFile.client.EarthwormImportClient"
  )
for (key in scriptNames.keys) {
  val scriptTask = tasks.register<CreateStartScripts>(key) {
    dependsOn(tasks.named("clientJar"))
    outputDir = file("build/scripts")
      mainClass.set(scriptNames[key])
    applicationName = key
    classpath = sourceSets["client"].runtimeClasspath +
      project.tasks[JavaPlugin.JAR_TASK_NAME].outputs.files +
      project.tasks["clientJar"].outputs.files
  }
  tasks.named("createRunScripts") {
      dependsOn(scriptTask)
  }
  tasks.register<JavaExec>("genManTemplate"+key) {
    description = "generate picocli/asciidoctor template for man pages "+key
    group = "Documentation"
    classpath = configurations.getByName("annotationProcessor") + sourceSets.getByName("client").runtimeClasspath
      getMainClass().set("picocli.codegen.docgen.manpage.ManPageGenerator")
    print("genManTemplate"+key)
    val outTemplateDir =  File(project.projectDir,  "src/doc/man-templates")
    val outDir =  file(layout.buildDirectory.dir("picocli/man"))
    print(outTemplateDir)
    print(outDir)
    args = listOf("-d", outDir.path, "--template-dir", outTemplateDir.path, scriptNames[key], "-v", "--force")
    dependsOn += tasks.getByName("compileJava")
    inputs.dir("src/client")
    outputs.files(File(outTemplateDir, scriptNames[key]+".adoc"))
    doLast {
        mkdir(outTemplateDir)
        mkdir(outDir)
    }
  }
  tasks.named("genManTemplate") {
    dependsOn("genManTemplate"+key)
  }
  val adocTask = tasks.register<JavaExec>("generateManpageAsciiDoc"+key) {
    description = "generate picocli man pages for "+key
    group = "Documentation"
    classpath = configurations.getByName("annotationProcessor") + sourceSets.getByName("client").runtimeClasspath
      getMainClass().set("picocli.codegen.docgen.manpage.ManPageGenerator")
    val outDir =  file(layout.buildDirectory.dir(  "picocli/man"))
    args = listOf("-f", "-d", outDir.path, scriptNames[key])
    dependsOn += tasks.getByName("compileJava")
      //dependsOn += tasks.getByName("genManTemplate"+key)
    inputs.dir("src/client")
    outputs.files(File(outDir, scriptNames[key]+".adoc"))
    doLast {
        mkdir(outDir)
    }
  }
  tasks.named("asciidoctor") {
      dependsOn(adocTask)
      inputs.property(key+".adoc", file(layout.buildDirectory.file( "picocli/man/"+scriptNames[key]+".adoc")))
  }

  val genAutoComTask = tasks.register<JavaExec>("genAutocomplete"+key) {
    description = "generate seisFile cmd line help output files"
    dependsOn += tasks.getByName("classes")
    classpath = configurations.getByName("annotationProcessor") + sourceSets.getByName("client").runtimeClasspath
    getMainClass().set("picocli.AutoComplete")
    args = listOf("--force", scriptNames[key])
    dependsOn += tasks.getByName("compileJava")
    workingDir = File("build/autocomplete")
    outputs.file(File("build/autocomplete/"+scriptNames[key]+"_completion"))
  }
  tasks.named("genAutocomplete") {
    dependsOn(genAutoComTask)
  }
}

val docsFiles: CopySpec = copySpec {
  with(specFiles)
  from(project.projectDir) {
    include("README.md")
  }
  from("src/doc/ghpages") {
    include("**")
  }
  from("build/manhtml/html5") {
    include("**")
  }
}


tasks.register<Sync>("docsDir") {
  dependsOn("asciidoctor")
  group = "dist"
  with(docsFiles)
  into( "docs")
  rename("README.md", "index.md")
}


tasks.named("sourcesJar") {
    dependsOn("makeVersionClass")
}

tasks.get("installDist").dependsOn("versionToVersionFile")
tasks.get("installDist").dependsOn(tasks.get("docsDir"))
tasks.get("assemble").dependsOn(tasks.get("versionToVersionFile"))
tasks.get("assemble").dependsOn(tasks.get("docsDir"))
tasks.get("assemble").dependsOn(tasks.get("dependencyUpdates"))
tasks.get("jreleaserDeploy").dependsOn(tasks.get("publishMavenJavaPublicationToMavenRepository"))
tasks.get("jreleaserFullRelease").dependsOn(tasks.get("publishMavenJavaPublicationToMavenRepository"))
