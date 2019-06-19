
class ScriptBuilder {

    static def cleanBashVar(name) {
        return name.replaceAll("[^\\w]","").toUpperCase()
    }

    static def classpather(dep, previous, scriptWriter, bat) {
        if ( ! previous.contains(dep.file.name)) {
            def upDepName = cleanBashVar(dep.name)
            if (bat) {
                scriptWriter.write('set '+upDepName+'=%LIB%\\'+dep.file.name) 
            } else {
                scriptWriter.write(upDepName+'=$LIB/'+dep.file.name) 
            }
            scriptWriter.newLine()
            previous.add(dep.file.name)
            if (bat) {
                return '%'+upDepName+'%;'
            } else {
                return '${'+upDepName+'}:'
            }
        }
        return ""
    }

    static def extrasDefaults(Map extras) {
        if ( ! extras.containsKey('bat')) { extras['bat'] = false }
        if ( ! extras.containsKey('java')) { extras['java'] = 'java' }
        if ( ! extras.containsKey('background')) { extras['background'] = false }
        if ( ! extras.containsKey('dExtras')) { extras['dExtras'] = [:] }
        if ( ! extras.containsKey('moreArgs')) { extras['moreArgs'] = '' }
        if ( ! extras.containsKey('mx')) { extras['mx'] = '512m' }
        if ( ! extras.containsKey('yourkit')) { extras['yourkit'] = false }
    }

    static def create(Map extras = [:],
                      scriptName,
                      mainClass,
                      project) {
        createBash(extras, scriptName, mainClass, project)
        if ( extras['bat']) {
            createBat(extras, scriptName+".bat", mainClass, project)
        }
    }

    static def createBash(Map extras = [:],
                      scriptName,
                      mainClass,
                      project) {
        extrasDefaults(extras)
        def binDir = project.file('build/output/bin')
        binDir.mkdirs()
        def projName = project.name.toUpperCase()
        def scriptFile = new File(binDir, scriptName)
        scriptFile.withWriter { scriptWriter ->
            scriptWriter.write("""#!/bin/bash

if [ -z "\${JAVA}" ] ; then
""")
scriptWriter.write("JAVA="+extras['java']+" \n")
            scriptWriter.write(
"""fi
PRG=\$0
saveddir=`pwd`

# need this for relative symlinks
PRGDIR=`dirname "\$PRG"`

${projName}_HOME="\$PRGDIR/.."

# make it fully qualified
${projName}_HOME=`cd "\${${projName}_HOME}" && pwd`

cd "\$saveddir"

#Jacorb-2.3.0 does bad things with a popular default LANG value, en_US.UTF-8
unset LANG


LIB=\${${projName}_HOME}/lib

""")

            def classpath='CLASSPATH='
            def previous = new HashSet<String>()
            project.configurations.default.allArtifacts.each { dep -> 
                classpath += classpather(dep, previous, scriptWriter, false)
            }
            project.configurations.default.resolvedConfiguration.resolvedArtifacts.each { dep -> 
                classpath += classpather(dep, previous, scriptWriter, false)
            }
            classpath = classpath.substring(0, classpath.length()-1)
            def javaopts=' -Xmx'+extras['mx']
            if (extras['yourkit']) {
                osForYourKit(scriptWriter)
                javaopts += " -agentpath:\$AGENTPATH "
            }
            javaopts += " -XX:+HeapDumpOnOutOfMemoryError "
            extras['dExtras'].each {key, value -> javaopts += " -D${key}=${value}" }

            if (extras['java'] == 'scala') {
                scriptWriter.write("""
JAVA_OPTS="$javaopts"
""")
            }
            scriptWriter.write("""

$classpath

\$JAVA -classpath \$CLASSPATH \\\n""")
            if (extras['java'] != 'scala') {
                scriptWriter.write("    "+javaopts+" \\\n")
            }
            scriptWriter.write("""    $mainClass  """)
            scriptWriter.write(extras['moreArgs'])
            scriptWriter.write(""" "\$@" """)
            if (extras['background']) {
                scriptWriter.write("""  >> ${scriptName}.out 2>&1 &
echo \$! > ${scriptName}.pid
""")
            }
            scriptWriter.newLine()
            scriptWriter.close()
        }
        project.ant.chmod(file: scriptFile, perm: 'ugo+rx')
    }

    static def createBat(Map extras = [:],
                      scriptName,
                      mainClass,
                      project) {
        extrasDefaults(extras)
        def binDir = project.file('build/output/bin')
        binDir.mkdirs()
        def projName = project.name.toUpperCase()
        def scriptFile = new File(binDir, scriptName)
        scriptFile.withWriter { scriptWriter ->
            scriptWriter.write("""@echo off
set APPDIR=%~dp0
set CMD_LINE_ARGS=%1
shift
:getArgs
if " "%1" "==" "" " goto doneArgs
set CMD_LINE_ARGS=%CMD_LINE_ARGS% %1
shift
goto getArgs
:doneArgs

if "%JAVA%"=="" set JAVA=java
if "%${projName}_HOME%"=="" GOTO FIND
echo ${projName}_HOME is no longer used and will be ignored
:FIND
PUSHD %APPDIR%
cd ..
set ${projName}_HOME=%CD%
POPD

set LIB=%${projName}_HOME%\\lib

""")

            def classpath='set CLASSPATH='
            def previous = new HashSet<String>()
            project.configurations.default.allArtifacts.each { dep ->
                classpath += classpather(dep, previous, scriptWriter, true)
            }
            project.configurations.default.resolvedConfiguration.resolvedArtifacts.each { dep ->
 
                classpath += classpather(dep, previous, scriptWriter, true)
            }
            classpath = classpath.substring(0, classpath.length()-1)

            def javaopts=' -Xmx'+extras['mx']
            if (extras['yourkit']) {
                javaopts += " -agentpath:\$AGENTPATH "
            }
            extras['dExtras'].each {key, value -> javaopts += " -D${key}=${value}" }

            scriptWriter.write("""

if EXIST "%${projName}%" GOTO LIBEND
echo %${projName}% doesn't exist
echo ${projName} requires this file to function.  It should be in the lib dir
echo parallel to the bin directory to this script in the filesystem.
echo If it seems like the lib dir is there, email sod@seis.sc.edu for help
GOTO END
:LIBEND
    
$classpath

%JAVA% -classpath %CLASSPATH% """)
            if (extras['java'] != 'scala') {
                scriptWriter.write(" "+javaopts)
            }
            scriptWriter.write("""    $mainClass  %CMD_LINE_ARGS%
:END
""")


        }
    }

    static def osForYourKit(scriptWriter) {
scriptWriter.write("""

# Set AGENTPATH to system-dependent path to the profiler agent library
if [ "`uname -a | grep Linux`" ] ; then
  if [ "`uname -a | grep x86_64`" ] ; then
    # We assume that 64-bit Java is used on Linux AMD 64; otherwise change "64" with "32" in the path.
    AGENTPATH="\$LIB/yourkit/linux-x86-64/libyjpagent.so"
  else
    # 32-bit Java
    AGENTPATH="\$LIB/yourkit/linux-x86-32/libyjpagent.so"
  fi
elif [ `uname` = 'Darwin' ] ; then
  # Mac OS X
  AGENTPATH="\$LIB/yourkit/mac/libyjpagent.jnilib"
elif [ `uname` = 'SunOS' ] ; then
  # Solaris:
  # We suppose that JVM is 32-bit by default.
  # Change "32" with "64" in the path to use with 64-bit Java.
  if [ "`uname -a | grep sparc`" ] ; then
    AGENTPATH="\$LIB/yourkit/solaris-sparc-32/libyjpagent.so"
  elif [ "`uname -a | grep i386`" ] ; then
    AGENTPATH="\$LIB/yourkit/solaris-x86-32/libyjpagent.so"
  else
    echo "Unsupported YourKit processor architecture"
    exit
  fi
elif [ `uname` = 'FreeBSD' ] ; then
  if [ "`uname -a | grep amd64`" ] ; then
    AGENTPATH="\$LIB/yourkit/freebsd-x86-64/libyjpagent.so"
  elif [ "`uname -a | grep i386`" ] ; then
    AGENTPATH="\$LIB/yourkit/freebsd-x86-32/libyjpagent.so"
  else
    echo "Unsupported processor architecture"
    exit
  fi
elif [ `uname` = 'HP-UX' ] ; then
  # On HP-UX, JVM is 32-bit by default.
  # Change "32" with "64" in the path to use with 64-bit Java.
  AGENTPATH="\$LIB/yourkit/hpux-ia64-32/libyjpagent.so"
else
  echo "Unsupported platform: `uname`"
  exit
fi
""")
}

}
