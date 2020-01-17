

plugins {
  id("edu.sc.seis.version-class") version "1.1.1"
  "java-library"
  "eclipse"
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
    implementation( "mysql:mysql-connector-java:5.1.47")

    // Use JUnit Jupiter API for testing.
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.5.1")

    // Use JUnit Jupiter Engine for testing.
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.5.1")
}

repositories {
    mavenLocal()
    maven { url  = uri("https://www.seis.sc.edu/software/maven2") }
    mavenCentral()
  //  maven { url "http://spring-rich-c.sourceforge.net/maven2repository" }
  //  maven { url "http://oss.sonatype.org/content/groups/public" }
  //  maven { url "https://repository.jboss.org/nexus/content/repositories/public" }
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
  dependsOn("makeScript")
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
  "cwbclient" to "edu.sc.seis.seisFile.usgsCWB.Client",
  "lissclient" to "edu.sc.seis.seisFile.liss.Client",
  "winstonclient" to "edu.sc.seis.seisFile.winston.WinstonClient",
  "winstonexport" to "edu.sc.seis.seisFile.winston.WinstonExport",
  "earthwormExportTest" to "edu.sc.seis.seisFile.earthworm.EarthwormExport",
  "earthwormImportTest" to "edu.sc.seis.seisFile.earthworm.EarthwormImport",
  "waveserverclient" to "edu.sc.seis.seisFile.waveserver.WaveServerClient",
  "syncfilecompare" to "edu.sc.seis.seisFile.syncFile.SyncFileCompare",
  "syncfile2gmt" to "edu.sc.seis.seisFile.syncFile.GMTSyncFile",
  "refinesyncfile" to "edu.sc.seis.seisFile.syncFile.RefineSyncFile",
  "gcfserialtoew" to "edu.sc.seis.seisFile.gcf.GCFEarthwormExport",
  //"fakegcfserial" to "edu.sc.seis.seisFile.gcf.GCFSerialOutput",
  "sfgroovy" to "groovy.lang.GroovyShell",
  "winstonpurge" to "edu.sc.seis.seisFile.winston.PurgeOldData"
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

tasks.register("makeScript") {
  doLast {
    group = "build"
    //val dExtras = getDExtras()
    val doBat = true
    dependsOn("fdsnevent")
    /*
    ScriptBuilder.create("fdsnevent", "edu.sc.seis.seisFile.fdsnws.EventClient", project, dExtras:dExtras, bat:doBat)
    ScriptBuilder.create("fdsnstationxml", "edu.sc.seis.seisFile.fdsnws.stationxml.FDSNStationXML", project, dExtras:dExtras, bat:doBat)
    ScriptBuilder.create("fdsnstation", "edu.sc.seis.seisFile.fdsnws.StationClient", project, dExtras:dExtras, bat:doBat)
    ScriptBuilder.create("fdsndataselect", "edu.sc.seis.seisFile.fdsnws.DataSelectClient", project, dExtras:dExtras, bat:doBat)
    ScriptBuilder.create("saclh", "edu.sc.seis.seisFile.sac.ListHeader", project, dExtras:dExtras, bat:doBat)
    ScriptBuilder.create("mseedlh", "edu.sc.seis.seisFile.mseed.ListHeader", project, dExtras:dExtras, bat:doBat)
    ScriptBuilder.create("seedlinkclient", "edu.sc.seis.seisFile.seedlink.Client", project, dExtras:dExtras, bat:doBat)
    ScriptBuilder.create("cwbclient", "edu.sc.seis.seisFile.usgsCWB.Client", project, dExtras:dExtras, bat:doBat)
    ScriptBuilder.create("lissclient", "edu.sc.seis.seisFile.liss.Client", project, dExtras:dExtras, bat:doBat)
    ScriptBuilder.create("winstonclient", "edu.sc.seis.seisFile.winston.WinstonClient", project, dExtras:dExtras, bat:doBat)
    ScriptBuilder.create("winstonexport", "edu.sc.seis.seisFile.winston.WinstonExport", project, dExtras:dExtras, bat:doBat)
    ScriptBuilder.create("earthwormExportTest", "edu.sc.seis.seisFile.earthworm.EarthwormExport", project, dExtras:dExtras, bat:doBat)
    ScriptBuilder.create("earthwormImportTest", "edu.sc.seis.seisFile.earthworm.EarthwormImport", project, dExtras:dExtras, bat:doBat)
    ScriptBuilder.create("waveserverclient", "edu.sc.seis.seisFile.waveserver.WaveServerClient", project, dExtras:dExtras, bat:doBat)
    ScriptBuilder.create("syncfilecompare", "edu.sc.seis.seisFile.syncFile.SyncFileCompare", project, dExtras:dExtras, bat:doBat)
    ScriptBuilder.create("syncfile2gmt", "edu.sc.seis.seisFile.syncFile.GMTSyncFile", project, dExtras:dExtras, bat:doBat)
    ScriptBuilder.create("refinesyncfile", "edu.sc.seis.seisFile.syncFile.RefineSyncFile", project, dExtras:dExtras, bat:doBat)
    ScriptBuilder.create("gcfserialtoew", "edu.sc.seis.seisFile.gcf.GCFEarthwormExport", project, dExtras:dExtras, bat:doBat)
    //ScriptBuilder.create("fakegcfserial", "edu.sc.seis.seisFile.gcf.GCFSerialOutput", project, dExtras:dExtras, bat:doBat)
    ScriptBuilder.create("sfgroovy", "groovy.lang.GroovyShell", project, dExtras:dExtras, bat:doBat)
    ScriptBuilder.create("winstonpurge", "edu.sc.seis.seisFile.winston.PurgeOldData", project, dExtras:dExtras, bat:doBat)
*/
    }
}
