import org.asciidoctor.gradle.jvm.AsciidoctorTask
import org.asciidoctor.gradle.jvm.pdf.AsciidoctorPdfTask
import org.gradle.crypto.checksum.Checksum

plugins {
  id("edu.sc.seis.version-class") version "1.2.2"
  id("org.gradle.crypto.checksum") version "1.3.0"
  "java-library"
  eclipse
  `project-report`
  `maven-publish`
  signing
  application
  id("org.asciidoctor.jvm.convert") version "3.3.2"
  id("org.asciidoctor.jvm.pdf") version "3.3.2"
    id("com.github.ben-manes.versions") version "0.42.0"
}

tasks.withType<JavaCompile>().configureEach { options.compilerArgs.addAll(arrayOf("-Xlint:deprecation")) }

application {
  mainClass.set("edu.sc.seis.seisFile.client.SeisFile")
  applicationName = "seisfiledefaultapp"
}

group = "edu.sc.seis"
version = "2.0.5-SNAPSHOT2"

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
    withJavadocJar()
    withSourcesJar()
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
        url = uri("$buildDir/repos/test-deploy")
      }
      maven {
          val releaseRepo = "https://oss.sonatype.org/service/local/staging/deploy/maven2/"
          val snapshotRepo = "https://oss.sonatype.org/content/repositories/snapshots/"
          url = uri(if ( version.toString().toLowerCase().endsWith("snapshot")) snapshotRepo else releaseRepo)
          name = "ossrh"
          // credentials in gradle.properties as ossrhUsername and ossrhPassword
          credentials(PasswordCredentials::class)
      }
    }

}

signing {
    sign(publishing.publications["mavenJava"])
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
configurations["clientRuntimeOnly"].extendsFrom(configurations.runtimeOnly.get())
val clientTestImplementation by configurations.getting {
    extendsFrom(clientImplementation)
}
configurations["clientTestRuntimeOnly"].extendsFrom(configurations["clientRuntimeOnly"]).extendsFrom(configurations["testRuntimeOnly"])


dependencies {
//    compile project(":seedCodec")
    implementation("edu.sc.seis:seedCodec:1.1.1")
    clientImplementation("info.picocli:picocli:4.6.3")

    annotationProcessor("info.picocli:picocli-codegen:4.6.3")
    implementation( "org.slf4j:slf4j-api:1.7.35")
    clientImplementation( "org.slf4j:slf4j-reload4j:1.7.35")
    implementation( "com.fasterxml.woodstox:woodstox-core:6.2.8")
    implementation( "org.apache.httpcomponents:httpclient:4.5.13")


    // Use JUnit Jupiter API for testing.
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.2")

    // Use JUnit Jupiter Engine for testing.
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.2")

    // Use JUnit Jupiter API for testing.
    clientTestImplementation("org.junit.jupiter:junit-jupiter-api:5.8.2")

}

// for picocli
tasks.withType<JavaCompile> {
	val compilerArgs = options.compilerArgs
	compilerArgs.add("-Aproject=${project.group}/${project.name}")
}

// for junit
tasks.named<Test>("test") {
    useJUnitPlatform()
}
task<Test>("clientTest") {
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

val specFilesCopy: CopySpec = copySpec {
  with(specFiles)
}
val distFiles: CopySpec = copySpec {
    with(binDistFiles)
    with(specFilesCopy) {
      into("docs")
      include("*")
    }
    from("build/docs") {
        include("javadoc/**")
        into("docs")
    }
    from("build/manhtml") {
        include("manpage/**")
        into("docs")
    }
    from("build/manhtml/html5") {
        include("**")
        into("docs/manhtml")
    }
    from("build") {
        include("manpdf/**")
        into("docs")
    }
    from("build/picocli") {
        include("bash_completion/**")
        into("docs/bash_completion.d")
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
        exclude("**/*.svn")
    }
    from("../seiswww/build/site") {
        include("seisFile.html")
    }
    from("build/generated-src/modVersion") {
        include("java/**")
        into("src/main")
    }
}

tasks.register<Sync>("explodeBin") {
  dependsOn("clientClasses")
  dependsOn("clientJar")
  dependsOn("createRunScripts")
  dependsOn("startScripts")
  group = "dist"
  with( binDistFiles)
  into( file("$buildDir/explode"))
}

tasks.register<Sync>("explodeDist") {
  dependsOn("explodeBin")
  dependsOn("javadoc")
  group = "dist"
  with( distFiles)
  into( file("$buildDir/explode"))
}


tasks.register<Tar>("tarBin") {
  dependsOn("explodeBin" )
    compression = Compression.GZIP
    into(project.name+"-"+archiveVersion.get()+"-bin") {
        with(binDistFiles)
    }
}

tasks.register<Tar>("tarDist") {
  dependsOn("explodeDist" )
    compression = Compression.GZIP
    into(project.name+"-"+archiveVersion.get()) {
        with(distFiles)
    }
}

tasks.register<Checksum>("checksumDist") {
  dependsOn("tarBin")
  dependsOn("tarDist")
  files = tasks.getByName("tarBin").outputs.files + tasks.getByName("tarDist").outputs.files
  outputDir=File(project.buildDir, "distributions")
  algorithm = Checksum.Algorithm.SHA256
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
tasks.register("genAutocomplete"){}
tasks.register("createRunScripts"){}
tasks.named("startScripts") {
    dependsOn("createRunScripts")
}

val scriptNames = mapOf(
  "seisfile" to "edu.sc.seis.seisFile.client.SeisFile",
  "fdsnevent" to "edu.sc.seis.seisFile.client.FDSNEventClient",
  "fdsnstation" to "edu.sc.seis.seisFile.client.FDSNStationClient",
  "fdsndataselect" to "edu.sc.seis.seisFile.client.FDSNDataSelectClient",
  "irisvirtnet" to "edu.sc.seis.seisFile.client.IRISVirtualNetClient",
  "saclh" to "edu.sc.seis.seisFile.client.SacListHeader",
  "mseedlh" to "edu.sc.seis.seisFile.client.MSeedListHeader",
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
    classpath = configurations.annotationProcessor + sourceSets.getByName("client").runtimeClasspath
    main = "picocli.codegen.docgen.manpage.ManPageGenerator"
    val outTemplateDir =  File(project.projectDir,  "src/doc/man-templates")
    val outDir =  File(project.buildDir,  "picocli/man")
    args = listOf("-d", outDir.path, "--template-dir", outTemplateDir.path, scriptNames[key])
    dependsOn += tasks.getByName("compileJava")
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
    classpath = configurations.annotationProcessor + sourceSets.getByName("client").runtimeClasspath
    mainClass.set("picocli.codegen.docgen.manpage.ManPageGenerator")
    val outDir =  File(project.buildDir,  "picocli/man")
    args = listOf("-f", "-d", outDir.path, scriptNames[key])
    dependsOn += tasks.getByName("compileJava")
    inputs.dir("src/client")
    outputs.files(File(outDir, scriptNames[key]+".adoc"))
    doLast {
        mkdir(outDir)
    }
  }
  tasks.named("asciidoctor") {
      dependsOn(adocTask)
      inputs.property(key+".adoc", File(project.buildDir,  "picocli/man/"+scriptNames[key]+".adoc"))
  }
  val bashautoTask = tasks.register<JavaExec>("genAutocomplete"+key) {
    description = "generate picocli bash/zsh autocomplete file "+key
    classpath = sourceSets.getByName("client").runtimeClasspath
    mainClass.set("picocli.AutoComplete")
    val outDir =  File(project.buildDir,  "picocli/bash_completion")
    val outFile = File(outDir, key+"_completion")
    args = listOf(scriptNames[key], "-f", "-o", outFile.path)
    dependsOn += tasks.getByName("compileJava")
    outputs.files(outFile)
    doLast {
        mkdir(outDir)
    }
  }
  tasks.named("genAutocomplete") {
      dependsOn(bashautoTask)
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


tasks.get("explodeDist").dependsOn(tasks.get("docsDir"))
tasks.get("explodeDist").dependsOn(tasks.get("genAutocomplete"))
tasks.get("explodeDist").dependsOn(tasks.get("asciidoctor"))
tasks.get("explodeDist").dependsOn(tasks.get("asciidoctorPdf"))
tasks.get("assemble").dependsOn(tasks.get("checksumDist"))
