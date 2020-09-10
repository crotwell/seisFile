

plugins {
  id("edu.sc.seis.version-class") version "1.1.1"
  "java-library"
  eclipse
  "project-report"
  "maven-publish"
  application
}

application {
  mainClassName = "edu.sc.seis.seisFile.fdsnws.EventClient"
  applicationName = "seisfile"
}

group = "edu.sc.seis"
version = "2.0.0-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
    withJavadocJar()
    withSourcesJar()
}

/*
publishing {
    publications {
        create<MavenPublication>("default") {
          from(components["java"])

          //  artifact(sourcesJar)
          //  artifact(javadocJar)

          pom {
            name = "seisFile"
            description = "A library for reading and writing seismic file formats in java."
            url = "http://www.seis.sc.edu/seisFile.html"

            scm {
              connection = "scm:git:https://github.com/crotwell/seisFile.git"
              developerConnection = "scm:git:https://github.com/crotwell/seisFile.git"
              url = "https://github.com/crotwell/seisFile"
            }

            licenses {
              license {
                name = "The GNU General Public License, Version 3"
                url = "http://www.gnu.org/licenses/gpl-3.0.html"
              }
            }

            developers {
              developer {
                id = "crotwell"
                name = "Philip Crotwell"
                email = "crotwell@seis.sc.edu"
              }
            }
          }
        }
    }

    repositories {
        maven {
            name = "myRepo"
            url = uri("file://${buildDir}/repo")
        }
    }
}
*/

dependencies {
//    compile project(":seedCodec")
    implementation("edu.sc.seis:seedCodec:1.0.11")
    implementation("com.martiansoftware:jsap:2.1")
    implementation( "org.slf4j:slf4j-api:1.7.26")
    implementation( "org.slf4j:slf4j-log4j12:1.7.26")
//
//    compile "org.rxtx:rxtx:2.2.pre2"  ...but not in maven
    implementation( "org.rxtx:rxtx:2.1.7")
    implementation( "com.fasterxml.woodstox:woodstox-core:5.2.1")
    implementation( "net.java.dev.msv:msv-core:2013.6.1")
    implementation( "org.apache.httpcomponents:httpclient:4.5.9")

    // Use JUnit Jupiter API for testing.
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.5.1")

    // Use JUnit Jupiter Engine for testing.
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.5.1")
}

repositories {
    mavenLocal()
    maven { url  = uri("https://www.seis.sc.edu/software/maven2") }
    mavenCentral()
}

sourceSets {
  create("example") {
    compileClasspath += sourceSets.main.get().output
    compileClasspath += sourceSets.main.get().compileClasspath
    runtimeClasspath += sourceSets.main.get().output
    runtimeClasspath += sourceSets.main.get().runtimeClasspath
  }
}

val binDistFiles: CopySpec = copySpec {
    from(configurations.default) {
        into("lib")
    }
    from(configurations.default.allArtifacts.files) {
        into("lib")
    }
    from("build/scripts") {
        include("bin/**")
        include("bat/**")
        fileMode = 755
    }
}

val distFiles: CopySpec = copySpec {
    with(binDistFiles)
    from("build/docs") {
        include("javadoc/**")
    }
    from("build") {
        include("build.gradle")
        include("settings.gradle")
    }
    from(".") {
        include("gpl-3.0.txt")
        include("doc/**")
        include("src/**")
        include("gradle/**")
        include("gradlew")
        include("gradlew.bat")
        exclude("**/*.svn")
    }
    from(".") {
        fileMode = 755
        include("gradlew")
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
  dependsOn("createRunScripts")
  dependsOn("exampleClasses")
  group = "dist"
  with( binDistFiles)
  into( file("$buildDir/explode"))
}

//task explodeDist(type: Sync, dependsOn: ["explodeBin", "javadoc", "modVersionClass", "createBuildScript", ":seiswww:makeSite"]) {
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
    into(project.name+"-"+version+"-bin") {
        with(binDistFiles)
    }
}

tasks.register<Tar>("tarDist") {
  dependsOn("explodeDist" )
    val dirName = project.name+"-"+version
    compression = Compression.GZIP
    into(dirName) {
        with(distFiles)
    }
}

/*
tasks.register<Jar>("exampleJar") {
  dependsOn("exampleClasses" )
    from(sourceSets.example.get().output)
    baseName("seisFileExample")
}

explodeBin.dependsOn(exampleJar)
artifacts {
    exampleJar
}
*/


tasks.register<CreateStartScripts>("xxxfdsnevent") {
  mainClassName = "edu.sc.seis.seisFile.fdsnws.EventClient"
  applicationName = "fdsnevent"
}

tasks.register("createRunScripts"){}
tasks.named("startScripts") {
    dependsOn("createRunScripts")
}

val scriptNames = mapOf(
  "fdsnevent" to "edu.sc.seis.seisFile.fdsnws.EventClient",
  "fdsnstationxml" to "edu.sc.seis.seisFile.fdsnws.stationxml.FDSNStationXML",
  "fdsnstation" to "edu.sc.seis.seisFile.fdsnws.StationClient",
  "fdsndataselect" to "edu.sc.seis.seisFile.fdsnws.DataSelectClient",
  "saclh" to "edu.sc.seis.seisFile.sac.ListHeader",
  "mseedlh" to "edu.sc.seis.seisFile.mseed.ListHeader",
  "seedlinkclient" to "edu.sc.seis.seisFile.seedlink.Client",
  "datalinkclient" to "edu.sc.seis.seisFile.datalink.Client",
  "earthwormExportTest" to "edu.sc.seis.seisFile.earthworm.EarthwormExport",
  "earthwormImportTest" to "edu.sc.seis.seisFile.earthworm.EarthwormImport",
  "waveserverclient" to "edu.sc.seis.seisFile.waveserver.WaveServerClient"
  )
for (key in scriptNames.keys) {
  tasks.register<CreateStartScripts>(key) {
    outputDir = file("build/scripts")
    mainClassName = scriptNames[key]
    applicationName = key
    classpath = sourceSets["main"].runtimeClasspath + project.tasks[JavaPlugin.JAR_TASK_NAME].outputs.files
  }
  tasks.named("createRunScripts") {
      dependsOn(key)
  }
}

